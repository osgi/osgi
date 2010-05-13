/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.condpermadmin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.impl.service.policy.AbstractPolicyPlugin;
import org.osgi.impl.service.policy.PermissionInfoMetaNode;
import org.osgi.impl.service.policy.RootMetaNode;
import org.osgi.impl.service.policy.util.ConditionInfoComparator;
import org.osgi.impl.service.policy.util.PermissionInfoComparator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * dmt plugin for exposing Conditional Permission Admin functionality
 * 
 * @version $Id$
 */
public class ConditionalPermissionAdminPlugin extends AbstractPolicyPlugin {
	
	
	private static final String EMPTY_STRING_MANGLED = "2jmj7l5rSw0yVb_vlWAYkK_YBwk";

	private static final String	PERMISSIONINFO	= PermissionInfoMetaNode.PERMISSIONINFO;
	private static final String CONDITIONINFO = ConditionInfoMetaNode.CONDITIONINFO;
	private static final String NAME = NameMetaNode.NAME;

	/**
	 * the conditional permission admin to communicate with
	 */
	private ConditionalPermissionAdmin	condPermAdmin;
	
	/**
	 * a map of String->ConditionalPermission, where the key is the hash as seen in the tree
	 */
	private Map conditionalPermissions;
	
	/**
	 * metanode given back when asked about ./OSGi/Policy/Java/ConditionalPermission
	 */
	private static final MetaNode rootMetaNode = new RootMetaNode("permissions specified by conditions",true);

	/**
	 * metanode given back when asked about ./OSGi/Policy/Java/ConditionalPermission/[...]/PermissionInfo
	 */
	private static final MetaNode permissionInfoMetaNode = new PermissionInfoMetaNode();
	
	/**
	 * metanode given back when asked about ./OSGi/Policy//Java/ConditionalPermission/[...]/ConditionInfo
	 */
	private static final MetaNode conditionInfoMetaNode = new ConditionInfoMetaNode();

	/**
	 * metanode given back when asked about ./OSGi/Policy/Java/ConditionalPermission/[...]
	 */
	private static final MetaNode conditionalPermissionMetaNode = new ConditionalPermissionMetaNode();

	/**
	 * metanode given back when asked about ./OSGi/Policy/Java/ConditionalPermission/[...]/Name
	 */
	private static final MetaNode nameMetaNode = new NameMetaNode();

	/**
	 * internal representation of a conditional permission
	 */
	private static class ConditionalPermission implements Comparable {
		public String name;
		public ConditionInfo[] conditionInfo;
		public PermissionInfo[] permissionInfo;
		
		public ConditionalPermission(String name,ConditionInfo[] conditionInfo,PermissionInfo permissionInfo[]) {
			this.name = name;
			this.conditionInfo = conditionInfo;
			this.permissionInfo = permissionInfo;
		}

		public ConditionalPermission() {
			name = "";
			conditionInfo = new ConditionInfo[0];
			permissionInfo = new PermissionInfo[0];
		}

		public DmtData getNodeValue(String nodeName) {
			// note: nodeName is already checked here
			if (nodeName.equals(NAME)) {
				return new DmtData(name);
			}
			if (nodeName.equals(PERMISSIONINFO)) {
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<permissionInfo.length;i++) {
					sb.append(permissionInfo[i].getEncoded());
					sb.append('\n');
				}
				return new DmtData(sb.toString());
			} else {
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<conditionInfo.length;i++) {
					sb.append(conditionInfo[i].getEncoded());
					sb.append('\n');
				}
				return new DmtData(sb.toString());
			}
		}
		
		public void setNodeValue(String nodeName, DmtData data) throws DmtException {
			if (nodeName.equals(NAME)) {
				name=data.getString();
			} else 	if (nodeName.equals(PERMISSIONINFO)) {
				String[] strs = Splitter.split(data.getString(),'\n',0);
				PermissionInfo[] pis = new PermissionInfo[strs.length];
				for(int i=0;i<pis.length;i++) {
					pis[i] = new PermissionInfo(strs[i]);
				}
				permissionInfo = pis;
			} else {
				String[] strs = Splitter.split(data.getString(),'\n',0);
				ConditionInfo[] cis = new ConditionInfo[strs.length];
				for(int i=0;i<cis.length;i++) {
					cis[i] = new ConditionInfo(strs[i]);
				}
				conditionInfo = cis;
			}
		}

		public int compareTo(Object var0) {
			ConditionalPermission other = (ConditionalPermission) var0;
			return this.name.compareTo(other.name);
		}

	}

	/**
	 * utility for ordering PermissionInfos lexicographically by their encoded string representations
	 */
	private static final Comparator permissionInfoComparator = new PermissionInfoComparator();
	
	/**
	 * utility for ordering ConditionInfos lexicographically by their encoded string representations
	 */
	private static final Comparator conditionInfoComparator = new ConditionInfoComparator();
	
		
	public ConditionalPermissionAdminPlugin(ComponentContext context) {
		super(context);
		condPermAdmin = (ConditionalPermissionAdmin) context.locateService("condPermAdmin");

		// copy everything from the conditional permission admin, populate our tree
		conditionalPermissions = new HashMap();
		for(Enumeration en = condPermAdmin.getConditionalPermissionInfos(); en.hasMoreElements();) {
			ConditionalPermissionInfo e = (ConditionalPermissionInfo)en.nextElement();

			String name = e.getName();
			// Spec bug #130  workaround
			// Possibly this will be the official behavior
			if (name.equals("")) { name = EMPTY_STRING_MANGLED; }
			conditionalPermissions.put(mangle(name),
					new ConditionalPermission(e.getName(),e.getConditionInfos(),e.getPermissionInfos()));
		}
}

	public MetaNode getMetaNode(String fullPath[])
			throws DmtException {
		String[] path = chopPath(fullPath);
		if (path.length==0) {
			return rootMetaNode;
		}
		if (path.length==1) {
			return conditionalPermissionMetaNode;
		}
		if (path.length==2) {
			if (path[1].equals(NAME)) return nameMetaNode;
			if (path[1].equals(PERMISSIONINFO)) return permissionInfoMetaNode;
			if (path[1].equals(CONDITIONINFO)) return conditionInfoMetaNode;
			throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,
					"Must be either "+PERMISSIONINFO+" or "+CONDITIONINFO);
		}
		
		throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
	}

	public void rollback() throws DmtException {
		// nothing to do, simply don't write back stuff to the system
		conditionalPermissions = null;
	}

	public void setNodeValue(String fullPath[], DmtData data) throws DmtException {
		if (!isLeafNode(fullPath)) throw new DmtException(fullPath,DmtException.FEATURE_NOT_SUPPORTED,"cannot set value for this interior node");
		String[] path = chopPath(fullPath);
		switchToWriteMode();
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		cp.setNodeValue(path[1],data);
	}

	public void deleteNode(String fullPath[]) throws DmtException {
		String[] path = chopPath(fullPath);
		switchToWriteMode();
		if (path.length!=1) throw new IllegalStateException(); // should not get here
		conditionalPermissions.remove(path[0]);
	}

	public void createInteriorNode(String fullPath[],String type) throws DmtException {
		String[] path = chopPath(fullPath);
		switchToWriteMode();
		if (path.length!=1) throw new IllegalStateException(); // should not get here
		conditionalPermissions.put(path[0],new ConditionalPermission());
	}


	public void commit() throws DmtException {
		if (!isDirty()) return; 
		
		// TODO check for consistency
		
		// find out which to delete, which to add
		Enumeration originals = condPermAdmin.getConditionalPermissionInfos();
		TreeSet toAdd = new TreeSet();
		toAdd.addAll(conditionalPermissions.values());
		List toDelete = new ArrayList();
		while(originals.hasMoreElements()) {
			ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) originals.nextElement();
			ConditionalPermission cp = new ConditionalPermission(cpi.getName(),cpi.getConditionInfos(),cpi.getPermissionInfos());

			if (toAdd.contains(cp)) {
				// it is already in the system, don't do anything with it
				toAdd.remove(cp);
			} else {
				// this needs to be removed from the system
				toDelete.add(cpi);
			}
		}
		for (Iterator iter = toAdd.iterator(); iter.hasNext();) {
			ConditionalPermission cp = (ConditionalPermission) iter.next();
			condPermAdmin.setConditionalPermissionInfo(cp.name,cp.conditionInfo,cp.permissionInfo);
		}
		for (Iterator iter = toDelete.iterator(); iter.hasNext();) {
			ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) iter.next();
			cpi.delete();
		}

		// do some cleanup
		conditionalPermissions = null;
	}
	
	public void close() {}

	public boolean isNodeUri(String path[]) {
		path = chopPath(path);
		if (path.length==0) return true;
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		if (cp==null) return false;
		if (path.length==1) return true;
		if (path.length!=2) return false;
		if (path[1].equals(NAME)) return true;
		if (path[1].equals(PERMISSIONINFO)) return true;
		if (path[1].equals(CONDITIONINFO)) return true;
		return false;
	}

	public DmtData getNodeValue(String[] path) throws DmtException {
		if (!isLeafNode(path)) throw new DmtException(path,DmtException.FEATURE_NOT_SUPPORTED,"cannot get value for this interior node");
		path = chopPath(path);
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		return cp.getNodeValue(path[1]);
	}

	public boolean isLeafNode(String path[]) throws DmtException {
		path = chopPath(path);
		return path.length==2;
	}

	public String[] getChildNodeNames(String fullPath[]) throws DmtException {
		String[] path = chopPath(fullPath);
		if (path.length==0) {
			Set hashes = conditionalPermissions.keySet();
			String children[] = new String[hashes.size()];
			return (String[]) hashes.toArray(children);
		} else {
			return new String[] { NAME,PERMISSIONINFO, CONDITIONINFO };
		}
	}



}

/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.impl.service.policy.PermissionInfoMetaNode;
import org.osgi.impl.service.policy.RootMetaNode;
import org.osgi.impl.service.policy.util.HashCalculator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * dmt plugin for exposing Conditional Permission Admin functionality
 * 
 * @version $Revision$
 */
public class ConditionalPermissionAdminPlugin implements DmtDataPlugin {
	
	
	private static final String	PERMISSIONINFO	= "PermissionInfo";
	private static final String CONDITIONINFO = "ConditionInfo";

	/**
	 * the conditional permission admin to communicate with
	 */
	private final ConditionalPermissionAdmin	condPermAdmin;

	/**
	 * the official root position in the management tree
	 */
	public static final String dataRootURI = "./OSGi/Policies/Java/ConditionalPermission";

	/**
	 * a map of String->ConditionalPermission, where the key is the hash as seen in the tree
	 */
	private Map conditionalPermissions;
	
	/**
	 * true, if something is changed, and needs to be written back to the system
	 */
	private boolean dirty;
	
	/**
	 * metanode given back when asked about ./OSGi/Policies/Java/ConditionalPermission
	 */
	private static final DmtMetaNode rootMetaNode = new RootMetaNode("permissions specified by conditions");

	/**
	 * metanode given back when asked about ./OSGi/Policies/Java/ConditionalPermission/[...]/PermissionInfo
	 */
	private static final DmtMetaNode permissionInfoMetaNode = new PermissionInfoMetaNode();
	
	/**
	 * metanode given back when asked about ./OSGi/Policies/Java/ConditionalPermission/[...]/ConditionInfo
	 */
	private static final DmtMetaNode conditionInfoMetaNode = new ConditionInfoMetaNode();

	/**
	 * metanode given back when asked about ./OSGi/Policies/Java/ConditionalPermission/[...]
	 */
	private static final DmtMetaNode conditionalPermissionMetaNode = new ConditionalPermissionMetaNode();

	/**
	 * internal representation of a conditional permission
	 */
	private static class ConditionalPermission {
		public ConditionInfo[] conditionInfo;
		public PermissionInfo[] permissionInfo;
		
		public ConditionalPermission(ConditionInfo[] conditionInfo,PermissionInfo permissionInfo[]) {
			this.conditionInfo = conditionInfo;
			this.permissionInfo = permissionInfo;
		}

		public ConditionalPermission() {
			conditionInfo = new ConditionInfo[0];
			permissionInfo = new PermissionInfo[0];
			
			
		}

		public DmtData getNodeValue(String nodeName) {
			// note: nodeName is already checked here
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
		
		
		public boolean equals(Object obj) {
			if (obj==this) return true;
			ConditionalPermission o = (ConditionalPermission)obj;
			return equals(o.permissionInfo,o.conditionInfo);
		}
		
		public boolean equals(PermissionInfo pi[],ConditionInfo ci[]) {
			if (conditionInfo.length!=ci.length) return false;
			if (permissionInfo.length!=pi.length) return false;
			for(int i=0;i<permissionInfo.length;i++) {
				if (!permissionInfo[i].equals(pi[i])) return false;
			}
			for(int i=0;i<conditionInfo.length;i++) {
				if (!conditionInfo[i].equals(ci[i])) return false;
			}
			return true;
			
		}
		
		
		public int hashCode() {
			return 0; // TODO
		}

		public void setNodeValue(String nodeName, DmtData data) {
			if (nodeName.equals(PERMISSIONINFO)) {
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
	}

	/**
	 * utility for ordering PermissionInfos lexicographically by their encoded string representations
	 */
	private static final Comparator permissionInfoComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			PermissionInfo p1 = (PermissionInfo) o1;
			PermissionInfo p2 = (PermissionInfo) o2;
			return p1.getEncoded().compareTo(p2.getEncoded());
		}
		
	};
	
	/**
	 * utility for ordering ConditionInfos lexicographically by their encoded string representations
	 */
	private static final Comparator conditionInfoComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			ConditionInfo c1 = (ConditionInfo) o1;
			ConditionInfo c2 = (ConditionInfo) o2;
			return c1.getEncoded().compareTo(c2.getEncoded());
		}
	};
	
	private final HashCalculator hashCalculator;
	
	public ConditionalPermissionAdminPlugin(ConditionalPermissionAdmin condPermAdmin) throws NoSuchAlgorithmException {
		this.condPermAdmin = condPermAdmin;
		hashCalculator = new HashCalculator();
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		// copy everything from the conditional permission admin, populate our tree
		conditionalPermissions = new HashMap();
		for(Enumeration enum = condPermAdmin.getConditionalPermissionInfos(); enum.hasMoreElements();) {
			ConditionalPermissionInfo e = (ConditionalPermissionInfo)enum.nextElement();

			// calculate hash
			ConditionInfo[] conditionInfo = (ConditionInfo[]) e.getConditionInfos().clone();
			PermissionInfo[] permissionInfo = (PermissionInfo[]) e.getPermissionInfos().clone();
			Arrays.sort(conditionInfo,conditionInfoComparator);
			Arrays.sort(permissionInfo,permissionInfoComparator);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < permissionInfo.length; i++) {
				PermissionInfo info = permissionInfo[i];
				sb.append(info.getEncoded());
				sb.append('\n');
			}
			for (int i = 0; i < conditionInfo.length; i++) {
				ConditionInfo info = conditionInfo[i];
				sb.append(info.getEncoded());
				sb.append('\n');
			}
			
			// add to tree
			String hash = hashCalculator.getHash(sb.toString());
			conditionalPermissions.put(hash,
					new ConditionalPermission(e.getConditionInfos(),e.getPermissionInfos()));
		}
		dirty = false;
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		// note: nodeUri is already checked here
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			return rootMetaNode;
		}
		if (path.length==1) {
			return conditionalPermissionMetaNode;
		}
		if (path[1].equals(PERMISSIONINFO)) {
			return permissionInfoMetaNode;
		} else {
			return conditionInfoMetaNode;
		}
	}

	public boolean supportsAtomic() {
		return true;
	}

	public void rollback() throws DmtException {
		throw new DmtException(dataRootURI,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		String path[] = getPath(nodeUri);
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		cp.setNodeValue(path[1],data);
		dirty = true;
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void deleteNode(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		if (path.length!=1) {
			throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,"");
		}
		conditionalPermissions.remove(path[0]);
		dirty = true;
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length!=1) {
			throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,"");
		}
		conditionalPermissions.put(path[0],new ConditionalPermission());
		dirty = true;
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void close() throws DmtException {
		if (!dirty) return; // the easy way :-)
		
		// TODO check for consistency
		
		// find out which to delete, which to add
		Enumeration originals = condPermAdmin.getConditionalPermissionInfos();
		Collection toAdd = conditionalPermissions.values();
		List toDelete = new ArrayList();
		while(originals.hasMoreElements()) {
			ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) originals.nextElement();
			ConditionalPermission cp = new ConditionalPermission(cpi.getConditionInfos(),cpi.getPermissionInfos());

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
			condPermAdmin.addConditionalPermissionInfo(cp.conditionInfo,cp.permissionInfo);
		}
		for (Iterator iter = toDelete.iterator(); iter.hasNext();) {
			ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) iter.next();
			cpi.delete();
		}

		// do some cleanup
		conditionalPermissions = null;
	}

	public boolean isNodeUri(String nodeUri) {
		String[] path = getPath(nodeUri);
		if (path.length==0) return true;
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		if (cp==null) return false;
		if (path.length==1) return true;
		if (path.length!=2) return false;
		if (path[1].equals(PERMISSIONINFO)) return true;
		if (path[1].equals(CONDITIONINFO)) return true;
		return false;
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		// note: nodeUri and metanode are already checked here
		String[] path = getPath(nodeUri);
		ConditionalPermission cp = (ConditionalPermission) conditionalPermissions.get(path[0]);
		return cp.getNodeValue(path[1]);
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			Set hashes = conditionalPermissions.keySet();
			String children[] = new String[hashes.size()];
			return (String[]) hashes.toArray(children);
		} else {
			return new String[] { PERMISSIONINFO, CONDITIONINFO };
		}
	}

	/**
	 * return the path elements, from our base
	 * @param nodeUri
	 * @return an array of nodenames
	 */
	private String[] getPath(String nodeUri) {
		if (!nodeUri.startsWith(dataRootURI)) 
			throw new IllegalStateException("Dmt should not give me URIs that are not mine");
		if (nodeUri.length()==dataRootURI.length()) return new String[] {};
		return Splitter.split(nodeUri.substring(dataRootURI.length()+1),'/',0);
	}

}

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
package org.osgi.impl.service.policy.dmtprincipal;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;

import java.io.IOException;
import java.security.*;
import java.util.*;
import java.util.Map.Entry;

import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.AbstractPolicyPlugin;
import org.osgi.impl.service.policy.PermissionInfoMetaNode;
import org.osgi.impl.service.policy.RootMetaNode;
import org.osgi.impl.service.policy.util.PermissionInfoComparator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * dmt plugin for exposing dmt principal data.
 * 
 * @version $Revision$
 */
public class DmtPrincipalPlugin extends AbstractPolicyPlugin {
	
	private static final String	PERMISSIONINFO	= PermissionInfoMetaNode.PERMISSIONINFO;
	private static final String	PRINCIPAL	= PrincipalMetaNode.PRINCIPAL;
	/**
	 * This object is the interface to the DMT Admin. Through this, we can
	 * modify the different Principals PermissionInfo.
	 */
	private DmtPrincipalPermissionAdmin dmtPrincipalPermissionAdmin;
	
	/**
	 * The current state of the tree. It is a map from hashes to PrincipalPermissions.
	 * The hashes are the first level node names of the tree.
	 */
	private Map currentState;

	/**
	 * utility for ordering permissioninfos lexicographically
	 */
	private final static Comparator permissionInfoComparator = new PermissionInfoComparator();

	/**
	 * Info about a principal and its associated permissions.
	 */
	private class PrincipalPermission {
		String principal;
		PermissionInfo permissionInfo[];
		public PrincipalPermission(String principal,PermissionInfo[] permissionInfo) {
			this.principal = principal;
			this.permissionInfo = permissionInfo;
		}

		public void setNodeValue(String nodename, String value) throws IllegalArgumentException {
			if (PRINCIPAL.equals(nodename)) {
				principal = value;
				return;
			}
			if (PERMISSIONINFO.equals(nodename)) {
				String[] values = Splitter.split(value,'\n',0);
				PermissionInfo[] pi = new PermissionInfo[values.length];
				for(int i=0;i<values.length;i++) {
					pi[i] = new PermissionInfo(values[i]); // throws IllegalArgumentException
				}
				Arrays.sort(pi,permissionInfoComparator);
				permissionInfo = pi;
				return;
			}
			throw new IllegalStateException("nodename="+nodename); // cannot get here 
		}

		public DmtData getNodeValue(String nodename) {
			if (nodename.equals(PRINCIPAL)) return new DmtData(principal);
			if (nodename.equals(PERMISSIONINFO)) {
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<permissionInfo.length;i++) {
					sb.append(permissionInfo[i].getEncoded());
					sb.append('\n');
				}
				return new DmtData(sb.toString());
			}
			throw new IllegalStateException("nodename="+nodename);
		}
	}

	private static final MetaNode rootMetaNode = new RootMetaNode("tree representing DMT Principal permissions");
	private static final MetaNode principalMetaNode = new PrincipalMetaNode();
	private static final MetaNode permissionInfoMetaNode = new PermissionInfoMetaNode();
	private static final MetaNode principalPermissionMetaNode = new PrincipalPermissionMetaNode();

	private final PrivilegedAction getPrincipalPermissions = new PrivilegedAction(){
		public Object run() {
			return dmtPrincipalPermissionAdmin.getPrincipalPermissions();
		}
	};

	private final class SetPrincipalPermissions implements PrivilegedExceptionAction {
		public Map systemState;
		public Object run() throws IOException {
			dmtPrincipalPermissionAdmin.setPrincipalPermissions(systemState);
			return null;
		}
	};
	private final SetPrincipalPermissions setPrincipalPermissions = new SetPrincipalPermissions();

	public DmtPrincipalPlugin(final ComponentContext context) {
		super(context);

		// The plugin may be created in the security context of a Dmt principal,
		// and it may not have ServicePermission, that's why the doPrivileged
		AccessController.doPrivileged(new PrivilegedAction(){
			public Object run() {
				dmtPrincipalPermissionAdmin = (DmtPrincipalPermissionAdmin) context.locateService("dmtPrincipalPermissionAdmin");
				return null;
			}
		});

		currentState = new HashMap();

		// Note that the security check is already done in the DMT Admin, when it
		// called us - whoever can read/write this subtree, has full access
		Map systemState = (Map) AccessController.doPrivileged(getPrincipalPermissions);

		// copy the permission structure to our tree structure
		for (Iterator iter = systemState.entrySet().iterator(); iter.hasNext();) {
			Map.Entry e = (Entry) iter.next();
			String p =  (String) e.getKey();
			PermissionInfo[] pi = (PermissionInfo[]) e.getValue();
			pi = (PermissionInfo[]) pi.clone();
			currentState.put(mangle(p),new PrincipalPermission(p,pi));
		}
	}
	
	public MetaNode getMetaNode(String fullPath[])
			throws DmtException {
		String[] path = chopPath(fullPath);
		if (path.length==0) return rootMetaNode;
		if (path.length==1) {
			return principalPermissionMetaNode;
		}
		if (path.length==2) {
			if (PRINCIPAL.equals(path[1])) return principalMetaNode;
			if (PERMISSIONINFO.equals(path[1])) return permissionInfoMetaNode;
			throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,
					"Must be either "+PRINCIPAL+" or "+PERMISSIONINFO);
		}
		throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
	}

	public void rollback() throws DmtException {
		throw new DmtException(ROOT,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void setNodeValue(String path[], DmtData data) throws DmtException {
		if (!isLeafNode(path)) throw new DmtException(path,DmtException.FEATURE_NOT_SUPPORTED,"cannot set value for this interior node");
		path = chopPath(path);
		switchToWriteMode();
		if (path.length!=2) throw new IllegalStateException(); // this cannot happen
		PrincipalPermission p = (PrincipalPermission) currentState.get(path[0]);
		p.setNodeValue(path[1],data.getString());
	}

	public void deleteNode(String path[]) throws DmtException {
		path = chopPath(path);
		switchToWriteMode();
		if (path.length!=1) throw new IllegalStateException(); // should not get here
		currentState.remove(path[0]);
	}

	public void createInteriorNode(String path[],String type) throws DmtException {
		path = chopPath(path);
		switchToWriteMode();
		if (path.length!=1) throw new IllegalStateException(); // should not get here
		currentState.put(path[0],new PrincipalPermission("",null));
	}

	public void commit() throws DmtException {
		if (!isDirty()) return;
		
		// used for consistency check...
		Set principals = new TreeSet();
		
		// create a map as dmt admin likes it
		Map systemState = new HashMap();
		for (Iterator iter = currentState.values().iterator(); iter.hasNext();) {
			PrincipalPermission element = (PrincipalPermission) iter.next();
			String principal = element.principal;
			if ((principal==null)||(principal.equals(""))) {
				throw new DmtException(ROOT,DmtException.COMMAND_FAILED,"empty principal");
			}
			if (principals.contains(principal)) {
				throw new DmtException(ROOT,DmtException.COMMAND_FAILED,"principal name "+principal+" occurs twice");
			}
			principals.add(principal);
			systemState.put(principal,element.permissionInfo);
		}
		try {
			setPrincipalPermissions.systemState = systemState;
		    AccessController.doPrivileged(setPrincipalPermissions);
        }
		catch (PrivilegedActionException e) {
            throw new DmtException(ROOT, DmtException.DATA_STORE_FAILURE,
                    "error persisting permissions", e);
		}
		
		// do some cleanup
		currentState = null;
	}

	public void close() {}
	
	public boolean isNodeUri(String path[]) {
		path = chopPath(path);
		if (path.length==0) return true;
		PrincipalPermission pp = null;
		pp = (PrincipalPermission) currentState.get(path[0]);
		if (pp==null) return false;
		if (path.length==1) return true;
		if (path.length>2) return false;
		if (path[1].equals(PRINCIPAL)) return true;
		if (path[1].equals(PERMISSIONINFO)) return true;
		return false;
	}

	public DmtData getNodeValue(String path[]) throws DmtException {
		if (!isLeafNode(path)) throw new DmtException(path,DmtException.FEATURE_NOT_SUPPORTED,"cannot get value for this interior node");
		path = chopPath(path);

		// isNodeUri and metadata should check all this
		if (path.length!=2) throw new IllegalStateException(""+path.length);
		PrincipalPermission pp = (PrincipalPermission) currentState.get(path[0]);
		return pp.getNodeValue(path[1]);
	}

	public boolean isLeafNode(String path[]) throws DmtException {
		path = chopPath(path);
		return path.length==2;
	}

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
	}

	public String[] getChildNodeNames(String path[]) throws DmtException {
		path = chopPath(path);
		// note: nodeUri is already checked
		if (path.length==0) {
			Set ks = currentState.keySet();
			String[] ss = new String[ks.size()];
			return (String[]) ks.toArray(ss);
		}
		if (path.length==1) {
			return new String[] { PRINCIPAL, PERMISSIONINFO };
		}
		
		// since nodeUri is checked, we CANNOT get here
		throw new IllegalStateException();
	}


}

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

package org.osgi.impl.service.policy.dmtprincipal;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.PermissionInfoMetaNode;
import org.osgi.impl.service.policy.RootMetaNode;
import org.osgi.impl.service.policy.util.HashCalculator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * dmt plugin for exposing dmt principal data.
 * 
 * @version $Revision$
 */
public class DmtPrincipalPlugin implements DmtDataPlugin {
	
	private static final String	PERMISSIONINFO	= "PermissionInfo";
	private static final String	PRINCIPAL	= "Principal";
	/**
	 * This object is the interface to the DMT Admin. Through this, we can
	 * modify the different Principals PermissionInfo.
	 */
	private final DmtPrincipalPermissionAdmin dmtPrincipalPermissionAdmin;
	
	/**
	 * The current state of the tree. It is a map from hashes to PrincipalPermissions.
	 * The hashes are the first level node names of the tree.
	 */
	private Map currentState;

	/**
	 * utility for ordering permissioninfos lexicographically
	 */
	private final static Comparator permissionInfoComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			PermissionInfo p1 = (PermissionInfo) o1;
			PermissionInfo p2 = (PermissionInfo) o2;
			return p1.toString().compareTo(p2.toString());
		}};

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
	}

	/**
	 * true, if changes are made from the system state
	 */
	private boolean dirty;

	/**
	 * The point where this plugin should go in the DMT.
	 */
	public static final String dataRootURI = "./OSGi/Policies/Java/DmtPrincipal";
	
	private static final DmtMetaNode rootMetaNode = new RootMetaNode("tree representing DMT Principal permissions");
	private static final DmtMetaNode principalMetaNode = new PrincipalMetaNode();
	private static final DmtMetaNode permissionInfoMetaNode = new PermissionInfoMetaNode();
	private final HashCalculator hashCalculator;

	public DmtPrincipalPlugin(DmtPrincipalPermissionAdmin dmtPrincipalPermissionAdmin) throws NoSuchAlgorithmException {
		this.dmtPrincipalPermissionAdmin = dmtPrincipalPermissionAdmin;
		this.currentState = null; // open() fills it
		hashCalculator = new HashCalculator();
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		currentState = new HashMap();
		Map systemState = dmtPrincipalPermissionAdmin.getPrincipalPermissions();

		// copy the permission structure to our tree structure
		for (Iterator iter = systemState.entrySet().iterator(); iter.hasNext();) {
			Map.Entry e = (Entry) iter.next();
			String p =  (String) e.getKey();
			PermissionInfo[] pi = (PermissionInfo[]) e.getValue();
			pi = (PermissionInfo[]) pi.clone();
			currentState.put(hashCalculator.getHash(p),new PrincipalPermission(p,pi));
		}
		
		dirty = false;
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) return rootMetaNode;
		if (path.length==1) {
			// TODO
			throw new IllegalStateException();
		}
		if (path.length==2) {
			if (PRINCIPAL.equals(path[1])) return principalMetaNode;
			if (PERMISSIONINFO.equals(path[1])) return permissionInfoMetaNode;
		}

		// cannot get here
		throw new IllegalStateException();
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
		if (path.length!=2) throw new IllegalStateException(); // this cannot happen
		PrincipalPermission p = (PrincipalPermission) currentState.get(path[0]);
		p.setNodeValue(path[1],data.getString());
		dirty = true;
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void deleteNode(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		if (path.length!=1) throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,"");
		currentState.put(path[0],new PrincipalPermission("",null));
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
		if (!dirty) return;
		
		// TODO check for consistency
		
		// create a map as dmt admin likes it
		Map systemState = new HashMap();
		for (Iterator iter = currentState.values().iterator(); iter.hasNext();) {
			PrincipalPermission element = (PrincipalPermission) iter.next();
			systemState.put(element.principal,element.permissionInfo);
		}
		
		dmtPrincipalPermissionAdmin.setPrincipalPermissions(systemState);
		
		// do some cleanup
		dirty = false;
		currentState = null;
	}

	public boolean isNodeUri(String nodeUri) {
		String path[] = getPath(nodeUri);
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

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
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
		// note: nodeUri is already checked
		if (path.length==0) {
			Set ks = currentState.keySet();
			String[] ss = new String[ks.size()];
			return (String[]) ks.toArray(ss);
		}
		if (path.length==1) {
			return new String[] { PRINCIPAL, PERMISSIONINFO };
		}
		
		// since nodeUri is checked, we CANNOT get her
		throw new IllegalStateException();
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

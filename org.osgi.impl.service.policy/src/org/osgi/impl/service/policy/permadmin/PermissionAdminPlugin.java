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

package org.osgi.impl.service.policy.permadmin;

import java.util.Date;
import java.util.HashMap;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugIn;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * implements the ./OSGi/Policies/Java/Bundle subtree.
 * 
 * @version $Revision$
 */
public class PermissionAdminPlugin implements DmtDataPlugIn {
	private final PermissionAdmin	permissionAdmin;
	public static final String dataRootURI = "./OSGi/Policies/Java/Bundle";

	/* internal state variables, that will be dumped back into the permission admin
	 * at commit
	 */
	private PermissionInfo[]	defaultPermissions;
	private String[]	locations;
	private HashMap	permissions; // location -> PermissionInfo[]

	/**
	 * reverse hash lookup table, for efficiency
	 */
	private HashMap reverseHash = new HashMap();
	
	/**
	 * create a new PermissionAdmin plugin for the DMT admin. It is the responsibility
	 * of the caller to register in the service registry.
	 * @param permissionAdmin
	 */
	public PermissionAdminPlugin(PermissionAdmin permissionAdmin) {
		this.permissionAdmin = permissionAdmin;
	}
	
	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		String[] path = getPath(subtreeUri); // this is also a check if it starts with the right subpath
		loadFromPermissionAdmin();
	}

	/**
	 * loads all settings from the permission admin
	 */
	private void loadFromPermissionAdmin() {
		locations = permissionAdmin.getLocations();
		if (locations==null) locations=new String[0];
		permissions = new HashMap();
		for(int i=0;i<locations.length;i++) {
			permissions.put(locations[i],permissionAdmin.getPermissions(locations[i]));
		}
		defaultPermissions = permissionAdmin.getDefaultPermissions();
	}

	/**
	 * @param nodeUri
	 * @param generic
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtDataPlugIn#getMetaNode(java.lang.String, org.osgi.service.dmt.DmtMetaNode)
	 */
	public DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @return
	 * @see org.osgi.service.dmt.DmtDataPlugIn#supportsAtomic()
	 */
	public boolean supportsAtomic() {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#rollback()
	 */
	public void rollback() throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param title
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#setNodeTitle(java.lang.String, java.lang.String)
	 */
	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param data
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#setNodeValue(java.lang.String, org.osgi.service.dmt.DmtData)
	 */
	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param type
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#setNodeType(java.lang.String, java.lang.String)
	 */
	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#deleteNode(java.lang.String)
	 */
	public void deleteNode(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String)
	 */
	public void createInteriorNode(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param type
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String, java.lang.String)
	 */
	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param value
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String, org.osgi.service.dmt.DmtData)
	 */
	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param newNodeUri
	 * @param recursive
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#clone(java.lang.String, java.lang.String, boolean)
	 */
	public void clone(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param newName
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#renameNode(java.lang.String, java.lang.String)
	 */
	public void renameNode(String nodeUri, String newName) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#close()
	 */
	public void close() throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	public boolean isNodeUri(String nodeUri) {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeValue(java.lang.String)
	 */
	public DmtData getNodeValue(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTitle(java.lang.String)
	 */
	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeType(java.lang.String)
	 */
	public String getNodeType(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeVersion(java.lang.String)
	 */
	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTimestamp(java.lang.String)
	 */
	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeSize(java.lang.String)
	 */
	public int getNodeSize(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getChildNodeNames(java.lang.String)
	 */
	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}


	/**
	 * return the path elements, from our base
	 * @param nodeUri
	 * @return
	 */
	private String[] getPath(String nodeUri) {
		if (!nodeUri.startsWith(dataRootURI)) 
			throw new IllegalStateException("Dmt should not give me URIs that are not mine");
		return Splitter.split(nodeUri.substring(dataRootURI.length()),'/',0);
	}
}

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
package org.osgi.impl.service.policy;

import java.util.*;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.dmt.*;
import org.osgi.service.permissionadmin.PermissionAdmin;


/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public final class JavaPolicyPlugin implements DmtDataPlugIn {

	private static final String BUNDLEDEFAULT_COLON = "BundleDefault:";
	private static final String BUNDLEDEFAULT = "BundleDefault";
	private static final int SHA1_HASH_HEXA_LENGTH = 40;
	private PermissionAdmin permissionAdmin;
	private BundleDefaultNode bundeDefaultNode;
	private ConditionalPermissionAdmin conditionalPermissionAdmin;

	final static TypeMetaNode typeMetaNode = new TypeMetaNode();
	
	// this contains the reverse map of sha-1 hashes of Strings
	// the hashes are calculated as: String -> UTF-8 -> sha-1 -> lowercase hexadecimal string rep
	private HashMap reverseHash = new HashMap();
	{
		reverseHash.put("64c9258c1c188441f6d468fa798ec585284f544e",BUNDLEDEFAULT_COLON);
	}
	
	private static final String javaPolicyBaseURI = "./OSGi/Policies/Java";

	/**
	 * create a policy plugin. Doesn't register anything anywhere
	 *
	 */
	public JavaPolicyPlugin(PermissionAdmin permissionAdmin,ConditionalPermissionAdmin conditionalPermissionAdmin) {
		this.permissionAdmin = permissionAdmin;
		this.conditionalPermissionAdmin = conditionalPermissionAdmin;
		this.bundeDefaultNode = new BundleDefaultNode(permissionAdmin);
	}

	public void open(int lockMode, DmtSession session) throws DmtException {
		// DmtAdmin takes care of everything
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		// DmtAdmin takes care of everything
	}

	public DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic)
			throws DmtException {
		String[] pathname = Splitter.split(nodeUri,'/',0);
		if (pathname.length==4) return null; // TODO
		HashNode hashNode = getFromHash(pathname[4]);
		if (hashNode==null) return null; // TODO
		String[] subNodeUri = new String[pathname.length-5];
		System.arraycopy(pathname,5,subNodeUri,0,pathname.length-5);
		return hashNode.getMetaNode(subNodeUri);
	}

	public boolean supportsAtomic() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#rollback()
	 */
	public void rollback() throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#setNodeTitle(java.lang.String, java.lang.String)
	 */
	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#setNodeValue(java.lang.String, org.osgi.service.dmt.DmtData)
	 */
	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#setNodeType(java.lang.String, java.lang.String)
	 */
	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#deleteNode(java.lang.String)
	 */
	public void deleteNode(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String)
	 */
	public void createInteriorNode(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String, java.lang.String)
	 */
	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String, org.osgi.service.dmt.DmtData)
	 */
	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#clone(java.lang.String, java.lang.String, boolean)
	 */
	public void clone(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.Dmt#renameNode(java.lang.String, java.lang.String)
	 */
	public void renameNode(String nodeUri, String newName) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#close()
	 */
	public void close() throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#isNodeUri(java.lang.String)
	 */
	public boolean isNodeUri(String nodeUri) {
		if (!nodeUri.startsWith(javaPolicyBaseURI)) return false;
		String[] pathname = Splitter.split(nodeUri,'/',0);
		if (pathname.length==4) return true;
		HashNode hashNode = getFromHash(pathname[4]);
		if (hashNode==null) return false;
		String[] subNodeUri = new String[pathname.length-5];
		System.arraycopy(pathname,5,subNodeUri,0,pathname.length-5);
		return hashNode.isNodeUri(subNodeUri);
	}

	/**
	 * @param string
	 * @return
	 */
	private HashNode getFromHash(String hash) {
		if (hash.length()!=SHA1_HASH_HEXA_LENGTH) { return null; }
		String val = (String) reverseHash.get(hash);
		// TODO Auto-generated method stub
		if (val.equals(BUNDLEDEFAULT_COLON)) {
			return this.bundeDefaultNode;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeValue(java.lang.String)
	 */
	public DmtData getNodeValue(String nodeUri) throws DmtException {
		if (!nodeUri.startsWith(javaPolicyBaseURI)) throw new DmtException(nodeUri,0,"");
		String[] pathname = Splitter.split(nodeUri,'/',0);
		if (pathname.length==4) throw new DmtException(nodeUri,0,"");
		HashNode hashNode = getFromHash(pathname[4]);
		if (hashNode==null) throw new DmtException(nodeUri,0,"");
		String[] subNodeUri = new String[pathname.length-5];
		System.arraycopy(pathname,5,subNodeUri,0,pathname.length-5);
		return hashNode.getNodeValue(subNodeUri);
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTitle(java.lang.String)
	 */
	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeType(java.lang.String)
	 */
	public String getNodeType(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeVersion(java.lang.String)
	 */
	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTimestamp(java.lang.String)
	 */
	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeSize(java.lang.String)
	 */
	public int getNodeSize(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.dmt.DmtReadOnly#getChildNodeNames(java.lang.String)
	 */
	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		throw new IllegalStateException();
	}

}

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

import java.util.Date;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;

/**
 *
 * dmt plugin for exposing Conditional Permission Admin functionality
 * 
 * @version $Revision$
 */
public class ConditionalPermissionAdminPlugin implements DmtDataPlugin {
	
	private final ConditionalPermissionAdmin	condPermAdmin;

	public static final String dataRootURI = "./OSGi/Policies/Java/ConditionalPermission";

	public ConditionalPermissionAdminPlugin(ConditionalPermissionAdmin condPermAdmin) {
		this.condPermAdmin = condPermAdmin;
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		throw new DmtException(subtreeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
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
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void deleteNode(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
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
		throw new DmtException("",DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public boolean isNodeUri(String nodeUri) {
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
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}
}

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

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.osgi.impl.service.policy.util.HashCalculator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;

/**
 *
 * dmt plugin base class for policy
 * 
 * @version $Revision$
 */
public abstract class AbstractPolicyPlugin implements DmtDataPlugin {
	/**
	 * the official root position in the management tree
	 */
	protected String ROOT;

	/**
	 * true, if something is changed, and needs to be written back to the system
	 */
	private boolean dirty;
	
	/**
	 * true, if session was opened in atomic mode
	 */
	boolean atomic;
	
	/**
	 * utility for calculating hashes
	 */
	protected final HashCalculator hashCalculator;
	
	public AbstractPolicyPlugin() throws NoSuchAlgorithmException {
		hashCalculator = new HashCalculator();
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		dirty = false;
		atomic = (lockMode == DmtSession.LOCK_TYPE_ATOMIC);
	}

	public final boolean supportsAtomic() {
		return true;
	}

	/**
	 * checks if we are allowed to write (eg. in atomic mode), and flips the dirty bit
	 * @param nodeUri
	 * @throws DmtException
	 */
	protected final void switchToWriteMode(String nodeUri) throws DmtException {
		if (!atomic) throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,
				"modifying tree is only allowed in atomic sessions");
		dirty=true;
	}
	
	/**
	 * return the path elements, from our base
	 * @param nodeUri
	 * @return an array of nodenames
	 */
	protected final  String[] getPath(String nodeUri) {
		if (!nodeUri.startsWith(ROOT)) 
			throw new IllegalStateException("Dmt should not give me URIs that are not mine");
		if (nodeUri.length()==ROOT.length()) return new String[] {};
		return Splitter.split(nodeUri.substring(ROOT.length()+1),'/',0);
	}
	
	protected final boolean isDirty() {
		return dirty;
	}
	
	/*
	 * methods that are not needed anywhere
	 */

	public final void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void setNodeType(String nodeUri, String type) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final void renameNode(String nodeUri, String newName) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final String getNodeTitle(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final String getNodeType(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final int getNodeVersion(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public final int getNodeSize(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createLeafNode(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}

	public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
		throw new DmtException(nodeUri,DmtException.FEATURE_NOT_SUPPORTED,"");
	}


}

/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Plugins;

import java.util.Date;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * A deafult TransactionalDataSession to be used in overlappings plugins (or plugins to be overlapped), 
 * so theses methods should never be called.
 *  
 */
public class DefaultOverlappingPluginTransactionalDataSession implements TransactionalDataSession {
	
	private String MESSAGE;
	
	public DefaultOverlappingPluginTransactionalDataSession(String className) {
		MESSAGE = className;
	}

	public void rollback() throws DmtException {

	}

	public void commit() throws DmtException {

    }

	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}
	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	public void setNodeType(String[] nodeUri, String type) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	public void deleteNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	public void createInteriorNode(String[] nodeUri, String type) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}


	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
		DmtConstants.PARAMETER_3 = MESSAGE;
	}

	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
		DmtConstants.PARAMETER_3 = MESSAGE;
	}

	public void renameNode(String[] nodeUri, String newName) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}
	public void close() throws DmtException {
		
	}

	public boolean isNodeUri(String[] nodeUri) {
		DmtConstants.TEMPORARY = MESSAGE;
		return true;
	}

	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return false;
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return MESSAGE;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return 0;
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return 0;
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return new String[] {};
	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	public void nodeChanged(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

}

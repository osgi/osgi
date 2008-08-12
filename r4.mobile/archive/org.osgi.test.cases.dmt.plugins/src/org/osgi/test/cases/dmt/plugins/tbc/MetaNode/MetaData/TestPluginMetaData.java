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
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * A test implementation of TestPluginMetaData. This implementation validates the
 * DmtSession calls to a subtree handled by a TestPluginMetaData.
 * 
 */
public class TestPluginMetaData implements DataPlugin, ExecPlugin, ReadWriteDataSession {

	public static final String CLOSE = "TestPluginMetaData.close";
	public static final String GETCHILDNODENAMES = "TestPluginMetaData.getChildNodeNames";
	public static final String GETMETANODE = "TestPluginMetaData.getMetaNode";
	public static final String GETNODESIZE = "TestPluginMetaData.getNodeSize";
	public static final String GETNODETIMESTAMP = "TestPluginMetaData.getNodeTimeStamp";
	public static final String GETNODETITLE = "TestPluginMetaData.getNodeTitle";
	public static final String GETNODETYPE = "TestPluginMetaData.getNodeType";
	public static final String GETNODEVALUE = "TestPluginMetaData.getNodeValue";
	public static final String GETNODEVERSION = "TestPluginMetaData.getNodeVersion";
	public static final String ISLEAFNODE = "TestPluginMetaData.isLeafNode";
	public static final String ISNODEURI = "TestPluginMetaData.isNodeUri";

	//Values (when a string is expected, the return is the same as the gets above)
	public static final int GETNODESIZE_VALUE = 1001;
	public static final int GETNODEVERSION_VALUE = 199;
	public static final DmtData GETNODEVALUE_VALUE = new DmtData(9);
	public static final Date GETNODETIMESTAMP_VALUE = new Date(System.currentTimeMillis());
	

	public static final String COMMIT = "TestPluginMetaData.commit";
	public static final String COPY = "TestPluginMetaData.copy";
	public static final String CREATEINTERIORNODE_1 = "TestPluginMetaData.createInteriorNode";
	public static final String CREATEINTERIORNODE_2 = "TestPluginMetaData.createInteriorNode(,)";
	public static final String CREATELEAFNODE_1 = "TestPluginMetaData.createLeafNode";
	public static final String CREATELEAFNODE_2 = "TestPluginMetaData.createLeafNode(,)";
	public static final String CREATELEAFNODE_3 = "TestPluginMetaData.createLeafNode(,,)";
	public static final String DELETENODE = "TestPluginMetaData.deleteNode";
	public static final String RENAMENODE = "TestPluginMetaData.renameNode";
	public static final String ROLLBACK = "TestPluginMetaData.rollback";
	public static final String SETDEFAULTNODEVALUE = "TestPluginMetaData.setDefaultNodeValue";
	public static final String SETNODETITLE = "TestPluginMetaData.setNodeTitle";
	public static final String SETNODETYPE = "TestPluginMetaData.setNodeType";
	public static final String SETNODEVALUE = "TestPluginMetaData.setNodeValue";

	private DmtTestControl tbc;

	public TestPluginMetaData(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

    public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {

    }

	


	
	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
			DmtConstants.TEMPORARY = SETNODETITLE; 
	}

	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		DmtConstants.TEMPORARY = SETNODEVALUE; 
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = SETDEFAULTNODEVALUE; 
	}

	public void setNodeType(String[] nodeUri, String type) throws DmtException {
			DmtConstants.TEMPORARY = SETNODETYPE; 
	}

	public void deleteNode(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = DELETENODE; 
	}

	public void createInteriorNode(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = CREATEINTERIORNODE_1; 
	}

	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {
			DmtConstants.TEMPORARY = CREATEINTERIORNODE_2; 
	}

	public void createLeafNode(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = CREATELEAFNODE_1; 
	}

	public void createLeafNode(String[] nodeUri, DmtData value) throws DmtException {
			DmtConstants.TEMPORARY = CREATELEAFNODE_2;
	}

	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
			DmtConstants.TEMPORARY = CREATELEAFNODE_3; 
	}

	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {
			DmtConstants.TEMPORARY = COPY; 
	}

	public void renameNode(String[] nodeUri, String newName) throws DmtException {
			DmtConstants.TEMPORARY = RENAMENODE;
	}

	public void close() throws DmtException {
	}

	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestPluginMetaDataActivator.INEXISTENT_NODE) 
				|| nodeName.equals(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE) 
				|| nodeName.equals(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME)
				|| nodeName.equals(TestPluginMetaDataActivator.INEXISTENT_NODE_INVALID_NAME)) { 
			return false;
		} else {
			return true;
		}
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
			return null;
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
			return GETNODETITLE;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
			return GETNODETYPE;
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
			return GETNODEVERSION_VALUE;
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
			return GETNODETIMESTAMP_VALUE;
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
			return GETNODESIZE_VALUE;
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
			return new String[] { GETCHILDNODENAMES, "newNode" };
	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
        return TestPluginMetaDataActivator.metaNodeDefault;
	}
	
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestPluginMetaDataActivator.LEAF_NODE) || 
				nodeName.equals(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME)) {
			return true; 
		}
		else {
			return false;
		}
	}


	public void nodeChanged(String[] nodeUri) throws DmtException {
		
	}


}

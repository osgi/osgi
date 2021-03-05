/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData;

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
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

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

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

    @Override
	public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {

    }

	


	
	@Override
	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
			DmtConstants.TEMPORARY = SETNODETITLE; 
	}

	@Override
	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		DmtConstants.TEMPORARY = SETNODEVALUE; 
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = SETDEFAULTNODEVALUE; 
	}

	@Override
	public void setNodeType(String[] nodeUri, String type) throws DmtException {
			DmtConstants.TEMPORARY = SETNODETYPE; 
	}

	@Override
	public void deleteNode(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = DELETENODE; 
	}

	public void createInteriorNode(String[] nodeUri) throws DmtException {
			DmtConstants.TEMPORARY = CREATEINTERIORNODE_1; 
	}

	@Override
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

	@Override
	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
			DmtConstants.TEMPORARY = CREATELEAFNODE_3; 
	}

	@Override
	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {
			DmtConstants.TEMPORARY = COPY; 
	}

	@Override
	public void renameNode(String[] nodeUri, String newName) throws DmtException {
			DmtConstants.TEMPORARY = RENAMENODE;
	}

	@Override
	public void close() throws DmtException {
	}

	@Override
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

	@Override
	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
			return null;
	}

	@Override
	public String getNodeTitle(String[] nodeUri) throws DmtException {
			return GETNODETITLE;
	}

	@Override
	public String getNodeType(String[] nodeUri) throws DmtException {
			return GETNODETYPE;
	}

	@Override
	public int getNodeVersion(String[] nodeUri) throws DmtException {
			return GETNODEVERSION_VALUE;
	}

	@Override
	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
			return GETNODETIMESTAMP_VALUE;
	}

	@Override
	public int getNodeSize(String[] nodeUri) throws DmtException {
			return GETNODESIZE_VALUE;
	}

	@Override
	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
			return new String[] { GETCHILDNODENAMES, "newNode" };
	}

	@Override
	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
        return TestPluginMetaDataActivator.metaNodeDefault;
	}
	
	@Override
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


	@Override
	public void nodeChanged(String[] nodeUri) throws DmtException {
		
	}


}

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

package org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin;

import java.util.Date;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.TestPluginMetaNode;

/**
 * @author Andre Assad
 * 
 * A test implementation of DmtDataPlugin. This implementation validates the
 * DmtSession calls to a subtree handled by a DmtDataPlugin.
 * 
 */
public class TestDataPlugin implements DmtDataPlugin {
	//DmtReadOnly
	public static final String CLOSE = "DmtDataPlugin.close";
	public static final String GETCHILDNODENAMES = "DmtDataPlugin.getChildNodeNames";
	public static final String GETMETANODE = "DmtDataPlugin.getMetaNode";
	public static final String GETNODESIZE = "DmtDataPlugin.getNodeSize";
	public static final String GETNODETIMESTAMP = "DmtDataPlugin.getNodeTimeStamp";
	public static final String GETNODETITLE = "DmtDataPlugin.getNodeTitle";
	public static final String GETNODETYPE = "DmtDataPlugin.getNodeType";
	public static final String GETNODEVALUE = "DmtDataPlugin.getNodeValue";
	public static final String GETNODEVERSION = "DmtDataPlugin.getNodeVersion";
	public static final String ISLEAFNODE = "DmtDataPlugin.isLeafNode";
	public static final String ISNODEURI = "DmtDataPlugin.isNodeUri";

	//Values (when a string is expected, the return is the same as the gets above)
	public static final int GETNODESIZE_VALUE = 1001;
	public static final int GETNODEVERSION_VALUE = 199;
	public static final DmtData GETNODEVALUE_VALUE = new DmtData(9);
	public static final Date GETNODETIMESTAMP_VALUE = new Date(System.currentTimeMillis());
	
	//Dmt
	public static final String COMMIT = "DmtDataPlugin.commit";
	public static final String COPY = "DmtDataPlugin.copy";
	public static final String CREATEINTERIORNODE_1 = "DmtDataPlugin.createInteriorNode";
	public static final String CREATEINTERIORNODE_2 = "DmtDataPlugin.createInteriorNode(,)";
	public static final String CREATELEAFNODE_1 = "DmtDataPlugin.createLeafNode";
	public static final String CREATELEAFNODE_2 = "DmtDataPlugin.createLeafNode(,)";
	public static final String CREATELEAFNODE_3 = "DmtDataPlugin.createLeafNode(,,)";
	public static final String DELETENODE = "DmtDataPlugin.deleteNode";
	public static final String RENAMENODE = "DmtDataPlugin.renameNode";
	public static final String ROLLBACK = "DmtDataPlugin.rollback";
	public static final String SETDEFAULTNODEVALUE = "DmtDataPlugin.setDefaultNodeValue";
	public static final String SETNODETITLE = "DmtDataPlugin.setNodeTitle";
	public static final String SETNODETYPE = "DmtDataPlugin.setNodeType";
	public static final String SETNODEVALUE = "DmtDataPlugin.setNodeValue";
	


	
	private DmtTestControl tbc;

	public TestDataPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	
	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
	}

	public boolean supportsAtomic() {
		return true;
	}

	public void rollback() throws DmtException {
		throw new DmtException(null,DmtException.ALERT_NOT_ROUTED,ROLLBACK);
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					SETNODETITLE);
		}
		else {
			DmtTestControl.TEMPORARY = SETNODETITLE; 
			DmtTestControl.PARAMETER_2 = title; 
		}
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.FORMAT_NOT_SUPPORTED,
					SETNODEVALUE);
		}
		else {
			DmtTestControl.TEMPORARY = SETNODEVALUE; 
			DmtTestControl.PARAMETER_2 = data.toString(); 
		}
	}

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.ALERT_NOT_ROUTED,
					SETDEFAULTNODEVALUE);
		}
		else {
			DmtTestControl.TEMPORARY = SETDEFAULTNODEVALUE; 
		}
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.DATA_STORE_FAILURE,
					SETNODETYPE);
		}
		else {
			DmtTestControl.TEMPORARY = SETNODETYPE; 
			DmtTestControl.PARAMETER_2 = type; 
		}

	}

	public void deleteNode(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.REMOTE_ERROR,
					DELETENODE);
		}
		else {
			DmtTestControl.TEMPORARY = DELETENODE; 
		}
		
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.OTHER_ERROR,
					CREATEINTERIORNODE_1);
		}
		else {
			DmtTestControl.TEMPORARY = CREATEINTERIORNODE_1; 
		}
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					CREATEINTERIORNODE_2);
		}
		else {
			DmtTestControl.TEMPORARY = CREATEINTERIORNODE_2; 
		}
	}

	public void createLeafNode(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					CREATELEAFNODE_1);
		}
		else {
			DmtTestControl.TEMPORARY = CREATELEAFNODE_1; 
		}
	}

	public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.FORMAT_NOT_SUPPORTED,
					CREATELEAFNODE_2);
		}
		else {
			DmtTestControl.TEMPORARY = CREATELEAFNODE_2;
			DmtTestControl.PARAMETER_2 = value.toString(); 
		}
	}

	public void createLeafNode(String nodeUri, DmtData value, String mimeType)
			throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.INVALID_URI,
					CREATELEAFNODE_3);
		}
		else {
			DmtTestControl.TEMPORARY = CREATELEAFNODE_3; 
			DmtTestControl.PARAMETER_2 = value.toString(); 
			DmtTestControl.PARAMETER_3 = mimeType; 
		}

	}

	public void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION3)) {
			throw new DmtException(nodeUri,
					DmtException.URI_TOO_LONG,
					COPY);
		}
		else {
			DmtTestControl.TEMPORARY = COPY; 
			DmtTestControl.PARAMETER_2 = newNodeUri; 
			DmtTestControl.PARAMETER_3 = String.valueOf(recursive);
		}
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.NODE_ALREADY_EXISTS,
					RENAMENODE);
		}
		else {
			DmtTestControl.TEMPORARY = RENAMENODE; 
			DmtTestControl.PARAMETER_2 = newName; 
		}
	}

	public void close() throws DmtException {
		throw new DmtException(null,DmtException.URI_TOO_LONG,CLOSE);
	}

	public boolean isNodeUri(String nodeUri) {
		if (nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE) 
				|| nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION)
				|| nodeUri.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE)
				|| nodeUri.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) { 

			return false;
		} else {
			return true;
		}
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.FORMAT_NOT_SUPPORTED,GETNODEVALUE);
		}else {
			return GETNODEVALUE_VALUE;
		}
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETITLE);
		}else {
			return GETNODETITLE;
		}
	}

	public String getNodeType(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETYPE);
		}else {
			return GETNODETYPE;
		}
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODEVERSION);
		}else {
			return GETNODEVERSION_VALUE;
		}
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.URI_TOO_LONG,GETNODETIMESTAMP);
		}else {
			return GETNODETIMESTAMP_VALUE;
		}
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.ALERT_NOT_ROUTED,GETNODESIZE);
		}else {
			return GETNODESIZE_VALUE;
		}
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.OTHER_ERROR,GETCHILDNODENAMES);
		}else if (nodeUri.equals(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT)){
			return new String[] { GETCHILDNODENAMES };
		} else {
			return new String[] {  };
		}
	}

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION2)) {
			throw new DmtException(nodeUri,DmtException.DATA_STORE_FAILURE,GETMETANODE);
		} else if(nodeUri.equals(TestDataPluginActivator.INTERIOR_NODE) || 
				nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE) || 
				nodeUri.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION) ) {
			return new TestPluginMetaNode();
			
		} else {
			return new TestPluginMetaNode(DmtData.FORMAT_INTEGER | DmtData.FORMAT_STRING);
		}		


	}
	
	public void commit() throws DmtException {
		throw new DmtException(null,DmtException.COMMAND_FAILED,COMMIT);
	}

	public boolean isLeafNode(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestDataPluginActivator.LEAF_NODE) || nodeUri.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			return true; 
		}
		else {
			return false;
		}
	}

}

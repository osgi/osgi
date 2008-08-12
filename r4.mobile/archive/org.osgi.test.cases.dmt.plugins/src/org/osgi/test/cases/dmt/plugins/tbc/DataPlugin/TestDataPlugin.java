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

package org.osgi.test.cases.dmt.plugins.tbc.DataPlugin;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.TestPluginMetaNode;

/**
 * @author Andre Assad
 * 
 * A test implementation of DataPluginFactory. This implementation validates the
 * DmtSession calls to a subtree handled by a DataPluginFactory.
 * 
 */
public class TestDataPlugin implements DataPlugin, TransactionalDataSession {
	
    public static final String CLASS = "TestDataPlugin.";
	public static final String CLOSE = CLASS + "close";
	public static final String GETCHILDNODENAMES = CLASS + "getChildNodeNames";
	public static final String GETMETANODE = CLASS + "getMetaNode";
	public static final String GETNODESIZE = CLASS + "getNodeSize";
	public static final String GETNODETIMESTAMP = CLASS + "getNodeTimeStamp";
	public static final String GETNODETITLE = CLASS + "getNodeTitle";
	public static final String GETNODETYPE = CLASS + "getNodeType";
	public static final String GETNODEVALUE = CLASS + "getNodeValue";
	public static final String GETNODEVERSION = CLASS + "getNodeVersion";
	public static final String ISLEAFNODE = CLASS + "isLeafNode";
	public static final String ISNODEURI = CLASS + "isNodeUri";

	//Values (when a string is expected, the return is the same as the gets above)
	public static final int GETNODESIZE_VALUE = 1001;
	public static final int GETNODEVERSION_VALUE = 199;
	public static final DmtData GETNODEVALUE_VALUE = new DmtData(9);
	public static final Date GETNODETIMESTAMP_VALUE = new Date(System.currentTimeMillis());
	
	public static final String COMMIT = CLASS + "commit";
	public static final String COPY = CLASS + "copy";
	public static final String CREATEINTERIORNODE = CLASS + "createInteriorNode";
	public static final String CREATELEAFNODE = CLASS + "createLeafNode";
	public static final String DELETENODE = CLASS + "deleteNode";
	public static final String RENAMENODE = CLASS + "renameNode";
	public static final String ROLLBACK = CLASS + "rollback";
	public static final String SETDEFAULTNODEVALUE = CLASS + "setDefaultNodeValue";
	public static final String SETNODETITLE = CLASS + "setNodeTitle";
	public static final String SETNODETYPE = CLASS + "setNodeType";
	public static final String SETNODEVALUE = CLASS + "setNodeValue";
	public static final String NODECHANGED = CLASS + "nodeChanged";
	

	private static boolean commitThrowsException = false;
	
	private static boolean rollbackThrowsException = false;
	
	private static boolean closeThrowsException = false;
	
	private static boolean nodeChangedThrowsException = false;
	
	private DmtTestControl tbc;

    public static String SESSION_OPENED="";
    
	public TestDataPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		SESSION_OPENED="openReadOnlySession";
        return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        SESSION_OPENED="openReadWriteSession";
        return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        SESSION_OPENED="openAtomicSession";
        return this;
	}
	public void rollback() throws DmtException {
	    if (rollbackThrowsException)
	        throw new DmtException((String)null,DmtException.ALERT_NOT_ROUTED,ROLLBACK);
	}

	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					SETNODETITLE);
		}
		else {
			DmtConstants.TEMPORARY = SETNODETITLE; 
			DmtConstants.PARAMETER_2 = title; 
		}
	}

	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					SETNODEVALUE);
		}
		else {
			DmtConstants.TEMPORARY = SETNODEVALUE; 
			DmtConstants.PARAMETER_2 = data.toString(); 
		}
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.ALERT_NOT_ROUTED,
					SETDEFAULTNODEVALUE);
		}
		else {
			DmtConstants.TEMPORARY = SETDEFAULTNODEVALUE; 
		}
	}

	public void setNodeType(String[] nodeUri, String type) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.DATA_STORE_FAILURE,
					SETNODETYPE);
		}
		else {
			DmtConstants.TEMPORARY = SETNODETYPE; 
			DmtConstants.PARAMETER_2 = type; 
		}

	}

	public void deleteNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.REMOTE_ERROR,
					DELETENODE);
		}
		else {
			DmtConstants.TEMPORARY = DELETENODE; 
		}
		
	}


	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.CONCURRENT_ACCESS,
					CREATEINTERIORNODE);
		}
		else {
			DmtConstants.TEMPORARY = CREATEINTERIORNODE; 
		}
	}


	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.INVALID_URI,
					CREATELEAFNODE);
		}
		else {
			DmtConstants.TEMPORARY = CREATELEAFNODE; 
			DmtConstants.PARAMETER_2 = (null==value)?null:value.toString(); 
			DmtConstants.PARAMETER_3 = mimeType; 
		}

	}

	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION3)) {
			throw new DmtException(nodeUri,
					DmtException.URI_TOO_LONG,
					COPY);
		}
		else {
			DmtConstants.TEMPORARY = COPY; 
			DmtConstants.PARAMETER_2 = tbc.mangleUri(newNodeUri); 
			DmtConstants.PARAMETER_3 = String.valueOf(recursive);
		}
	}

	public void renameNode(String[] nodeUri, String newName) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,
					DmtException.NODE_ALREADY_EXISTS,
					RENAMENODE);
		}
		else {
			DmtConstants.TEMPORARY = RENAMENODE; 
			DmtConstants.PARAMETER_2 = newName;
		}
	}

	public void close() throws DmtException {
		if (closeThrowsException)
		    throw new DmtException((String)null, DmtException.CONCURRENT_ACCESS, CLOSE);
	}

	public boolean isNodeUri(String[] nodeUri) {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.INEXISTENT_NODE) 
				|| nodeName.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION)
				|| nodeName.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE)
				|| nodeName.equals(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION)) { 

			return false;
		} else {
			return true;
		}
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,GETNODEVALUE);
		}else {
			return GETNODEVALUE_VALUE;
		}
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETITLE);
		}else {
			return GETNODETITLE;
		}
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETYPE);
		}else {
			return GETNODETYPE;
		}
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODEVERSION);
		}else {
			return GETNODEVERSION_VALUE;
		}
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.URI_TOO_LONG,GETNODETIMESTAMP);
		}else {
			return GETNODETIMESTAMP_VALUE;
		}
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.ALERT_NOT_ROUTED,GETNODESIZE);
		}else {
			return GETNODESIZE_VALUE;
		}
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.DATA_STORE_FAILURE,GETCHILDNODENAMES);
		}else if (nodeName.equals(TestDataPluginActivator.ROOT)){
			return new String[] { GETCHILDNODENAMES };
		} else {
			return new String[] {  };
		}
	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION2)) {
			throw new DmtException(nodeUri,DmtException.DATA_STORE_FAILURE,GETMETANODE);
		} else if(nodeName.equals(TestDataPluginActivator.INTERIOR_NODE) || 
				nodeName.equals(TestDataPluginActivator.INEXISTENT_NODE) || 
				nodeName.equals(TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION) ) {
			return new TestPluginMetaNode();
			
		} else {
			return new TestPluginMetaNode(DmtData.FORMAT_INTEGER | DmtData.FORMAT_STRING);
		}		


	}
	
	public void commit() throws DmtException {
		if (commitThrowsException)
		    throw new DmtException((String)null,DmtException.COMMAND_FAILED,COMMIT);
	}

	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestDataPluginActivator.LEAF_NODE) || nodeName.equals(TestDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			return true; 
		}
		else {
			return false;
		}
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
	    if (nodeChangedThrowsException)
	        throw new DmtException(nodePath, DmtException.DATA_STORE_FAILURE, NODECHANGED);
	}

	
    public static void setCommitThrowsException(boolean commitThrowsException) {
        TestDataPlugin.commitThrowsException = commitThrowsException;
    }
    public static void setRollbackThrowsException(
            boolean rollbackThrowsException) {
        TestDataPlugin.rollbackThrowsException = rollbackThrowsException;
    }

    public static void setCloseThrowsException(boolean closeThrowsException) {
        TestDataPlugin.closeThrowsException = closeThrowsException;
    }

    public static void setNodeChangedThrowsException(
            boolean nodeChangedThrowsException) {
        TestDataPlugin.nodeChangedThrowsException = nodeChangedThrowsException;
    }
}

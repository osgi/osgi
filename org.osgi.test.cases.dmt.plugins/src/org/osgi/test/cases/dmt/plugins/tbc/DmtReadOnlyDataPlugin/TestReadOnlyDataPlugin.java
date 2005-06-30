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
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtReadOnlyDataPlugin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.TestPluginMetaNode;
import org.osgi.test.cases.dmt.plugins.tbc.DmtReadOnlyDataPlugin.TestReadOnlyDataPluginActivator;

/**
 * @author Andre Assad
 *
 * A test implementation of TestDmtReadOnlyDataPlugin. This implementation validates the
 * DmtSession calls to a subtree handled by a TestDmtReadOnlyDataPlugin.
 */
public class TestReadOnlyDataPlugin implements DmtReadOnlyDataPlugin {
	//DmtReadOnly
	public static final String CLOSE = "DmtReadOnlyDataPlugin.close";
	public static final String GETCHILDNODENAMES = "DmtReadOnlyDataPlugin.getChildNodeNames";
	public static final String GETMETANODE = "DmtReadOnlyDataPlugin.getMetaNode";
	public static final String GETNODESIZE = "DmtReadOnlyDataPlugin.getNodeSize";
	public static final String GETNODETIMESTAMP = "DmtReadOnlyDataPlugin.getNodeTimeStamp";
	public static final String GETNODETITLE = "DmtReadOnlyDataPlugin.getNodeTitle";
	public static final String GETNODETYPE = "DmtReadOnlyDataPlugin.getNodeType";
	public static final String GETNODEVALUE = "DmtReadOnlyDataPlugin.getNodeValue";
	public static final String GETNODEVERSION = "DmtReadOnlyDataPlugin.getNodeVersion";
	public static final String ISLEAFNODE = "DmtReadOnlyDataPlugin.isLeafNode";
	public static final String ISNODEURI = "DmtReadOnlyDataPlugin.isNodeUri";
	
	//Values (when a string is expected, the return is the same as the gets above)
	public static final int GETNODESIZE_VALUE = 231;
	public static final int GETNODEVERSION_VALUE = 12;
	public static final DmtData GETNODEVALUE_VALUE = new DmtData(5);
	public static final Date GETNODETIMESTAMP_VALUE = new Date(System.currentTimeMillis());
	
	private DmtTestControl tbc;


	public TestReadOnlyDataPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}
	
	public boolean isNodeUri(String nodeUri) {
		if (nodeUri.equals(TestReadOnlyDataPluginActivator.INEXISTENT_NODE)) { 
			return false;
		} else {
			return true;
		}
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.FORMAT_NOT_SUPPORTED,GETNODEVALUE);
		}else {
			return GETNODEVALUE_VALUE;
		}
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETITLE);
		}else {
			return GETNODETITLE;
		}
	}

	public String getNodeType(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODETYPE);
		}else {
			return GETNODETYPE;
		}
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.COMMAND_FAILED,GETNODEVERSION);
		}else {
			return GETNODEVERSION_VALUE;
		}
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.URI_TOO_LONG,GETNODETIMESTAMP);
		}else {
			return GETNODETIMESTAMP_VALUE;
		}
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.ALERT_NOT_ROUTED,GETNODESIZE);
		}else {
			return GETNODESIZE_VALUE;
		}
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri,DmtException.OTHER_ERROR,GETCHILDNODENAMES);
		}else if (nodeUri.equals(TestReadOnlyDataPluginActivator.TEST_READ_ONLY_PLUGIN_ROOT)){
			return new String[] { GETCHILDNODENAMES };
		} else {
			return new String[] {  };
		}
	}

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
		if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE_EXCEPTION2)) {
			throw new DmtException(nodeUri,DmtException.DATA_STORE_FAILURE,GETMETANODE);
		} else if(nodeUri.equals(TestReadOnlyDataPluginActivator.INTERIOR_NODE) || 
				nodeUri.equals(TestReadOnlyDataPluginActivator.INEXISTENT_NODE)) {
			return new TestPluginMetaNode();
			
		} else {
			return new TestPluginMetaNode(DmtData.FORMAT_INTEGER | DmtData.FORMAT_STRING);
		}		


	}
	

	public boolean isLeafNode(String nodeUri) throws DmtException {
		if (nodeUri.equals(TestReadOnlyDataPluginActivator.LEAF_NODE) || nodeUri.equals(TestReadOnlyDataPluginActivator.LEAF_NODE_EXCEPTION)) {
			return true; 
		}
		else {
			return false;
		}
	}


	
	public void close() throws DmtException {
		throw new DmtException(null,DmtException.TRANSACTION_ERROR,CLOSE);
	}

	public void open(String subtreeUri, DmtSession session) throws DmtException {
		
	}

}

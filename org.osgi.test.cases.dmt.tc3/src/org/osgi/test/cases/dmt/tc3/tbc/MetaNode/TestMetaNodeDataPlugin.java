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

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import java.util.Date;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

public class TestMetaNodeDataPlugin implements DataPlugin, ExecPlugin, ReadWriteDataSession {
	
	private static boolean rootNodeAllowsAddOperation = true;
	
    private DmtTestControl tbc;
    
	public TestMetaNodeDataPlugin(DmtTestControl tbc) {
	    this.tbc=tbc;
    }
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}
	public void close() throws DmtException {

	}

	public boolean isNodeUri(String[] nodeUri) {
        String nodeName = tbc.mangleUri(nodeUri);
        if (nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE) ||
            nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_WITHOUT_METANODE) ||
            nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_NODE) ||
            nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_METANODE) ||
            nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_PERMISSIONS) ||
            nodeName.equals(TestMetaNodeDataPluginActivator.PERMANENT_INEXISTENT_NODE)) {
            return false;
        } else {
            return true;
        }
        
	}

	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return null;
	}

	public String getNodeType(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String[] nodeUri) throws DmtException {
		return 0;
	}

	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		return null;
	}

	public int getNodeSize(String[] nodeUri) throws DmtException {
		return 0;
	}

	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		return null;
	}

	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
        String nodeName = tbc.mangleUri(nodeUri);
        if (nodeName.equals(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE) ||
                nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_WITHOUT_METANODE) ||
                nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_METANODE)) {
            return null;
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE)){ 
            return new TestMetaNode(true,null,null);
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.PARENT_OF_NODE_THAT_CANNOT_BE_DELETED)){ 
            return new TestMetaNode(false,null,null,false,false,false,false,true,MetaNode.DYNAMIC);            
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.NODE_CANNOT_BE_DELETED)){ 
            return new TestMetaNode(false,null,null,true,true,true,true,false,MetaNode.DYNAMIC);
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_PERMISSIONS)){ 
            return new TestMetaNode(false,null,null,false,false,false,false,false,MetaNode.DYNAMIC);
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.PERMANENT_INTERIOR_NODE)){ 
            return new TestMetaNode(false,null,null,true,true,true,true,true,MetaNode.PERMANENT);     
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.PERMANENT_INEXISTENT_NODE)){ 
            return new TestMetaNode(false,null,null,true,true,true,true,true,MetaNode.PERMANENT);                 
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.INTERIOR_NODE_WITHOUT_GET_PERMISSION)){ 
            return new TestMetaNode(false,null,null,true,false,true,true,true,MetaNode.DYNAMIC);
            //It is needed for the test where DmtSession.copy is called and the parent node does not allow add operation
        } else if (nodeName.equals(TestMetaNodeDataPluginActivator.ROOT) && !rootNodeAllowsAddOperation){ 
            return new TestMetaNode(false,null,null,false,true,true,true,true,MetaNode.DYNAMIC);
        } else {
            return new TestMetaNode();
        }

	}
	
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
        String nodeName = tbc.mangleUri(nodeUri);
        if (nodeName.equals(TestMetaNodeDataPluginActivator.LEAF_NODE)){
            return true;
        } else {
            return false;
        }
	}

	public void nodeChanged(String[] nodeUri) throws DmtException {

	}
    
    public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {
    }
    
    public void copy(String[] nodePath, String[] newNodePath, boolean recursive) throws DmtException {
    }
    
    public void createInteriorNode(String[] nodePath, String type) throws DmtException {
    }
    
    public void createLeafNode(String[] nodePath, DmtData value, String mimeType) throws DmtException {
         DmtConstants.TEMPORARY = mimeType;
    }
    
    public void deleteNode(String[] nodePath) throws DmtException {
    }
    
    public void renameNode(String[] nodePath, String newName) throws DmtException {
    }
    
    public void setNodeTitle(String[] nodePath, String title) throws DmtException {
    }
    
    public void setNodeType(String[] nodePath, String type) throws DmtException {
    }
    
    public void setNodeValue(String[] nodePath, DmtData data) throws DmtException {
       
    }
	public static void setRootNodeAllowsAddOperation(
			boolean rootNodeAllowsAddOperation) {
		TestMetaNodeDataPlugin.rootNodeAllowsAddOperation = rootNodeAllowsAddOperation;
	}
}

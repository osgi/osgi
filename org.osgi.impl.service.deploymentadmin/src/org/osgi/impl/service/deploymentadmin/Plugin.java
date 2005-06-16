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
package org.osgi.impl.service.deploymentadmin;

import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.Dmt;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtReadOnly;
import org.osgi.service.dmt.DmtSession;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class Plugin implements DmtDataPlugin, DmtExecPlugin {

    private ServiceRegistration regData;
    private ServiceRegistration regExec;
    
    private DeploymentAdminImpl da;
    
    private PluginDeployed pluginDeployed;
        
    public Plugin(DeploymentAdminImpl da) {
        this.da = da;
        pluginDeployed = new PluginDeployed(da);
    }

    public void stop(BundleContext context) throws Exception {
    }

    public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
        // TODO
    }

    public boolean supportsAtomic() {
        return false;
    }

    public void rollback() throws DmtException {
        // it is never called (doesn't support atomic)
    }

    public void commit() throws DmtException {
        // it is never called (doesn't support atomic)
    }

    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        // TODO
    }

    public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void setNodeType(String nodeUri, String type) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void deleteNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void createInteriorNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void createInteriorNode(String nodeUri, String type) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void createLeafNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void renameNode(String nodeUri, String newName) throws DmtException {
        // TODO Auto-generated method stub
    }

    public void close() throws DmtException {
        // TODO Auto-generated method stub
    }

    public boolean isNodeUri(String nodeUri) {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l == 3 && nodeUriArr[2].equals("Deployment"))
            return true;
        if (l == 4 && nodeUriArr[3].equals("Inventory"))
            return true;
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return false;
        else 
            return p.isNodeUri(nodeUri);
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l == 3 && nodeUriArr[2].equals("Deployment"))
            return false;
        if (l == 4 && nodeUriArr[3].equals("Inventory"))
            return false;
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return false;
        else 
            return p.isLeafNode(nodeUri);
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            throw new RuntimeException("Internal error");
        else 
            return p.getNodeValue(nodeUri);
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        return null;
    }

    public String getNodeType(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return null;
        else 
            return p.getNodeType(nodeUri);
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return -1; // undefined
        else 
            return p.getNodeVersion(nodeUri);
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return null;
        else 
            return p.getNodeTimestamp(nodeUri);
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            return 0;
        else 
            return p.getNodeSize(nodeUri);
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l == 3 && nodeUriArr[2].equals("Deployment"))
            return new String[] {"Download", "Ext", "Inventory"};
        if (l == 4 && nodeUriArr[3].equals("Inventory"))
            return new String[] {"Delivered", "Deployed"};
        if (l == 5 && nodeUriArr[3].equals("Ext"))
            return new String[] {};
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            throw new RuntimeException("Internal error");
        else 
            return p.getChildNodeNames(nodeUri);
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l == 3 && nodeUriArr[2].equals("Deployment"))
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.PERMANENT,
                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        if (l == 4 && nodeUriArr[3].equals("Inventory"))
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.PERMANENT,
                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        if (l == 4 && nodeUriArr[3].equals("Ext"))
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.PERMANENT,
                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        DmtReadOnly p = getForward(nodeUriArr);
        if (null == p)
            throw new RuntimeException("Internal error");
        else 
            return p.getMetaNode(nodeUri);
    }

    public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
        // TODO Auto-generated method stub
    }

    /********************************************************************************/
    
    private DmtReadOnly getForward(String[] nodeUriArr) {
        int l = nodeUriArr.length;
        if (l >= 5 && nodeUriArr[4].equals("Deployed"))
            return pluginDeployed;
        if (l >= 5 && nodeUriArr[4].equals("Delivered"))
            return null; // TODO
        if (l >= 4 && nodeUriArr[3].equals("Download"))
            return null; // TODO
        
        return null;
    }

}

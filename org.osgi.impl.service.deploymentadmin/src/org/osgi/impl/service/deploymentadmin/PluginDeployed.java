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
import java.util.Hashtable;

import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;

public class PluginDeployed implements DmtDataPlugin, DmtExecPlugin {

    private DeploymentAdminImpl da;

    public PluginDeployed(DeploymentAdminImpl da) {
        this.da= da;
    }

    /**
     * @param subtreeUri
     * @param lockMode
     * @param session
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtDataPlugin#open(java.lang.String, int, org.osgi.service.dmt.DmtSession)
     */
    public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @return
     * @see org.osgi.service.dmt.DmtDataPlugin#supportsAtomic()
     */
    public boolean supportsAtomic() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#rollback()
     */
    public void rollback() throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#commit()
     */
    public void commit() throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param title
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#setNodeTitle(java.lang.String, java.lang.String)
     */
    public void setNodeTitle(String nodeUri, String title) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param data
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#setNodeValue(java.lang.String, org.osgi.service.dmt.DmtData)
     */
    public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#setDefaultNodeValue(java.lang.String)
     */
    public void setDefaultNodeValue(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param type
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#setNodeType(java.lang.String, java.lang.String)
     */
    public void setNodeType(String nodeUri, String type) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#deleteNode(java.lang.String)
     */
    public void deleteNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String)
     */
    public void createInteriorNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param type
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String, java.lang.String)
     */
    public void createInteriorNode(String nodeUri, String type) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String)
     */
    public void createLeafNode(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param value
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String, org.osgi.service.dmt.DmtData)
     */
    public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param value
     * @param mimeType
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String, org.osgi.service.dmt.DmtData, java.lang.String)
     */
    public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param newNodeUri
     * @param recursive
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#copy(java.lang.String, java.lang.String, boolean)
     */
    public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param nodeUri
     * @param newName
     * @throws DmtException
     * @see org.osgi.service.dmt.Dmt#renameNode(java.lang.String, java.lang.String)
     */
    public void renameNode(String nodeUri, String newName) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#close()
     */
    public void close() throws DmtException {
        // TODO Auto-generated method stub
        
    }

    public boolean isNodeUri(String nodeUri) {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (!"Deployed".equals(nodeUriArr[4]))
         	return false;
        if (l == 5)
            return true;
        Hashtable mt = getMangleTable();
        if (!mt.containsKey(nodeUriArr[5]))
            return false;                
        if (l == 6)
            return true; 
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return true;
            if (nodeUriArr[6].equals("EnvType"))
                return true;
            if (nodeUriArr[6].equals("Operations"))
                return true;
            if (nodeUriArr[6].equals("Ext"))
                return true;
            return false;
        }
        if ("Operations".equals(nodeUriArr[6])) {
            if (nodeUriArr[7].equals("Remove"))
                return true;
            return false;
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Version".equals(nodeUriArr[7]))
                    return true;
                // TODO
            }
        }
        
        return false;
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return false;
        Hashtable mt = getMangleTable();
        if (!mt.containsKey(nodeUriArr[5]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");                
        if (l == 6)
            return false; 
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return true;
            if (nodeUriArr[6].equals("EnvType"))
                return true;
            if (nodeUriArr[6].equals("Operations"))
                return false;
            if (nodeUriArr[6].equals("Ext"))
                return false;
            return false;
        }
        if ("Operations".equals(nodeUriArr[6])) {
            if (nodeUriArr[7].equals("Remove"))
                return true;
            throw new RuntimeException("Internal error");
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Version".equals(nodeUriArr[7]))
                    return true;
                // TODO
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    public DmtData getNodeValue(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 7)
            throw new RuntimeException("Internal error");
        Hashtable mt = getMangleTable();
        if (!mt.containsKey(nodeUriArr[5]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        DeploymentPackageImpl dp = (DeploymentPackageImpl) mt.get(nodeUriArr[5]);
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return new DmtData(dp.getName());
            if (nodeUriArr[6].equals("EnvType"))
                return new DmtData("OSGi.R4");
            throw new RuntimeException("Internal error");
        }
        if ("Operations".equals(nodeUriArr[6])) {
            throw new RuntimeException("Internal error");
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Version".equals(nodeUriArr[7]))
                    return new DmtData(dp.getVersion().toString());
                // TODO
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getNodeTitle(java.lang.String)
     */
    public String getNodeTitle(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getNodeType(java.lang.String)
     */
    public String getNodeType(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getNodeVersion(java.lang.String)
     */
    public int getNodeVersion(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getNodeTimestamp(java.lang.String)
     */
    public Date getNodeTimestamp(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getNodeSize(java.lang.String)
     */
    public int getNodeSize(String nodeUri) throws DmtException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getChildNodeNames(java.lang.String)
     */
    public String[] getChildNodeNames(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        Hashtable mt = getMangleTable();
        if (l == 5)
            return (String[]) mt.keySet().toArray(new String[] {});
        if (!mt.containsKey(nodeUriArr[5]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");                
        if (l == 6)
            return new String[] {"ID", "EnvType", "Operations", "Ext"}; 
        if ("Operations".equals(nodeUriArr[6])) {
            if (l == 7)
                return new String[] {"Remove"};
            throw new RuntimeException("Internal error");
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 7)
                return new String[] {"Version", "Processors", "ProcessorBundle", 
                    "Bundles"};
        }
        
        throw new RuntimeException("Internal error");
    }

    /**
     * @param nodeUri
     * @return
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtReadOnly#getMetaNode(java.lang.String)
     */
    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, 
                    DmtMetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, 
                    null, 0, 0, null, DmtData.FORMAT_NODE);
        Hashtable mt = getMangleTable();
        if (!mt.containsKey(nodeUriArr[5]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");                
        if (l == 6)
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                    "", Integer.MAX_VALUE, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        if (l == 7) {
            if ("ID".equals(nodeUriArr[6]))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
            if ("EnvType".equals(nodeUriArr[6]))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
            if ("Operations".equals(nodeUriArr[6]))
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            if ("Ext".equals(nodeUriArr[6]))
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        }
        if ("Operations".equals(nodeUriArr[6])) {
            if (nodeUriArr[7].equals("Remove"))
                return new Metanode(DmtMetaNode.CMD_EXECUTE, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_BOOLEAN);
            throw new RuntimeException("Internal error");
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Version".equals(nodeUriArr[7]))
                    return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
                // TODO
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    /**
     * @param session
     * @param nodeUri
     * @param correlator
     * @param data
     * @throws DmtException
     * @see org.osgi.service.dmt.DmtExecPlugin#execute(org.osgi.service.dmt.DmtSession, java.lang.String, java.lang.String, java.lang.String)
     */
    public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
        // TODO Auto-generated method stub
        
    }

    /*************************************************************************/
    
    private Hashtable getMangleTable() {
        Hashtable ht = new Hashtable();
        DeploymentPackage[] dps = da.listDeploymentPackages();
        for (int i = 0; i < dps.length; i++) {
            String mv = da.getDmtAdmin().mangle(null, dps[i].getName());
            ht.put(mv, dps[i]);
        }
        return ht;
    }

}

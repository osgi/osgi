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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.DeploymentException;
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

    public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "");
    }

    public void renameNode(String nodeUri, String newName) throws DmtException {
        throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED, "");
    }

    public void close() throws DmtException {
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
        if (!nodeUriArr[6].equals("ID") && 
            !nodeUriArr[6].equals("EnvType") &&
            !nodeUriArr[6].equals("Operations") &&
            !nodeUriArr[6].equals("Ext"))
            	return false;
        if (l == 7)
            return true;
        if ("Operations".equals(nodeUriArr[6])) {
            if (!nodeUriArr[7].equals("Remove"))
                return false;
            return true;
        } else if ("Ext".equals(nodeUriArr[6])) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) mt.get(nodeUriArr[5]);
            if (!"Version".equals(nodeUriArr[7]) &&
                !"ProcessorBundle".equals(nodeUriArr[7]) &&
                !"Processors".equals(nodeUriArr[7]) &&
                !"Bundles".equals(nodeUriArr[7]))
                	return false;
            if ("ProcessorBundle".equals(nodeUriArr[7]) &&
                getCustomizers(dp).length == 0)
                	return false;
            if ("Processors".equals(nodeUriArr[7]) &&
                    getCustomizers(dp).length == 0)
                    	return false;
            if (l == 8)
                return true;
            Set bundleSet = new HashSet(Arrays.asList(getBundleIDs(dp)));
            if (!bundleSet.contains(nodeUriArr[8]))
                return false;
            if (l == 9)
                return true;
            if (!"SymbolicName".equals(nodeUriArr[9]) &&
                !"Location".equals(nodeUriArr[9]) &&
                !"Version".equals(nodeUriArr[9]) &&
                !"State".equals(nodeUriArr[9]) &&
                !"NativeCode".equals(nodeUriArr[9]) &&
                !"ApplicationType".equals(nodeUriArr[9]) &&
                !"ImportPackage".equals(nodeUriArr[9]) &&
                !"ExportPackage".equals(nodeUriArr[9]) &&
                !"Signers".equals(nodeUriArr[9]) &&
                !"ApplicationDescriptor".equals(nodeUriArr[9]))
                	return false;
            if (l == 10)
                return true;
            if ("ImportPackage".equals(nodeUriArr[9])) {
                String[] ips = getImportedPackages(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8])));
                Set s = new HashSet(Arrays.asList(ips));
                return s.contains(nodeUriArr[10]);
            }
            if ("ExportPackage".equals(nodeUriArr[9])) {
                String[] ips = getExportedPackages(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8])));
                Set s = new HashSet(Arrays.asList(ips));
                return s.contains(nodeUriArr[10]);
            }
            if ("Signers".equals(nodeUriArr[9])) {
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
            if (nodeUriArr[6].equals("ID") ||
                nodeUriArr[6].equals("EnvType"))
                	return true;
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
                return false;
            }
            if (l == 9)
                return false;
            if (l == 10) {
	            if ("SymbolicName".equals(nodeUriArr[9]) ||
	                "Location".equals(nodeUriArr[9]) ||
	                "Version".equals(nodeUriArr[9]) ||
	                "State".equals(nodeUriArr[9]) ||
	                "NativeCode".equals(nodeUriArr[9]) ||
	                "ApplicationType".equals(nodeUriArr[9]) ||
	                "ApplicationDescriptor".equals(nodeUriArr[9]))
	                	return true;
	            return false;
            }
            if (l == 11)
                return true;
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
                throw new RuntimeException("Internal error");
            }
            if (l == 10) {
                if ("SymbolicName".equals(nodeUriArr[9]))
                    return new DmtData(getBundleSymbolicName(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]))));
                if ("Location".equals(nodeUriArr[9]))
                    return new DmtData(getBundleLocation(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]))));
                if ("Version".equals(nodeUriArr[9]))
                    return new DmtData(getBundleVersion(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]))));
                if ("State".equals(nodeUriArr[9]))
                    return new DmtData(getBundleState(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]))));
                if ("NativeCode".equals(nodeUriArr[9]))
                    return new DmtData(getBundleNativeCode(
                        dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]))));
                if ("ApplicationType".equals(nodeUriArr[9]))
                    return new DmtData(0); // TODO ???
                if ("ApplicationDescriptor".equals(nodeUriArr[9]))
                    return new DmtData(""); // TODO ???
                throw new RuntimeException("Internal error");
            }
            // TODO
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
            DeploymentPackageImpl dp = (DeploymentPackageImpl) mt.get(nodeUriArr[5]);
            if (l == 7)
                return new String[] {"Version", "Processors", "ProcessorBundle", 
                    "Bundles"};
            if (l == 8) {
                if ("ProcessorBundle".equals(nodeUriArr[7]))
                    return getCustomizers(dp);
                if ("Processors".equals(nodeUriArr[7]))
                    return getCustomizerPIDs(dp);
                if ("Bundles".equals(nodeUriArr[7]))
                    return getBundleIDs(dp);
            }
            Set s = new HashSet(Arrays.asList(getBundleIDs(dp)));
            if (!s.contains(nodeUriArr[8]))
                throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
            if (l == 9)
                return new String[] {"SymbolicName", "Location", "Version", "State", 
                    "NativeCode", "ApplicationType", "ImportPackage", "ExportPackage", 
                    "Signers", "ApplicationDescriptor"};
            if (l == 10) {
                if ("ImportPackage".equals(nodeUriArr[9])) {
	                BundleEntry be = 
	                    dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]));
	                return getImportedPackages(be);
                }
                if ("ExportPackage".equals(nodeUriArr[9])) {
	                BundleEntry be = 
	                    dp.getBundleEntryByBundleId(Long.parseLong(nodeUriArr[8]));
	                return getExportedPackages(be);
                }
                if ("Signers".equals(nodeUriArr[9])) {
                    // TODO
                }
            }
        }
        
        throw new RuntimeException("Internal error");
    }

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
                if ("ProcessorBundle".equals(nodeUriArr[7]))
                    return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
                if ("Processors".equals(nodeUriArr[7]))
                    return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
                if ("Bundles".equals(nodeUriArr[7]))
                    return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            }
            if (l == 9)
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                        "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            if (l == 10) {
                if ("SymbolicName".equals(nodeUriArr[9]) ||
                    "Location".equals(nodeUriArr[9]) ||
                    "Version".equals(nodeUriArr[9]) ||
                    "ApplicationType".equals(nodeUriArr[9]) ||
                    "ApplicationDescriptor".equals(nodeUriArr[9]))
                    return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
                if ("State".equals(nodeUriArr[9]))
                    return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_INTEGER);
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            }
            if (l == 10)
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF, DmtMetaNode.DYNAMIC,
                    "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
        }
        
        throw new RuntimeException("Internal error");
    }

    public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        
        if (l != 8 && !nodeUriArr[7].equals("Remove"))
            throw new RuntimeException("Internal error");
        
        Hashtable mt = getMangleTable();
        DeploymentPackageImpl dp = (DeploymentPackageImpl) mt.get(nodeUriArr[5]);
        try {
            dp.uninstall();
        }
        catch (DeploymentException e) {
            throw new DmtException(nodeUri, DmtException.COMMAND_FAILED, "", e);
        }
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

    private String[] getCustomizers(DeploymentPackageImpl dp) {
        ArrayList ret = new ArrayList();
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                ret.add(be.getSymbName());
        }
        return (String[]) ret.toArray(new String[] {});
    }

    private String[] getCustomizerPIDs(DeploymentPackageImpl dp) {
        ArrayList ret = new ArrayList();
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                ret.add(be.getPid());
        }
        return (String[]) ret.toArray(new String[] {});
    }

    private String[] getBundleIDs(DeploymentPackageImpl dp) {
        ArrayList ret = new ArrayList();
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            ret.add(String.valueOf(be.getId()));
        }
        return (String[]) ret.toArray(new String[] {});
    }

    private String[] getImportedPackages(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        String iph = (String) b.getHeaders().get("Import-Package");
        if (null == iph)
            return new String[] {};
        String[] fip = Splitter.split(iph, ',', 0);
        ArrayList ipal = new ArrayList();
        for (int i = 0; i < fip.length; i++)
            ipal.add(Splitter.split(fip[i], ';', 0)[0].trim());
        return (String[]) ipal.toArray(new String[] {});
    }

    private String[] getExportedPackages(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        String iph = (String) b.getHeaders().get("Export-Package");
        if (null == iph)
            return new String[] {};
        String[] fip = Splitter.split(iph, ',', 0);
        ArrayList ipal = new ArrayList();
        for (int i = 0; i < fip.length; i++)
            ipal.add(Splitter.split(fip[i], ';', 0)[0].trim());
        return (String[]) ipal.toArray(new String[] {});
    }

    private String getBundleSymbolicName(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        return b.getSymbolicName();
    }
    
    private String getBundleLocation(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        return b.getLocation();
    }
    
    private String getBundleVersion(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        String ver = (String) b.getHeaders().get("Bundle-Version");
        return ver;
    }
    
    private int getBundleState(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        return b.getState();
    }

    private String getBundleNativeCode(BundleEntry be) {
        BundleContext context = da.getContext();
        Bundle b = context.getBundle(be.getId());
        String nc = (String) b.getHeaders().get("Bundle-NativeCode");
        return nc;
    }
    
}

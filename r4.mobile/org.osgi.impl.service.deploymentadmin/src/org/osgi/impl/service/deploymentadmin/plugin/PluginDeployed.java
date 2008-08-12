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
package org.osgi.impl.service.deploymentadmin.plugin;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.BundleEntry;
import org.osgi.impl.service.deploymentadmin.BackDoor;
import org.osgi.impl.service.deploymentadmin.CaseInsensitiveMap;
import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.PluginCtx;
import org.osgi.impl.service.deploymentadmin.ResourceEntry;
import org.osgi.service.deploymentadmin.DeploymentPackage;

public class PluginDeployed implements DataPlugin, ReadableDataSession, 
        ExecPlugin, Serializable {

    private static final String DP_PREF            = "DP:";
    private static final String BUNDLE_PREF        = "Bundle:";
    private static final int    PACKAGETYPE_DP     = 1;
    private static final int    PACKAGETYPE_BUNDLE = 2;
    
    private transient PluginCtx pluginCtx;
    private Hashtable dpIdMappings     = new Hashtable();
    private Hashtable bundleIdMappings = new Hashtable();

    public PluginDeployed(PluginCtx pluginCtx) {
        this.pluginCtx = pluginCtx;
        
        AlertSender.setLogger(pluginCtx.getLogger());
    }
    
    static String dpToNodeId(String dpsn) {
        return DP_PREF + dpsn;
    }
    
    static String bundleToNodeId(long bid) {
        return BUNDLE_PREF + String.valueOf(bid);
    }
    
    private String fromNodeId(String nodeId) {
        if (nodeId.startsWith(DP_PREF))
            return nodeId.substring(DP_PREF.length());
        return nodeId.substring(BUNDLE_PREF.length());
    }

    private String createIdForBundle(Bundle b) {
        String ret = b.getSymbolicName();
        if (null == ret)
            b.getLocation();
        return ret;
    }

    private boolean dpNode(String nodeId) {
        return nodeId.startsWith(DP_PREF);
    }
    
    private Set nodeIdMangleSet() {
        HashSet set = new HashSet();
        
        // gathers deployed DPs
        DeploymentPackage[] dps = pluginCtx.getDeploymentAdmin().listDeploymentPackages();
        for (int i = 0; i < dps.length; i++) {
            String mv = Uri.mangle(dpToNodeId(dps[i].getName()));
            set.add(mv);
        }

        // gathers deployed bundles (not belonging to any DP)
        Set bset = new HashSet();
        Bundle[] bs = pluginCtx.getBundleContext().getBundles();
        for (int i = 0; i < bs.length; i++)
            bset.add(new Long(bs[i].getBundleId()));
        for (int i = 0; i < dps.length; i++) {
            for (Iterator iter = ((DeploymentPackageImpl) dps[i]).
                    getBundleEntries().iterator(); iter.hasNext();) {
                BundleEntry be = (BundleEntry) iter.next();
                bset.remove(new Long(be.getBundleId()));
            }
        }
        for (Iterator iter = bset.iterator(); iter.hasNext();) {
            Long bid = (Long) iter.next();
            String mv = Uri.mangle(bundleToNodeId(bid.longValue()));
            set.add(mv);
        }
        
        return set;
    }
    
    private String[] getBundleIDs(String nodeId) {
        ArrayList ret = new ArrayList();
        String str = fromNodeId(nodeId);

        if (dpNode(nodeId)) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.
                    getDeploymentAdmin().getDeploymentPackage(str);
            for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
                BundleEntry be = (BundleEntry) iter.next();
                ret.add(String.valueOf(be.getBundleId()));
            }
        } else {
            Bundle[] bundles = pluginCtx.getBundleContext().getBundles();
            for (int i = 0; i < bundles.length; i++) {
                long bid = bundles[i].getBundleId();
                if (bid == Long.parseLong(str)) {
                    ret.add(String.valueOf(bid));
                    break; 
                }
            }
        }
        
        return (String[]) ret.toArray(new String[] {});
    }

    private void checkBundleExistance(String[] nodeUriArr, String nodeId,
            String bundleId) throws DmtException 
    {
        Set bundleSet = new HashSet(Arrays.asList(getBundleIDs(nodeId)));
        if (!bundleSet.contains(bundleId))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
    }
    
    private void checkStandaloneSignerExistance(String[] signers, int n,
            String[] nodeUriArr) throws DmtException {
        int lim = signers.length - 1;
        if (n < 0 || n > lim)
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
    }

    private String createCertString(String[] sa) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sa.length; i++)
            sb.append(sa[i] + "; ");
        if (sa.length > 0)
            sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    // DMT Data Plugin methods

    public ReadableDataSession openReadOnlySession(String[] subtreePath, 
            DmtSession session) {
        return this;
    }

    public ReadWriteDataSession openReadWriteSession(String[] subtreePath, 
            DmtSession session) {
        return null;
    }

    public TransactionalDataSession openAtomicSession(String[] subtreePath, 
            DmtSession session) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DMT Readable Session methods

    public void close() throws DmtException {
    }

    public boolean isNodeUri(String[] nodeUriArr) {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (!"Deployed".equals(nodeUriArr[4]))
         	return false;
        if (l == 5)
            return true;
        Set mset = nodeIdMangleSet();
        if (!mset.contains(nodeUriArr[5]))
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
            if (l == 8)
                return true;
        } else if ("Ext".equals(nodeUriArr[6])) {
            String[] bis = getBundleIDs(nodeUriArr[5]);
            if ("Bundles".equals(nodeUriArr[7]) && bis.length <= 0)
                return false;
            if (!"Signers".equals(nodeUriArr[7]) &&
                !"Manifest".equals(nodeUriArr[7]) &&
                !"Bundles".equals(nodeUriArr[7]) &&
                !"PackageType".equals(nodeUriArr[7]))
                	return false;
            if ("Signers".equals(nodeUriArr[7]) && !dpNode(nodeUriArr[5]))
                    return false;
            if ("Manifest".equals(nodeUriArr[7]) && !dpNode(nodeUriArr[5]))
                    return false;
            if (l == 8)
                return true;
            if ("Signers".equals(nodeUriArr[7])) {
                // DP signers
                DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.getDeploymentAdmin().
                    getDeploymentPackage(fromNodeId(nodeUriArr[5]));
                int lim = dp.getCertChains().size() - 1;
                int n = Integer.parseInt(nodeUriArr[8]);
                return n >= 0 && n <= lim;
            } 
            if ("Bundles".equals(nodeUriArr[7])) {
                Set bundleSet = new HashSet(Arrays.asList(getBundleIDs(nodeUriArr[5])));
                if (!bundleSet.contains(nodeUriArr[8]))
                    return false;
                if (l == 9)
                    return true;
                if (!"Location".equals(nodeUriArr[9]) &&
                    !"Signers".equals(nodeUriArr[9]) &&
                    !"Manifest".equals(nodeUriArr[9]) && 
                    !"State".equals(nodeUriArr[9]))
                    	return false;
                String[] signers = getStandaloneBundleSigners(nodeUriArr[8]);
                if ("Signers".equals(nodeUriArr[9]))
                	return signers.length > 0;
                if (l == 10)
                    return true;
                if ("Signers".equals(nodeUriArr[9])) {
                    int lim = signers.length - 1;
                    int n = Integer.parseInt(nodeUriArr[10]);
                    return n >= 0 && n <= lim;
                } 
            } 
        }
        
        return false;
    }

    public boolean isLeafNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return false;
        Set mset = nodeIdMangleSet();
        if (!mset.contains(nodeUriArr[5]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");                
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
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Manifest".equals(nodeUriArr[7]) ||
                    "PackageType".equals(nodeUriArr[7]))
                    return true;
                return false;
            }
            if (!"Signers".equals(nodeUriArr[7]))
                checkBundleExistance(nodeUriArr, nodeUriArr[5], nodeUriArr[8]);
            if (l == 9) {
                if ("Signers".equals(nodeUriArr[7]))
                    return true;
                return false;
            }
            if (l == 10) {
	            if ("Location".equals(nodeUriArr[9]) ||
	                "Manifest".equals(nodeUriArr[9]) ||
                    "State".equals(nodeUriArr[9]))
	                	return true;
	            return false;
            }
            if (l == 11) {
                if ("Signers".equals(nodeUriArr[9])) {
                    String[] signers = getStandaloneBundleSigners(nodeUriArr[8]);
                    checkStandaloneSignerExistance(signers, 
                            Integer.parseInt(nodeUriArr[10]), nodeUriArr);
                }
                return true;
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    public DmtData getNodeValue(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 7)
            throw new RuntimeException("Internal error");
        Set mset = nodeIdMangleSet();
        if (!mset.contains(nodeUriArr[5]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        String str = fromNodeId(nodeUriArr[5]);
        if (l == 7) {
            if (nodeUriArr[6].equals("ID")) {
                if (dpNode(nodeUriArr[5])) {
                    String id = (String) dpIdMappings.get(str);
                    if (null == id) {
                        DeploymentPackage dp = pluginCtx.getDeploymentAdmin().getDeploymentPackage(str);
                        id = dp.getName();
                    }
                    return new DmtData(id);
                }
                String id = (String) bundleIdMappings.get(new Long(str));
                if (null == id) {
                    Bundle b = pluginCtx.getBundleContext().getBundle(Long.parseLong(str));
                    id = createIdForBundle(b);
                }
                return new DmtData(id);
            }
            if (nodeUriArr[6].equals("EnvType"))
                return new DmtData("OSGi.R4");
        }
        if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Manifest".equals(nodeUriArr[7]))
                    return new DmtData(createManifest(str));
                if ("PackageType".equals(nodeUriArr[7])) {
                    if (dpNode(nodeUriArr[5]))
                        return new DmtData(PACKAGETYPE_DP);
                    return new DmtData(PACKAGETYPE_BUNDLE);
                }
            }
            if (l == 9) {
                // DP signers
                DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.getDeploymentAdmin().
                    getDeploymentPackage(fromNodeId(nodeUriArr[5]));
                int n = Integer.parseInt(nodeUriArr[8]);
                List certs = dp.getCertChains();
                return new DmtData(createCertString((String[]) certs.get(n)));
            }
            checkBundleExistance(nodeUriArr, nodeUriArr[5], nodeUriArr[8]);
            if (l == 10) {
                Bundle b = pluginCtx.getBundleContext().getBundle(
                        Long.parseLong(nodeUriArr[8]));
                if ("Location".equals(nodeUriArr[9]))
                    return new DmtData(b.getLocation());
                if ("State".equals(nodeUriArr[9]))
                    return new DmtData(b.getState());
                if ("Manifest".equals(nodeUriArr[9])) {
                    try {
                        return new DmtData(getManifest(b));
                    }
                    catch (IOException e) {
                        throw new DmtException(nodeUriArr, DmtException.DATA_STORE_FAILURE, "", e);
                    }
                }
            }
            if (l == 11) {
                if ("Signers".equals(nodeUriArr[9])) {
                    String[] signers = getStandaloneBundleSigners(nodeUriArr[8]);
                    int n = Integer.parseInt(nodeUriArr[10]);
                    checkStandaloneSignerExistance(signers, n, nodeUriArr);
                    return new DmtData(signers[n]);
                }
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    public String getNodeTitle(String[] nodeUriArr) throws DmtException {
        return null;
    }

    public String getNodeType(String[] nodeUriArr) throws DmtException {
        return null;
    }

    public int getNodeVersion(String[] nodeUriArr) throws DmtException {
        return 0;
    }

    public Date getNodeTimestamp(String[] nodeUriArr) throws DmtException {
        return null;
    }

    public int getNodeSize(String[] nodeUriArr) throws DmtException {
        DmtData data = getNodeValue(nodeUriArr);
        return data.getSize();
    }

    public String[] getChildNodeNames(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        Set mset = nodeIdMangleSet();
        if (l == 5)
            return (String[]) mset.toArray(new String[] {});
        if (!mset.contains(nodeUriArr[5]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");                
        if (l == 6)
            return new String[] {"ID", "EnvType", "Operations", "Ext"}; 
        if ("Operations".equals(nodeUriArr[6])) {
            if (l == 7)
                return new String[] {"Remove"};
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 7) {
                if (dpNode(nodeUriArr[5])) {
                    String[] bis = getBundleIDs(nodeUriArr[5]);
                    if (bis.length <= 0)
                        return new String[] {"Signers", "Manifest", "PackageType"};
                    return new String[] {"Signers", "Manifest", "Bundles", "PackageType"};
                }
                return new String[] {"Bundles", "PackageType"};
            }
            if (l == 8) {
                if ("Signers".equals(nodeUriArr[7])) {
                    // DP signers
                    DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.getDeploymentAdmin().
                        getDeploymentPackage(fromNodeId(nodeUriArr[5]));
                    List certs = dp.getCertChains();
                    ArrayList al = new ArrayList();
                    for (int i = 0; i < certs.size(); i++)
                        al.add(String.valueOf(i));
                    return (String[]) al.toArray(new String[] {});
                }
                if ("Bundles".equals(nodeUriArr[7]))
                    return getBundleIDs(nodeUriArr[5]);
            }
            Set s = new HashSet(Arrays.asList(getBundleIDs(nodeUriArr[5])));
            if (!s.contains(nodeUriArr[8]))
                throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
            if (l == 9)
                return new String[] {"Manifest", "Signers", "Location", "State"};
            if (l == 10) {
                if ("Signers".equals(nodeUriArr[9])) {
                    String[] signers = getStandaloneBundleSigners(nodeUriArr[8]);
                    ArrayList al = new ArrayList();
                    for (int i = 0; i < signers.length; i++)
                        al.add(String.valueOf(i));
                    return (String[]) al.toArray(new String[] {});
                }
            }
        }
        
        throw new RuntimeException("Internal error");
    }

    public MetaNode getMetaNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, 
                    MetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, 
                    null, 0, 0, null, DmtData.FORMAT_NODE);
        Set mset = nodeIdMangleSet();
        if (!mset.contains(nodeUriArr[5]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        if (l == 6)
            return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                    "", Integer.MAX_VALUE, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        if (l == 7) {
            if ("ID".equals(nodeUriArr[6]))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
            if ("EnvType".equals(nodeUriArr[6]))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
            if ("Operations".equals(nodeUriArr[6]))
                return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            if ("Ext".equals(nodeUriArr[6]))
                return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
        }
        if ("Operations".equals(nodeUriArr[6])) {
            if (nodeUriArr[7].equals("Remove"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                        "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NULL).
                        orOperation(MetaNode.CMD_EXECUTE);
            throw new RuntimeException("Internal error");
        } else if ("Ext".equals(nodeUriArr[6])) {
            if (l == 8) {
                if ("Signers".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
                if ("Manifest".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
                if ("PackageType".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_INTEGER);
                if ("Bundles".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            }
            if (l == 9) {
                if ("Signers".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                    		"", Integer.MAX_VALUE, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
                if ("Bundles".equals(nodeUriArr[7]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                    		"", Integer.MAX_VALUE, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
            }
            if (l == 10) {
                if ("Signers".equals(nodeUriArr[9]))
                    return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
                if ("State".equals(nodeUriArr[9]))
                    return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_INTEGER);
                if ("Location".equals(nodeUriArr[9]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
                if ("Manifest".equals(nodeUriArr[9]))
                    return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                            "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);
            }
            if (l == 11) {
                if ("Signers".equals(nodeUriArr[9]))
                    return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF, MetaNode.AUTOMATIC,
                    		"", Integer.MAX_VALUE, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_STRING);        
            }
        }
       
        throw new RuntimeException("Internal error");
    }

    public void nodeChanged(String[] nodeUriArr) throws DmtException {
    }

    public void execute(final DmtSession session, final String[] nodeUriArr, 
    		final String correlator, String data) throws DmtException {
        int l = nodeUriArr.length;
        
        if (l != 8 && !nodeUriArr[7].equals("Remove"))
            throw new RuntimeException("Internal error");
        
        final DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.getDeploymentAdmin().
                getDeploymentPackage(fromNodeId(nodeUriArr[5]));
        if (null != dp) {
            UndeployThread undThread = new UndeployThread(dp);
            undThread.setListener(new UndeployThread.Listener() {
                public void onFinish(Exception exception) {
                	String nodeUriRes = null;
                    if (null == exception) {
                    	nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT + 
                    		Uri.mangle(dpToNodeId(dp.getName()));
                        dpIdMappings.remove(dp.getName());
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e);
                        }
                    } else {
                    	nodeUriRes = PluginCtx.convertUri(nodeUriArr, 2); // the original URI
                        pluginCtx.getLogger().log(exception);
                    }
                    AlertSender.sendDeploymentRemoveAlert(exception, session.getPrincipal(), 
                    		correlator, nodeUriRes, pluginCtx.getNotificationService());
                }
            });
            undThread.start();
        } else {
            String sId = fromNodeId(nodeUriArr[5]);
            long id;
            try {
                id = Integer.parseInt(sId);    
            } catch (NumberFormatException e) {
                return;
            }
            final Bundle bundle = getBundleByBundleId(id);
            UndeployThread undThread = new UndeployThread(bundle);
            undThread.setListener(new UndeployThread.Listener() {
                public void onFinish(Exception exception) {
                	String nodeUriRes = null;
                    if (null == exception) {
                    	nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT + 
                			Uri.mangle(bundleToNodeId(bundle.getBundleId()));
                        bundleIdMappings.remove(fromNodeId(nodeUriArr[5]));
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e);
                        }
                    } else {
                    	nodeUriRes = PluginCtx.convertUri(nodeUriArr, 2); // the original URI
                        pluginCtx.getLogger().log(exception);
                    }
                    AlertSender.sendDeploymentRemoveAlert(exception, session.getPrincipal(), 
                    		correlator, nodeUriRes, pluginCtx.getNotificationService());
                }
            });
            undThread.start();
        }
    }
    
    private Bundle getBundleByBundleId(long id) {
        Bundle[] bundles = pluginCtx.getBundleContext().getBundles();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].getBundleId() == id)
                return bundles[i];
        }
        return null;
    }

    private String getManifest(Bundle b) throws IOException {
        URL url = b.getEntry("/META-INF/MANIFEST.MF");
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String line = br.readLine();
        while (null != line) {
            sb.append(line + "\n");
            line = br.readLine();
        }
        return sb.toString();
    }
    
    private String createManifest(String symbName) {
        StringBuffer manifest = new StringBuffer();
        DeploymentPackageImpl dp = (DeploymentPackageImpl) pluginCtx.getDeploymentAdmin().
                getDeploymentPackage(symbName);
     
        // create Manifest-Version entry
        CaseInsensitiveMap cm = dp.getMainSection();
        manifest.append(keyValuePair("Manifest-Version", cm.get("Manifest-Version")) + "\n");
        
        // create main section
        for (Iterator iter = cm.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if ("Manifest-Version".equals(key))
                continue;
            String kvp = keyValuePair(key, cm.get(key));
            manifest.append(kvp + "\n");
        }
        manifest.append("\n");
        
        // create name sections for bundles
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            CaseInsensitiveMap attrs = be.getAttrs();
            if (null == attrs)
                continue;
            manifest.append(keyValuePair("Name", be.getResName()) + "\n");
            for (Iterator beit = attrs.keySet().iterator(); beit.hasNext();) {
                String key = (String) beit.next();
                String kvp = keyValuePair(key, be.getAttrs().get(key));
                manifest.append(kvp + "\n");
            }
            manifest.append("\n");
        }
        
        // create name sections for resources
        for (Iterator iter = dp.getResourceEntries().iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            manifest.append(keyValuePair("Name", re.getResName()) + "\n");
            for (Iterator reit = re.getAttrs().keySet().iterator(); reit.hasNext();) {
                String key = (String) reit.next();
                String kvp = keyValuePair(key, re.getAttrs().get(key));
                manifest.append(kvp + "\n");
            }
            manifest.append("\n");
        }
       
        //System.out.println(manifest.toString());
        return manifest.toString();
    }

    private String keyValuePair(String key, Object value) {
        StringBuffer ret = new StringBuffer();
        String tmp = key + ": " + value;
        while (tmp.length() > 70) {
            String s = tmp.substring(0, 70);
            tmp = tmp.substring(70);
            ret.append(s + "\n ");
        }
        ret.append(tmp);
        return ret.toString();
    }
    
    private String[] getStandaloneBundleSigners(String bStr) {
        Bundle b = pluginCtx.getBundleContext().getBundle(
                Long.parseLong(bStr));
        BackDoor bu = new BackDoor(pluginCtx.getBundleContext());
        String[] ret = bu.getDNChains(b);
        bu.destroy();
        if (null == ret)
        	ret = new String[] {};
        return ret; 
    }

    public String associateID(DeploymentPackageImpl dp, String dwnlId) {
       dpIdMappings.put(dp.getName(), dwnlId);
       return Uri.mangle(dpToNodeId(dp.getName()));
    }
    
    public String associateID(Bundle b, String dwnlId) {
       bundleIdMappings.put(new Long(b.getBundleId()), dwnlId);
       return Uri.mangle(bundleToNodeId(b.getBundleId()));
    }

    public void setPluginCtx(PluginCtx pluginCtx) {
        this.pluginCtx = pluginCtx;
    }

}

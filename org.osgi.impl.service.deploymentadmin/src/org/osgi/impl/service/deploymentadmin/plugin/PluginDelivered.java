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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.PluginCtx;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.*;

public class PluginDelivered implements DataPluginFactory, ReadableDataSession, 
        ExecPlugin, Serializable {
    
	private transient PluginCtx pluginCtx;
	private transient File      store;

	public PluginDelivered(PluginCtx pluginCtx) {
	    String delArea = System.getProperty(DAConstants.DELIVERED_AREA);
	    if (null == delArea)
	        delArea = "/temp";
	    store = new File(delArea);
	    if (!store.exists())
	        throw new RuntimeException("Delivered area ('" + delArea + "') does not exist. " +
                "Set the " + DAConstants.DELIVERED_AREA + " system property");
        
		this.pluginCtx = pluginCtx;		
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
        if (!"Delivered".equals(nodeUriArr[4]))
         	return false;
        if (l == 5)
            return true;
        if (!Arrays.asList(getFiles(nodeUriArr)).contains(new File(store, nodeUriArr[5])))
            return false;
        if (l == 6)
            return true;
        if (!nodeUriArr[6].equals("ID") && 
            !nodeUriArr[6].equals("Data") &&
            !nodeUriArr[6].equals("EnvType") &&
            !nodeUriArr[6].equals("Operations"))
            	return false;
        if (l == 7)
            return true;
        if (nodeUriArr[7].equals("Remove"))
            return true;
        if (nodeUriArr[7].equals("InstallAndActivate"))
            return true;
        
        return false;
    }

    public boolean isLeafNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
            return false;
        if (!Arrays.asList(getFiles(nodeUriArr)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        if (l == 6)
            return false;
        if (l == 7) {
            if (nodeUriArr[6].equals("ID") || 
                nodeUriArr[6].equals("Data") ||
                nodeUriArr[6].equals("EnvType"))
                    return true;
            return false;
        }
        if (l == 8)
            return true;
        
        throw new RuntimeException("Internal error");
    }

    public DmtData getNodeValue(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (!Arrays.asList(getFiles(nodeUriArr)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
		if (l == 7) {
		    if (nodeUriArr[6].equals("ID"))
		        return new DmtData(nodeUriArr[5]); // TODO globally unique
		    if (nodeUriArr[6].equals("Data")) {
		        File f = new File(store, nodeUriArr[5]);
		        FileInputStream is = null;
		        ByteArrayOutputStream os = new ByteArrayOutputStream();
		        try {
		            is = new FileInputStream(f);
		            byte[] data = new byte[0x1000];
		            int i = is.read(data);
		            while (-1 != i) {
		                os.write(data, 0, i);
		                i = is.read(data);
		            } 
                }
                catch (Exception e) {
                    throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, "", e);
                }
                finally {
                    try {
                        if (null != is)
                            is.close();
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                return new DmtData(os.toByteArray());
            }
		    if (nodeUriArr[6].equals("EnvType"))
		        return new DmtData("OSGi.R4");
		}
		if (l == 8)
		    return DmtData.NULL_VALUE;
		
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
        return getNodeValue(nodeUriArr).getSize();
    }

    public String[] getChildNodeNames(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        File[] files = getFiles(nodeUriArr);
        if (l == 5) {
            String[] ret = new String[files.length];
            for (int i = 0; i < files.length; i++)
                ret[i] = files[i].getName();
        	return ret;
        }
        if (!Arrays.asList(getFiles(nodeUriArr)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
	    if (l == 6)
            return new String[] {"ID", "EnvType", "Data", "Operations"};
        if (l == 7) {
            if (nodeUriArr[6].equals("Operations"))
                return new String[] {"Remove", "InstallAndActivate"};
        }
        
        throw new RuntimeException("Internal error");
    }

    public MetaNode getMetaNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 5)
            throw new RuntimeException("Internal error");
        if (l == 5)
			return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
					MetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(MetaNode.CMD_ADD);
        if (l == 6)
			return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(MetaNode.CMD_ADD);
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(MetaNode.CMD_REPLACE);
		    if (nodeUriArr[6].equals("Data"))
		        return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_BINARY);
		    if (nodeUriArr[6].equals("EnvType"))
		        return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
            if (nodeUriArr[6].equals("Operations"))
                return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_NODE);
        }
        if (l == 8) {
            return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NULL).orOperation(MetaNode.CMD_EXECUTE);
        }
        
        throw new RuntimeException("Internal error");    }

    public void execute(DmtSession session, String[] nodeUriArr, String correlator, String data) throws DmtException {
        int l = nodeUriArr.length;
        if (l != 8)
            throw new RuntimeException("Internal error");
        if (!Arrays.asList(getFiles(nodeUriArr)).contains(new File(store, nodeUriArr[5])))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");        
        if (nodeUriArr[7].equals("Remove")) {
            File f = new File(store, nodeUriArr[5]);
            f.delete();
        }
        if (nodeUriArr[7].equals("InstallAndActivate")) {
            install(nodeUriArr);
        }
    }
    
    public void nodeChanged(String[] nodeUriArr) throws DmtException {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods
    
    private void install(String[] nodeUriArr) throws DmtException {
        File f = new File(store, nodeUriArr[5]);
        final FileInputStream is;
        try {
            is = new FileInputStream(f);
        }
        catch (FileNotFoundException e) {
            throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, e.getMessage());
        }
        String mimeType;
        if (f.getName().endsWith(".dp"))
            mimeType = PluginConstants.MIME_DP;
        else 
            mimeType = PluginConstants.MIME_BUNDLE;

        final String id = nodeUriArr[5];
        DeploymentThread deplThr = new DeploymentThread(mimeType, 
                pluginCtx, is, id);
        deplThr.setDpListener(new DeploymentThread.ListenerDp() {
            public void onFinish(DeploymentPackageImpl dp, Exception exception) {
                if (null == exception) {
                    pluginCtx.getDeployedPlugin().associateID(dp, id);
                    try {
                        pluginCtx.save();
                        // TODO sendAlert
                    }
                    catch (IOException e) {
                        pluginCtx.getLogger().log(e); 
                    }
                } else { 
                    pluginCtx.getLogger().log(exception);
                    // TODO sendAlert
            }}});
        deplThr.setBundleListener(new DeploymentThread.ListenerBundle() {
            public void onFinish(Bundle b, Exception exception) {
                if (null == exception) {
                    pluginCtx.getDeployedPlugin().associateID(b, id);
                    try {
                        pluginCtx.save();
                        // TODO sendAlert
                    }
                    catch (IOException e) {
                        pluginCtx.getLogger().log(e); 
                    }
                } else {
                    pluginCtx.getLogger().log(exception);
                    // TODO sendAlert
            }}});
        deplThr.start();
    }
    
    private File[] getFiles(String[] nodeUriArr) {
        DmtAdmin dmtA = pluginCtx.getDmtAdmin();
        if (null == dmtA)
            throw new RuntimeException("DMT Admin doesn't run");
        File[] files = null;
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return store.listFiles();
            }});
        ArrayList ret = new ArrayList();
        for (int i = 0; i < files.length; i++) {
            String a = files[i].getName();
            String b = dmtA.mangle(files[i].getName());
            if (a.equals(b) && !files[i].isDirectory())
                ret.add(files[i]);
        }
        return (File[]) ret.toArray(new File[] {});
    }

}

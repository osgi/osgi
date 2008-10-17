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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Logger;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.PluginCtx;

public class PluginDelivered implements DataPlugin, ReadableDataSession, 
        ExecPlugin, Serializable {
    
	private transient PluginCtx pluginCtx;
	
	// the directory that represents the delivered area
	private transient File      store;

	public PluginDelivered(PluginCtx pluginCtx) {
	    String delArea = System.getProperty(DAConstants.DELIVERED_AREA);
	    if (null == delArea)
	        delArea = "/temp";
	    store = new File(delArea);
	    if (!store.exists()) {
            pluginCtx.getLogger().log(Logger.LOG_WARNING, "Delivered area ('" + 
                    delArea + "') does not exist. Set the " + 
                    DAConstants.DELIVERED_AREA + " system property");
            store = null;
        }
        
		this.pluginCtx = pluginCtx;		
		
		AlertSender.setLogger(pluginCtx.getLogger());
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
        if (!fileExists(nodeUriArr))
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
        if (!fileExists(nodeUriArr))
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
        if (!fileExists(nodeUriArr))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
		if (l == 7) {
		    if (nodeUriArr[6].equals("ID"))
		        return new DmtData(nodeUriArr[5]); // TODO globally unique
		    if (nodeUriArr[6].equals("Data"))
				try {
					return new DmtData(extractData(nodeUriArr[5]));
				} catch (IOException e) {
					throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, "", e);
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
        if (!fileExists(nodeUriArr))
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
					MetaNode.AUTOMATIC, "", Integer.MAX_VALUE, Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE);
        if (l == 7) {
            if (nodeUriArr[6].equals("ID"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
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
        	remove(nodeUriArr, session, correlator);
        }
        if (nodeUriArr[7].equals("InstallAndActivate")) {
            install(nodeUriArr, session, correlator);
        }
    }
    
	public void nodeChanged(String[] nodeUriArr) throws DmtException {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private methods
    
	private void remove(final String[] nodeUriArr, final DmtSession session, final String correlator) {
		Thread t = new Thread () {
			public void run() {
		        boolean b = removeNode(new File(store, nodeUriArr[5]));
		        AlertSender.sendDeliveredRemoveAlert(b, session.getPrincipal(), correlator, 
		        		PluginCtx.convertUri(nodeUriArr, 2), pluginCtx.getNotificationService());
			}
		};
		t.start();
    }
	
    private byte[] extractData(String fileName) throws IOException {
        File f = new File(store, fileName);
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
            return os.toByteArray();
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
	}
    
    private void install(final String[] nodeUriArr, DmtSession session, String correlator) throws DmtException {
    	String fileName = nodeUriArr[5];
        final File file = new File(store, fileName);
        final FileInputStream is; 
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, e.getMessage());
        }
        
        String mimeType;
        if (file.getName().endsWith(".dp"))
            mimeType = PluginConstants.MIME_DP;
        else 
            mimeType = PluginConstants.MIME_BUNDLE;

        final String nodeId = fileName;
        DeploymentThread deplThr = new DeploymentThread(mimeType, 
                pluginCtx, is, nodeId);
        String nodeUri = PluginCtx.convertUri(nodeUriArr, 2);
        setDpListener(deplThr, is, file, session.getPrincipal(), correlator, nodeUri);
        setBundleListener(deplThr, is, file, session.getPrincipal(), correlator, nodeUri);
        deplThr.start();
    }

    private void setBundleListener(DeploymentThread deplThr,
			final FileInputStream is, final File file, final String principal,
			final String correlator, final String nodeUri) {
		deplThr.setBundleListener(new DeploymentThread.ListenerBundle() {
			public void onFinish(Bundle b, Exception exception) {
				if (null != is)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				String nodeUriRes = null;
				if (null == exception) {
					nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT + 
							pluginCtx.getDeployedPlugin().associateID(b, file.getName());
					removeNode(file);
					try {
						pluginCtx.save();
					} catch (IOException e) {
						pluginCtx.getLogger().log(e);
					}
				} else {
					nodeUriRes = nodeUri; // the original URI
					pluginCtx.getLogger().log(exception);
				}
				AlertSender.sendDeployAlert(false, exception, principal, correlator, nodeUriRes, 
						DAConstants.ALERT_TYPE_INS_ACT, pluginCtx.getNotificationService());
			}
		});
	}

	private void setDpListener(DeploymentThread deplThr, final InputStream is, final File file, 
			final String principal, final String correlator, final String nodeUri) {
		deplThr.setDpListener(new DeploymentThread.ListenerDp() {
			public void onFinish(DeploymentPackageImpl dp, Exception exception) {
				if (null != is)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				String nodeUriRes = null;
				if (null == exception) {
					nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT + 
							pluginCtx.getDeployedPlugin().associateID(dp, file.getName());
					removeNode(file);
					try {
						pluginCtx.save();
					} catch (IOException e) {
						pluginCtx.getLogger().log(e);
					}
				} else {
					nodeUriRes = nodeUri; // the original URI
					pluginCtx.getLogger().log(exception);
				}
				AlertSender.sendDeployAlert(pluginCtx.bundlesNotStarted(dp).length != 0,
						exception, principal, correlator, nodeUriRes, 
                		DAConstants.ALERT_TYPE_INS_ACT, pluginCtx.getNotificationService());
			}
		});
	}

	private boolean removeNode(final File file) {
		boolean ret = ((Boolean) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Boolean(file.delete());
			}})).booleanValue();
        if (!ret)
        	pluginCtx.getLogger().log(Logger.LOG_WARNING, "Cannot remove the subtree under " +
        			"the 'Delivered' node. Not able to delete file " + file);
        return ret;
   }
    
    private File[] getFiles(String[] nodeUriArr) {
        File[] files;
        if (null != store) {
            files = (File[]) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return store.listFiles();
                }});
        } else {
            files = new File[] {};
        }
        
        ArrayList ret = new ArrayList();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                String a = files[i].getName();
                String b = Uri.mangle(files[i].getName());
                if (a.equals(b) && !files[i].isDirectory())
                    ret.add(files[i]);
            }
        }
        return (File[]) ret.toArray(new File[] {});
    }

    private boolean fileExists(String[] nodeUriArr) {
        if (null != store)
            return Arrays.asList(getFiles(nodeUriArr)).contains(
                    new File(store, nodeUriArr[5]));
        return false;
    }
    
}

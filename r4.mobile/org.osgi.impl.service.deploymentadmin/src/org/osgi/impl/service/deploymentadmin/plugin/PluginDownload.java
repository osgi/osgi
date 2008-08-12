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
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.DAConstants;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.PluginCtx;
import org.osgi.impl.service.dwnl.DownloadAgent;
import org.xml.sax.helpers.DefaultHandler;

public class PluginDownload extends DefaultHandler implements DataPlugin,
        ReadWriteDataSession, ExecPlugin, Serializable 
{
    
    // download and deployment states (value of the 
    // ./OSGi/Deployment/Download/<node_id>/Status DMT node)
    private static final int STATUS_IDLE                   = 10;
    private static final int STATUS_DOWNLD_FAILED          = 20;
    private static final int STATUS_STREAMING              = 50;
    private static final int STATUS_DEPLOYMENT_FAILED      = 70;
    private static final int STATUS_DEPLOYED               = 80;
    
	private transient PluginCtx pluginCtx;
	private Entries             entries = new Entries();
    
    public PluginDownload(PluginCtx pluginCtx) {
        this.pluginCtx = pluginCtx;       
        
        AlertSender.setLogger(pluginCtx.getLogger());
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // Private classes
    
    private class Entry implements Serializable {
        private String  dwnlId;
        private String  uri;
        private String  envType;
        private Integer status = new Integer(STATUS_IDLE);
        
        public void setStatus(int status) {
            this.status = new Integer(status);
        }
        
        public int getStatus() {
            return status.intValue();
        }
    }
    
    private class Entries implements Serializable {
        private Hashtable ht = new Hashtable();
        
        private boolean contains(String nodeId) {
            return ht.containsKey(nodeId);
        }
        
        private Entry get(String nodeId) {
            return (Entry) ht.get(nodeId);
        }
        
        private void add(String nodeID) {
            ht.put(nodeID, new Entry());
        }

        public void remove(String nodeId) {
            ht.remove(nodeId);
        }

        private String[] keys() {
            return (String[]) ht.keySet().toArray(new String[] {});
        }
        
        private void start(final String[] nodeUriArr, final String correlator, final String principal) 
                throws DmtException 
        {
            final String nodeUri = PluginCtx.convertUri(nodeUriArr, 2);
            final Entry entry = (Entry) ht.get(nodeUriArr[4]);
            entry.setStatus(STATUS_STREAMING);
            
            DownloadAgent dwnlAgent = pluginCtx.getDownloadAgent();
            if (null == dwnlAgent) {
                entry.setStatus(STATUS_DOWNLD_FAILED);
                throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, 
                    "Download Agent service is not available");
            }

            SAXParser parser = null;
            try {
                parser = (SAXParser) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws Exception {
                        ServiceReference refs[] = pluginCtx.getBundleContext().getServiceReferences(
                                SAXParserFactory.class.getName(),
                                "(&(parser.namespaceAware=true)" + "(parser.validating=true))");
                        if (refs == null)
                            throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, 
                                "SAX Parser is not available");
                        SAXParserFactory factory = (SAXParserFactory) pluginCtx.getBundleContext().
                                getService(refs[0]);
                        return factory.newSAXParser();
                    }});
            } catch (PrivilegedActionException e) {
                entry.setStatus(STATUS_DOWNLD_FAILED);
                throw new RuntimeException("Internal error: " + e);
            }

            DownloadThread dwnlThr = new DownloadThread(parser, dwnlAgent, entry.uri);
            dwnlThr.start();
            
            // waits until the download thread ends
            try {
                dwnlThr.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Internal error: " + e);
            }
            
            if (dwnlThr.getStatus() != DownloadThread.RESULT_OK) {
                entry.setStatus(STATUS_DOWNLD_FAILED);
                AlertSender.sendDownloadAlert(dwnlThr.getStatus(), 
                    principal, correlator, nodeUri, pluginCtx.getNotificationService());
                return;
            }

            DeploymentThread deplThr = new DeploymentThread(dwnlThr.getMimeType(), pluginCtx, 
                    dwnlThr.getInputStream(), entry.dwnlId);
            deplThr.setDpListener(new DeploymentThread.ListenerDp() {
                public void onFinish(DeploymentPackageImpl dp, Exception exception) {
                	String nodeUriRes = null;
                    if (null == exception) {
                        entry.setStatus(STATUS_DEPLOYED);
                        nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT + 
                        	pluginCtx.getDeployedPlugin().associateID(dp, entry.dwnlId);
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e); 
                        }
                    } else {
                    	nodeUriRes = nodeUri; // the original URI
                        entry.setStatus(STATUS_DEPLOYMENT_FAILED);
                    }
                    AlertSender.sendDeployAlert(pluginCtx.bundlesNotStarted(dp).length != 0, 
                    		exception, principal, correlator, nodeUriRes, 
                    		DAConstants.ALERT_TYPE_DWNL_INS_ACT, pluginCtx.getNotificationService());
                }});
            deplThr.setBundleListener(new DeploymentThread.ListenerBundle() {
                public void onFinish(Bundle b, Exception exception) {
                	String nodeUriRes = null;
                    if (null == exception) {
                        entry.setStatus(STATUS_DEPLOYED);
                        nodeUriRes = DAConstants.DMT_DEPLOYMENT_ROOT +
                        	pluginCtx.getDeployedPlugin().associateID(b, entry.dwnlId);
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e); 
                        }
                    } else {
                    	nodeUriRes = nodeUri; // the original URI
                        entry.setStatus(STATUS_DEPLOYMENT_FAILED);
                    }
                    AlertSender.sendDeployAlert(false, exception, principal, correlator, nodeUriRes,
                            DAConstants.ALERT_TYPE_DWNL_INS_ACT, pluginCtx.getNotificationService());
                }});
            deplThr.start();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // DMT Data Plugin methods

	public ReadableDataSession openReadOnlySession(String[] subtreePath, 
            DmtSession session) {
        return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] subtreePath, 
            DmtSession session) {
        return this;
	}

	public TransactionalDataSession openAtomicSession(String[] subtreePath, 
            DmtSession session) {
        return null;
	}

    ///////////////////////////////////////////////////////////////////////////
    // DMT Read-Write Session methods

	public void setNodeTitle(String[] nodeUriArr, String title) throws DmtException {
	}

	public void setNodeValue(String[] nodeUriArr, DmtData data) throws DmtException {
        int l = nodeUriArr.length;
        if (l != 6)
            throw new RuntimeException("Internal error");
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        if (nodeUriArr[5].equals("ID"))
            entries.get(nodeUriArr[4]).dwnlId = data.getString();            
        if (nodeUriArr[5].equals("URI"))
            entries.get(nodeUriArr[4]).uri = data.getString();
        if (nodeUriArr[5].equals("EnvType"))
            entries.get(nodeUriArr[4]).envType = data.getString();
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUriArr, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
    }

	public void setNodeType(String[] nodeUriArr, String type) throws DmtException {
	}

	public void deleteNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        entries.remove(nodeUriArr[4]);
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUriArr, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
	}

	public void createInteriorNode(String[] nodeUriArr, String type) throws DmtException {
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        if (entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUriArr, DmtException.NODE_ALREADY_EXISTS, "");
        entries.add(nodeUriArr[4]);
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUriArr, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
	}

	public void createLeafNode(String[] nodeUriArr, DmtData value, String mimeType) throws DmtException {
	}

	public void copy(String[] nodeUriArr, String[] newNodeUriArr, boolean recursive) throws DmtException {
	}

	public void renameNode(String[] nodeUriArr, String newName) throws DmtException {
	}

	public void close() throws DmtException {
	}

	public boolean isNodeUri(String[] nodeUriArr) {
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (!"Download".equals(nodeUriArr[3]))
         	return false;
        if (l == 4)
            return true;
        if (!entries.contains(nodeUriArr[4]))
        	return false;
        if (l == 5)
            return true;
        if (!nodeUriArr[5].equals("ID") && 
            !nodeUriArr[5].equals("URI") &&
            !nodeUriArr[5].equals("EnvType") &&
            !nodeUriArr[5].equals("Status") &&
            !nodeUriArr[5].equals("Operations"))
            	return false;
        if (l == 6)
            return true;
        if (!nodeUriArr[5].equals("Operations"))
            return false;
        if (!nodeUriArr[6].equals("DownloadAndInstallAndActivate"))
            return false;
        if (l == 7)
            return true;
        
        return false;
	}

	public boolean isLeafNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
            return false;
        if (l == 5)
            return false;
        if (l == 6) {
            if (nodeUriArr[5].equals("ID") || 
                nodeUriArr[5].equals("URI") ||
                nodeUriArr[5].equals("EnvType") ||
                nodeUriArr[5].equals("Status"))
                    return true;
            return false;
        }
        if (l == 7) {
            if (nodeUriArr[5].equals("DownloadAndInstallAndActivate"))
                return true;
        }
        
        throw new RuntimeException("Internal error");
	}

	public DmtData getNodeValue(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
		if (l == 6) {
		    if (nodeUriArr[5].equals("ID"))
		        return new DmtData(entries.get(nodeUriArr[4]).dwnlId);
		    if (nodeUriArr[5].equals("URI"))
                return new DmtData(entries.get(nodeUriArr[4]).uri);
		    if (nodeUriArr[5].equals("EnvType"))
                return new DmtData(entries.get(nodeUriArr[4]).envType);
		    if (nodeUriArr[5].equals("Status"))
                return new DmtData(entries.get(nodeUriArr[4]).getStatus());
		    throw new RuntimeException("Internal error");
		}
		if (l == 7) {
		    return DmtData.NULL_VALUE;
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
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
        	return entries.keys();
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        if (l == 5)
            return new String[] {"ID", "URI", "EnvType", "Status", "Operations"};
        if (l == 6)
            return new String[] {"DownloadAndInstallAndActivate"};
        
        throw new RuntimeException("Internal error");
	}

	public MetaNode getMetaNode(String[] nodeUriArr) throws DmtException {
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
			return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
					MetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE);
        if (l == 5)
			return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
					MetaNode.DYNAMIC, "", Integer.MAX_VALUE, Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(MetaNode.CMD_ADD).
                    orOperation(MetaNode.CMD_DELETE);
        if (l == 6) {
            if (nodeUriArr[5].equals("ID"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(MetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("URI"))
		        return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(MetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("EnvType"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
                        MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
                        0, null, DmtData.FORMAT_STRING).orOperation(MetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("Status"))
		        return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_INTEGER);
            if (nodeUriArr[5].equals("Operations"))
                return new Metanode(MetaNode.CMD_GET, !Metanode.IS_LEAF,
    					MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_NODE);
        }
        if (l == 7) {
            if (nodeUriArr[6].equals("DownloadAndInstallAndActivate"))
                return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
                        MetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
                        0, null, DmtData.FORMAT_NULL).orOperation(MetaNode.CMD_EXECUTE);
            return new Metanode(MetaNode.CMD_GET, Metanode.IS_LEAF,
					MetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NULL).orOperation(MetaNode.CMD_EXECUTE);
        }
        
        throw new RuntimeException("Internal error");
	}

	public void execute(DmtSession session, String[] nodeUriArr, String correlator, String data) throws DmtException {
        int l = nodeUriArr.length;
        if (l != 7)
            throw new RuntimeException("Internal error");
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUriArr, DmtException.NODE_NOT_FOUND, "");
        String envType = entries.get(nodeUriArr[4]).envType;
        if (null == envType || !envType.equals("OSGi.R4"))
            throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, "EnvType has to " +
                    "be 'OSGi.R4'");
        String id = entries.get(nodeUriArr[4]).dwnlId;
        if (null == id || id.trim().length() == 0)
            throw new DmtException(nodeUriArr, DmtException.COMMAND_FAILED, "ID has to " +
                    "be set");
        
        entries.start(nodeUriArr, correlator, session.getPrincipal());
	}
    
    public void nodeChanged(String[] nodeUriArr) throws DmtException {
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Private methods
    
    public void setPluginCtx(PluginCtx pluginCtx) {
        this.pluginCtx = pluginCtx;
    }

}

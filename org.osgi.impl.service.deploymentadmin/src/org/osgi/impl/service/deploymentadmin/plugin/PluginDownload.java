package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.Serializable;
import java.security.AccessControlException;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.DeploymentAdminImpl;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.PluginCtx;
import org.osgi.impl.service.deploymentadmin.Splitter;
import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;
import org.osgi.impl.service.dwnl.DownloadAgent;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.dmt.DmtAlertItem;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.xml.sax.helpers.DefaultHandler;

public class PluginDownload extends DefaultHandler 
        implements DmtDataPlugin, DmtExecPlugin, Serializable 
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
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // Private classes
    
    private class Entry implements Serializable {
        private String  id;
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
        
        private void start(final String nodeUri, final String correlator, final String principal) 
                throws DmtException 
        {
            String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
            final Entry entry = (Entry) ht.get(nodeUriArr[4]);
            
            entry.setStatus(STATUS_STREAMING);
            
            DownloadAgent dwnlAgent = pluginCtx.getDownloadAgent();
            if (null == dwnlAgent) {
                entry.setStatus(STATUS_DOWNLD_FAILED);
                throw new DmtException(nodeUri, DmtException.COMMAND_FAILED, 
                    "Download Agent service is not available");
            }

            SAXParser parser = null;
            try {
                ServiceReference refs[] = pluginCtx.getBundleContext().getServiceReferences(
                        SAXParserFactory.class.getName(),
                        "(&(parser.namespaceAware=true)" + "(parser.validating=true))");
                if (refs == null)
                    throw new DmtException(nodeUri, DmtException.COMMAND_FAILED, 
                        "SAX Parser is not available");
                SAXParserFactory factory = (SAXParserFactory) pluginCtx.getBundleContext().
                        getService(refs[0]);
                parser = factory.newSAXParser();
            } catch (Exception e) {
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
            
            if (dwnlThr.getStatus() != DownloadThread.OK) {
                entry.setStatus(STATUS_DOWNLD_FAILED);
                sendDownloadAlert(dwnlThr.getStatus(), 
                    principal, correlator, nodeUri);
                return;
            }

            DeploymentThread deplThr = new DeploymentThread(dwnlThr.getMimeType(), pluginCtx, 
                    dwnlThr.getInputStream(), entry.id);
            deplThr.setDpListener(new DeploymentThread.ListenerDp() {
                public void onFinish(DeploymentPackageImpl dp, Exception exception) {
                    if (null == exception) {
                        entry.setStatus(STATUS_DEPLOYED);
                        pluginCtx.getDeployedPlugin().associateID(dp, entry.id);
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e); 
                        }
                    } else 
                        entry.setStatus(STATUS_DEPLOYMENT_FAILED);
                    DeplAlertSender.sendAlert(exception, principal, correlator, nodeUri, 
                            pluginCtx.getDmtAdmin());
                }});
            deplThr.setBundleListener(new DeploymentThread.ListenerBundle() {
                public void onFinish(Bundle b, Exception exception) {
                    if (null == exception) {
                        entry.setStatus(STATUS_DEPLOYED);
                        pluginCtx.getDeployedPlugin().associateID(b, entry.id);
                        try {
                            pluginCtx.save();
                        }
                        catch (IOException e) {
                            pluginCtx.getLogger().log(e); 
                        }
                    } else 
                        entry.setStatus(STATUS_DEPLOYMENT_FAILED);
                    DeplAlertSender.sendAlert(exception, principal, correlator, nodeUri,
                            pluginCtx.getDmtAdmin());
                }});
            deplThr.start();
        }

        private void sendDownloadAlert(int alertCode, String principal, String correlator, String nodeUri) {
            if (null == principal)
                return;

            try {
                pluginCtx.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] {
                        new DmtAlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
                        null, new DmtData(alertCode))});
            }
            catch (DmtException e) {
                // TODO log
                e.printStackTrace();
            }
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // DMT methods

	public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
	}

	public boolean supportsAtomic() {
		return false;
	}

	public void rollback() throws DmtException {
	}

	public void commit() throws DmtException {
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 6)
            throw new RuntimeException("Internal error");
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        if (nodeUriArr[5].equals("ID"))
            entries.get(nodeUriArr[4]).id = data.getString();            
        if (nodeUriArr[5].equals("URI"))
            entries.get(nodeUriArr[4]).uri = data.getString();
        if (nodeUriArr[5].equals("EnvType"))
            entries.get(nodeUriArr[4]).envType = data.getString();
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
    }

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
	}

	public void deleteNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        entries.remove(nodeUriArr[4]);
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        if (entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUri, DmtException.NODE_ALREADY_EXISTS, "");
        entries.add(nodeUriArr[4]);
        try {
            pluginCtx.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.DATA_STORE_FAILURE, 
                    "Changes cannot be persisted", e); 
        }
	}

	public void createInteriorNode(String nodeUri, String type) throws DmtException {
	    createInteriorNode(nodeUri);
	}

	public void createLeafNode(String nodeUri) throws DmtException {
	}

	public void createLeafNode(String nodeUri, DmtData value) throws DmtException {
	}

	public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
	}

	public void copy(String nodeUri, String newNodeUri, boolean recursive) throws DmtException {
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
	}

	public void close() throws DmtException {
	}

	public boolean isNodeUri(String nodeUri) {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
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

	public boolean isLeafNode(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
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

	public DmtData getNodeValue(String nodeUri) throws DmtException {
	    String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
		if (l == 6) {
		    if (nodeUriArr[5].equals("ID"))
		        return new DmtData(entries.get(nodeUriArr[4]).id);
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

	public String getNodeTitle(String nodeUri) throws DmtException {
		return null;
	}

	public String getNodeType(String nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		return 0;
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		return null;
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		DmtData data = getNodeValue(nodeUri);
		return data.getSize();
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
        	return entries.keys();
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        if (l == 5)
            return new String[] {"ID", "URI", "EnvType", "Status", "Operations"};
        if (l == 6)
            return new String[] {"DownloadAndInstallAndActivate"};
        
        throw new RuntimeException("Internal error");
	}

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE);
        if (l == 5)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.DYNAMIC, "", Integer.MAX_VALUE, Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD).
                    orOperation(DmtMetaNode.CMD_DELETE);
        if (l == 6) {
            if (nodeUriArr[5].equals("ID"))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("URI"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("EnvType"))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
                        DmtMetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
                        0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("Status"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
            if (nodeUriArr[5].equals("Operations"))
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
    					DmtMetaNode.AUTOMATIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_NODE);
        }
        if (l == 7) {
            return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NULL).orOperation(DmtMetaNode.CMD_EXECUTE);
        }
        
        throw new RuntimeException("Internal error");
	}

	public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 7)
            throw new RuntimeException("Internal error");
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        String envType = entries.get(nodeUriArr[4]).envType;
        if (null == envType || !envType.equals("OSGi.R4"))
            throw new DmtException(nodeUri, DmtException.COMMAND_FAILED, "EnvType has to " +
                    "be 'OSGi.R4'");
        String id = entries.get(nodeUriArr[4]).id;
        if (null == id || id.trim().length() == 0)
            throw new DmtException(nodeUri, DmtException.COMMAND_FAILED, "ID has to " +
                    "be set");
        
        entries.start(nodeUri, correlator, session.getPrincipal());
	}
    
    public void nodeChanged(String nodeUri) throws DmtException {
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Private methods
    
    public void setPluginCtx(PluginCtx pluginCtx) {
        this.pluginCtx = pluginCtx;
    }

}

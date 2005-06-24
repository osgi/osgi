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

    private DeploymentAdminImpl da;
    
    private PluginDeployed pluginDeployed;
	private PluginDownload pluginDownload;
        
    public Plugin(DeploymentAdminImpl da) {
        this.da = da;
        pluginDeployed = new PluginDeployed(da);
        pluginDownload = new PluginDownload(da);
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
    	String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 4)
			throw new RuntimeException("Internal error");
		if (nodeUriArr[3].equals("Download"))
			pluginDownload.createInteriorNode(nodeUri);
		if (nodeUriArr[3].equals("Inventory")) {
			if (nodeUriArr[4].equals("Delivered"))
				; // TODO
			if (nodeUriArr[4].equals("Deployed"))
				pluginDeployed.createInteriorNode(nodeUri);
		}

		throw new RuntimeException("Internal error");
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
        if (l < 3)
        	throw new RuntimeException("Internal error");
        if (!nodeUriArr[2].equals("Deployment"))
            return false;
        if (l == 3)
        	return true;
        if (!nodeUriArr[3].equals("Inventory") &&
        	!nodeUriArr[3].equals("Download"))
        		return false;
        if (l == 4)
        	return true;
        if (nodeUriArr[3].equals("Inventory")) {
        	if (!nodeUriArr[4].equals("Deployed") &&
                !nodeUriArr[4].equals("Delivered"))
                	return false;
        	if (l == 5)
        		return true;
        	if (nodeUriArr[4].equals("Deployed"))
        		return pluginDeployed.isNodeUri(nodeUri);
        	if (nodeUriArr[4].equals("Delivered"))
        		return false; // TODO
        }
        if (nodeUriArr[3].equals("Download"))
        	return pluginDownload.isNodeUri(nodeUri);

        return false;
    }

    public boolean isLeafNode(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 3)
			throw new RuntimeException("Internal error");
		if (l == 3)
			return false;
		if (nodeUriArr[3].equals("Download"))
			return pluginDownload.isLeafNode(nodeUri);
		if (nodeUriArr[3].equals("Inventory")) {
			if (l == 4)
				return false;
			if (nodeUriArr[4].equals("Delivered"))
				return false; // TODOD
			if (nodeUriArr[4].equals("Deployed"))
				return pluginDeployed.isLeafNode(nodeUri);
		}

		throw new RuntimeException("Internal error");
	}

    public DmtData getNodeValue(String nodeUri) throws DmtException {
    	String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 4)
			throw new RuntimeException("Internal error");
		if (nodeUriArr[3].equals("Download"))
			return pluginDownload.getNodeValue(nodeUri);
		if (nodeUriArr[3].equals("Inventory")) {
			if (l == 4)
				throw new RuntimeException("Internal error");
			if (nodeUriArr[4].equals("Delivered"))
				return null; // TODOD
			if (nodeUriArr[4].equals("Deployed"))
				return pluginDeployed.getNodeValue(nodeUri);
		}

		throw new RuntimeException("Internal error");
    }

    public String getNodeTitle(String nodeUri) throws DmtException {
        return null;
    }

    public String getNodeType(String nodeUri) throws DmtException {
    	return null;
        // TODO
    }

    public int getNodeVersion(String nodeUri) throws DmtException {
    	return -1;
    	// TODO
    }

    public Date getNodeTimestamp(String nodeUri) throws DmtException {
    	return null;
        // TODO
    }

    public int getNodeSize(String nodeUri) throws DmtException {
        // TODO
    	return -1;
    }

    public String[] getChildNodeNames(String nodeUri) throws DmtException {
    	String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 3)
			throw new RuntimeException("Internal error");
		if (l == 3)
			return new String[] {"Inventory", "Download"};
		if (nodeUriArr[3].equals("Download"))
			return pluginDownload.getChildNodeNames(nodeUri);
		if (nodeUriArr[3].equals("Inventory")) {
			if (l == 4)
				return new String[] {"Delivered", "Deployed"};
			if (nodeUriArr[4].equals("Delivered"))
				return null; // TODOD
			if (nodeUriArr[4].equals("Deployed"))
				return pluginDeployed.getChildNodeNames(nodeUri);
		}

		throw new RuntimeException("Internal error");
    }

    public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
    	String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 3)
			throw new RuntimeException("Internal error");
		if (l == 3)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.PERMANENT,
                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
		if (nodeUriArr[3].equals("Download"))
			return pluginDownload.getMetaNode(nodeUri);
		if (nodeUriArr[3].equals("Inventory")) {
			if (l == 4)
				return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF, DmtMetaNode.PERMANENT,
	                    "", 1, !Metanode.ZERO_OCC, null, 0, 0, null, DmtData.FORMAT_NODE);
			if (nodeUriArr[4].equals("Delivered"))
				return null; // TODOD
			if (nodeUriArr[4].equals("Deployed"))
				return pluginDeployed.getMetaNode(nodeUri);
		}

		throw new RuntimeException("Internal error");
    }

    public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
    	String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
		int l = nodeUriArr.length;
		if (l < 5)
			throw new RuntimeException("Internal error");
		if (nodeUriArr[3].equals("Download"))
			pluginDownload.execute(session, nodeUri, correlator, data);
		if (nodeUriArr[3].equals("Inventory")) {
			if (nodeUriArr[4].equals("Delivered"))
				; // TODOD
			if (nodeUriArr[4].equals("Deployed"))
				pluginDeployed.execute(session, nodeUri, correlator, data);
		}

		throw new RuntimeException("Internal error");
    }

}

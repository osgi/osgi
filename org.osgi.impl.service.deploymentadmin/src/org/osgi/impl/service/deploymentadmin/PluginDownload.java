package org.osgi.impl.service.deploymentadmin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.api.DownloadAgent;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PluginDownload extends DefaultHandler implements DmtDataPlugin, DmtExecPlugin {
    
    // download and deployment states
    public static final int STATUS_IDLE                   = 10;
    public static final int STATUS_DOWNLOAD_FAILED        = 20;
    public static final int STATUS_DOWNLOAD_PROGRESSING   = 30;
    public static final int STATUS_DOWNLOAD_COMPLETE      = 40;
    public static final int STATUS_STARTING_DPLOYMENT     = 50;
    public static final int STATUS_DEPLOYMENT_PROGRESSING = 60;
    public static final int STATUS_DEPLOYMENT_FAILED      = 70;
    public static final int STATUS_DEPLOYMENT_SUCCESSFUL  = 80;
    
	private DeploymentAdminImpl da;

	// used for XML parsing
	private String              actElement;
	private StringBuffer        contentURI;
	
	// TODO allow more than one download
	private String              nodeId = "";
	private String				id;
	private String   			uri;
	private int 				status = STATUS_IDLE;
	
	PluginDownload(DeploymentAdminImpl da) {
		this.da = da;		
	}
	
    ///////////////////////////////////////////////////////////////////////////
    // Parser methods
	
	private SAXParser getParser() throws Exception {
		ServiceReference refs[] = da.getBundleContext().getServiceReferences(
				SAXParserFactory.class.getName(),
				"(&(parser.namespaceAware=true)" + "(parser.validating=true))");
		if (refs == null)
			return null;
		SAXParserFactory factory = (SAXParserFactory) da.getBundleContext()
				.getService(refs[0]);
		SAXParser parser = factory.newSAXParser();
		return parser;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException 
	{
		actElement = localName;
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException 
	{
	    if (actElement.equals("objectURI")) {
	        if (null == contentURI)
	            contentURI = new StringBuffer();
	        String s = new String(ch, start, length).trim();
	        contentURI.append(s);
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
        if (nodeUriArr[5].equals("ID"))
            id = data.getString();            
        if (nodeUriArr[5].equals("URI"))
            uri = data.getString();
    }

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
	}

	public void setNodeType(String nodeUri, String type) throws DmtException {
	}

	public void deleteNode(String nodeUri) throws DmtException {
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        nodeId = nodeUriArr[4];
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
        if (!nodeUriArr[4].equals(nodeId))
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
		        return new DmtData(id);
		    if (nodeUriArr[5].equals("URI"))
		        return new DmtData(uri);
		    if (nodeUriArr[5].equals("EnvType"))
		        return new DmtData("OSGi.R4");
		    if (nodeUriArr[5].equals("Status"))
		        return new DmtData(status);
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
        	return new String[] {nodeId};
        if (!nodeUriArr[4].equals(nodeId))
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
					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD);
        if (l == 6) {
            if (nodeUriArr[5].equals("ID"))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("URI"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("EnvType"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
		    if (nodeUriArr[5].equals("Status"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING);
            if (nodeUriArr[5].equals("Operations"))
                return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
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
	    status = STATUS_DEPLOYMENT_PROGRESSING;
	    
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 7)
            throw new RuntimeException("Internal error");
        if (!nodeUriArr[4].equals(nodeId))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        
        // parse the DLOTA descriptor
        parseDescriptor(nodeUri);

        // install content
        downloadAndInstall(nodeUri);
	}

    private void downloadAndInstall(String nodeUri) throws DmtException {
        InputStream is = null;
        DownloadAgent dwnl = da.getDownloadAgent();
        if (null == dwnl)
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                "Download Agent service is not available");
            
        // TODO states doesn't describe streaming
        try {
            Hashtable props = new Hashtable();
            props.put("url", contentURI.toString());
            is = dwnl.download("url", props);
            status = STATUS_DEPLOYMENT_SUCCESSFUL;
            da.installDeploymentPackage(is);
            status = STATUS_DEPLOYMENT_SUCCESSFUL;
        }
        catch (DeploymentException e) {
            status = STATUS_DEPLOYMENT_FAILED;
        }
        catch (Exception e) {
            status = STATUS_DOWNLOAD_FAILED;
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "");
        } finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                
            }
        }
    }

    private void parseDescriptor(String nodeUri) throws DmtException {
        DownloadAgent dwnl = da.getDownloadAgent();
        if (null == dwnl)
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                "Download Agent service is not available");
        
        InputStream is = null;
        try {
            Hashtable props = new Hashtable();
            props.put("url", uri);
            is = dwnl.download("url", props);
            SAXParser p = getParser();
            p.parse(is, this);
        }
        catch (Exception e) {
            status = STATUS_DOWNLOAD_FAILED;
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "");
        }
        finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }

            }
        }
    }

}

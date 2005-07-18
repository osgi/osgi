package org.osgi.impl.service.deploymentadmin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.api.DownloadAgent;
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
    public static final int STATUS_DOWNLD_FAILED          = 20;
    public static final int STATUS_STREAMING              = 50;
    public static final int STATUS_DEPLOYMENT_FAILED      = 70;
    public static final int STATUS_DEPLOYED               = 80;
    
	private DeploymentAdminImpl da;

	// used for XML parsing
	private String       actElement;
	private StringBuffer contentURI;
    private StringBuffer contentType;
	private Entries      entries = new Entries();
    
    PluginDownload(DeploymentAdminImpl da) {
        this.da = da;       
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // Private classes
    
    private class Entry {
        private String id;
        private String uri;
        private String envType;
        private int    status = STATUS_IDLE;
    }
    
    private class Entries {
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
	    } else if (actElement.equals("type")) {
            if (null == contentType)
                contentType = new StringBuffer();
            String s = new String(ch, start, length).trim();
            contentType.append(s);
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
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 5)
            throw new RuntimeException("Internal error");
        entries.add(nodeUriArr[4]);
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
                return new DmtData(entries.get(nodeUriArr[4]).status);
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
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("URI"))
		        return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
    					DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
    					0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
		    if (nodeUriArr[5].equals("EnvType"))
                return new Metanode(DmtMetaNode.CMD_GET, Metanode.IS_LEAF,
                        DmtMetaNode.DYNAMIC, "", 1, !Metanode.ZERO_OCC, null, 0,
                        0, null, DmtData.FORMAT_STRING).orOperation(DmtMetaNode.CMD_REPLACE);
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
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l != 7)
            throw new RuntimeException("Internal error");
        if (!entries.contains(nodeUriArr[4]))
            throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND, "");
        String envType = entries.get(nodeUriArr[4]).envType;
        if (null == envType || !envType.equals("OSGi.R4"))
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "EnvType has to " +
                    "be 'OSGi.R4'");
        String id = entries.get(nodeUriArr[4]).id;
        if (null == id || id.trim().length() == 0)
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "ID has to " +
                    "be set");
        
        entries.get(nodeUriArr[4]).status = STATUS_STREAMING;
        
        // parse the DLOTA descriptor
        initParser();
        try {
            parseDescriptor(nodeUri);
            Set allowed = new HashSet();
            allowed.add("application/java-archive");
            allowed.add("application/vnd.osgi.dp");
            if (!allowed.contains(contentType.toString().trim()))
                throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                        contentType + " MIME type is not supported");
        } catch (DmtException e) {
            entries.get(nodeUriArr[4]).status = STATUS_DOWNLD_FAILED;
            throw e;
        }

        // install content
        try{
            downloadAndInstall(nodeUri);
        } catch (DmtException e) {
            entries.get(nodeUriArr[4]).status = STATUS_DEPLOYMENT_FAILED;
            throw e;
        }
        
        entries.get(nodeUriArr[4]).status = STATUS_DEPLOYED;
	}
    
    private void initParser() {
        contentType = null;
        contentURI = null;
    }

    public void nodeChanged(String nodeUri) throws DmtException {
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Private methods

    private void downloadAndInstall(String nodeUri) throws DmtException {
        InputStream is = null;
        DownloadAgent dwnl = da.getDownloadAgent();
        if (null == dwnl)
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                "Download Agent service is not available");
        try {
            Hashtable props = new Hashtable();
            props.put("url", contentURI.toString());
            is = dwnl.download("url", props);
            da.installDeploymentPackage(is);
        } catch (Exception e) {
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
        
        String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        InputStream is = null;
        try {
            Hashtable props = new Hashtable();
            props.put("url", entries.get(nodeUriArr[4]).uri);
            is = dwnl.download("url", props);
            SAXParser p = getParser();
            p.parse(is, this);
        }
        catch (Exception e) {
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

package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.deploymentadmin.DeploymentAdminImpl;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageImpl;
import org.osgi.impl.service.deploymentadmin.Metanode;
import org.osgi.impl.service.deploymentadmin.Splitter;
import org.osgi.impl.service.deploymentadmin.api.DownloadAgent;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.dmt.DmtAlertItem;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PluginDownload extends DefaultHandler 
        implements DmtDataPlugin, DmtExecPlugin, Serializable 
{
    
    // download and deployment states
    private static final int STATUS_IDLE                   = 10;
    private static final int STATUS_DOWNLD_FAILED          = 20;
    private static final int STATUS_STREAMING              = 50;
    private static final int STATUS_DEPLOYMENT_FAILED      = 70;
    private static final int STATUS_DEPLOYED               = 80;
    
    // generic alert results
    private static final int RESULT_SUCCESSFUL             = 200;
    private static final int RESULT_BUNDLE_START_WARNING   = 250;
    private static final int RESULT_REQUEST_TIMED_OUT      = 406;
    private static final int RESULT_UNDEFINED_ERROR        = 407;
    private static final int RESULT_DWNLD_DESCR_ERROR      = 410;
    private static final int RESULT_ORDER_ERROR            = 450;
    private static final int RESULT_MISSING_HEADER         = 451;
    private static final int RESULT_BAD_HEADER             = 452;
    private static final int RESULT_MISSING_FIXPACK_TARGET = 453;
    private static final int RESULT_MISSING_BUNDLE         = 454;
    private static final int RESULT_MISSING_RESOURCE       = 455;
    private static final int RESULT_SIGNING_ERROR          = 456;
    private static final int RESULT_BUNDLE_NAME_ERROR      = 457;
    private static final int RESULT_FOREIGN_CUSTOMIZER     = 458;
    private static final int RESULT_NO_SUCH_RESOURCE       = 459;
    private static final int RESULT_BUNDLE_SHARING_VIOLATION = 460;
    private static final int RESULT_CODE_RESOURCE_SHARING_VIOLATION = 461;
    
	private transient DeploymentAdminImpl da;

	// used for XML parsing
	private transient String       actElement;
	private transient StringBuffer contentURI;
    private transient StringBuffer contentType;
    
	private Entries      entries = new Entries();
    
    public PluginDownload(DeploymentAdminImpl da) {
        this.da = da;       
    }
	
    ///////////////////////////////////////////////////////////////////////////
    // Private classes
    
    private class Entry implements Serializable {
        private String  id;
        private String  uri;
        private String  envType;
        private Integer status = new Integer(STATUS_IDLE);
        
        private transient Thread thread;

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
        
        private void start(String nodeUri, String correlator, String principal) {
            String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
            Entry entry = (Entry) ht.get(nodeUriArr[4]);
            entry.thread = new Thread(new DownloadThread(nodeUri, correlator, principal));
            entry.thread.start();
        }
    }
    
    private class DownloadThread implements Runnable {
        private String               nodeUri;
        private String[]             nodeUriArr;
        private String               correlator;
        private String               principal;
        
        private DownloadThread(String nodeUri, String correlator, String principal) {
            this.nodeUri =  nodeUri;
            nodeUriArr = Splitter.split(nodeUri, '/', 0);
            this.principal = principal;
        }
        
        public void run() {
            entries.get(nodeUriArr[4]).setStatus(STATUS_STREAMING);
            
            // parse the DLOTA descriptor
            try {
                initParser();
                parseDescriptor(nodeUri);
                Set allowed = new HashSet();
                allowed.add("application/java-archive");
                allowed.add("application/vnd.osgi.dp");
                if (!allowed.contains(contentType.toString().trim()))
                    throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                            contentType + " MIME type is not supported");
            } catch (Exception e) {
                entries.get(nodeUriArr[4]).setStatus(STATUS_DOWNLD_FAILED);
                sendAlertWithException(correlator, nodeUri, principal, e);
                // TODO log
                e.printStackTrace();
                return;
            }

            InputStream is = null;
            try {
                is = getStream(nodeUri);
            } catch (Exception e) {
                if (null != is) {
                    try {
                        is.close();
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                entries.get(nodeUriArr[4]).setStatus(STATUS_DOWNLD_FAILED);
                sendAlertWithException(correlator, nodeUri, principal, e);
                // TODO log
                e.printStackTrace();
                return;
            }
            
            // deploy content
            DeploymentPackageImpl dp = null;
            try {
                dp = (DeploymentPackageImpl) da.installDeploymentPackage(is);
            } catch (DeploymentException e) {
                sendAlertWithException(correlator, nodeUri, principal,e);
                entries.get(nodeUriArr[4]).setStatus(STATUS_DEPLOYMENT_FAILED);
                // TODO log
                e.printStackTrace();
                return;
            } finally {
                if (null != is)
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            entries.get(nodeUriArr[4]).setStatus(STATUS_DEPLOYED);
            da.getDeployedPlugin().associateID(dp, entries.get(nodeUriArr[4]).id);
            try {
                da.save();
            }
            catch (IOException e) {
                e.printStackTrace();
                // TODO log
            }
            
            try {
                if (null != principal)
                    da.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] { 
                        new DmtAlertItem(nodeUri, 
                        "org.osgi.deployment.downloadandinstallandactivate", 
                        null, new DmtData(RESULT_SUCCESSFUL))});
            }
            catch (DmtException e) {
                // TODO log
                e.printStackTrace();
            }
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
	
	public void characters(char[] ch, int start, int length) throws SAXException {
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
        try {
            da.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Changes cannot be " +
                    "persisted", e); 
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
            da.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Changes cannot be " +
                    "persisted", e); 
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
            da.save();
        }
        catch (IOException e) {
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "Changes cannot be " +
                    "persisted", e); 
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
        
        entries.start(nodeUri, correlator, session.getPrincipal());
	}
    
    private void initParser() {
        contentType = null;
        contentURI = null;
    }

    public void nodeChanged(String nodeUri) throws DmtException {
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Private methods

    private void sendAlertWithException(String correlator, String nodeUri,
            String principal, Exception exception)  
    {
        if (null == principal)
            return;
        
        int result_code = -1;
        if (exception instanceof DmtException) {
            DmtException e = (DmtException) exception;
            switch (e.getCode()) {
                case DeploymentException.CODE_BUNDLE_START :
                    result_code = RESULT_BUNDLE_START_WARNING;
                    break;
                case DeploymentException.CODE_TIMEOUT :
                    result_code = RESULT_REQUEST_TIMED_OUT;
                    break;
                case DeploymentException.CODE_ORDER_ERROR :
                    result_code = RESULT_ORDER_ERROR;
                    break;
                case DeploymentException.CODE_MISSING_HEADER:
                    result_code = RESULT_MISSING_HEADER;
                    break;
                case DeploymentException.CODE_BAD_HEADER :
                    result_code = RESULT_BAD_HEADER;
                    break;
                case DeploymentException.CODE_MISSING_FIXPACK_TARGET :
                    result_code = RESULT_MISSING_FIXPACK_TARGET;
                    break;
                case DeploymentException.CODE_MISSING_BUNDLE :
                    result_code = RESULT_MISSING_BUNDLE;
                    break;
                case DeploymentException.CODE_MISSING_RESOURCE :
                    result_code = RESULT_MISSING_RESOURCE;
                    break;
                case DeploymentException.CODE_SIGNING_ERROR :
                    result_code = RESULT_SIGNING_ERROR;
                    break;
                case DeploymentException.CODE_BUNDLE_NAME_ERROR :
                    result_code = RESULT_BUNDLE_NAME_ERROR;
                    break;
                case DeploymentException.CODE_FOREIGN_CUSTOMIZER :
                    result_code = RESULT_FOREIGN_CUSTOMIZER;
                    break;
                case DeploymentException.CODE_NO_SUCH_RESOURCE :
                    result_code = RESULT_NO_SUCH_RESOURCE;
                    break;
                case DeploymentException.CODE_BUNDLE_SHARING_VIOLATION :
                    result_code = RESULT_BUNDLE_SHARING_VIOLATION;
                    break;
                case DeploymentException.CODE_RESOURCE_SHARING_VIOLATION :
                    result_code = RESULT_CODE_RESOURCE_SHARING_VIOLATION;
                    break;
                case DeploymentException.CODE_OTHER_ERROR :
                    result_code = RESULT_UNDEFINED_ERROR;
                    break;
                default :
                    break;
            }
        } else if (exception instanceof SAXException){
            result_code = RESULT_DWNLD_DESCR_ERROR;
        }
        try {
            da.getDmtAdmin().sendAlert(principal, 1226, correlator, new DmtAlertItem[] {
                    new DmtAlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
                    null, new DmtData(result_code))});
        }
        catch (DmtException ex) {
            // TODO log
            ex.printStackTrace();
        }
    }
    
    private InputStream getStream(String nodeUri) throws DmtException {
        InputStream is = null;
        DownloadAgent dwnl = da.getDownloadAgent();
        if (null == dwnl)
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, 
                "Download Agent service is not available");
        try {
            Hashtable props = new Hashtable();
            props.put("url", contentURI.toString());
            is = dwnl.download("url", props);
            return is;
        }
        catch (Exception e) {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                
            }
            throw new DmtException(nodeUri, DmtException.OTHER_ERROR, "", e);
        }
    }

    private void parseDescriptor(String nodeUri) throws DmtException, SAXException {
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
        } catch (SAXException e) {
            throw e;
        } catch (Exception e) {
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

    public void setDeploymentAdmin(DeploymentAdminImpl da) {
        this.da = da;
    }

}

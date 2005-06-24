package org.osgi.impl.service.deploymentadmin;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
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

	private DeploymentAdminImpl da;
	private String              nodeId = "";

	PluginDownload(DeploymentAdminImpl da) {
		this.da = da;		
	}

	private SAXParser getParser(BundleContext context) throws Exception {
		ServiceReference refs[] = context.getServiceReferences(
				SAXParserFactory.class.getName(),
				"(&(parser.namespaceAware=true)" + "(parser.validating=true))");
		if (refs == null)
			return null;
		SAXParserFactory factory = (SAXParserFactory) context
				.getService(refs[0]);
		SAXParser parser = factory.newSAXParser();
		return parser;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException 
	{
		System.out.println(">" + localName + "" + qName);
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException 
	{
		System.out.println("<" + localName + "" + qName);
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException 
	{
		String s = new String(ch, start, length).trim();
		System.out.println(" '" + s + "'");
	}

	public void open(String subtreeUri, int lockMode, DmtSession session) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public boolean supportsAtomic() {
		// TODO Auto-generated method stub
		return false;
	}

	public void rollback() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void commit() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		// TODO Auto-generated method stub
		
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
        if (l != 5)
            throw new RuntimeException("Internal error");
        nodeId = nodeUriArr[4];
        // TODO
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
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (!"Download".equals(nodeUriArr[3]))
         	return false;
        if (l == 4)
            return true;
        if (!nodeUriArr[4].equals(nodeUri))
        	return false;
        // TODO
        
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
        // TODO
        
        throw new RuntimeException("Internal error");
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeType(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] nodeUriArr = Splitter.split(nodeUri, '/', 0);
        int l = nodeUriArr.length;
        if (l < 4)
            throw new RuntimeException("Internal error");
        if (l == 4)
        	return new String[] {nodeId};
        // TODO
        
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
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD);
        if (l == 5)
			return new Metanode(DmtMetaNode.CMD_GET, !Metanode.IS_LEAF,
					DmtMetaNode.PERMANENT, "", 1, !Metanode.ZERO_OCC, null, 0,
					0, null, DmtData.FORMAT_NODE).orOperation(DmtMetaNode.CMD_ADD);
        
        throw new RuntimeException("Internal error");
	}

	public void execute(DmtSession session, String nodeUri, String correlator, String data) throws DmtException {
		// TODO Auto-generated method stub
		
	}

}

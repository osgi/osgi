package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import java.util.Date;
import java.util.Iterator;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.Uri;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

public class GenericDataPlugin implements DataPlugin, TransactionalDataSession {
	
	public static final int ACTION_SET_NODE_VALUE = 1;
	
	public String pluginID;
	public int lastAction;
	public String lastUri;
	public Object lastValue;
	
	private Node root;
	
	
	public GenericDataPlugin( String pluginID, Node root ) {
		this.pluginID = pluginID;
		this.root = root;
	}
	
	/*
	 * non optimized version of a find node mechanims
	 */
	private Node findNode( Node start, String uri ) {
		if ( uri.equals(start.getURI() ))
			return start;
		Iterator iterator = start.getChildren().iterator();
		while (iterator.hasNext()) {
			Node found = findNode((Node) iterator.next(), uri);
			if ( found != null )
				return found;
		}
		return null;
	}
	
	private Node findNode( Node start, String[] nodePath ) {
		return findNode(start, Uri.toUri(nodePath));
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		return this;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		return this;
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		return this;
	}

	
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		this.lastAction = ACTION_SET_NODE_VALUE;
		this.lastUri = Uri.toUri(nodePath);
		this.lastValue = data;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		Node n = findNode(root, nodePath);
		return n != null ? n.getMetaNode() : null;
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		Node n = findNode(root, nodePath);
		return n != null ? n.getTitle() : null;
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNodeUri(String[] nodePath) {
		String uri = Uri.toUri(nodePath);
		return findNode(root, uri) != null;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		Node n = findNode(root, nodePath );
		return n != null ? n.getChildren().size() == 0 : false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void commit() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public void rollback() throws DmtException {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args ) {
		Node n1 = new Node(null, ".", "root" );
		Node n2 = new Node(n1, "A", "node A");
		Node n4 = new Node(n1, "A2", "node A2");

		Node n3 = new Node(n2, "B", "node B");
		GenericDataPlugin gdp = new GenericDataPlugin("P1", n1);
		System.out.println( gdp.findNode(n1, "./A/B" ) );
	}

}

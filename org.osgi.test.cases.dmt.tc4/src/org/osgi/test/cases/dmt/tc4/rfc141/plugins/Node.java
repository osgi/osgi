package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import java.util.Iterator;
import java.util.TreeSet;

public class Node implements Comparable {
	
	private String title;
	private String value;
	private MetaNode metaNode;
	
	private Node parent;
	private TreeSet children;

	public Node( Node parent, String title, String value ) {
		this.parent = parent;
		this.title = title;
		this.value = value;
		if ( parent != null ) {
			parent.addChild(this);
		}
	}
	
	public String getURI() {
		return getPath();
	}
	
	private String getPath() {
		String path = title;
		if ( parent != null )
			path = parent.getPath() + "/" + path;
		return path;
	}
	
	private Node getChildNode( String name ) {
		Iterator iterator = getChildren().iterator();
		Node node = null;
		while ( iterator.hasNext() ) {
			Node n = (Node) iterator.next();
			if ( name.equals( n.getTitle() ))
				node = n;
		}
		return node;
	}

	void addChild( Node node ) {
		getChildren().add(node);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MetaNode getMetaNode() {
		return metaNode;
	}

	public void setMetaNode(MetaNode metaNode) {
		this.metaNode = metaNode;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public TreeSet getChildren() {
		if ( children == null )
			children = new TreeSet();
		return children;
	}

	
	public String toString() {
		return getURI();
	}

	public static void main(String[] args ) {
		Node n1 = new Node(null, ".", "root" );
		Node n2 = new Node(n1, "A", "node A");
		Node n3 = new Node(n2, "B", "node B");
		System.out.println( n1.getPath() );
		System.out.println( n2.getPath() );
		System.out.println( n3.getPath() );
	}

	public int compareTo(Object o) {
		if ( o == null || ! (o instanceof Node) )
			return -1; 
		return this.getURI().compareTo(((Node)o).getURI());
	}
}

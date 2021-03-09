/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.residentialmanagement.plugins;

import java.util.TreeSet;

/**
 * @author steffen
 *
 */
public class Node implements Comparable<Node> {
	
	private String name;
	private String value;
	private MetaNode metaNode;
	private String title;
	

	private Node parent;
	private TreeSet<Node> children;

	/**
	 * @param parent
	 * @param name
	 * @param value
	 */
	public Node( Node parent, String name, String value ) {
		this.parent = parent;
		this.name = name;
		this.value = value;
		if ( parent != null ) {
			parent.addChild(this);
		}
	}
	
	/**
	 * @return
	 */
	public String getURI() {
		return getPath();
	}
	
	private String getPath() {
		String path = name;
		if ( parent != null )
			path = parent.getPath() + "/" + path;
		return path;
	}
	
	@SuppressWarnings("unused")
	private Node getChildNode( String name ) {
		Node node = null;
		for ( Node child : getChildren() )
			if ( name.equals( child.getName() ))
				node = child;
		return node;
	}

	void addChild( Node node ) {
		getChildren().add(node);
	}

	void removeChild( Node node ) {
		getChildren().remove(node);
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public MetaNode getMetaNode() {
		return metaNode;
	}

	/**
	 * @param metaNode
	 */
	public void setMetaNode(MetaNode metaNode) {
		this.metaNode = metaNode;
	}

	/**
	 * @return
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * @return
	 */
	public TreeSet<Node> getChildren() {
		if ( children == null )
			children = new TreeSet<Node>();
		return children;
	}

	
	@Override
	public String toString() {
		return getURI();
	}

	public static void main(String[] args ) {
//		Node n1 = new Node(null, ".", "root" );
		Node n2 = new Node(null, "A", "node A");
		Node n3 = new Node(n2, "B", "node B");
//		System.out.println( n1.getPath() );
		System.out.println( n2.getPath() );
		System.out.println( n3.getPath() );
	}

	@Override
	public int compareTo(Node o) {
		if (o == null)
			return -1; 
		return this.getURI().compareTo(o.getURI());
	}
	
	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}

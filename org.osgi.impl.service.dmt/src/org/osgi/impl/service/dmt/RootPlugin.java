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
package org.osgi.impl.service.dmt;

import java.util.Date;
import org.osgi.service.dmt.*;

// TODO implement leaf nodes as well if needed
public class RootPlugin implements DmtReadOnlyDataPlugin {
	// TODO find the missing parts of the "main" tree
	private static Node	root	= 
        new Node(".", new Node[] {new Node("OSGi", new Node[] {
			new Node("Policies", new Node[] {new Node("Java", 
                    new Node[] { new Node("Bundle", null),
			                     new Node("DmtPrincipal", null),
                                 new Node("ConditionalPermission", null)
            })}),
			new Node("applications", null),
			new Node("application_instances", null),
			new Node("application_containers", null),
			new Node("content_handlers", null), new Node("cfg", null),
			new Node("log", null), new Node("mon", null),
			new Node("deploy", null)	})});

	//----- DmtReadOnlyDataPlugin methods -----//
	public void open(DmtSession session) throws DmtException {
	}

	public void open(String subtreeUri, DmtSession session) throws DmtException {
		findNode(subtreeUri); // check that the node exists
		open(session);
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		findNode(nodeUri); // check that the node exists
		return new DmtMetaNodeImpl(); // TODO return different info for different nodes
	}

	//----- DmtReadOnly methods -----//
	public void close() throws DmtException {
		// TODO
	}

	public boolean isNodeUri(String nodeUri) {
		try {
			findNode(nodeUri);
			return true;
		}
		catch (DmtException e) {
			return false;
		}
	}
    
    public boolean isLeafNode(String nodeUri) throws DmtException {
        findNode(nodeUri); // check that the node exists
        return false; // currently all nodes are internal
    }

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		// should never be called because all nodes are internal
		return null;
	}

	public String getNodeTitle(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property not supported.");
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		// TODO
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// should never be called because all nodes are internal
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		Node[] childNodes = findNode(nodeUri).getChildren();
		String[] childNames = new String[childNodes.length];
		for (int i = 0; i < childNodes.length; i++)
			childNames[i] = childNodes[i].getName();
		return childNames;
	}

	/*
	 * // methods of Dmt, probably not needed here public void rollback() throws
	 * DmtException {}
	 * 
	 * public void setNodeTitle(String nodeUri, String title) throws
	 * DmtException { throw new DmtException(nodeUri,
	 * DmtException.FEATURE_NOT_SUPPORTED, "Title property not supported."); }
	 * 
	 * public void setNode(String nodeUri, DmtData data) throws DmtException {
	 * findNode(nodeUri); // check that the node exists throw new
	 * DmtException(nodeUri, DmtException.OTHER_ERROR, "Specified URI points to
	 * an internal node."); }
	 * 
	 * public void setNodeType(String nodeUri, String type) throws DmtException {
	 * findNode(nodeUri); // check that the node exists throw new
	 * DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Permanent node,
	 * cannot set type."); }
	 * 
	 * public void deleteNode(String nodeUri) throws DmtException {
	 * findNode(nodeUri); // check that the node exists throw new
	 * DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Permanent node,
	 * cannot be deleted."); }
	 * 
	 * public void createInteriorNode(String nodeUri) throws DmtException {
	 * throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Cannot
	 * create nodes."); }
	 * 
	 * public void createInteriorNode(String nodeUri, String type) throws
	 * DmtException { throw new DmtException(nodeUri,
	 * DmtException.COMMAND_NOT_ALLOWED, "Cannot create nodes."); }
	 * 
	 * public void createLeafNode(String nodeUri, DmtData value) throws
	 * DmtException { throw new DmtException(nodeUri,
	 * DmtException.COMMAND_NOT_ALLOWED, "Cannot create nodes."); }
	 * 
	 * public void copy(String nodeUri, String newNodeUri) throws DmtException {
	 * throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Cannot
	 * create nodes."); }
	 * 
	 * public void renameNode(String nodeUri, String newName) throws
	 * DmtException { findNode(nodeUri); // check that the node exists throw new
	 * DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED, "Permanent node,
	 * cannot be renamed."); }
	 */
	private Node findNode(String uri) throws DmtException {
		if (uri.equals("."))
			return root;
		// any other absolute URI must begin with "./"
		Node node = root.findNode(uri.substring(2));
		if (node == null)
			throw new DmtException(uri, DmtException.NODE_NOT_FOUND,
					"Specified URI not found in tree.");
		return node;
	}
}

class Node {
	String	name;
	Node[]	children;

	Node(String name, Node[] children) {
		this.name = name;
		if (children != null)
			this.children = children;
		else
			this.children = new Node[] {};
	}

	Node findNode(String name) {
		if (name.length() == 0)
			return this;
		String first = Utils.firstSegment(name);
		for (int i = 0; i < children.length; i++)
			if (first.equals(children[i].name))
				return children[i].findNode(Utils.lastSegments(name));
		return null;
	}

	String getName() {
		return name;
	}

	Node[] getChildren() {
		return children;
	}
}

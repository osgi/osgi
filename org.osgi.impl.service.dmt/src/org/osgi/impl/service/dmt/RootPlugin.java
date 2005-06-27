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
import java.util.Set;
import org.osgi.service.dmt.*;

public class RootPlugin implements DmtReadOnlyDataPlugin {
	private static Node	root	= 
        new Node(".", new Node[] {
                new Node("OSGi", new Node[] {
                        new Node("Application", null),
                        new Node("Configuration", null), 
                        new Node("Deployment", new Node[] {
                                new Node("Inventory", new Node[] {
                                        new Node("Deployed", null),
                                        new Node("Delivered", null)
                                }),
                                new Node("Download", null)
                        }),
                        new Node("Log", null), 
                        new Node("Monitor", null),
                        new Node("Policy", new Node[] {
                                new Node("Java", new Node[] { 
                                        new Node("LocationPermission", null),
                                        new Node("DmtPrincipalPermission", null),
                                        new Node("ConditionalPermission", null)
                                })
                        })
                })
        });
    
    private DmtPluginDispatcher dispatcher;

    RootPlugin(DmtPluginDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
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
		return new DmtMetaNodeImpl();
	}

	//----- DmtReadOnly methods -----//
	public void close() throws DmtException {}

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
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property not supported.");
	}

	public String getNodeType(String nodeUri) throws DmtException {
		return null;
	}

	public int getNodeVersion(String nodeUri) throws DmtException {
        return 0;
	}

	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new DmtException(nodeUri, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property not supported.");
	}

	public int getNodeSize(String nodeUri) throws DmtException {
		// should never be called because all nodes are internal
		return 0;
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
        Set children = dispatcher.getChildPluginNames(nodeUri);
        
		Node[] childNodes = findNode(nodeUri).getChildren();
		for (int i = 0; i < childNodes.length; i++)
			children.add(childNodes[i].getName());

        return (String[]) children.toArray(new String[] {});
	}

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

package org.osgi.test.cases.dmt.tc4.tb1;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

import java.util.Date;

import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class ReadOnlyDS implements ReadableDataSession {
	private Node framework;
	

	public ReadOnlyDS(Node frameworkNode) {
		framework = frameworkNode;
		framework.open();
	}
	

	public void close() throws DmtException {
		framework.close();
		System.out.println("[Framework Data Session] Closing a Read-Only Session");
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		return findNode(nodePath).getChildNodeNames();
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		return findNode(nodePath).getMetaNode();
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeSize();
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		return findNode(nodePath).getTimestamp();
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		return findNode(nodePath).getTitle();
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeType();
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeValue();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		return findNode(nodePath).getVersion();
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		return findNode(nodePath).isLeaf();
	}

	public boolean isNodeUri(String[] nodePath) {
		return ( findNode(nodePath) == null ) ? false : true;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		findNode(nodePath).nodeChanged();
	}
	
	
	private Node findNode(String[] nodePath) {
		Node node = null;
		boolean nodePathValid = true;
		
		if ( nodePath[0].equals(".") && nodePath[1].equals("OSGi") &&
				nodePath[2].equals(framework.getNodeName()) ) {
			Node currNode = framework;
			
			for ( int inx = 3 ; nodePathValid && inx < nodePath.length ; inx++ ) {
				int foundChildIndex = -1;
				Node[] children = currNode.getChildNodes();
				
				for ( int jnx = 0 ; foundChildIndex < 0 && jnx < children.length ; jnx++ ) {
					if ( nodePath[inx].equals(children[jnx].getNodeName()) ) {
						foundChildIndex = jnx;
					}
				}
				
				if ( foundChildIndex >= 0 ) {
					currNode = children[foundChildIndex];
				} else {
					nodePathValid = false;
				}
			}
			
			if ( nodePathValid ) {
				node = currNode;
			}
		}
		
		return node;
	}
}

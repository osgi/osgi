package org.osgi.test.cases.dmt.tc4.tb1;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadableDataSession;

import java.util.Date;

import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class ReadOnlyDS implements ReadableDataSession {
	private Node framework;
	

	public ReadOnlyDS(Node frameworkNode) {
		framework = frameworkNode;
		framework.open();
	}
	

	@Override
	public void close() throws DmtException {
		framework.close();
		System.out.println("[Framework Data Session] Closing a Read-Only Session");
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		return findNode(nodePath).getChildNodeNames();
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		return findNode(nodePath).getMetaNode();
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeSize();
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		return findNode(nodePath).getTimestamp();
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		return findNode(nodePath).getTitle();
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeType();
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		return findNode(nodePath).getNodeValue();
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		return findNode(nodePath).getVersion();
	}

	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		return findNode(nodePath).isLeaf();
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		return ( findNode(nodePath) == null ) ? false : true;
	}

	@Override
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

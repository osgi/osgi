package org.osgi.test.cases.dmt.tc4.tb1;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadWriteDataSession;

import java.util.Date;

import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class ReadWriteDS implements ReadWriteDataSession {
	private Node framework;
	

	public ReadWriteDS(Node frameworkNode) {
		framework = frameworkNode;
		framework.open();
	}
	

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		findNode(nodePath).setTitle(title);
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
		"Operation is not allowed - static tree");
	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		findNode(nodePath).setNodeValue(data);
	}

	public void close() throws DmtException {
		framework.close();
		System.out.println("[Framework Data Session] Closing a Read-Write Session");
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

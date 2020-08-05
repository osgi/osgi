
package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadableDataSession;

public class TestPluginReadableDataSession implements ReadableDataSession {

	protected final Map<String,TestNode> nodeMap;

	public TestPluginReadableDataSession(Map<String,TestNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getMetaNode();
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		TestNode node = nodeMap.get(Uri.toUri(nodePath));
		return (node != null);
	}

	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.isLeaf();
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		List<String> childNodeNameList = new LinkedList<>();
		String nodeUri = Uri.toUri(nodePath);
		for (Iterator<String> nodeUris = nodeMap.keySet().iterator(); nodeUris
				.hasNext();) {
			String existNodeUril = nodeUris.next();
			if (existNodeUril.startsWith(nodeUri)) {
				String[] existNodePath = Uri.toPath(existNodeUril);
				int existNodePathLength = existNodePath.length;
				if (nodePath.length + 1 == existNodePathLength) {
					String childNodeName = existNodePath[existNodePathLength - 1];
					childNodeNameList.add(childNodeName);
				}
			}
		}
		String[] childNodeNames = new String[childNodeNameList.size()];
		childNodeNameList.toArray(childNodeNames);
		return childNodeNames;
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getTitle();
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getType();
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		DmtData nodeData = node.getValue();
		if (nodeData == null) {
			throw new DmtException(
					nodePath,
					node.isLeaf() ? DmtException.COMMAND_FAILED : DmtException.FEATURE_NOT_SUPPORTED,
					"No value for this node.");
		}
		return nodeData;
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getSize();
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getTimestamp();
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		TestNode node = getNode(nodePath);
		return node.getVersion();
	}

	protected TestNode getNode(String[] nodePath) throws DmtException {
		TestNode node = nodeMap.get(Uri.toUri(nodePath));
		if (node == null) {
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND, "node not found.");
		}
		return node;
	}

	@Override
	public void nodeChanged(String[] nodePath) throws DmtException {
	}

	@Override
	public void close() throws DmtException {
	}
}

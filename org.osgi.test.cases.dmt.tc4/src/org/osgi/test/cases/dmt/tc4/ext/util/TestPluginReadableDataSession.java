package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadableDataSession;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestPluginReadableDataSession implements ReadableDataSession {

    protected final Map nodeMap;

    public TestPluginReadableDataSession(Map nodeMap) {
        this.nodeMap = nodeMap;
    }

    public MetaNode getMetaNode(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getMetaNode();
    }

    public boolean isNodeUri(String[] nodePath) {
        TestNode node = (TestNode) nodeMap.get(Uri.toUri(nodePath));
        return (node != null);
    }

    public boolean isLeafNode(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.isLeaf();
    }

    public String[] getChildNodeNames(String[] nodePath) throws DmtException {
        List childNodeNameList = new LinkedList();
        String nodeUri = Uri.toUri(nodePath);
        for (Iterator nodeUris = nodeMap.keySet().iterator(); nodeUris.hasNext();) {
            String existNodeUril = (String) nodeUris.next();
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

    public String getNodeTitle(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getTitle();
    }

    public String getNodeType(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getType();
    }

    public DmtData getNodeValue(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getValue();
    }

    public int getNodeSize(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getSize();
    }

    public Date getNodeTimestamp(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getTimestamp();
    }

    public int getNodeVersion(String[] nodePath) throws DmtException {
        TestNode node = getNode(nodePath);
        return node.getVersion();
    }

    protected TestNode getNode(String[] nodePath) throws DmtException {
        TestNode node = (TestNode) nodeMap.get(Uri.toUri(nodePath));
        if (node == null) {
            throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND, "node not found.");
        }
        return node;
    }

    public void nodeChanged(String[] nodePath) throws DmtException {
    }

    public void close() throws DmtException {
    }
}

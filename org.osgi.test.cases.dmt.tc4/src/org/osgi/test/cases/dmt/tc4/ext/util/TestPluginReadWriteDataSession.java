package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.Map;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadWriteDataSession;

public class TestPluginReadWriteDataSession extends TestPluginReadableDataSession implements ReadWriteDataSession {

	public TestPluginReadWriteDataSession(Map<String,TestNode> nodeMap) {
        super(nodeMap);
    }

    @Override
	public void setNodeTitle(String[] nodePath, String title) throws DmtException {
        TestNode node = getNode(nodePath);
        node.setTitle(title);
    }

    @Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
        TestNode node = getNode(nodePath);
        node.setType(type);
    }

    @Override
	public void setNodeValue(String[] nodePath, DmtData data) throws DmtException {
        TestNode node = getNode(nodePath);
        node.setValue(data);
    }

    @Override
	public void createInteriorNode(String[] nodePath, String type) throws DmtException {
        nodeMap.put(Uri.toUri(nodePath), TestNode.newInteriorNode(null, type));
    }

    @Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType) throws DmtException {
        nodeMap.put(Uri.toUri(nodePath), TestNode.newLeafNode(null, mimeType, value));
    }

    @Override
	public void deleteNode(String[] nodePath) throws DmtException {
        nodeMap.remove(Uri.toUri(nodePath));
    }

    @Override
	public void renameNode(String[] nodePath, String newName) throws DmtException {
        throw new UnsupportedOperationException();
    }

    @Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive) throws DmtException {
        throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED, "");
    }
}

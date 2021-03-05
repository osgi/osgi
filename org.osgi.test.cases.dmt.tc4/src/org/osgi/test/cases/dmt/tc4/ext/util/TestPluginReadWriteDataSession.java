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

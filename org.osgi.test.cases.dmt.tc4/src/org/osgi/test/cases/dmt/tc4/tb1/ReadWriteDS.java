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
package org.osgi.test.cases.dmt.tc4.tb1;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class ReadWriteDS implements ReadWriteDataSession {
	private Node framework;
	

	public ReadWriteDS(Node frameworkNode) {
		framework = frameworkNode;
		framework.open();
	}
	

	@Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	@Override
	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
				"Operation is not allowed - static tree");
	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		findNode(nodePath).setTitle(title);
	}

	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED, 
		"Operation is not allowed - static tree");
	}

	@Override
	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		findNode(nodePath).setNodeValue(data);
	}

	@Override
	public void close() throws DmtException {
		framework.close();
		System.out.println("[Framework Data Session] Closing a Read-Write Session");
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

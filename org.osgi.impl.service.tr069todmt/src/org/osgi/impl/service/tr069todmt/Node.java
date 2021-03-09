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

package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class Node {

	private static final String	FAKE_NODE_NAME	= "Fake";

	private String				uri;
	private DmtSession			session;
	private MetaNode			metanode;

	/**
	 * @param uri
	 * @param session
	 */
	public Node(String uri, DmtSession session) {
		this.uri = uri;
		this.session = session;
		try {
			metanode = session.getMetaNode(uri);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	DmtData getDmtValue() throws DmtException {
		return session.getNodeValue(uri);
	}

	String[] getMimeTypes() {
		if (metanode == null) {
			return null;
		}
		return metanode.getMimeTypes();
	}

	String[] getChildrenNames() throws DmtException {
		return session.getChildNodeNames(uri);
	}

	DmtData getLeafValue(String leafName) throws DmtException {
		String leafUri = makeUri(uri, leafName);
		MetaNode leafMeta = session.getMetaNode(leafUri);
		if ((leafMeta == null && session.isLeafNode(leafUri)) || leafMeta.isLeaf()) {
			return session.getNodeValue(leafUri);
		}
		throw new IllegalArgumentException("Node: " + leafUri + " is not a leaf node!");
	}

	Node getChildNode(String childName) {
		return new Node(makeUri(uri, childName), session);
	}

	private String makeUri(String parentUri, String nodeName) {
		return parentUri + Uri.PATH_SEPARATOR + nodeName;
	}

	boolean isLeaf() {
		try {
			// TODO - what should be returned if the node is lazily created?!?
			return metanode == null ? session.isLeafNode(uri) : metanode.isLeaf();
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	boolean isMultiInstanceParent() {
		return isMultiInstanceParent(session, uri);
	}

	boolean isMultiInstanceNode() {
		return isMultiInstanceNode(session, uri);
	}

	// TODO to check if only nodes with type DmtConstants.DDF_LIST and
	// DmtConstants.DDF_MAP are multi instance parents
	static boolean isMultiInstanceParent(DmtSession session, String nodeUri) {
		try {
			String nodeType = session.getNodeType(nodeUri);
			return DmtConstants.DDF_LIST.equals(nodeType) || DmtConstants.DDF_MAP.equals(nodeType);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	// TODO to see if only these nodes are multi instances
	static boolean isMultiInstanceNode(DmtSession session, String nodeUri) {
		/* Check if the parent node is multi instance parent */
		return isMultiInstanceParent(session, getParentUri(nodeUri));
	}

	static String getParentUri(String nodeUri) {
		String[] path = Uri.toPath(nodeUri);
		if (path.length > 0) {
			String[] parentPath = new String[path.length - 1];
			System.arraycopy(path, 0, parentPath, 0, parentPath.length);
			return Uri.toUri(parentPath);
		}
		return "";
	}

	static String getNodeName(String nodeUri) {
		String[] path = Uri.toPath(nodeUri);
		if (path.length > 0) {
			return path[path.length - 1];
		}
		return "";
	}

	boolean canAddChild() {
		try {
			String[] children = getChildrenNames();
			if (children == null || children.length == 0) {
				/* try to get a fake node meta */
				MetaNode fakeNodeMeta = session.getMetaNode(makeUri(uri, FAKE_NODE_NAME));
				if (fakeNodeMeta == null) {
					return true;
				}
				return fakeNodeMeta.can(MetaNode.CMD_ADD);
			} else {
				MetaNode childMeta;
				for (int i = 0; i < children.length; i++) {
					childMeta = session.getMetaNode(makeUri(uri, children[i]));
					if (childMeta != null && childMeta.can(MetaNode.CMD_ADD)) {
						return true;
					}
				}
				return false;
			}
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	boolean can(int operation) {
		if (metanode == null) {
			/* If no meta-data is provided for a node, all operations are valid */
			return true;
		}
		return metanode.can(operation);
	}
}

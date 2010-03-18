/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleContext;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.spi.ReadWriteDataSession;
/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
public class FiltersReadWriteSession extends FiltersReadOnlySession implements
		ReadWriteDataSession {

	FiltersReadWriteSession(FiltersPlugin plugin, BundleContext context, FiltersReadOnlySession session) {
		super(plugin, context);
		this.searches=session.searches;
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Cannot copy filters' nodes.");

	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		if (type != null)
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");

		String[] path = shapedPath(nodePath);

		if (path.length == 2) {
			searches.put(path[1], new Search(null, null));
			return;
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");

	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"There is no leaf node to be created in the filters subtree.");

	}

	public void deleteNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 2) {
			searches.remove(path[1]);
			return;
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");

	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the filters subtree.");

	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");

	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		if (type == null)
			return;

		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");

	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 3) {
			if (path[2].equals(TARGETSUBTREE)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null) {
					search.target = data.getString();
					return;
				}

				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified key does not exist in the filters object.");

			} else if (path[2].equals(FILTER)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null) {
					search.filter = data.getString();
					return;
				}

				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified key does not exist in the filters object.");
			}
		}

		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"The given path indicates an unmodifiable node.");

	}

}

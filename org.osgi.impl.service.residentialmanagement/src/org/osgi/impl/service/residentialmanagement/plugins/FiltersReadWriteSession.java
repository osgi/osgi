package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleContext;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.spi.ReadWriteDataSession;

public class FiltersReadWriteSession extends FiltersReadOnlySession implements
		ReadWriteDataSession {

	FiltersReadWriteSession(FiltersPlugin plugin, BundleContext context) {
		super(plugin, context);
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

		if (path.length == 1) {
			searches.remove(path[2]);
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
						"The specified key does not exist in the framework object.");

			} else if (path[2].equals(FILTER)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null) {
					search.filter = data.getString();
					return;
				}

				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The specified key does not exist in the framework object.");
			}
		}

		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"The given path indicates an unmodifiable node.");

	}

}

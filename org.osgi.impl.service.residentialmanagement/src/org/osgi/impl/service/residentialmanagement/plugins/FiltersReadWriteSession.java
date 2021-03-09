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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.spi.TransactionalDataSession;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
public class FiltersReadWriteSession extends FiltersReadOnlySession implements
		TransactionalDataSession {
	
	private Vector<Operation> operations = null;

	FiltersReadWriteSession(FiltersPlugin plugin, BundleContext context,
			FiltersReadOnlySession session) {
		super(plugin, context);
		operations = new Vector<>();
	}
	
	@Override
	public void close() throws DmtException {
		for (Iterator<String> it = searches.keySet().iterator(); it
				.hasNext();) {
			Filters filters = searches.get(it.next());
			filters.clearResult();
		}
	}

	@Override
	public void commit() throws DmtException {
		Iterator<Operation> i = operations.iterator();
		while (i.hasNext()) {
			Operation operation = i.next();
			if (operation.getOperation() == Operation.ADD_OBJECT) {
				String[] path = operation.getObjectName();
				if (path.length == 2)
					searches.put(path[1], new Filters(path[1]));
			} else if (operation.getOperation() == Operation.SET_VALUE) {
				String[] path = operation.getObjectName();				
				if (path.length == 3) {
					if (path[2].equals(RMTConstants.TARGET)) {
						Filters fs = searches.get(path[1]);
						if (fs != null)
							fs.setTarget(operation.getData().getString());
					}
					if (path[2].equals(RMTConstants.FILTER)) {
						Filters fs = searches.get(path[1]);
						if (fs != null)
							fs.setFilter(operation.getData().getString());
					}
					if (path[2].equals(RMTConstants.LIMIT)) {
						Filters fs = searches.get(path[1]);
						if (fs != null)
							fs.setLimit(operation.getData().getInt());
					}
				}
			} else if (operation.getOperation() == Operation.DELETE_OBJECT) {
				String[] path = operation.getObjectName();
				if (path.length == 2)
					searches.remove(path[1]);
			}
		}
		rollback();
	}

	@Override
	public void rollback() throws DmtException {
		operations.clear();
	}

	@Override
	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		if (type != null)
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
		String[] path = RMTUtil.shapedPath(nodePath,rootLength);
		if (path.length == 2) {
			if(searches.get(path[1])!=null)
				throw new DmtException(nodePath,
						DmtException.NODE_ALREADY_EXISTS,
						"A given node already exists in the Filters MO.");
			operations.add(new Operation(Operation.ADD_OBJECT, path));
			return;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");
	}

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath,rootLength);
		if (path.length == 2) {
			if(searches.get(path[1])==null)
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");
			operations.add(new Operation(Operation.DELETE_OBJECT, path));			
			return;
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");
	}

	@Override
	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath,rootLength);
		
		if (path.length < 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		
		if (path.length == 3) {
			if (path[2].equals(RMTConstants.TARGET)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			if (path[2].equals(RMTConstants.FILTER)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			if (path[2].equals(RMTConstants.LIMIT)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"The given path indicates an unmodifiable node.");
	}
	
	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		if (type == null)
			return;
		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
	}

	@Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Cannot copy Filters MO.");
	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"There is no leaf node to be created in the Filters MO.");

	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the Filters MO.");

	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");

	}
	
	protected class Operation {
		static final int ADD_OBJECT = 0;
		static final int DELETE_OBJECT = 1;
		static final int SET_VALUE = 2;

		private int operation;
		private String[] objectname;
		private DmtData data;

		protected Operation(int operation, String[] node) {
			this.operation = operation;
			this.objectname = node;
		}

		protected Operation(int operation, String[] node, DmtData data) {
			this.operation = operation;
			this.objectname = node;
			this.data = data;
		}

		protected int getOperation() {
			return operation;
		}

		protected String[] getObjectName() {
			return objectname;
		}

		protected DmtData getData() {
			return data;
		}
	}
}

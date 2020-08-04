/*
 * Copyright (c) OSGi Alliance (2000, 2012). All Rights Reserved.
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

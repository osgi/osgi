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

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

public class LogReadWriteSession extends LogReadOnlySession implements ReadWriteDataSession, TransactionalDataSession{
	
	LogReadWriteSession(BundleContext context) {
		super(context);
	}

	@Override
	public void close() throws DmtException {
		this.resetKeepLogEntry();
	}
	
	@Override
	public void commit() throws DmtException {
		// empty
	}

	@Override
	public void rollback() throws DmtException {
		// empty
	}

	@Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	@Override
	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"There is no interior node to be created in the log subtree.");
	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"There is no leaf node to be created in the log subtree.");
	}

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}

	@Override
	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
		"This operation is not allowed in the log subtree.");
	}
}

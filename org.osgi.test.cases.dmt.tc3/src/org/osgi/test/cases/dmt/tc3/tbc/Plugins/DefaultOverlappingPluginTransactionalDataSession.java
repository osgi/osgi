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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Plugins;

import java.util.Date;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * A deafult TransactionalDataSession to be used in overlappings plugins (or plugins to be overlapped), 
 * so theses methods should never be called.
 *  
 */
public class DefaultOverlappingPluginTransactionalDataSession implements TransactionalDataSession {
	
	private String MESSAGE;
	
	public DefaultOverlappingPluginTransactionalDataSession(String className) {
		MESSAGE = className;
	}

	@Override
	public void rollback() throws DmtException {

	}

	@Override
	public void commit() throws DmtException {

    }

	@Override
	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}
	@Override
	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	@Override
	public void setNodeType(String[] nodeUri, String type) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	@Override
	public void deleteNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

	@Override
	public void createInteriorNode(String[] nodeUri, String type) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}


	@Override
	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
		DmtConstants.PARAMETER_3 = MESSAGE;
	}

	@Override
	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
		DmtConstants.PARAMETER_3 = MESSAGE;
	}

	@Override
	public void renameNode(String[] nodeUri, String newName) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		DmtConstants.PARAMETER_2 = MESSAGE;
	}
	@Override
	public void close() throws DmtException {
		
	}

	@Override
	public boolean isNodeUri(String[] nodeUri) {
		DmtConstants.TEMPORARY = MESSAGE;
		return true;
	}

	@Override
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return false;
	}

	@Override
	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	@Override
	public String getNodeTitle(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return MESSAGE;
	}

	@Override
	public String getNodeType(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	@Override
	public int getNodeVersion(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	@Override
	public int getNodeSize(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return 0;
	}

	@Override
	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return new String[] {};
	}

	@Override
	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
		return null;
	}

	@Override
	public void nodeChanged(String[] nodeUri) throws DmtException {
		DmtConstants.TEMPORARY = MESSAGE;
	}

}

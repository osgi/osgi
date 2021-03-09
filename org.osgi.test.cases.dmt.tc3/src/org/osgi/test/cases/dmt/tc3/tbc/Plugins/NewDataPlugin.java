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
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Plugins;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

public class NewDataPlugin implements DataPlugin, TransactionalDataSession {
	
	public static final String ROLLBACK = "NewDataPlugin.rollback,";
	public static final String CLOSE = "NewDataPlugin.close,";
	public static final String COMMIT = "NewDataPlugin.commit,";
	private DmtTestControl tbc;

	public NewDataPlugin(DmtTestControl tbc) {
	    this.tbc = tbc;
	}

	
	@Override
	public void rollback() throws DmtException {
		DmtConstants.TEMPORARY += ROLLBACK;
	}

	@Override
	public void setNodeTitle(String[] nodeUri, String title) throws DmtException {
		
	}

	@Override
	public void setNodeValue(String[] nodeUri, DmtData data) throws DmtException {

	}

	public void setDefaultNodeValue(String[] nodeUri) throws DmtException {

	}

	@Override
	public void setNodeType(String[] nodeUri, String type) throws DmtException {
	
	}

	@Override
	public void deleteNode(String[] nodeUri) throws DmtException {
	
	}

	@Override
	public void createInteriorNode(String[] nodeUri, String type)
			throws DmtException {
	}

	@Override
	public void createLeafNode(String[] nodeUri, DmtData value, String mimeType)
			throws DmtException {

	}

	@Override
	public void copy(String[] nodeUri, String[] newNodeUri, boolean recursive)
			throws DmtException {
	}

	@Override
	public void renameNode(String[] nodeUri, String newName) throws DmtException {
	}

	@Override
	public void close() throws DmtException {
		DmtConstants.TEMPORARY += CLOSE;
	}

	@Override
	public boolean isNodeUri(String[] nodeUri) {
        String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(NewDataPluginActivator.INEXISTENT_NODE)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public DmtData getNodeValue(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public String getNodeTitle(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public String getNodeType(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public int getNodeVersion(String[] nodeUri) throws DmtException {
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public int getNodeSize(String[] nodeUri) throws DmtException {
		return 0;
	}

	@Override
	public String[] getChildNodeNames(String[] nodeUri) throws DmtException {
		return null;
	}

	@Override
	public MetaNode getMetaNode(String[] nodeUri) throws DmtException {
		return null;	


	}
	
	@Override
	public void commit() throws DmtException {
		DmtConstants.TEMPORARY += COMMIT;
	}

	@Override
	public boolean isLeafNode(String[] nodeUri) throws DmtException {
		return false; 
	}

	@Override
	public void nodeChanged(String[] nodeUri) throws DmtException {
		
	}
    @Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }
    
    @Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }

    @Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return this;
    }
}

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
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 04, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.ExecPlugin;

import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * A test implementation of ExecPlugin. This implementation validates the
 * DmtSession calls to a subtree handled by the ExecPlugin.
 */
public class TestExecPlugin implements ExecPlugin, DataPlugin, ReadableDataSession {

	public static final String EXECUTE = "TestExecPlugin.execute";
	
	public static final String GETNODETITLE = "TestExecPlugin.getNodeTitle";

	private DmtTestControl tbc;

	public TestExecPlugin(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	@Override
	public void execute(DmtSession session, String[] nodeUri, String correlator,String data)
			throws DmtException {
		String nodeName = tbc.mangleUri(nodeUri);
		if (nodeName.equals(TestExecPluginActivator.INTERIOR_NODE_EXCEPTION)) {
			throw new DmtException(nodeUri, DmtException.CONCURRENT_ACCESS,
					EXECUTE);
			
		} else {
			if (null == session) {
				TestCase
						.fail("A reference to the session in which the operation was issued is null");
			} else {
				TestCase
						.assertEquals(
								"Asserts that DmtSession is fowarded to ExecPlugin with the same lock type",
								DmtSession.LOCK_TYPE_EXCLUSIVE, session
										.getLockType());
				TestCase
						.assertEquals(
								"Asserts that DmtSession is fowarded to ExecPlugin with the same subtree",
								TestExecPluginActivator.ROOT, session
										.getRootUri());
			}
			DmtConstants.TEMPORARY = EXECUTE;
			DmtConstants.PARAMETER_2 = correlator;
			DmtConstants.PARAMETER_3 = data;

		}

	}

	
	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
		return this;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
		return null;
	}


    @Override
	public void nodeChanged(String[] nodePath) throws DmtException {

        
    }


    @Override
	public void close() throws DmtException {
        
        
    }

    @Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
        
        return null;
    }


    @Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
        
        return null;
    }

    @Override
	public int getNodeSize(String[] nodePath) throws DmtException {
        
        return 0;
    }


    @Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
        
        return null;
    }


    @Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
        
        return GETNODETITLE;
    }


    @Override
	public String getNodeType(String[] nodePath) throws DmtException {
        
        return null;
    }


    @Override
	public boolean isNodeUri(String[] nodePath) {
        
        return true;
    }


    @Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
        
        return false;
    }


    @Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
        
        return null;
    }
 
    @Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
        return 0;
    }
}

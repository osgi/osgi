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
 * Mar 04, 2005  Andre Assad
 * 11		     Implement DMT Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import java.util.Date;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTimestamp</code> method, 
 * according to MEG specification
 */
public class GetNodeTimestamp {
	private DmtTestControl tbc;

	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        testGetNodeTimestamp001();
        testGetNodeTimestamp002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of getNodeTimestamp 
	 * to the correct plugin.
	 * 
	 * @spec ReadableDataSession.getNodeTimestamp(String[])
	 */
	public void testGetNodeTimestamp001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			Date date = session.getNodeTimestamp(TestDataPluginActivator.ROOT);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.GETNODETIMESTAMP
					+" to the correct plugin",TestDataPlugin.GETNODETIMESTAMP_VALUE,date);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ReadableDataSession.getNodeTimestamp(String[])
	 */
	public void testGetNodeTimestamp002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeTimestamp(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION);
			DefaultTestBundleControl.failException("", DmtException.class);
			
		} catch (DmtException e) {
			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", 
					TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, e.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", 
					DmtException.URI_TOO_LONG, e.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", 
					e.getMessage().indexOf(TestDataPlugin.GETNODETIMESTAMP)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
    
}

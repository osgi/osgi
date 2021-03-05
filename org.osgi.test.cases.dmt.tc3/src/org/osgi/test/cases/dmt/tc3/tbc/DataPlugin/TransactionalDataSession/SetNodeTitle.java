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
 * Mar 02, 2005  Luiz Felipe Guimaraes
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>setNodeTitle</code> method, 
 * according to MEG specification
 */
public class SetNodeTitle {

	private DmtTestControl tbc;

	public SetNodeTitle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetNodeTitle001();
		testSetNodeTitle002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of setNodeTitle 
	 * to the correct plugin.
	 * 
	 * @spec ReadWriteDataSession.setNodeTitle(String[],String)
	 */
	public void testSetNodeTitle001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetNodeTitle001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			String title = "title";
			session.setNodeTitle(TestDataPluginActivator.INTERIOR_NODE,title);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.SETNODETITLE+" to the correct plugin",TestDataPlugin.SETNODETITLE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",title,DmtConstants.PARAMETER_2);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
			DmtConstants.TEMPORARY = "";
		}
		
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ReadWriteDataSession.setNodeTitle(String[],String)
	 */
	public void testSetNodeTitle002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetNodeTitle002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeTitle(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION,"title");
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.CONCURRENT_ACCESS, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.SETNODETITLE)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}

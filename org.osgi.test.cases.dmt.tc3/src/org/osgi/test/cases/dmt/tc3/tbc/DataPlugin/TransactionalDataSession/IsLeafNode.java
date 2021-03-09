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
 * Mar 14, 2005  Luiz Felipe Guimaraes
 * 11		     Implement DMT Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>isLeafNode</code> method, 
 * according to MEG specification
 */
public class IsLeafNode {
	private DmtTestControl tbc;

	public IsLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsLeafNode001();
		testIsLeafNode002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of isLeafNode to the correct plugin.
	 * 
	 * @spec ReadableDataSession.isLeafNode(String[])
	 */

	public void testIsLeafNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testIsLeafNode001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertTrue("Asserts that DmtAdmin fowarded "+ TestDataPlugin.ISLEAFNODE
					+" to the correct plugin",session.isLeafNode(TestDataPluginActivator.LEAF_NODE));

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of isLeafNode to the correct plugin.
	 * 
	 * @spec ReadableDataSession.isLeafNode(String[])
	 */

	public void testIsLeafNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testIsLeafNode002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertTrue("Asserts that DmtAdmin fowarded "+ TestDataPlugin.ISLEAFNODE
					+" to the correct plugin",!session.isLeafNode(TestDataPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}

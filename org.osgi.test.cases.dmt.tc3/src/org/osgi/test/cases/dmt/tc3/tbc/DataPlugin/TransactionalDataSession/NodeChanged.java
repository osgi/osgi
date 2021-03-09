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
 * July 18, 2005 Alexandre Santos
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Alexandre Santos
 * 
 * This test case validates the implementation of <code>nodeChanged</code> method, 
 * according to MEG specification
 */
public class NodeChanged {

	private DmtTestControl tbc;

	public NodeChanged(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testNodeChanged001();
	}

	/**
	 * Asserts that our plugin implementation is notified when the 
	 * given node has changed outside the scope of our plugin.
	 * 
	 * @spec ReadableDataSession.nodeChanged(String[])
	 */
	public void testNodeChanged001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testNodeChanged001");
			String[] principal = { "www.cesar.org.br" };
			int[] perm = { org.osgi.service.dmt.Acl.GET
					| org.osgi.service.dmt.Acl.EXEC };

			Acl acl = new Acl(principal, perm);

			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestDataPlugin.setNodeChangedThrowsException(true);
			session.setNodeAcl(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, acl);
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, e
				.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.DATA_STORE_FAILURE, e
				.getCode());
			TestCase.assertTrue(
					"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
					e.getMessage().indexOf(TestDataPlugin.NODECHANGED) > -1);

		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
			tbc.cleanAcl(TestDataPluginActivator.INTERIOR_NODE);
			TestDataPlugin.setNodeChangedThrowsException(false);
		}
	}

}

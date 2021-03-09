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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 28/02/2005   Andre Assad
 * 11           Implement DMT Use Cases 
 * ===========  ==============================================================
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
 * This test case validates the implementation of <code>createInteriorNode</code> method, 
 * according to MEG specification
 */
public class CreateInteriorNode {

	private DmtTestControl tbc;

	public CreateInteriorNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCreateInteriorNode001();
		testCreateInteriorNode002();
		testCreateInteriorNode003();
		testCreateInteriorNode004();
		
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of createInteriorNode to the correct plugin,
     * using DmtSession.createInteriorNode(String)
	 * 
	 * @spec ReadWriteDataSession.createInteriorNode(String[],String)
	 */
	public void testCreateInteriorNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateInteriorNode001");

			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.createInteriorNode(TestDataPluginActivator.INEXISTENT_NODE);

			TestCase.assertEquals("Asserts that DmtAdmin fowarded "
					+ TestDataPlugin.CREATEINTERIORNODE
					+ " to the correct plugin",
					TestDataPlugin.CREATEINTERIORNODE,
					DmtConstants.TEMPORARY);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin,
     * using DmtSession.createInteriorNode(String)
	 * 
	 * @spec ReadWriteDataSession.createInteriorNode(String[],String)
	 */

	public void testCreateInteriorNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateInteriorNode002");

			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.createInteriorNode(
					TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION);

			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ",
							TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION,
							e.getURI());
			TestCase
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct code: ",
							DmtException.CONCURRENT_ACCESS, e.getCode());
			TestCase
					.assertTrue(
							"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
							e
									.getMessage()
									.indexOf(
											TestDataPlugin.CREATEINTERIORNODE) > -1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	/**
	 * Asserts that DmtAdmin correctly forwards the call of createInteriorNode to the correct plugin,
     * using DmtSession.createInteriorNode(String,String)
	 * 
	 * @spec ReadWriteDataSession.createInteriorNode(String[],String)
	 */
	public void testCreateInteriorNode003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateInteriorNode003");

			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.createInteriorNode(TestDataPluginActivator.INEXISTENT_NODE,
					DmtConstants.DDF);

			TestCase.assertEquals("Asserts that DmtAdmin fowarded "
					+ TestDataPlugin.CREATEINTERIORNODE
					+ " to the correct plugin",
					TestDataPlugin.CREATEINTERIORNODE,
					DmtConstants.TEMPORARY);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin,
     * using DmtSession.createInteriorNode(String,String)
	 * 
	 * @spec ReadWriteDataSession.createInteriorNode(String[],String)
	 */

	public void testCreateInteriorNode004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateInteriorNode004");

			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.createInteriorNode(
					TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION,
					DmtConstants.DDF);

			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ",
							TestDataPluginActivator.INEXISTENT_NODE_EXCEPTION,
							e.getURI());
			TestCase
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct code: ",
							DmtException.CONCURRENT_ACCESS, e.getCode());
			TestCase
					.assertTrue(
							"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
							e
									.getMessage()
									.indexOf(
											TestDataPlugin.CREATEINTERIORNODE) > -1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}

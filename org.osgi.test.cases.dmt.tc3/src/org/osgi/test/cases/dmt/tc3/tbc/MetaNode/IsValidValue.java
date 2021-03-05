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
 * Jul 11, 2005  Luiz Felipe Guimarães
 * 1             Implement TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the implementation of <code>isValidValue</code> method of MetaNode, 
 * according to MEG specification

 */
public class IsValidValue {
	private DmtTestControl tbc;

	public IsValidValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsValidValue001();
		testIsValidValue002();
	}


	/**
	 * Asserts that true is returned from the MetaNode of the specified node
	 * if the value passed is valid
	 * 
	 * @spec MetaNode.isValidValue(DmtData)
	 */
	public void testIsValidValue001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testIsValidValue001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			TestCase.assertTrue("Asserts isValidValue method",
					metanode.isValidValue(TestMetaNode.DEFAULT_VALID_VALUES[0]));
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * Asserts that valse is returned from the MetaNode of the specified node
	 * if the value passed is invalid
	 * 
	 * @spec MetaNode.isValidValue(DmtData)
	 */
	public void testIsValidValue002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testIsValidValue001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			TestCase.assertTrue("Asserts isValidValue method",
					!metanode.isValidValue(new DmtData("invalid")));
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}

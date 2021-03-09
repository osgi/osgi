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
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 25, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the implementation of <code>isZeroOccurrenceAllowed</code> method of MetaNode, 
 * according to MEG specification

 */
public class IsZeroOccurrenceAllowed {
	private DmtTestControl tbc;

	public IsZeroOccurrenceAllowed(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsZeroOccurrenceAllowed001();
	}

	/**
	 * Asserts that true is returned from the MetaNode of the specified node
	 * if zero occurrence of that node is allowed
	 * 
	 * @spec MetaNode.isZeroOccurrenceAllowed()
	 */
	public void testIsZeroOccurrenceAllowed001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testIsZeroOccurrenceAllowed001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			TestCase.assertTrue("Asserts IsZeroOccurrenceAllowed method", !metanode
					.isZeroOccurrenceAllowed());
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

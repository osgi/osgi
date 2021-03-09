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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the implementation of <code>getRawFormatNames</code> method of MetaNode, 
 * according to MEG specification

 */
public class GetRawFormatNames {
	private DmtTestControl tbc;

	public GetRawFormatNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetRawFormatNames001();
	}

	/**
	 * Asserts that the correct value is returned from the MetaNode of the specified node
	 * 
	 * @spec MetaNode.getRawFormatNames()
	 */
	public void testGetRawFormatNames001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetRawFormatNames001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			TestCase.assertEquals("Asserts getRawFormatNames() method",
					TestMetaNode.DEFAULT_RAW_FORMAT_NAMES, metanode
							.getRawFormatNames());
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}

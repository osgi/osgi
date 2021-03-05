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
 * Mar 09, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * Tests all constants of MetaNode
 */

public class DmtMetaNodeConstants {
	@SuppressWarnings("unused")
	private DmtTestControl tbc;

	public DmtMetaNodeConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}
	
	/**
	 * Tests all constants of MetaNode
	 * 
	 * @spec 117.12.8
	 */
	public void testConstants001(){
		DefaultTestBundleControl.log("#testConstants001");
		TestCase.assertEquals("Asserts MetaNode.CMD_ADD",0,MetaNode.CMD_ADD);
		TestCase.assertEquals("Asserts MetaNode.CMD_DELETE",1,MetaNode.CMD_DELETE);
		TestCase.assertEquals("Asserts MetaNode.CMD_EXECUTE",2,MetaNode.CMD_EXECUTE);
		TestCase.assertEquals("Asserts MetaNode.CMD_GET",4,MetaNode.CMD_GET);
		TestCase.assertEquals("Asserts MetaNode.CMD_REPLACE",3,MetaNode.CMD_REPLACE);
		TestCase.assertEquals("Asserts MetaNode.AUTOMATIC",2,MetaNode.AUTOMATIC);
		TestCase.assertEquals("Asserts MetaNode.DYNAMIC",1,MetaNode.DYNAMIC);
		TestCase.assertEquals("Asserts MetaNode.PERMANENT",0,MetaNode.PERMANENT);
	}

}

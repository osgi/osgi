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
 * Feb 15, 2005  Alexandre Santos
 * 1             Implements TCK
 * ============  ==============================================================
 * Feb 28, 2005  Luiz Felipe Guimaraes
 * 1             Updates 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This class tests all constants of DmtSession
 *                     
 * @author Alexandre Santos
 */

public class DmtSessionConstants implements TestInterface {
    
	@SuppressWarnings("unused")
	private DmtTestControl tbc;

    public DmtSessionConstants(DmtTestControl tbc) {
        this.tbc = tbc;
    }

    @Override
	public void run() {
        testConstants001();
    }
    
    /**
     * This method asserts the constants values 
     * 
     * @spec 118.12.16
     */    
    private void testConstants001() {
		DefaultTestBundleControl.log("#testConstants001");
        TestCase.assertEquals("Asserting LOCK_TYPE_SHARED value", 0, DmtSession.LOCK_TYPE_SHARED);
        TestCase.assertEquals("Asserting LOCK_TYPE_EXCLUSIVE value", 1, DmtSession.LOCK_TYPE_EXCLUSIVE);
        TestCase.assertEquals("Asserting LOCK_TYPE_ATOMIC value", 2, DmtSession.LOCK_TYPE_ATOMIC);
        TestCase.assertEquals("Asserting STATE_OPEN value", 0, DmtSession.STATE_OPEN);
        TestCase.assertEquals("Asserting STATE_CLOSED value", 1, DmtSession.STATE_CLOSED);
        TestCase.assertEquals("Asserting STATE_INVALID value", 2, DmtSession.STATE_INVALID);
    }   
    

}

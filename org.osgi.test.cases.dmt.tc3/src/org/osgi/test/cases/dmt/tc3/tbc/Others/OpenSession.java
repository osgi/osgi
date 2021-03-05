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
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 11            Implemente MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.Others;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates that openAtomicSession is called when using an atomic session, 
 * openReadWriteSession is called when using an exclusive session and 
 * openReadOnlySession is called when using an atomic session.
 */
public class OpenSession {

	private DmtTestControl tbc;
	public OpenSession(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
	    testOpenSession001();
        testOpenSession002();
        testOpenSession003();

	}

	/**
	 * Asserts that a openReadOnlySession is called when the the first reference is made within a DmtSession 
     * a node which is handled by this plugin using the LOCK_TYPE_SHARED
	 * 
	 * @spec 117.6.1 Data Sessions
	 */
	public void testOpenSession001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testOpenSession001");

			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_SHARED);
			
			TestCase.assertEquals("Asserts that a openReadOnlySession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_SHARED",
                    TestDataPlugin.SESSION_OPENED,"openReadOnlySession");
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
            TestDataPlugin.SESSION_OPENED="";
		}
	}
    
    /**
     * Asserts that a openReadWriteSession is called when the the first reference is made within a DmtSession 
     * a node which is handled by this plugin using the LOCK_TYPE_EXCLUSIVE
     * 
     * @spec 117.6.1 Data Sessions
     */
    public void testOpenSession002() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testOpenSession002");

            session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            TestCase.assertEquals("Asserts that a openReadWriteSession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_EXCLUSIVE",
                    TestDataPlugin.SESSION_OPENED,"openReadWriteSession");
        } catch (Exception e) {
            DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
            TestDataPlugin.SESSION_OPENED="";
        }
    }
    /**
     * Asserts that a openAtomicSession is called when the the first reference is made within a DmtSession 
     * a node which is handled by this plugin using the LOCK_TYPE_ATOMIC
     * 
     * @spec 117.6.1 Data Sessions
     */
    public void testOpenSession003() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testOpenSession003");

            session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
                    DmtSession.LOCK_TYPE_ATOMIC);
            
            TestCase.assertEquals("Asserts that a openAtomicSession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_ATOMIC",
                    TestDataPlugin.SESSION_OPENED,"openAtomicSession");
        } catch (Exception e) {
            DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.cleanUp(session,true);
            TestDataPlugin.SESSION_OPENED="";
        }
    }
	
}

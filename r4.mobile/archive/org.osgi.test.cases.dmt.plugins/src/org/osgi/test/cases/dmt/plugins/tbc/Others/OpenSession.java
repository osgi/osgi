/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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

package org.osgi.test.cases.dmt.plugins.tbc.Others;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPluginActivator;

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
	private void testOpenSession001() {
		DmtSession session = null;
		try {
			tbc.log("#testOpenSession001");

			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_SHARED);
			
			tbc.assertEquals("Asserts that a openReadOnlySession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_SHARED",
                    TestDataPlugin.SESSION_OPENED,"openReadOnlySession");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
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
    private void testOpenSession002() {
        DmtSession session = null;
        try {
            tbc.log("#testOpenSession002");

            session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            tbc.assertEquals("Asserts that a openReadWriteSession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_EXCLUSIVE",
                    TestDataPlugin.SESSION_OPENED,"openReadWriteSession");
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
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
    private void testOpenSession003() {
        DmtSession session = null;
        try {
            tbc.log("#testOpenSession003");

            session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.INTERIOR_NODE,
                    DmtSession.LOCK_TYPE_ATOMIC);
            
            tbc.assertEquals("Asserts that a openAtomicSession is called when the the first reference is made within a DmtSession" +
                    " a node which is handled by this plugin using the LOCK_TYPE_ATOMIC",
                    TestDataPlugin.SESSION_OPENED,"openAtomicSession");
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
        } finally {
            tbc.cleanUp(session,true);
            TestDataPlugin.SESSION_OPENED="";
        }
    }
	
}

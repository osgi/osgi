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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Jan 31, 2005 Andre Assad
 * 1            Implement MEG TCK
 * ===========  ==============================================================
 * Feb 22, 2005 Luiz Felipe Guimaraes
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.Others;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * @generalDescription This Test Case Validates the constraints specified on the
 *                     session 5.2 of the rfc0085, according to MEG reference
 *                     documentation (rfc0085).
 */
public class Constraints {

	private DmtTestControl tbc;

	public Constraints(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstraints001();
		testConstraints002();
		testConstraints003();
		testConstraints004();
		testConstraints005();
		testConstraints006();
		testConstraints007();
		testConstraints008();
		testConstraints009();
		testConstraints010();
		testConstraints011();
		testConstraints012();
		testConstraints013();
		testConstraints014();
		testConstraints015();
	}

	/**
	 * @testID testConstraints001
	 * @testDescription Tests if there can be any number of concurrent read only
	 *                  sessions within the same subtree.
	 */
	private void testConstraints001() {
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			tbc.log("#testConstraints001");
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.pass("Two concurrent read only sessions were created.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints002
	 * @testDescription Tests if a read only session blocks the creation of an
	 *                  updating session (with a LOCK_TYPE_EXCLUSIVE) within the
	 *                  same subtree.
	 */

	private void testConstraints002() {
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			tbc.log("#testConstraints002");
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("A read only session didn't block the creation of an updating session (with a LOCK_TYPE_EXCLUSIVE)");
		} catch (DmtException e) {
			tbc.assertEquals("A read only session blocked the creation of an updating session (with a LOCK_TYPE_EXCLUSIVE)",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints003
	 * @testDescription Tests if a read only session blocks the creation of an
	 *                  updating session (with a LOCK_TYPE_ATOMIC) within the
	 *                  same subtree.
	 */
	private void testConstraints003() {
		tbc.log("#testConstraints003");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc
					.fail("A read only session didn't block the creation of an updating session (with a LOCK_TYPE_ATOMIC)");
		} catch (DmtException e) {
			tbc.assertEquals("A read only session blocked the creation of an updating session (with a LOCK_TYPE_ATOMIC)",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}


	/**
	 * @testID testConstraints004
	 * @testDescription Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can
	 *                  not be shared with LOCK_TYPE_ATOMIC lock
	 */
	private void testConstraints004() {
		tbc.log("#testConstraints004");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc
					.fail("An EXCLUSIVE session could be shared with an ATOMIC session");
		} catch (DmtException e) {
			tbc.assertEquals("An EXCLUSIVE session could NOT be shared with an ATOMIC session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints005
	 * @testDescription Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can
	 *                  not be shared with LOCK_TYPE_SHARED lock
	 */
	private void testConstraints005() {
		tbc.log("#testConstraints005");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.fail("An EXCLUSIVE session could be shared with a SHARED session");
		} catch (DmtException e) {
			tbc.assertEquals("An EXCLUSIVE session could NOT be shared with aa SHARED session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints006
	 * @testDescription Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can
	 *                  not be shared with LOCK_TYPE_EXCLUSIVE lock
	 */
	private void testConstraints006() {
		tbc.log("#testConstraints006");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("An EXCLUSIVE session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			tbc.assertEquals("An EXCLUSIVE session could NOT be shared with an EXCLUSIVE session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints007
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin
	 */
	private void testConstraints007() {
		tbc.log("#testConstraints007");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc
					.fail("An ATOMIC session could be shared with an ATOMIC session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with an ATOMIC session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints008
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin
	 */

	private void testConstraints008() {
		tbc.log("#testConstraints008");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("An ATOMIC session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with an EXCLUSIVE session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * @testID testConstraints009
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin
	 */

	private void testConstraints009() {
		tbc.log("#testConstraints009");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.fail("An ATOMIC session could be shared with a SHARED session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with a SHARED session",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	

	/**
	 * @testID testConstraints010
	 * @testDescription Test if concurrent updating sessions can be opened
	 *                  within different plugins
	 */

	private void testConstraints010() {
		tbc.log("#testConstraints010");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.pass("Two sessions could be opened with different plugins.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}
	
	
	/**
	 * @testID testConstraints011
	 * @testDescription Test if concurrent updating sessions can be opened
	 *                  within different plugins
	 */

	private void testConstraints011() {
		tbc.log("#testConstraints011");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.pass("Two sessions could be opened with different plugins.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}	
	
	/**
	 * @testID testConstraints012
	 * @testDescription Test if concurrent updating sessions can be opened
	 *                  within different plugins
	 */

	private void testConstraints012() {
		tbc.log("#testConstraints012");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.pass("Two sessions could be opened with different plugins.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}		
	/**
	 * @testID testConstraints013
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin (different subtrees)
	 */

	private void testConstraints013() {
		tbc.log("#testConstraints013");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT2,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc
					.fail("Two updating sessions could be opened within the same plugin (on different subtrees).");
		} catch (DmtException e) {
			tbc.assertEquals("Two updating sessions could NOT be opened within the same plugin (on different subtrees).",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}
	
	/**
	 * @testID testConstraints014
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin (different subtrees)
	 */

	private void testConstraints014() {
		tbc.log("#testConstraints014");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT2,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("Two updating sessions could be opened within the same plugin (on different subtrees).");
		} catch (DmtException e) {
			tbc.assertEquals("Two updating sessions could NOT be opened within the same plugin (on different subtrees).",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}
	/**
	 * @testID testConstraints015
	 * @testDescription Test if concurrent updating sessions cannot be opened
	 *                  within the same plugin (different subtrees)
	 */

	private void testConstraints015() {
		tbc.log("#testConstraints015");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT2,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("Two updating sessions could be opened within the same plugin (on different subtrees).");
		} catch (DmtException e) {
			tbc.assertEquals("Two updating sessions could NOT be opened within the same plugin (on different subtrees).",DmtException.TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}
}

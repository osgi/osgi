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
 * Jan 02, 2006  Luiz Felipe Guimaraes
 * 276           [MEGTCK][DMT] Split org.osgi.test.cases.dmt.main in two parts
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Constraints;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the constraints according to MEG specification
 */
public class DmtSessionConstraints {

	private DmtTestControl tbc;

	public DmtSessionConstraints(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		//This classes was splitted in two parts for preventing the TCK of timeout.	The first one is at
		//org.osgi.test.cases.dmt.plugins.tbc.Others.DmtSessionContraints and the second one is this class
		testConstraints001();
		testConstraints002();
		testConstraints003();
		testConstraints004();
		testConstraints005();
		testConstraints006();
		testConstraints007();
	}

	/**
	 * Tests if a session (with the LOCK_TYPE_EXCLUSIVE) can not be shared 
     * with LOCK_TYPE_EXCLUSIVE lock
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	private void testConstraints001() {
		tbc.log("#testConstraints001");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("An EXCLUSIVE session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			tbc.assertEquals("An EXCLUSIVE session could NOT be shared with an EXCLUSIVE session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be opened within the same plugin.
     * Both session have LOCK_TYPE_ATOMIC
     * 
     * 
     * @spec 117.3 The DMT Admin Service
	 */
	private void testConstraints002() {
		tbc.log("#testConstraints002");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc
					.fail("An ATOMIC session could be shared with an ATOMIC session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with an ATOMIC session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be opened within the same plugin.
     * One session has LOCK_TYPE_ATOMIC and other has LOCK_TYPE_EXCLUSIVE
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	private void testConstraints003() {
		tbc.log("#testConstraints003");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc
					.fail("An ATOMIC session could be shared with an EXCLUSIVE session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with an EXCLUSIVE session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	/**
	 * Test if concurrent updating sessions cannot be shared with a shared session.
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	private void testConstraints004() {
		tbc.log("#testConstraints004");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.fail("An ATOMIC session could be shared with a SHARED session");
		} catch (DmtException e) {
			tbc.assertEquals("An ATOMIC session could NOT be shared with a SHARED session",DmtException.SESSION_CREATION_TIMEOUT,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName()
					+ " but it was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session1);
			tbc.closeSession(session2);
		}
	}

	

	/**
	 * Test if concurrent updating sessions can be opened within different plugins
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	private void testConstraints005() {
		tbc.log("#testConstraints005");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
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
	 * Test if concurrent updating sessions can be opened within different plugins
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	private void testConstraints006() {
		tbc.log("#testConstraints006");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
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
	 * Test if concurrent updating sessions can be opened within different plugins
     * 
     * @spec 117.3 The DMT Admin Service
	 */

	private void testConstraints007() {
		tbc.log("#testConstraints007");
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			session1 = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session2 = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT,
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
}

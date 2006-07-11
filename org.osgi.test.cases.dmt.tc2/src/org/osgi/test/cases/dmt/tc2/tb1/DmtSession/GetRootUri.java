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
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 * Feb 14, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * This test case validates the implementation of <code>getRootUri</code> method of DmtSession, 
 * according to MEG specification
 */

public class GetRootUri implements TestInterface {
	private DmtTestControl tbc;

	public GetRootUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetRootUri001();
		testGetRootUri002();
		testGetRootUri003();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), "*", DmtPermission.GET)});
    }
	/**
	 * Asserts that the complete URI of the root node is returned.
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetRootUri001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting root uri", DmtConstants.OSGi_ROOT,
					session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that "." is returned when the session is created without specifying a root
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetRootUri002");
			session = tbc.getDmtAdmin().getSession(null,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting root uri", ".", session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * Asserts that the complete URI of the root node is returned when the root is a leaf node.
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetRootUri003");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting root uri", TestExecPluginActivator.LEAF_NODE, session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

/*
 * Copyright (c) OSGi Alliance (2004, 2020). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tb1.DmtEvent.DmtEventListenerImpl;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * This test case validates the implementation of <code>removeEventListener</code> method of DmtAdmin, 
 * according to MEG specification
 * 
 */

public class RemoveEventListener implements TestInterface {
	private DmtTestControl tbc;
	
	public RemoveEventListener(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        testRemoveEventListener001();
        testRemoveEventListener002();
	}
	
    private void prepare() {
        tbc.setPermissions(
                new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
                new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
                );
    }
	/**
	 * Asserts that NullPointerException is thrown if the listener parameter is null
	 * 
	 * @spec DmtAdmin.removeEventListener(DmtEventListener) 

	 */
	private void testRemoveEventListener001() {
		try {
			DefaultTestBundleControl.log("#testRemoveEventListener001");
			// Does not compile anymore
			// tbc.getDmtAdmin().removeEventListener(null);
			DefaultTestBundleControl.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			DefaultTestBundleControl.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class, e);
		}
	}
	
	/**
	 * Asserts that DmtAdmin.removeEventListener removes a previously registered listener. 
	 * After this call, the listener will not receive change notifications
	 * 
	 * @spec DmtAdmin.removeEventListener(DmtEventListener) 
	 */
	private void testRemoveEventListener002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRemoveEventListener002");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			@SuppressWarnings("unused")
			DmtEventListenerImpl event = new DmtEventListenerImpl();
			// Does not compile anymore
			
//			tbc.getDmtAdmin().addEventListener(DmtConstants.ALL_DMT_EVENTS,
//					TestExecPluginActivator.ROOT,event);
//			
//			tbc.getDmtAdmin().removeEventListener(event);
//			
//			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
//			
//			synchronized (tbc) {
//				tbc.wait(DmtConstants.WAITING_TIME);
//			}
//			
//			tbc.assertEquals("Asserts that the listener does not receive change notifications after DmtAdmin.removeEventListener() is called",0,event.getCount());

			TestCase.fail("API changed - pkriens");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}


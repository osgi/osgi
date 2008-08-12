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
 * Feb 16, 2005  Alexandre Santos
 * 1		     Implement MEG TCK
 * ============  =================================================================
 * Mar 15, 2005  Alexandre Santos
 * 28		     Implement test cases for DmtSession.close()
 * ============  =================================================================
 * Jun 17, 2005  Alexandre Alves
 * 28            [MEGTCK][DMT] Implement test cases for DmtSession.close()
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ReadOnly.TestReadOnlyPlugin;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;

/**
 * This test case validates the implementation of <code>close</code> method of DmtSession, 
 * according to MEG specification
 * 
 * @author Alexandre Santos
 */

public class Close implements TestInterface  {
	private DmtTestControl tbc;
	
	public Close(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	public void run() {
        prepare();
		testClose001();
        testClose002();
	}
    private void prepare() {
        //No DmtPermission is needed. 
        tbc.setPermissions(new PermissionInfo[0]);
    }
	/**
	 * Asserts that after a close, the state is really set to STATE_CLOSED.
	 * 
	 * @spec DmtSession.close()
	 */
	private void testClose001() {
		DmtSession session = null;
		try {
			tbc.log("#testClose001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			tbc.assertEquals("Asserting if the state after a close is really STATE_CLOSED.", DmtSession.STATE_CLOSED, session.getState());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
		    tbc.closeSession(session);
        }
		
	}
    
    /**
     * Asserts that the session becomes STATE_INVALID if the close operation completed unsuccessfully
     * 
     * @spec DmtSession.close()
     */
    private void testClose002() {
        DmtSession session = null;
        try {
            tbc.log("#testClose002");
            session = tbc.getDmtAdmin().getSession(TestReadOnlyPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
            TestReadOnlyPlugin.setExceptionAtClose(true);
            session.close();
            tbc.failException("",DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserts that the session becomes STATE_INVALID if the close operation completed unsuccessfully", DmtSession.STATE_INVALID, session.getState());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "
                + e.getClass().getName());
        } finally {
            tbc.closeSession(session);
            TestReadOnlyPlugin.setExceptionAtClose(false);
        }
        
    }
	
	
}

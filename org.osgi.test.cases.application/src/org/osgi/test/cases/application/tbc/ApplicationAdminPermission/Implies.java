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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 13/10/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.ApplicationAdminPermission;

import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>implies</code> method, according to MEG reference
 * documentation.
 */
public class Implies {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public Implies(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testImplies001();
		testImplies002();
		testImplies003();
		testImplies004();
		testImplies005();
	}

    /**
     * This method asserts if two equals ApplicationAdminPermission returns
     * true in an implies call.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies001() {
		try {
			tbc.log("#testImplies001");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions returns true in an implies call.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts if two differents ApplicationAdminPermission returns
     * false in an implies call.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies002() {
		try {
			tbc.log("#testImplies002");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER2, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions returns false in an implies call.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts if an ApplicationAdminPermission that has a wildcard
     * implies other ApplicationAdminPermission that does not have wildcard.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER2, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if if an ApplicationAdminPermission that has a wildcard implies other ApplicationAdminPermission that does not have wildcard.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if an ApplicationAdminPermission that has a different pid
     * does not implies other ApplicationAdminPermission.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies004() {
		try {
			tbc.log("#testImplies004");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if if an ApplicationAdminPermission that has a different pid does not implies other ApplicationAdminPermission.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts the behaviour when the filter in 
     * the permission is the <<SELF>> pseudo target.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies005() {
		try {
			tbc.log("#testImplies005");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if false is returned when we use <<SELF>> in (String,String) constructor.", !app2.implies(app));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

}

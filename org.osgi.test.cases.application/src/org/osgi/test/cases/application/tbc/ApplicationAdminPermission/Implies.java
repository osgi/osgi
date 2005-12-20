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

import org.osgi.service.application.ApplicationAdminPermission;
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
		testImplies006();
		testImplies007();
		testImplies008();
		testImplies009();
		testImplies010();
		testImplies011();
		testImplies012();
		testImplies013();
		testImplies014();
		testImplies015();
		testImplies016();
		testImplies017();
		testImplies018();
		testImplies019();
		testImplies020();
		testImplies021();
	}

    /**
     * This method asserts if two equals ApplicationAdminPermission(with filter) returns
     * false in an implies call.
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
			tbc.assertTrue("Asserting if two equals permissions(with filter) returns false in an implies call.", !app.implies(app2));
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
     * This method asserts if two equals ApplicationAdminPermission(AppDesc) returns
     * false in an implies call.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions(AppDesc) returns false in an implies call.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that true is returned when all
     * the steps are followed using <<SELF>> as filter.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies004() {
		try {
			tbc.log("#testImplies004");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS_DIFFERENT_ORDER);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if true is returned using <<SELF>> as filter when all the steps are followed.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that false is returned when all
     * the steps are followed using <<SELF>> as filter but
     * the applicationId is different.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies005() {
		try {
			tbc.log("#testImplies005");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			app2 = app2.setCurrentApplicationId(ApplicationConstants.APPLICATION_LOCATION); //just a different value to make a false result.
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed but a different applicationId is used.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that true is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies006() {
		try {
			tbc.log("#testImplies006");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS_DIFFERENT_ORDER);
			tbc.assertTrue("Asserting if true is returned to an implies call of an AppAdminPerm filter that matches the application certificate.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that false is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that does not matches the application certificate.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies007() {
		try {
			tbc.log("#testImplies007");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT_SIGNER, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if false is returned to an implies call of an AppAdminPerm filter that matches does not the application certificate.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that false is returned when no
     * Applicationid is set to the AppAdminPerm(with filter)
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies008() {
		try {
			tbc.log("#testImplies008");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed but no ApplicationId was set.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that false is returned when null 
     * was set to the Applicationid of the AppAdminPerm(with filter)
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies009() {
		try {
			tbc.log("#testImplies009");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed and null was set by setApplicationid.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that false is returned when all
     * the steps are followed using <<SELF>> as filter but the
     * actions are different.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies010() {
		try {
			tbc.log("#testImplies010");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationAdminPermission.LOCK_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.SCHEDULE_ACTION);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed but using different actions.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that true is returned when all
     * the steps are followed using <<SELF>> as filter and
     * SCHEDULE action for the first AppAdminPerm and LIFECYCLE 
     * action for the second in order to validate if SCHEDULE
     * action implies LIFECYCLE.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies011() {
		try {
			tbc.log("#testImplies011");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationAdminPermission.SCHEDULE_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.LIFECYCLE_ACTION);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if true is returned using <<SELF>> as filter when all the steps are followed using SCHEDULE" +
					" action for the first AppAdminPerm and LIFECYCLE action for the second in order to validate if " +
					"SCHEDULE action implies LIFECYCLE.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}		

    /**
     * This method asserts that true is returned when all
     * the steps are followed using <<SELF>> as filter and
     * are using LOCK action for the second AppAdminPerm and LIFECYCLE
     * and LOCK actions for the first.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies012() {
		try {
			tbc.log("#testImplies012");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.LOCK_ACTION);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if true is returned using <<SELF>> as filter when all the steps are followed " +
					"and using LOCK action for the second AppAdminPerm and LIFECYCLE and LOCK actions for the first.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that false is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate but have different
     * actions.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies013() {
		try {
			tbc.log("#testImplies013");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LOCK_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.SCHEDULE_ACTION);
			tbc.assertTrue("Asserting if false is returned to an implies call of an AppAdminPerm filter that matches the application certificate but have different actions.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that true is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate and have
     * SCHEDULE action for the first AppAdminPerm and LIFECYCLE 
     * action for the second in order to validate if SCHEDULE
     * action implies LIFECYCLE.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies014() {
		try {
			tbc.log("#testImplies014");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.LIFECYCLE_ACTION);
			tbc.assertTrue("Asserting if true is returned to an implies call of an AppAdminPerm filter that matches the application certificate" +
					" and have SCHEDULE action for the first AppAdminPerm and LIFECYCLE action for the second in order to " +
					" validate if SCHEDULE action implies LIFECYCLE.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that true is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate but are
     * using LOCK action for the second AppAdminPerm and LIFECYCLE
     * and LOCK actions for the first.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies015() {
		try {
			tbc.log("#testImplies015");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.LOCK_ACTION);
			tbc.assertTrue("Asserting if true is returned to an implies call of an AppAdminPerm filter that matches the application certificate" +
					" but are using LOCK action for the second AppAdminPerm and LIFECYCLE and LOCK actions for the first.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that true is returned when null
     * is passed as String filter and all the steps are followed.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies016() {
		try {
			tbc.log("#testImplies016");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					(String) null, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS_DIFFERENT_ORDER);
			tbc.assertTrue("Asserting if true is returned when null is passed as String filter and all the steps are followed.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that true is returned when a wildcard
     * is passed for the second AppAdminPerm and SCHEDULE and LOCK
     * actions are passed to the first AppAdminPerm. It is important
     * to note that LIFECYCLE is already implied by SCHEDULED action.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies017() {
		try {
			tbc.log("#testImplies017");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					(String) null, ApplicationConstants.ACTIONS_COMPLETE);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), "*");
			tbc.assertTrue("Asserting if true is returned when a wildcard is passed for the second AppAdminPerm and SCHEDULE" +
					" and LOCK actions are passed to the first AppAdminPerm. It is important to note that LIFECYCLE is " +
					"already implied by SCHEDULED action.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that false is returned when all
     * the steps are followed using <<SELF>> as filter and
     * LIFECYCLE action for the first AppAdminPerm and SCHEDULE 
     * action for the second in order to validate if LIFECYCLE
     * action does not implies SCHEDULE.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies018() {
		try {
			tbc.log("#testImplies018");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationAdminPermission.LIFECYCLE_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.SCHEDULE_ACTION);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed using LIFECYCLE" +
					" action for the first AppAdminPerm and SCHEDULE action for the second in order to validate if " +
					"LIFECYCLE action does not implies SCHEDULE.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that false is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate and have
     * LIFECYCLE action for the first AppAdminPerm and SCHEDULE 
     * action for the second in order to validate if LIFECYCLE
     * action does not implies SCHEDULE.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies019() {
		try {
			tbc.log("#testImplies019");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.SCHEDULE_ACTION);
			tbc.assertTrue("Asserting if false is returned to an implies call of an AppAdminPerm filter that matches the application certificate" +
					" and have LIFECYCLE action for the first AppAdminPerm and SCHEDULE action for the second in order to " +
					" validate if LIFECYCLE action does not implies SCHEDULE.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
		
    /**
     * This method asserts that false is returned to
     * an implies call of an ApplicationAdminPermission filter
     * that matches the application certificate but are
     * using LOCK action for the first AppAdminPerm and LIFECYCLE
     * and LOCK actions for the second.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies020() {
		try {
			tbc.log("#testImplies020");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationAdminPermission.LOCK_ACTION);
			tbc.assertTrue("Asserting if false is returned to an implies call of an AppAdminPerm filter that matches the application certificate" +
					" but are using LOCK action for the first AppAdminPerm and LIFECYCLE and LOCK actions for the second.", app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that false is returned when all
     * the steps are followed using <<SELF>> as filter and
     * are using LOCK action for the first AppAdminPerm and LIFECYCLE
     * and LOCK actions for the second.
     * 
     * @spec ApplicationAdminPermission.implies(Permission)
     */         
	private void testImplies021() {
		try {
			tbc.log("#testImplies021");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					"<<SELF>>", ApplicationAdminPermission.LOCK_ACTION);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			app2 = app2.setCurrentApplicationId(tbc.getAppDescriptor().getApplicationId());
			tbc.assertTrue("Asserting if false is returned using <<SELF>> as filter when all the steps are followed " +
					"and using LOCK action for the first AppAdminPerm and LIFECYCLE and LOCK actions for the second.", !app.implies(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
}

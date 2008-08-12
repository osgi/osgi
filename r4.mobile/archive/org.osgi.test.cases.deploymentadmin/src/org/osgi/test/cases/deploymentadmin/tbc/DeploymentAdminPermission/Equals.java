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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK
 * ============  ==============================================================
 * May 26, 2005  Andre Assad
 * 26            Implement MEG TCK
 * ============  ==============================================================
 */


package org.osgi.test.cases.deploymentadmin.tbc.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentAdminPermission#equals
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>equals<code> method of DeploymentAdminPermission, according to MEG reference
 *                     documentation.
 */
public class Equals {
	private DeploymentTestControl tbc;
	

	
	public Equals(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
		testEquals005();
		testEquals006();
		testEquals007();
	}

	/**
	 * @testID testEquals001
	 * @testDescription This method asserts that a DeploymentAdminPermission
	 *                  object is equal to another when its parameters are
	 *                  equals.
	 */
	public void testEquals001() {
		tbc.log("#testEquals001");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		tbc.assertTrue("Both DeploymentAdminPermission objects are equal when both target and action are equals",deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * @testID testEquals002
	 * @testDescription This method asserts that a DeploymentAdminPermission object is different to another 
	 * 					when its action is different. 
	 */
	public void testEquals002() {
		tbc.log("#testEquals002");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_CANCEL);
		tbc.assertTrue("Both DeploymentAdminPermission are different when the action is different",!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testEquals003
	 * @testDescription This method asserts that a DeploymentAdminPermission
	 *                  object is different to other when its signature is
	 *                  different.
	 */
	public void testEquals003() {
		tbc.log("#testEquals003");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_DIFFERENT_SIGNATURE,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		tbc.assertTrue("Both DeploymentAdminPermission are different when the signer filter is different",!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testEquals004
	 * @testDescription This method asserts a DeploymentAdminPermission object is different to another 
	 * 					when both target and action are differents.
	 */
	public void testEquals004() {
		tbc.log("#testEquals004");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME1,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_CANCEL);
		tbc.assertTrue("Both DeploymentAdminPermission when target and action are different",!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testEquals005
	 * @testDescription This method asserts that a DeploymentAdminPermission object is different to another 
	 * 					when its name is different. 
	 */
	public void testEquals005() {
		tbc.log("#testEquals005");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME1,org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
		tbc.assertTrue("Both DeploymentAdminPermission are different when the name filter is different",!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testEquals006
	 * @testDescription This test creates two DeploymentAdminPermission with
	 *                  same actions and different order, and assert they are
	 *                  equal.
	 */
	public void testEquals006() {
		tbc.log("#testEquals006");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,DeploymentAdminPermission.ACTION_INSTALL+","+DeploymentAdminPermission.ACTION_CANCEL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,DeploymentAdminPermission.ACTION_CANCEL+","+DeploymentAdminPermission.ACTION_INSTALL);
		tbc.assertTrue("Both DeploymentAdminPermission are equal when they have the same action in different orders",deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testEquals007
	 * @testDescription This test case creates two permissions where one have
	 *                  less actions than the other.
	 */
	public void testEquals007() {
		tbc.log("#testEquals007");
		try {
		DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,DeploymentAdminPermission.ACTION_INSTALL+","+DeploymentAdminPermission.ACTION_CANCEL);
		DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,DeploymentAdminPermission.ACTION_INSTALL);
		tbc.assertTrue("Both DeploymentAdminPermission are different",!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
}

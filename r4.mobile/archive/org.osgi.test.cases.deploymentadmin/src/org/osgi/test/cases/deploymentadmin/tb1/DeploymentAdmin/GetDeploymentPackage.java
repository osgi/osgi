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
 * ============  ====================================================================
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ====================================================================
 * Jun 06, 2005  Andre Assad
 * 111           Implement/Change test cases according to Spec updates of 6th of June
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentAdmin#getDeploymentPackage
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getDeploymentPackage<code> method, according to MEG reference
 *                     documentation (rfc0088).
 */
public class GetDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public GetDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetDeploymentPackage001();
		testGetDeploymentPackage002();
		testGetDeploymentPackage003();
		testGetDeploymentPackage004();
	}

	/**
	 * @testID testGetDeploymentPackage001
	 * @testDescription This test case installs a simple deployment package and then tries to get the deployment package
	 *                  without granting this caller the list permission. A SecurityException must be throw.
	 */
	private void testGetDeploymentPackage001() {
		tbc.log("#testGetDeploymentPackage001");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_INSTALL + "," + DeploymentAdminPermission.ACTION_UNINSTALL + "," + DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.getDeploymentAdmin().getDeploymentPackage(dp.getName());
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * @testID testGetDeploymentPackage002
	 * @testDescription This test case installs a simple deployment package and
	 *                  then checks if getDeploymentPackage returns the correct
	 *                  deployment package
	 */

	private void testGetDeploymentPackage002() {
		tbc.log("#testGetDeploymentPackage002");
		DeploymentPackage dpInstall = null, dpGet = null;
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dpInstall = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dpGet = tbc.getDeploymentAdmin().getDeploymentPackage(dpInstall.getName());
			tbc.assertEquals("Asserts that getDeploymentPackage returns the correct instance of a deployment package",dpInstall, dpGet);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dpInstall);
		}
	}
	/**
	 * @testID testGetDeploymentPackage003
	 * @testDescription Asserts that if there is no deployment package with a correct symbolic name, 
	 * 					null is returned from getDeploymentPackage
	 */
	private void testGetDeploymentPackage003() {
		tbc.log("#testGetDeploymentPackage003");
		DeploymentPackage dp = null;
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		try {
			dp = tbc.getDeploymentAdmin().getDeploymentPackage(DeploymentTestControl.INVALID_NAME);
			tbc.assertNull("Asserts that if there is no deployment package with the correct symbolic name, null is returned from getDeploymentPackage", dp);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * @testID testGetDeploymentPackage004
	 * @testDescription Asserts that IllegalArgumentException is thrown when the symbolic name is null
	 */
	private void testGetDeploymentPackage004() {
		tbc.log("#testGetDeploymentPackage004");
 		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		try {
			tbc.getDeploymentAdmin().getDeploymentPackage(null);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "IllegalArgumentException" }));			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"IllegalArgumentException", e.getClass().getName() }));
		}
	}	
}

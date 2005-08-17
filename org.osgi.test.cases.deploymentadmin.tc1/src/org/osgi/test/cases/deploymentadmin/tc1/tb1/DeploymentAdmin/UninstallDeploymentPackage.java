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
 * Aug 10, 2005  Luiz Felipe Guimaraes
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Class Validates the implementation of
 * <code>uninstall, uninstallForced<code> method, according to MEG reference
 * documentation.
 */
public class UninstallDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public UninstallDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testUninstallDeploymentPackage001();
		testUninstallDeploymentPackage002();
		testUninstallDeploymentPackage003();
		testUninstallDeploymentPackage004();
	}


	
	
	/**
	 * This test case installs a simple deployment package and
	 * then tries to uninstall without granting this caller the
	 * uninstall permission. A SecurityException must be throw.
	 * 
	 * @spec DeploymentPackage.uninstall()
	 */	
	private void testUninstallDeploymentPackage001() {
		tbc.log("#testUninstallDeploymentPackage001");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp.uninstall();
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
	 * This test case installs a simple deployment package and 
	 * then tries to uninstall forcefully without granting this caller 
	 * the uninstallForceful permission. A SecurityException must be throw.
	 * 
	 * @spec DeploymentPackage.uninstallForced()
	 */		
	private void testUninstallDeploymentPackage002() {
		tbc.log("#testUninstallDeploymentPackage002");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp.uninstallForced();
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_UNINSTALL+","+DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
		}
	}
	/**
	 * This test case uninstalls a simple deployment package,
	 * granting the correct "uninstall" permission.
	 * 
	 * @spec DeploymentPackage.uninstall()
	 */			
	private void testUninstallDeploymentPackage003() {
		tbc.log("#testUninstallDeploymentPackage003");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_UNINSTALL);
			dp.uninstall();
			tbc.pass("A DeploymentPackage could be uninstalled using only \"uninstall\" permission");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case uninstalls a simple deployment package,
	 * granting the correct "uninstall" permission.
	 * 
	 * @spec DeploymentPackage.uninstallForced()
	 */			
	private void testUninstallDeploymentPackage004() {
		tbc.log("#testUninstallDeploymentPackage004");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
			dp.uninstallForced();
			tbc.pass("A DeploymentPackage could be uninstalled forceful using only \"uninstallForced\" permission");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}

}

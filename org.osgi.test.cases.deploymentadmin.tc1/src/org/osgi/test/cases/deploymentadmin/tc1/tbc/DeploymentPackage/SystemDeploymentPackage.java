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
 * Jul 12, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Andre Assad
 *
 */
public class SystemDeploymentPackage {

	private DeploymentTestControl tbc;

	public SystemDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSystemDeploymentPackage001();
		testSystemDeploymentPackage002();
		testSystemDeploymentPackage003();
		testSystemDeploymentPackage004();
		testSystemDeploymentPackage005();
	}
	
	/**
	 * This test case asserts that the System Deployment Package with "System"
	 * name is always present. It also validates that the System Deployment
	 * Package must be contained in the list that is returned from the
	 * listDeploymentPackages method.
	 * 
	 * @spec 114.2.4 The System Deployment Package
	 */
	private void testSystemDeploymentPackage001() {
		tbc.log("#testSystemDeploymentPackage001");
		try {
			DeploymentPackage[] dp = tbc.getDeploymentAdmin().listDeploymentPackages();
			boolean found = false;
			for (int i = 0; !found && i < dp.length; i++) {
				if (dp[i].getName().equals(DeploymentConstants.SYSTEM_DP_NAME)) {
					found = true;
				}
			}
			tbc.assertTrue("The System Deployment Package is present in the framework", found);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * This test case asserts that the System Deployment Package with "System"
	 * is case sensitive 
	 * 
	 * @spec 114.2.4 The System Deployment Package
	 */
	private void testSystemDeploymentPackage002() {
		tbc.log("#testSystemDeploymentPackage002");
		try {
			DeploymentPackage[] dp = tbc.getDeploymentAdmin().listDeploymentPackages();
			boolean found = false;
			for (int i = 0; !found && i < dp.length; i++) {
				if (dp[i].getName().equals(DeploymentConstants.SYSTEM_DP_NAME.toUpperCase())) {
					found = true;
				}
			}
			tbc.assertTrue("The System Deployment Package is case sensitive",!found);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * This test case asserts that the System Deployment Package version must be
	 * zero.
	 * 
	 * @spec 114.2.4 The System Deployment Package
	 */
	private void testSystemDeploymentPackage003() {
		tbc.log("#testSystemDeploymentPackage003");
		try {
			DeploymentPackage[] dp = tbc.getDeploymentAdmin().listDeploymentPackages();
			Version versionFound = null;
			boolean found = false;
			for (int i = 0; !found && i < dp.length; i++) {
				if (dp[i].getName().equals(DeploymentConstants.SYSTEM_DP_NAME)) {
					found = true;
					versionFound = dp[i].getVersion();
				}
			}
	
			tbc.assertTrue("The System Deployment Package is present in the framework",found);
			tbc.assertEquals("System Deployment Package version is zero", Version.emptyVersion, versionFound);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
		
	/**
	 * Asserts that it must be an error to install or update a Deployment Package with the
	 * name system
	 * 
	 * @spec 114.2.4 The System Deployment Package
	 */
	private void testSystemDeploymentPackage004() {
		tbc.log("#testSystemDeploymentPackage004");
		DeploymentPackage sysDP = null;
		try {
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SYSTEM_DP);
		sysDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
		tbc.fail("No exception thrown when installing a Deployment Package with name symbolic name like system");
			
		} catch (DeploymentException e) {
			tbc.pass("Failed to install/update the deployment package");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sysDP);
		}
	}
	
	/**
	 * Asserts that it must be an error if an attempt is made to delete the
	 * System Deployment Package.
	 * 
	 * @spec 114.2.4 The System Deployment Package
	 */
	private void testSystemDeploymentPackage005() {
		tbc.log("#testSystemDeploymentPackage005");
		DeploymentPackage sysDP = null;
		try {
			DeploymentPackage[] dp = tbc.getDeploymentAdmin().listDeploymentPackages();
			boolean found = false;
			for (int i = 0; !found && i < dp.length; i++) {
				if (dp[i].getName().equals(DeploymentConstants.SYSTEM_DP_NAME)) {
					sysDP = dp[i];
					found = true;
				}
			}
			tbc.assertTrue("The System Deployment Package is present in the framework",found);
			sysDP.uninstall();
			tbc.failException("",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.pass("It correcty failed uninstalling the System Deployment Package");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
}

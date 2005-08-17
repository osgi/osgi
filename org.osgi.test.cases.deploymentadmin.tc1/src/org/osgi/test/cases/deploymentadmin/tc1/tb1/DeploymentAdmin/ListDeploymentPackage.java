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
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ==============================================================
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentException;
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
 * <code>listDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class ListDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public ListDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testListDeploymentPackage001();
		testListDeploymentPackage002();
		testListDeploymentPackage003();
	}

	/**
	 * Asserts that if there are no deployment packages
	 * installed it gives back an empty array.
	 * 
	 * @spec DeploymentAdmin.listDeploymentPackage()
	 */			
	private void testListDeploymentPackage001() {
		tbc.log("#testListDeploymentPackage001");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		try {
			tbc.assertTrue("Asserts that all of the deployment package had been uninstalled.",uninstallAllDeploymentPackages());
			DeploymentPackage dps[] = tbc.getDeploymentAdmin().listDeploymentPackages();
			tbc.assertTrue("Asserts if there are no deployment packages installed it will give back an empty array.",dps.length==0);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Installs a deployment package and assert that an array of
	 * DeploymentPackage objects representing all the installed
	 * deployment packages is returned. After that, uninstall the
	 * deployment package and asserts that it was uninstalled.
	 * 
	 * @spec DeploymentAdmin.listDeploymentPackage()
	 */			
	private void testListDeploymentPackage002() {
		tbc.log("#testListDeploymentPackage002");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		int initialNumberOfPackages;
		int finalNumberOfPackages;
		boolean found;
		try {
			//Installs a deployment package
			initialNumberOfPackages = tbc.getDeploymentAdmin().listDeploymentPackages().length;
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			DeploymentPackage dps[] = tbc.getDeploymentAdmin().listDeploymentPackages();
			finalNumberOfPackages = dps.length;
			tbc.assertTrue("Asserts that after installing a deployment package there are more deployment packages installed than before",(finalNumberOfPackages==(initialNumberOfPackages+1)));
			
			found = false;
			for(int i=0; i<dps.length && !found ;i++) {
				if (dps[i].equals(dp)) {
					found = true;
				}
			}
			tbc.assertTrue("Asserts that a deployment package installed is really returned by the listDeploymentPackages",found);

			//Uninstalls a deployment package
			tbc.uninstall(dp);
			
			dps = tbc.getDeploymentAdmin().listDeploymentPackages();
			finalNumberOfPackages = dps.length;
			tbc.assertTrue("Asserts that after uninstalling a deployment package there are the same number of DP again",finalNumberOfPackages==initialNumberOfPackages);
			
			found = false;
			for(int i=0; i<dps.length && !found; i++) {
				if (dps[i].getName().equals(testDP.getName())) {
					found = true;
				}
			}
			tbc.assertTrue("Asserts that a deployment package uninstalled is not returned by the listDeploymentPackages",!found);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Asserts if the installation fails, the source must never become
	 * visible, also not transiently.
	 * 
	 * @spec DeploymentAdmin.listDeploymentPackage()
	 */			
	private void testListDeploymentPackage003() {
		tbc.log("#testListDeploymentPackage003");

		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_VERSION_DP);
		DeploymentPackage dp = null;
		boolean found = false;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", Exception.class);
		} catch (Exception e) {
			DeploymentPackage[] list = tbc.getDeploymentAdmin().listDeploymentPackages();
			for (int i=0; !found && i<list.length; i++) {
				if (list[i].getName().equals(testDP.getName())){
					found = true;
				}
			}
			tbc.assertTrue("The installed deployment package is not visible", !found);
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	//Returns if all deployment packages could be uninstalled.
	private boolean uninstallAllDeploymentPackages() {
		boolean passed = true;
		DeploymentPackage dps[] = tbc.getDeploymentAdmin().listDeploymentPackages();
		for(int i=0; i<dps.length; i++) {
			try {
				if (!dps[i].getName().equals(DeploymentConstants.SYSTEM_DP_NAME))
					dps[i].uninstall();
			} catch (DeploymentException e) {
				passed = passed && dps[i].uninstallForced();
			} catch (Exception e) {
				passed = false;
			}
		}
		return passed;
	}
}

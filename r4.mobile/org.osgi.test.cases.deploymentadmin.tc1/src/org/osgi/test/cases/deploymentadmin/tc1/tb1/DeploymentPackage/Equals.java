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
 * Apr 14, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * May 26, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;



import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 *
 * This Test Class Validates the implementation of <code>equals</code> method of DeploymentPackage,
 * according to MEG specification.
 */

public class Equals implements TestInterface {
	
	private DeploymentTestControl tbc;
	
	public Equals(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		prepare();
		testEquals001();
		testEquals002();
		testEquals003();
	}
	
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }
	/**
	 * Asserts that two deployment packages are equal when they have the same name and version
	 * 
	 * @spec DeploymentPackage.equals(Object)
	 */
	private void testEquals001() {
		tbc.log("#testEquals001");
		DeploymentPackage dp = null, dp2 = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);

			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp2 = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			
			tbc.assertTrue("Asserts that two deployment packages are equal when they have the same name and version", dp.equals(dp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			prepare();
			tbc.uninstall(dp);
		}
	}

	/**
	 * Asserts that two deployment packages are different when they have the same version and different names
	 * 
	 * @spec DeploymentPackage.equals(Object) 
	 */
	private void testEquals002() {
		tbc.log("#testEquals002");		
		DeploymentPackage dp = null, dp2 = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP);

			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp2 = tbc.getDeploymentAdmin().getDeploymentPackage(testRP.getName());
			
			tbc.assertTrue("Asserts that two deployment packages are different when they have the same version and different names ", !dp.equals(dp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	/**
	 * Asserts that two deployment packages are different when they have different versions and names
	 * It also assures that no DeploymentAdminPermission is needed.
	 * 
	 * @spec DeploymentPackage.equals(Object)
	 */
	private void testEquals003() {
		tbc.log("#testEquals003");
		DeploymentPackage dp = null, dp2 = null, rp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_DP);
			TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
			
			rp = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			
			tbc.setMininumPermission();
			//Check whether no DeploymentAdminPermission is needed when calling this method.
			tbc.assertTrue("Asserts that two deployment packages are different when they have different versions and names",
							!dp.equals(dp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			prepare();
			tbc.uninstall(new DeploymentPackage[] { rp, dp, dp2 });
			
		}
	}	
}

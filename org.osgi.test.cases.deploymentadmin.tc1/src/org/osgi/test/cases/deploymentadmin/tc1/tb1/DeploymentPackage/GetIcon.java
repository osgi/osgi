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
package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;

import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Stoyan Boshev
 *
 * This Test Class Validates the implementation of <code>getIcon</code> method of DeploymentPackage, 
 * according to the Mobile specification.
 */

public class GetIcon implements TestInterface {
	
	private DeploymentTestControl tbc;
	
	public GetIcon(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		prepare();
		testGetIcon001();
		testGetIcon002();
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
    
	/**
	 * Asserts that it returns the URL of the icon of the deployment package when it is set correctly in the DP's headers
	 * 
	 * @spec DeploymentPackage.getIcon()
	 */
	private void testGetIcon001() {
		tbc.log("#testGetIcon001");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP2);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			
			tbc.setMininumPermission();
			tbc.assertNotNull("The deployment package returns icon URL", dp.getIcon());
			
			InputStream is = null;
			try {
				is = dp.getIcon().openStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			tbc.assertNotNull("The deployment package icon URL can be read ", is);
			is.close();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			prepare();
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Asserts that it does not return an URL of the icon of the deployment package when it is not set in the DP's headers
	 * 
	 * @spec DeploymentPackage.getIcon()
	 */
	private void testGetIcon002() {
		tbc.log("#testGetIcon002");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			
			tbc.assertNull("The deployment package must return null for icon URL", dp.getIcon());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			tbc.uninstall(dp);
		}
	}
}

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
 * 14/04/2005    Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * May 26, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;



import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getBundle
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getBundle</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetBundle {
	private DeploymentTestControl tbc;
	
	public GetBundle(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		testGetBundle001();
		testGetBundle002();
	}
	
	/**
	 * @testID testGetBundle001
	 * @testDescription Asserts that a bundle instance is part of the installed deployment package.
	 */
	private void testGetBundle001() {
		tbc.log("#testGetBundle001");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingBundle[] testBundle = testDP.getBundles();
			Bundle bundle = dp.getBundle(testBundle[0].getName());
			tbc.assertNotNull("Bundle "+ testBundle[0].getName() +" is part of the deployment package " + testBundle[0].getName(), bundle.getSymbolicName());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}

	/**
	 * @testID testGetBundle002
	 * @testDescription Asserts that a null is returned when a bundle that are not part 
	 * 					of the deployment package is passed as symbolicName parameter
	 */
	private void testGetBundle002() {
		tbc.log("#testGetBundle002");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			Bundle bundle = dp.getBundle(DeploymentTestControl.INVALID_NAME);
			tbc.assertNull("Bundle "+ DeploymentTestControl.INVALID_NAME +" is not part of the deployment package ", bundle);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
}

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
 * 27 May, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getBundleSymNameVersionPairs
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getBundleSymNameVersionPairs</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetBundleSymNameVersionPairs {
	private DeploymentTestControl tbc;
	
	public GetBundleSymNameVersionPairs(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		testGetBundleSymNameVersionPairs001();
		testGetBundleSymNameVersionPairs002();
	}
	/**
	 * @testID testGetBundleSymNameVersionPairs001
	 * @testDescription Asserts that it returns the requested bundle/version pair
	 */
	private void testGetBundleSymNameVersionPairs001() {
		tbc.log("#testGetBundleSymNameVersionPairs001");
		DeploymentPackage dp = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingBundle[] testBundle = testDP.getBundles();
			String[][] symNameVersion = dp.getBundleSymNameVersionPairs();
			
			int count = 0;
			for (int i = 0; i < testBundle.length; i++) {
				for (int j=0; j<symNameVersion.length; j++) {
					if (testBundle[i].getName().equals(symNameVersion[j][0])
							&& testBundle[i].getVersionString().equals(symNameVersion[j][1])) {
						count++;
						break;
					}
				}
			}
			tbc.assertEquals("The Deployment Package returned as many bundles/versions as exists in the testing deployment package", testBundle.length, count);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	/**
	 * @testID testGetBundleSymNameVersionPairs002
	 * @testDescription Asserts that it returns a zero dimensional array when there is no bundles.
	 */
	private void testGetBundleSymNameVersionPairs002() {
		tbc.log("#testGetBundleSymNameVersionPairs002");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NO_BUNDLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			String[][] symNameVersion = dp.getBundleSymNameVersionPairs();
			tbc.assertTrue("Asserts that it returns a zero dimensional array when there is no bundles.",symNameVersion.length == 0);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}		
}

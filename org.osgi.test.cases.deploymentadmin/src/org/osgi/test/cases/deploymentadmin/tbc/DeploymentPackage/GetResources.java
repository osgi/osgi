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
 */

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResource;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getResources
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getResources</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetResources {
	private DeploymentTestControl tbc;
	
	public GetResources(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		testGetResources001();
		testGetResources002();
	}
	
	/**
	 * @testID testGetResources001
	 * @testDescription Asserts that it returns an array of strings representing the resources 
	 * 					that are specified in the manifest of the deployment package 
	 */
	private void testGetResources001() {
		tbc.log("#testGetResources001");
		DeploymentPackage dp = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_RESOURCE_PROCESSOR_DP);
			TestingResource testResource[] = testDP.getResources(); 
			
			// using the pre-installed resource processor
			dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			String[] resources = dp.getResources();

			boolean passed = false;
			int resourceLengthExpected  = testResource.length;
			int resourceLengthGot = resources.length;
			boolean found[] = new boolean[resourceLengthGot];
			
			if (resourceLengthExpected == resourceLengthGot) {
				for (int i=0;i<resourceLengthExpected;i++)
				{
					for (int j=0;j<resourceLengthGot;j++) {
						if(resources[j].equals(testResource[i])) {
							found[i]=true;
							break;
						}
					}
				}
				
				passed = true;
				for (int i=0;i<found.length;i++) {
					if (!found[i]) {
						passed=false;
						break;
					}
				}
			}
			
			tbc.assertTrue("Asserts that it returns the requested resources.",passed);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}

	/**
	 * @testID testGetResources002
	 * @testDescription Asserts that it returns an array zero dimensional if there is no resources
	 */
	private void testGetResources002() {
		tbc.log("#testGetResources002");
		DeploymentPackage dp = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NO_RESOURCE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());			
			String[] resources = dp.getResources();
			tbc.assertTrue("Asserts that it returns the requested resources",resources.length==0);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
		
	}
}

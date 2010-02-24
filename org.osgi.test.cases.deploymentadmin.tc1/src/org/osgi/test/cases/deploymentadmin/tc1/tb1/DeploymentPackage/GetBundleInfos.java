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
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 *
 * This Test Class Validates the implementation of <code>getBundleSymNameVersionPairs</code> method, 
 * according to MEG specification.
 */

public class GetBundleInfos implements TestInterface  {
	private DeploymentTestControl tbc;
	
	public GetBundleInfos(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		prepare();
		testGetBundleInfos001();
		testGetBundleInfos002();
		testGetBundleInfos003();
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
	/**
	 * Asserts that it returns a two-dimensional array of strings representing the bundles and their version 
	 * that are specified in the manifest  
	 * 
	 * DeploymentPackage.getBundleInfos()
	 */
	private void testGetBundleInfos001() {
		tbc.log("#testGetBundleInfos001");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		DeploymentPackage dp = null;		
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			BundleInfo[] symNameVersion = dp.getBundleInfos();
			TestingBundle[] testBundle = testDP.getBundles();
			
			int count = 0;
			for (int i = 0; i < testBundle.length; i++) {
				for (int j=0; j < symNameVersion.length; j++) {
					if (testBundle[i].getName().equals(symNameVersion[j].getSymbolicName())
							&& testBundle[i].getVersion().equals(symNameVersion[j].getVersion())) {
						count++;
						break;
					}
				}
			}
			tbc.assertEquals("The Deployment Package returned as many bundles/versions as exists in the testing deployment package", testBundle.length, count);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			tbc.uninstall(dp);
		}
	}
	/**
	 * Asserts that it returns a zero dimensional array when there are no bundles.
	 * It also tests if only DeploymentAdminPermission with "metadata" is needed.
	 * 
	 * @spec DeploymentPackage.getBundleInfos()
	 */
	private void testGetBundleInfos002() {
		tbc.log("#testGetBundleInfos002");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_NO_BUNDLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
		DeploymentPackage dp = null, rp = null;
		try {
			rp = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(testDP.getName()), DeploymentAdminPermission.METADATA);
			BundleInfo[] symNameVersion = dp.getBundleInfos();
			tbc.assertTrue("Asserts that it returns a zero dimensional array when there is no bundles.",symNameVersion.length == 0);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			prepare();
			tbc.uninstall(new DeploymentPackage[] { rp, dp });
		}
	}	
	/**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action 
	 * 
	 * @spec DeploymentPackage.getBundleInfos()
	 */
	private void testGetBundleInfos003() {
		tbc.log("#testGetBundleInfos003");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			tbc.setMininumPermission();
			dp.getBundleInfos();
			
			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }),e);
		} finally {
			prepare();
			tbc.uninstall(dp);
		}
	}
}

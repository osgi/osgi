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
 * Mar 31, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tb1.DeploymentAdmin;

import java.util.Vector;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResource;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentAdmin#installDeploymentPackage
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>installDeploymentPackage<code> method, according to MEG reference
 *                     documentation (rfc0088).
 */
public class InstallFixPack implements TestInterface {
	
	private DeploymentTestControl tbc;
	
	public InstallFixPack(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testInstallFixPack001();
		testInstallFixPack002();
		testInstallFixPack003();
		testInstallFixPack004();
		testInstallFixPack005();
		testInstallFixPack006();
		testInstallFixPack007();
		testInstallFixPack008();
		testInstallFixPack009();
		testInstallFixPack010();
		testInstallFixPack011();
		testInstallFixPack012();
		testInstallFixPack013();
	}
	
	/**
	 * @testID testInstallFixPack001
	 * @testDescription This test case install a simple fix pack for a range of
	 *                  deployment packages, which updates the version of the
	 *                  first bundle in the deployment package.
	 */
	private void testInstallFixPack001() {
		tbc.log("#testInstallFixPack001");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			String[][] pairs =  dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0][0]);
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), new Version(pairs[0][1]));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack002
	 * @testDescription This test case uninstall all bundles from a deployment
	 *                  package, which initially had two bundles.
	 */
	private void testInstallFixPack002() {
		tbc.log("#testInstallFixPack002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.MISSING_BUNDLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// This deployment package had two bundles; after the fix-pack installation it must have none.			
			String[][] pairs = dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("All bundles in the deployment package have been removed "+testDP.getName(), 0, pairs.length);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack003
	 * @testDescription This test case installs a fix-pack that adds a bundles
	 *                  to the fixed deployment package.
	 */
	private void testInstallFixPack003() {
		tbc.log("#testInstallFixPack003");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.ADD_BUNDLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// This deployment package had two bundles; after the fix-pack installation it must have three.
			String[][] pairs = dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("There are three bundles in "+testDP.getName(), 3, pairs.length);
			TestingBundle[] bundles = (TestingBundle[])addArrays(testDP.getBundles(), testFixDP.getBundles());
			// asserts the name and version of the installed bundles
			int count = 0;
			for(int i=0; i<pairs.length; i++) {
				for(int j=0; j<bundles.length; j++) {
					if (pairs[i][0].equals(bundles[j].getName())) {
						tbc.assertEquals("The bundles "+bundles[j].getName() + "is installed in "+dp.getName(), bundles[j].getName(), pairs[i][0]);
						tbc.assertEquals("The bundles "+bundles[j].getName() + " version is the same as "+pairs[i][0] + " version", bundles[j].getVersion(), pairs[i][1]);
						count++;
					}
				}
			}
			// there are at least three bundles that match symbolic names with installed ones
			tbc.assertTrue("There are at least three bundles that matches symbolic names", count>=3);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack004
	 * @testDescription This test case install a simple fix pack for a higher
	 *                  micro version of deployment package within range of
	 *                  deployment packages defined in the manifest.
	 */
	private void testInstallFixPack004() {
		tbc.log("#testInstallFixPack004");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MICRO_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			String[][] pairs =  dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0][0]);
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0][1]);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack005
	 * @testDescription This test case install a simple fix pack for a higher
	 *                  minor version of deployment package within range of
	 *                  deployment packages defined in the manifest.
	 * 
	 */
	private void testInstallFixPack005() {
		tbc.log("#testInstallFixPack005");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MINOR_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			String[][] pairs =  dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0][0]);
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0][1]);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack006
	 * @testDescription This test case install a simple fix pack for a higher
	 *                  major version of deployment package within range of
	 *                  deployment packages defined in the manifest.
	 * 
	 */
	private void testInstallFixPack006() {
		tbc.log("#testInstallFixPack006");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			String[][] pairs =  dp.getBundleSymNameVersionPairs();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0][0]);
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0][1]);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack007
	 * @testDescription This test case install a simple fix pack for a range of
	 *                  deployment packages higher than the version of the
	 *                  installed deployment package.
	 * 
	 */
	private void testInstallFixPack007() {
		tbc.log("#testInstallFixPack007");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.FIX_PACK_HIGHER_RANGE_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack008
	 * @testDescription This test case installs a simple fix pack without any
	 *                  target deployment package. In order to check if
	 *                  DeploymentException with CODE_MISSING_FIXPACK_TARGET
	 *                  code is thrown.
	 */
	private void testInstallFixPack008() {
		tbc.log("#testInstallFixPack008");
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage fixDP = null;
		try {
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack009
	 * @testDescription This test case install a simple fix pack that adds a
	 *                  resource for a deployment package.
	 * 
	 */
	private void testInstallFixPack009() {
		tbc.log("#testInstallFixPack009");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingResource[] testRes = testDP.getResources();
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.ADD_RESOURCE_FIX_PACK);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			//TODO the instalation is faling
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			String[] res = dp.getResources();
			boolean found = false;
			for (int i=0; i < res.length; i++) {
				for (int j=0; (j < testRes.length); j++) {
					if (res[i].equals(testRes[j].getName())) {
						found = true;
					}
				}
			}
			tbc.assertTrue("The resource was added to the deployment package", found);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}

	/**
	 * @testID testInstallFixPack010
	 * @testDescription This test case install a simple fix pack that uninstalls
	 *                  a resource from a deployment package.
	 */
	private void testInstallFixPack010() {
		tbc.log("#testInstallFixPack010");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_RESOURCE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.MISSING_RESOURCE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);

			String[] res = dp.getResources();
			tbc.assertEquals("The resource has been uninstalled", 0, res.length);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack011
	 * @testDescription This test case installs a simple fix pack that uninstalls
	 *                  a resource from a deployment package, which had no resources.
	 */
	private void testInstallFixPack011() {
		tbc.log("#testInstallFixPack011");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NO_RESOURCE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.MISSING_RESOURCE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_RESOURCE, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack012
	 * @testDescription This test case install a simple fix pack that uninstalls
	 *                  a bundle from a deployment package, which had no bundle.
	 * 
	 */
	private void testInstallFixPack012() {
		tbc.log("#testInstallFixPack012");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NO_BUNDLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.MISSING_BUNDLE_FIX_PACK_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_BUNDLE, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @testID testInstallFixPack013
	 * @testDescription This test case install a simple fix pack for a range of
	 *                  deployment packages below the version of the
	 *                  installed deployment package.
	 * 
	 */
	private void testInstallFixPack013() {
		tbc.log("#testInstallFixPack013");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.FIX_PACK_LOWER_RANGE_DP);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * @param bundles
	 * @param bundles2
	 * @return Object array
	 */
	private Object[] addArrays(TestingBundle[] bundles, TestingBundle[] bundles2) {
		Vector res = new Vector();
		for(int i=0; i<bundles.length; i++) {
			res.add(bundles[i]);
		}
		for(int i=0; i<bundles2.length; i++) {
			res.add(bundles2[i]);
		}
		return res.toArray();
	}
}

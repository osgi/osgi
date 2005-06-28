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
 * 26            Implement MEG TCK
 * ============  ==============================================================
 * Mar 31, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ==============================================================
 * Apr 28, 2005  Eduardo Oliveira
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ============================================================== 
 */

package org.osgi.test.cases.deploymentadmin.tb1.DeploymentAdmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.Event.BundleEventHandlerImpl;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentAdmin#installDeploymentPackage
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>installDeploymentPackage<code> method, according to MEG reference
 *                     documentation (rfc0088).
 */
public class InstallDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public InstallDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testInstallDeploymentPackage001();
		testInstallDeploymentPackage002();
		testInstallDeploymentPackage003();
//		testInstallDeploymentPackage004();
		testInstallDeploymentPackage005();
		testInstallDeploymentPackage006();
		testInstallDeploymentPackage007();
		testInstallDeploymentPackage008();
//		testInstallDeploymentPackage009();
//		testInstallDeploymentPackage010();
//		testInstallDeploymentPackage011();
		testInstallDeploymentPackage012();
//		testInstallDeploymentPackage013();
		testInstallDeploymentPackage014();
		testInstallDeploymentPackage015();
		testInstallDeploymentPackage016();
		testInstallDeploymentPackage017();
		testInstallDeploymentPackage018();
		testInstallDeploymentPackage019();
//		testInstallDeploymentPackage020();
//		testInstallDeploymentPackage021();
		testInstallDeploymentPackage022();
		testInstallDeploymentPackage023();
	}

	/**
	 * @testID testInstallDeploymentPackage001
	 * @testDescription This test case installs a simple deployment package,
	 *                  without granting this caller the
	 *                  installDeploymentPackage permission. The deployment
	 *                  package returned must throw SecurityException.
	 */
	private void testInstallDeploymentPackage001() {
		tbc.log("#testInstallDeploymentPackage001");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_LIST);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
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
	 * @testID testInstallDeploymentPackage002
	 * @testDescription This test case installs a simple deployment package,
	 *                  granting the correct "install", and then unintalls
	 *                  granting "uninstall" permission.
	 */
	private void testInstallDeploymentPackage002() {
		tbc.log("#testInstallDeploymentPackage002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			
			Bundle b = tbc.getBundle(testDP.getBundles()[0].getName());
			tbc.assertNotNull("Deployment package bundle "+testDP.getBundles()[0].getName()+" was correctly installed in the framework ", b);
			
			b = tbc.getBundle(testDP.getBundles()[1].getName());
			tbc.assertNotNull("Deployment package bundle "+testDP.getBundles()[1].getName()+" was correctly installed in the framework ", b);
			
			tbc.assertEquals("Asserts the Deployment Package name ", testDP.getName(), dp.getName());
			tbc.assertEquals("Asserts the Deployment Package version ", testDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_UNINSTALL);
			tbc.uninstall(dp);
		}
	}

	/**
	 * @testID testInstallDeploymentPackage003
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with the same deployment package
	 *                  name/version, in order to check if it no action was
	 *                  taken.
	 */
	private void testInstallDeploymentPackage003() {
		tbc.log("#testInstallDeploymentPackage003");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP_CLONE);
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			DeploymentPackage[] initialList = tbc.getDeploymentAdmin().listDeploymentPackages();
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			// do not assert not null; the implementation may cancel the installation
			DeploymentPackage[] finalList = tbc.getDeploymentAdmin().listDeploymentPackages();
			
			tbc.assertTrue("Only one deployment package was added to the list of DPs", (initialList.length==(finalList.length-1)));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			// just the first dp must be unistalled
			tbc.uninstall(dp1);
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage004
	 * @testDescription This test case attempts to pass a null input stream to
	 *                  installDeploymentPackage method to verify its behaviour.
	 */
	private void testInstallDeploymentPackage004() {
		//TODO the bahavior of passing null is not specified.
		tbc.log("#testInstallDeploymentPackage004");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			dp = tbc.getDeploymentAdmin().installDeploymentPackage(null);
			tbc.assertNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[]{"deployment package"}), dp);
		} catch (DeploymentException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage005
	 * @testDescription Asserts that a bundle must belong to one and only one deployment package throwing 
	 * 					DeploymentException.CODE_BUNDLE_SHARING_VIOLATION
	 */
	private void testInstallDeploymentPackage005() {
		tbc.log("#testInstallDeploymentPackage005");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage dp2 = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(dp2);
		}
	}

	/**
	 * @testID testInstallDeploymentPackage006
	 * @testDescription Asserts that a bundle must belong to only one deployment
	 *                  package, even if its version is different. A deployment
	 *                  package that installs a bundle that was already in the
	 *                  framework should throw
	 *                  DeploymentException.CODE_BUNDLE_SHARING_VIOLATION.
	 */
	private void testInstallDeploymentPackage006() {
		tbc.log("#testInstallDeploymentPackage006");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage dp2 = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(dp2);
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage007
	 * @testDescription Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
	 * 					mandatory manifest header is missing. 
	 */
	private void testInstallDeploymentPackage007() {
		tbc.log("#testInstallDeploymentPackage007");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.MISSING_NAME_HEADER_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}	

	/**
	 * @testID testInstallDeploymentPackage008
	 * @testDescription Asserts that a DeploymentException.CODE_BUNDLE_NAME_ERROR is thrown when
	 * 					bundle symbolic name is not the same as defined by the deployment package manifest
	 */
	private void testInstallDeploymentPackage008() {
		tbc.log("#testInstallDeploymentPackage008");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_NAME_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	/**
	 * @testID testInstallDeploymentPackage009
	 * @testDescription Asserts that a
	 *                  DeploymentException.CODE_RESOURCE_SHARING_VIOLATION is
	 *                  thrown when a resource already exists.
	 */
	private void testInstallDeploymentPackage009() {
		tbc.log("#testInstallDeploymentPackage009");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null, dp2 = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_RESOURCE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());			
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.RESOURCE_FROM_OTHER_DP);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_RESOURCE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			tbc.uninstall(dp2);			
		}
	}	

	/**
	 * @testID testInstallDeploymentPackage010
	 * @testDescription Asserts that an event is sent when a installation is started.
	 */
	private synchronized void testInstallDeploymentPackage010() {
		tbc.log("#testInstallDeploymentPackage010");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		BundleEventHandlerImpl bundleEventHandler = tbc.getBundleEventHandler();
		// tell BundleEventHandler to process any deployment event
		bundleEventHandler.setSychronizing(true);
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			synchronized (bundleEventHandler) {
				wait(DeploymentTestControl.TIMEOUT);
				String prop = (String) bundleEventHandler.getProperty("deploymentpackage.name");
				tbc.assertTrue("An install event occured", bundleEventHandler.isInstall());
				tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), prop);
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			bundleEventHandler.reset();
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage011
	 * @testDescription Asserts that an event is sent when a uninstallation is started.
	 */
	private synchronized void testInstallDeploymentPackage011() {
		tbc.log("#testInstallDeploymentPackage011");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		BundleEventHandlerImpl bundleEventHandler = tbc.getBundleEventHandler();
		// tell BundleEventHandler to process any deployment event
		bundleEventHandler.setSychronizing(true);
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			dp.uninstall();
			
			synchronized (bundleEventHandler) {
				wait(DeploymentTestControl.TIMEOUT);
				String prop = (String) bundleEventHandler.getProperty("deploymentpackage.name");
				tbc.assertTrue("An uninstall event occured", bundleEventHandler.isUninstall());
				tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), prop);
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			bundleEventHandler.reset();
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage012
	 * @testDescription Asserts that an event is sent when a installation is completed.
	 */
	private synchronized void testInstallDeploymentPackage012() {
		tbc.log("#testInstallDeploymentPackage012");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		BundleEventHandlerImpl bundleEventHandler = tbc.getBundleEventHandler();
		// tell BundleEventHandler to process any deployment event
		bundleEventHandler.setSychronizing(true);
		bundleEventHandler.setHandlingComplete(true);
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			synchronized (bundleEventHandler) {
				wait(DeploymentTestControl.TIMEOUT);
				String dpNameProp = (String) bundleEventHandler.getProperty("DPname");
				Boolean successProp = (Boolean) bundleEventHandler.getProperty("successful");
				tbc.assertTrue("A complete event occured", bundleEventHandler.isComplete());
				tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
				tbc.assertTrue("The installation of deployment package was successfull ", successProp.booleanValue());
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			bundleEventHandler.reset();
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage013
	 * @testDescription Asserts that an event is sent when an uninstallation is completed.
	 */
	private synchronized void testInstallDeploymentPackage013() {
		tbc.log("#testInstallDeploymentPackage013");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		DeploymentPackage dp = null;
		BundleEventHandlerImpl bundleEventHandler = tbc.getBundleEventHandler();
		// tell BundleEventHandler to process any deployment event
		bundleEventHandler.setSychronizing(true);
		bundleEventHandler.setHandlingComplete(true);
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			dp.uninstall();
			
			synchronized (bundleEventHandler) {
				wait(DeploymentTestControl.TIMEOUT);
				String dpNameProp = (String) bundleEventHandler.getProperty("DPname");
				Boolean successProp = (Boolean) bundleEventHandler.getProperty("successful");
				tbc.assertTrue("A complete event occured", bundleEventHandler.isComplete());
				tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
				tbc.assertTrue("The installation of deployment package was successfull ", successProp.booleanValue());
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			bundleEventHandler.reset();
		}
	}	
	
	/**
	 * @testID testInstallDeploymentPackage014
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a higher
	 *                  major version, in order to check if it is updated.
	 */
	private void testInstallDeploymentPackage014() {
		tbc.log("#testInstallDeploymentPackage014");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename(), testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The major version of the bundle was updated with a higher version", testDP2.getVersion().getMajor(), version.getMajor());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage015
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a
	 *                  lower major version, in order to check if the deployment
	 *                  package version was decreased.
	 */
	private void testInstallDeploymentPackage015() {
		tbc.log("#testInstallDeploymentPackage015");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The major version of the bundle was updated with a lower version", testDP2.getVersion().getMajor(), version.getMajor());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage016
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a
	 *                  higher micro version, in order to check if it the
	 *                  version was increased.
	 */
	private void testInstallDeploymentPackage016() {
		tbc.log("#testInstallDeploymentPackage016");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MICRO_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename(), testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The micro version of the bundle was updated with a higher version", testDP2.getVersion().getMicro(), version.getMicro());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}

	/**
	 * @testID testInstallDeploymentPackage017
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a lower
	 *                  micro version, in order to check if the version was decreased.
	 */
	private void testInstallDeploymentPackage017() {
		tbc.log("#testInstallDeploymentPackage017");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MICRO_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The micro version of the bundle was updated with a lower version", testDP2.getVersion().getMicro(), version.getMicro());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage018
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a higher
	 *                  minor version, in order to check if it the version was increased.
	 */
	private void testInstallDeploymentPackage018() {
		tbc.log("#testInstallDeploymentPackage018");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MINOR_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The minor version of the bundle was updated with a higher version", testDP2.getVersion().getMinor(), version.getMinor());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage019
	 * @testDescription This test case installs a simple deployment package and
	 *                  then updates it with another deployment package with a lower
	 *                  minor version, in order to check if the version was decreased.
	 */
	private void testInstallDeploymentPackage019() {
		tbc.log("#testInstallDeploymentPackage019");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_HIGHER_MINOR_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The minor version of the bundle was updated with a lower version", testDP2.getVersion().getMinor(), version.getMinor());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage020
	 * @testDescription This test case installs a deployment package without
	 *                  signing its bundles.
	 */
	private void testInstallDeploymentPackage020() {
		tbc.log("#testInstallDeploymentPackage020");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NONSIGNED_BUNDLE_DP);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Deployment Exception thrown signing code error", DeploymentException.CODE_SIGNING_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * @testID testInstallDeploymentPackage021
	 * @testDescription This test case installs a unsigned deployment package.
	 */
	private void testInstallDeploymentPackage021() {
		tbc.log("#testInstallDeploymentPackage021");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_NONSIGNED_DP);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("DeploymentException thrown signing code error", DeploymentException.CODE_SIGNING_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}

	/**
	 * @testID testInstallDeploymentPackage022
	 * @testDescription This test case installs a simple deployment package and then tries to uninstall
	 *                  without granting this caller the uninstall permission. A SecurityException must be throw.
	 */
	private void testInstallDeploymentPackage022() {
		tbc.log("#testInstallDeploymentPackage022");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp.uninstall();
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			if (dp!=null && !dp.isStale()) {
				tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_UNINSTALL+","+DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
				tbc.uninstall(dp);
			}
		}
	}	
	/**
	 * @testID testInstallDeploymentPackage023
	 * @testDescription This test case installs a simple deployment package and then tries to uninstall forcefully
	 *                  without granting this caller the uninstallForceful permission. A SecurityException must be throw.
	 */
	private void testInstallDeploymentPackage023() {
		tbc.log("#testInstallDeploymentPackage023");
		tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_INSTALL);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp.uninstallForced();
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			if (dp!=null && !dp.isStale()) {
				tbc.setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_UNINSTALL+","+DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
				tbc.uninstall(dp);
			}
		}
	}		
}

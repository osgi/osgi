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
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentCustomizerPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>installDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class InstallDeploymentPackageAPI implements TestInterface {

	private DeploymentTestControl tbc;

	public InstallDeploymentPackageAPI(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testInstallDeploymentPackageAPI001();
		testInstallDeploymentPackageAPI002();
		testInstallDeploymentPackageAPI003();
		testInstallDeploymentPackageAPI004();
		testInstallDeploymentPackageAPI005();
		testInstallDeploymentPackageAPI006();
		testInstallDeploymentPackageAPI007();
		testInstallDeploymentPackageAPI008();
		testInstallDeploymentPackageAPI009();
		testInstallDeploymentPackageAPI010();
		testInstallDeploymentPackageAPI011();
		testInstallDeploymentPackageAPI012();
		testInstallDeploymentPackageAPI013();
		testInstallDeploymentPackageAPI014();
		testInstallDeploymentPackageAPI015();
		testInstallDeploymentPackageAPI016();
		testInstallDeploymentPackageAPI017();
	}

	/**
	 * This test case installs a simple deployment package,
	 * without granting this caller the installDeploymentPackage
	 * permission. It must throw SecurityException.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI001() {
		tbc.log("#testInstallDeploymentPackageAPI001");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_LIST);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
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
	 * This test case installs a simple deployment package,
	 * granting the correct action "install"
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI002() {
		tbc.log("#testInstallDeploymentPackageAPI002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.ACTION_INSTALL);
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
			tbc.uninstall(dp);
		}
	}

	/**
	 * This test case installs a simple deployment package and
	 * then updates it with the same deployment package
	 * name/version, in order to check if no action was
	 * taken.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI003() {
		tbc.log("#testInstallDeploymentPackageAPI003");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP_CLONE);
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
	 * This test case attempts to pass a null input stream to
	 * installDeploymentPackage method to verify that null is returned.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI004() {
		//TODO the bahavior of passing null is not specified.
		tbc.log("#testInstallDeploymentPackageAPI004");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
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
	 * Asserts that a bundle must belong to one and only one deployment
	 * package throwing DeploymentException.CODE_BUNDLE_SHARING_VIOLATION.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI005() {
		tbc.log("#testInstallDeploymentPackageAPI005");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage dp2 = null;		
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, dp2 });
		}
	}

	/**
	 * Asserts that a bundle must belong to only one deployment
	 * package, even if its version is different. A deployment
	 * package that installs a bundle that was already in the
	 * framework should throw
	 * DeploymentException.CODE_BUNDLE_SHARING_VIOLATION.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */
	private void testInstallDeploymentPackageAPI006() {
		tbc.log("#testInstallDeploymentPackageAPI006");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage dp2 = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, dp2 });
		}
	}
	
	/**
	 * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
	 * mandatory manifest header is missing. 
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */	
	private void testInstallDeploymentPackageAPI007() {
		tbc.log("#testInstallDeploymentPackageAPI007");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_NAME_HEADER_DP);
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
	 * Asserts that a DeploymentException.CODE_BUNDLE_NAME_ERROR is thrown when
	 * bundle symbolic name is not the same as defined by the deployment package manifest 
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI008() {
		tbc.log("#testInstallDeploymentPackageAPI008");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP);
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
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a higher
	 * major version, in order to check if the deployment package version
	 * was increased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI009() {
		tbc.log("#testInstallDeploymentPackageAPI009");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename(), testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The major version of the bundle was updated with a higher version", testDP2.getVersion().getMajor(), version.getMajor());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a
	 * lower major version, in order to check if the deployment
	 * package version was decreased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */	
	private void testInstallDeploymentPackageAPI010() {
		tbc.log("#testInstallDeploymentPackageAPI010");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The major version of the bundle was updated with a lower version", testDP2.getVersion().getMajor(), version.getMajor());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a
	 * higher micro version, in order to check if the
	 * version was increased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI011() {
		tbc.log("#testInstallDeploymentPackageAPI011");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename(), testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The micro version of the bundle was updated with a higher version", testDP2.getVersion().getMicro(), version.getMicro());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}

	/**
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a lower
	 * micro version, in order to check if the version was decreased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI012() {
		tbc.log("#testInstallDeploymentPackageAPI012");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The micro version of the bundle was updated with a lower version", testDP2.getVersion().getMicro(), version.getMicro());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}

	
	/**
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a higher
	 * minor version, in order to check if the version was increased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI013() {
		tbc.log("#testInstallDeploymentPackageAPI013");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The minor version of the bundle was updated with a higher version", testDP2.getVersion().getMinor(), version.getMinor());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * This test case installs a simple deployment package and
	 * then updates it with another deployment package with a lower
	 * minor version, in order to check if the version was decreased.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */				
	private void testInstallDeploymentPackageAPI014() {
		tbc.log("#testInstallDeploymentPackageAPI014");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename(), testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp2);
			
			DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			Version version =  dp.getVersion();
			tbc.assertEquals("The minor version of the bundle was updated with a lower version", testDP2.getVersion().getMinor(), version.getMinor());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp2);

		}
	}
	
	/**
	 * Asserts that DmtException.CODE_BUNDLE_START is thrown when one
	 * or more bundles couldn't be started when installing a
	 * deployment package.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI015() {
		tbc.log("#testInstallDeploymentPackageAPI015");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Asserts that DmtException.CODE_BUNDLE_START is thrown when one or more bundles couldn't be started",DeploymentException.CODE_BUNDLE_START,e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	/**
	 * Asserts that DmtException.CODE_BUNDLE_START is thrown when one
	 * or more bundles couldn't be started when updating a
	 * deployment package.
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI016() {
		tbc.log("#testInstallDeploymentPackageAPI016");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentPackage dp2 = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BUNDLE_DOESNT_THROW_EXCEPTION_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Asserts that DmtException.CODE_BUNDLE_START is thrown when one or more bundles couldn't be started",DeploymentException.CODE_BUNDLE_START,e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, dp2 });
		}
	}
	
	/**
	 * Asserts that a DeploymentException.CODE_RESOURCE_SHARING_VIOLATION is
	 * thrown when a resource already exists. 
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */		
	private void testInstallDeploymentPackageAPI017() {
		tbc.log("#testInstallDeploymentPackageAPI017");
		tbc.setDeploymentAdminAndCustomizerPermission(
				DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL,
				DeploymentConstants.ALL_PERMISSION,
				DeploymentConstants.BUNDLE_NAME_ALL,
				DeploymentCustomizerPermission.ACTION_PRIVATEAREA);
		DeploymentPackage dp = null, dp2 = null, rp = null;
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
		try {
			rp = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());			
			TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_FROM_OTHER_DP);
			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_RESOURCE_SHARING_VIOLATION, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { rp, dp, dp2 });
		}
	}
}

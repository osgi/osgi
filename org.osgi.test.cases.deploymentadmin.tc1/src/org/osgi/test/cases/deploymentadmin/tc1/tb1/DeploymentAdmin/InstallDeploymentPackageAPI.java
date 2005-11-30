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
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
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
        prepare();
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
	}

    /**
     * Sets permission needed and wait for PermissionWorker
     */
    private synchronized void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing installDeploymentPackage");
        }
    }

    /**
	 * This test case installs a simple deployment package,
	 * granting the correct action "install"
	 * 
	 * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
	 */			
	private void testInstallDeploymentPackageAPI001() {
		tbc.log("#testInstallDeploymentPackageAPI001");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
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
     * Asserts if the two versions are the same, then this method simply returns with
     * the <b>old (target)</b> deployment package without any action.
     * 
     * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
     */		
	private void testInstallDeploymentPackageAPI002() {
		tbc.log("#testInstallDeploymentPackageAPI002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP_CLONE);
		DeploymentPackage dp1 = null, dp2 = null;
		try {
			dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp1);

			dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
			BundleInfo[] depBundles = dp2.getBundleInfos();
            TestingBundle[] testBundles = testDP.getBundles();
            
            tbc.assertTrue("Returned DP has bundles", depBundles.length > 0);
            int count = 0;
            for (int i = 0; (i < depBundles.length) && (count < 2); i++) {
                for (int j = 0; j < testBundles.length; j++) {
                    if (depBundles[i].getSymbolicName().trim().equals(testBundles[j].getName())) {
                        tbc.assertEquals("Assert Deployment Package bundle "
                            + depBundles[i].getSymbolicName() + " Version", depBundles[i].getVersion(), testBundles[j].getVersion());
                        count++;
                        break;
                    }
                }
            }
            tbc.assertTrue("The 2 old DP bundles are the same as returned", count==2);
            // to be really sure assert the Manifest header too
            String header = dp2.getHeader(DeploymentConstants.DP_HEADER_COPYRIGHT);
            tbc.assertEquals("Header also belongs to old DP", DeploymentConstants.DP_MY_COPYRIGHT, header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp1);
		}
	}
	
	/**
     * Asserts that IllegalArgumentException is thrown if the input stream of
     * the deployment package is null.
     * 
     * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
     */			
	private void testInstallDeploymentPackageAPI003() {
		tbc.log("#testInstallDeploymentPackageAPI003");
		try {
            tbc.getDeploymentAdmin().installDeploymentPackage(null);
			tbc.failException("#", DeploymentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
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
	private void testInstallDeploymentPackageAPI004() {
		tbc.log("#testInstallDeploymentPackageAPI004");
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
	private void testInstallDeploymentPackageAPI005() {
		tbc.log("#testInstallDeploymentPackageAPI005");
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
	private void testInstallDeploymentPackageAPI006() {
		tbc.log("#testInstallDeploymentPackageAPI006");
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
	private void testInstallDeploymentPackageAPI007() {
		tbc.log("#testInstallDeploymentPackageAPI007");
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
	private void testInstallDeploymentPackageAPI008() {
		tbc.log("#testInstallDeploymentPackageAPI008");
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
	private void testInstallDeploymentPackageAPI009() {
		tbc.log("#testInstallDeploymentPackageAPI009");
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
     * This test case installs a simple deployment package,
     * without granting this caller the installDeploymentPackage
     * permission. It must throw SecurityException.
     * 
     * @spec DeploymentAdmin.installDeploymentPackage(InputStream)
     */     
    private void testInstallDeploymentPackageAPI010() {
        tbc.log("#testInstallDeploymentPackageAPI010");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.LIST);
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
            // it sets all permissions
            tbc.cleanUp(dp);
        }
    }
}

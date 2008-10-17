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
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingResource;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>installDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class InstallFixPack implements TestInterface {
	
	private DeploymentTestControl tbc;
	
	public InstallFixPack(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
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
     * Sets permission needed and wait for PermissionWorker
     */
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail("Failed to set Permission necessary for testing installDeploymentPackage");
        }
    }
	
	/**
	 * This test case install a simple fix pack for a range of
	 * deployment packages, which updates the version of the
	 * first bundle in the deployment package.
	 * 
	 * @spec 115.4 Fix Package
	 */			
	private void testInstallFixPack001() {
		tbc.log("#testInstallFixPack001");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0].getSymbolicName());
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0].getVersion());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	/**
     * This test case uninstall bundle.tb2 from the deployment package, and then
     * check if it was removed from the framework as well as from the deployment
     * package.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */			
	private void testInstallFixPack002() {
		tbc.log("#testInstallFixPack002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_BUNDLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			
			// asserts dp bundles
			tbc.assertTrue("There are no more bundles in deployment package", (pairs.length==1));
			tbc.assertEquals("The remaining bundle name is correct", testDP.getBundles()[0].getName(), pairs[0].getSymbolicName());
			tbc.assertEquals("The remaining bundle version is correct",testDP.getBundles()[0].getVersion(), pairs[0].getVersion());
			
			// assert that the bundles have been uninstalled from the framework
			String b2Name = testDP.getBundles()[1].getName();
			Bundle b2 = tbc.getBundle(b2Name);
			
			tbc.assertNull("The bundle "+b2Name+" have been removed", b2);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	/**
	 * This test case installs a fix-pack that adds a bundles
	 * to the fixed deployment package.
	 * 
	 * @spec 114.4 Fix Package
	 */			
	private void testInstallFixPack003() {
		tbc.log("#testInstallFixPack003");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// This deployment package had two bundles; after the fix-pack installation it must have three.
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			tbc.assertEquals("There are three bundles in "+testDP.getName(), 3, pairs.length);

			TestingBundle[] bundles = testFixDP.getBundles();
			// asserts the name and version of the installed bundles
			int count = 0;
			for(int i=0; i<pairs.length; i++) {
				for(int j=0; j<bundles.length; j++) {
					if (pairs[i].getSymbolicName().equals(bundles[j].getName())) {
						tbc.assertEquals("The bundles "+bundles[j].getName() + "is installed in "+newDP.getName(), bundles[j].getName(), pairs[i].getSymbolicName());
						tbc.assertEquals("The bundles "+bundles[j].getName() + " version is the same as "+pairs[i].getVersion() + " version", bundles[j].getVersion(), pairs[i].getVersion());
						count++;
					}
				}
			}
			// there are at least three bundles that match symbolic names with installed ones
			tbc.assertTrue("There are three bundles that matches symbolic names", count==3);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	/**
	 * This test case install a simple fix pack for a higher
	 * micro version of deployment package within range of
	 * deployment packages defined in the manifest.
	 * 
	 * @spec 114.4 Fix Package
	 */		
	private void testInstallFixPack004() {
		tbc.log("#testInstallFixPack004");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0].getSymbolicName());
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0].getVersion());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	
	/**
	 * This test case install a simple fix pack for a higher
	 * minor version of deployment package within range of
	 * deployment packages defined in the manifest.
	 * 
	 * @spec 114.4 Fix Package
	 */		
	private void testInstallFixPack005() {
		tbc.log("#testInstallFixPack005");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0].getSymbolicName());
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0].getVersion());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	/**
	 * This test case install a simple fix pack for a higher
	 * major version of deployment package within range of
	 * deployment packages defined in the manifest.
	 * 
	 * @spec 114.4 Fix Package
	 */		
	private void testInstallFixPack006() {
		tbc.log("#testInstallFixPack006");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			BundleInfo[] pairs = newDP.getBundleInfos();
			tbc.assertEquals("The symbolic name of the bundle is ", testFixDP.getBundles()[0].getName(), pairs[0].getSymbolicName());
			tbc.assertEquals("The new version of the bundle is ", testFixDP.getBundles()[0].getVersion(), pairs[0].getVersion());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
	
	/**
	 * This test case install a simple fix pack for a range of
	 * deployment packages higher than the version of the
	 * installed deployment package.
	 * 
	 * @spec 114.4 Fix Package
	 */			
	private void testInstallFixPack007() {
		tbc.log("#testInstallFixPack007");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.FIX_PACK_HIGHER_RANGE_DP);
		
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}

	/**
	 * This test case installs a simple fix pack without any
	 * target deployment package. In order to check if
	 * DeploymentException with CODE_MISSING_FIXPACK_TARGET
	 * code is thrown.
	 * 
	 * @spec 114.14.5.6 CODE_MISSING_FIXPACK_TARGET
	 */				
	private void testInstallFixPack008() {
		tbc.log("#testInstallFixPack008");
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
		
		DeploymentPackage fixDP = null;
		try {
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(fixDP);
		}
	}
	
	/**
	 * This test case install a simple fix pack that adds a
	 * resource for a deployment package.
	 * 
	 * @spec 114.4 Fix Package
	 */	
	private void testInstallFixPack009() {
		tbc.log("#testInstallFixPack009");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);

		TestingResource[] testRes = testFixDP.getResources();
		
		
		DeploymentPackage dp = null, fixDP = null, rp =null;
		try {
			// install resource processor to handle resources
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), rp);
			
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);

			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);
			
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			String[] res = newDP.getResources();
			boolean found = false;
			for (int i=0; i < res.length; i++) {
				for (int j=0; (j < testRes.length) && !found; j++) {
					if (res[i].equals(testRes[j].getName())) {
						found = true;
					}
				}
			}
			tbc.assertTrue("The resource was added to the deployment package", found);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP });
		}
	}

	/**
	 * This test case install a simple fix pack that uninstalls
	 * a resource from a deployment package.
	 * 
	 * @spec 114.4 Fix Package
	 */		
	private void testInstallFixPack010() {
		tbc.log("#testInstallFixPack010");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_RESOURCE_FIX_PACK_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);

		
		DeploymentPackage dp = null, fixDP = null, rp = null;
		try {
			// install resource processor to handle resources
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), rp);

			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			// get the number of resources installed before the fix-pack
			int before = dp.getResources().length;

			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), fixDP);

			// the dp instance is is stale
			DeploymentPackage newDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
			int after = newDP.getResources().length;

			tbc.assertTrue("The resource has been uninstalled", (after == before-1));
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP });
		}
	}
	
	/**
	 * This test case try to install a simple fix pack that uninstall
	 * a resource from a deployment package, which had no resources, expecting
	 * a DeploymentException as result with error code equal to CODE_MISSING_RESOURCE. 
	 * 
	 * @spec 114.14.5.8 CODE_MISSING_RESOURCE
	 */			
	private void testInstallFixPack011() {
		tbc.log("#testInstallFixPack011");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_NO_RESOURCE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_RESOURCE_FIX_PACK_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);

		
		DeploymentPackage dp = null, fixDP = null, rp = null;
		try {
			// install resource processor to handle resources
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_RESOURCE, e.getCode());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP });
		}
	}
	
	/**
	 * This test case install a simple fix pack that uninstalls
	 * a bundle from a deployment package, which had no bundle, expecting
	 * a DeploymentException as result with error code equal to CODE_MISSING_BUNDLE. 
	 * 
	 * @spec 114.14.5.5 CODE_MISSING_BUNDLE
	 */		
	private void testInstallFixPack012() {
		tbc.log("#testInstallFixPack012");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_NO_BUNDLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_BUNDLE_FIX_PACK_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
		
		
		DeploymentPackage dp = null, fixDP = null, rp = null;
		try {
			// install resource processor to handle resources
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_BUNDLE, e.getCode());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP });
		}
	}
	
	/**
	 * This test case install a simple fix pack for a range of
	 * deployment packages below the version of the
	 * installed deployment package. 
	 * 
	 * @spec 114.14.5.6 CODE_MISSING_FIXPACK_TARGET
	 */			

	private void testInstallFixPack013() {
		tbc.log("#testInstallFixPack013");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.FIX_PACK_LOWER_RANGE_DP);
		DeploymentPackage dp = null;
		DeploymentPackage fixDP = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Misssing Resource Deployment Exception thrown", DeploymentException.CODE_MISSING_FIXPACK_TARGET, e.getCode());
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail("Expected DeploymentException and got "+e.getClass().getName());
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dp, fixDP });
		}
	}
}

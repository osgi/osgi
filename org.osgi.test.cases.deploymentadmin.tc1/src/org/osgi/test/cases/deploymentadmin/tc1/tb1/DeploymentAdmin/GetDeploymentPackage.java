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
 * ============  ====================================================================
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ====================================================================
 * Jun 06, 2005  Andre Assad
 * 111           Implement/Change test cases according to Spec updates of 6th of June
 * ============  ====================================================================
 * Aug 30, 2005  Andre Assad
 * 179           Implement Review Issues
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingGetServiceRegistrationResourceProcessor;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Class Validates the implementation of
 * <code>getDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class GetDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public GetDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetDeploymentPackage001();
		testGetDeploymentPackage002();
		testGetDeploymentPackage003();
		testGetDeploymentPackage004();
        testGetDeploymentPackage005();
        testGetDeploymentPackage006();
        testGetDeploymentPackage007();
        testGetDeploymentPackage008();
	}

	
	/**
     * Sets permission needed and wait for PermissionWorker
     */
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }

    /**
	 * This test case installs a simple deployment package and
	 * then checks if getDeploymentPackage returns the correct
	 * deployment package.
	 * 
	 * @spec DeploymentAdmin.getDeploymentPackage(String)
	 */		
	private void testGetDeploymentPackage001() {
		tbc.log("#testGetDeploymentPackage001");
		DeploymentPackage dpInstall = null, dpGet = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dpInstall = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dpGet = tbc.getDeploymentAdmin().getDeploymentPackage(dpInstall.getName());
			tbc.assertEquals("Asserts that getDeploymentPackage returns the correct instance of a deployment package",dpInstall, dpGet);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.cleanUp(dpInstall);
		}
	}
	
	/**
	 * Asserts that if there is no deployment package with the passed symbolic name,
	 * null is returned from getDeploymentPackage.
	 * 
	 * @spec DeploymentAdmin.getDeploymentPackage(String)
	 */		
	private void testGetDeploymentPackage002() {
		tbc.log("#testGetDeploymentPackage002");
		DeploymentPackage dp = null;
		try {
			dp = tbc.getDeploymentAdmin().getDeploymentPackage(DeploymentConstants.INVALID_NAME);
			tbc.assertNull("Asserts that if there is no deployment package with the passwd symbolic name, null is returned from getDeploymentPackage", dp);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * Asserts that IllegalArgumentException is thrown when
	 * we pass null as symbolic name.
	 * 
	 * @spec DeploymentAdmin.getDeploymentPackage(String)
	 */		
	private void testGetDeploymentPackage003() {
		tbc.log("#testGetDeploymentPackage003");
		try {
			tbc.getDeploymentAdmin().getDeploymentPackage((String)null);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "IllegalArgumentException" }));
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"IllegalArgumentException", e.getClass().getName() }));
		}
	}
    
    /**
     * Asserts that uninstalled packages are not retrieved. 
     * 
     * @spec DeploymentAdmin.getDeploymentPackage(String)
     */     
    private void testGetDeploymentPackage004() {
        tbc.log("#testGetDeploymentPackage004");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            //
            tbc.uninstall(dp);
            
            DeploymentPackage uninstalledtDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
            tbc.assertNull("No deployment package is retrieved after uninstallation", uninstalledtDP);
            
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts if during an installation of an existing package (update), the
     * target deployment package must remain the return value until the
     * installation process is completed.
     * 
     * @spec DeploymentAdmin.getDeploymentPackage(String)
     */     
    private synchronized void testGetDeploymentPackage005() {
        tbc.log("#testGetDeploymentPackage005");
        
        DeploymentPackage initialDP = null, fixDP = null, rp =null;
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    	TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
        TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
        InstallPackageThread installThread = null;
        
        try {
   			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
   			
   			TestingGetServiceRegistrationResourceProcessor test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
   			
   			tbc.assertNotNull("Resource Processor was registered", test);
   			
   			test.setRelease(false);
   			
   			initialDP = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
   			

   			installThread = new InstallPackageThread(tbc,testFixDP);

            installThread.start();

            //Just to guarantee that this Thread is executed
            while (!installThread.isRunning()) {
               	this.wait(500);
            }
               
            tbc.assertTrue("Asserts that the installation was not completed", !installThread.isInstalled());
            
            DeploymentPackage middleDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
   			
   			tbc.assertTrue("Asserts if during an installation of an existing package (update), the " +
   					"target deployment package must remain the return value",
   					middleDP.getName().equals(testDP.getName()) && 
   					middleDP.getVersion().equals(testDP.getVersion()));
   			
            test.setRelease(true);
   			
             //Just to guarantee that the installation terminated
            while (!installThread.isInstalled()) {
               	this.wait(500);
            }
               
            fixDP = installThread.getDeploymentPackage();
               
   			DeploymentPackage finalDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
   			
   			tbc.assertTrue("Asserts that after the installation process is " +
   					"completed, the source replaces the target.",
   					finalDP.getName().equals(testFixDP.getName()) && 
   					finalDP.getVersion().equals(testFixDP.getVersion()));

        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
       	 installThread.uninstallDP(false);
            tbc.uninstall(new DeploymentPackage[] { rp, initialDP, fixDP });
        }
    }
    
    /**
     * Asserts that after an installation of an existing package (update) is
     * completed, the source is the return value of getDeploymentPackage method.
     * 
     * @spec DeploymentAdmin.getDeploymentPackage(String)
     */     
    private void testGetDeploymentPackage006() {
        tbc.log("#testGetDeploymentPackage006");
        DeploymentPackage dp1 = null, dp2 = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            TestingDeploymentPackage updateDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP);
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + updateDP.getFilename());
            
            DeploymentPackage got = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
            
            tbc.assertEquals(
                    "After an update is completed, the source DP is correctly returned",
                    dp2.getName().trim() + "_" + dp2.getVersion().toString().trim(), 
                    got.getName().trim() + "_" + got.getVersion().toString().trim());
            
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[]{dp1, dp2});
        }
    }
    
    /**
     * Asserts if the installation fails, the source must never become visible,
     * also not transiently.
     * 
     * @spec 114.6.1 Introspection
     */     
    private void testGetDeploymentPackage007() {
        tbc.log("#testGetDeploymentPackage007");
        DeploymentPackage dp = null, invisibleDP = null;
        TestingDeploymentPackage testDP = null;
        try {
            testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_NAME_HEADER_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
        } catch (DeploymentException e) {
            invisibleDP = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
            tbc.assertNull("Deployment Package is not visible", invisibleDP);
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * This test case installs a simple deployment package and then tries 
     * to get the deployment package without granting this caller the list
     * permission. A SecurityException must be throw.
     * 
     * @spec DeploymentAdmin.getDeploymentPackage(String)
     */ 
    private void testGetDeploymentPackage008() {
        tbc.log("#testGetDeploymentPackage008");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.INSTALL + "," + DeploymentAdminPermission.UNINSTALL + "," + DeploymentAdminPermission.UNINSTALL_FORCED);
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.getDeploymentAdmin().getDeploymentPackage(dp.getName());
            tbc.failException("#", SecurityException.class);
        } catch (SecurityException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
        } finally {
            tbc.cleanUp(dp);
        }
    }

}

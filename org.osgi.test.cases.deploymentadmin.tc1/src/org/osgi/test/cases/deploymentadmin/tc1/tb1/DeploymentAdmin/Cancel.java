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
 * Sep 02, 2005  Andre Assad
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
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>cancel<code> method, according to MEG reference
 * documentation.
 *
 */
public class Cancel implements TestInterface {
    
    private DeploymentTestControl tbc;
    
    public Cancel(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
    	prepare();
        testCancel001();
        testCancel002();
        testCancel003();
        testCancel004();
    }
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }
    /**
     * Tests if <code>cancel</coede> returns true if there was an active 
     * <b>install</b> session and it was successfully cancelled.
     * 
     * @spec DeploymentAdmin.cancel()
     */
    private synchronized void testCancel001() {
        tbc.log("#testCancel001");
        
        DeploymentPackage initialDP = null, fixDP = null, rp =null;
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    	TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
        TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
        InstallPackageThread installThread = null;
        TestingGetServiceRegistrationResourceProcessor test = null;
        
        try {
   			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
   			
   			test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
   			
   			tbc.assertNotNull("Resource Processor was registered", test);
   			
   			test.setRelease(false);
   			
   			initialDP = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
   			

   			installThread = new InstallPackageThread(tbc,testFixDP);

            installThread.start();

            //Just to guarantee that this Thread is running
            while (!installThread.isRunning()) {
               	this.wait(500);
            }
               
            Thread.sleep(1000); // give some time to init the deployment session by the thread
            
            tbc.assertTrue("Asserts that the installation was not completed", !installThread.isInstalled());
            
            boolean canceled = tbc.getDeploymentAdmin().cancel();
   			
            tbc.assertTrue("Asserts that the session was cancelled", canceled);
            
            test.setRelease(true);
            
            while (installThread.getDepExceptionCodeInstall() == InstallPackageThread.EXCEPTION_NOT_THROWN) {
            	this.wait(500);
            }
            
            tbc.assertTrue("Asserts that the installation is still not completed", !installThread.isInstalled());
            
            tbc.assertTrue("Asserts that DeploymentException.CODE_CANCELLED was thrown by another Thread", 
            		installThread.getDepExceptionCodeInstall() == DeploymentException.CODE_CANCELLED);
   			
            
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
        	test.setRelease(true);
       	 	installThread.uninstallDP(false);
            tbc.uninstall(new DeploymentPackage[] { rp, initialDP, fixDP });
        }
    }
    /**
    * Tests if <code>cancel</coede> returns true if there was an active 
    * <b>uninstall</b> session and it was successfully cancelled.
    *  
    * @spec DeploymentAdmin.cancel()
    */
    private synchronized void testCancel002() {
        tbc.log("#testCancel002");
        
        DeploymentPackage initialDP = null, fixDP = null, rp =null;
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    	TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
        TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
        InstallPackageThread installThread = null;
        TestingGetServiceRegistrationResourceProcessor test = null;
        
        try {
   			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
   			
   			test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
   			
   			tbc.assertNotNull("Resource Processor was registered", test);
   			
   			initialDP = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());

   			installThread = new InstallPackageThread(tbc,testFixDP);

            installThread.start();

            //Just to guarantee that the DP is installed
            while (!installThread.isInstalled()) {
               	this.wait(500);
            }
               
            test.setRelease(false);
            
            installThread.uninstallDP(true);
            
            while (!installThread.isUninstalling()) {
               	this.wait(500);
            }
            Thread.sleep(1000); // give some time to init the deployment session by the thread
            
            tbc.assertTrue("Asserts that the uninstallation was not completed", !installThread.isUninstalled());
            
            boolean canceled = tbc.getDeploymentAdmin().cancel();
   			
            tbc.assertTrue("Asserts that the session was cancelled", canceled);
            
            test.setRelease(true);
            
            while (installThread.getDepExceptionCodeUninstall() == InstallPackageThread.EXCEPTION_NOT_THROWN) {
            	this.wait(500);
            }
            tbc.assertTrue("Asserts that the uninstallation is still not completed", !installThread.isUninstalled());
               
            tbc.assertTrue("Asserts that DeploymentException.CODE_CANCELLED is throw by another Thread", 
            		installThread.getDepExceptionCodeUninstall() == DeploymentException.CODE_CANCELLED);
            
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
        	test.setRelease(true);
       	 	installThread.uninstallDP(true);
            tbc.uninstall(new DeploymentPackage[] { rp, initialDP, fixDP });
        }
    }

    /**
     * Tests if <code>cancel</coede> throws <code>SecurityException</code> 
     * if the operation is not permitted based on the <b>current security policy</b>.
     * 
     * @spec DeploymentAdmin.cancel()
     */
    
    private synchronized void testCancel003() {
        tbc.log("#testCancel003");
        
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, 
        		DeploymentAdminPermission.INSTALL + ","
    			+ DeploymentAdminPermission.LIST + ","
    			+ DeploymentAdminPermission.UNINSTALL + ","
    			+ DeploymentAdminPermission.UNINSTALL_FORCED + ","
                + DeploymentAdminPermission.METADATA);
        
        DeploymentPackage initialDP = null, fixDP = null, rp =null;
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    	TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
        TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
        InstallPackageThread installThread = null;
        TestingGetServiceRegistrationResourceProcessor test = null;
        
        try {
   			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
   			
   			test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
   			
   			tbc.assertNotNull("Resource Processor was registered", test);
   			
   			
   			initialDP = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());

   			test.setRelease(false);

   			installThread = new InstallPackageThread(tbc,testFixDP);

            installThread.start();

            //Just to guarantee that this Thread is running
            while (!installThread.isRunning()) {
               	this.wait(500);
            }
            
            Thread.sleep(1000); // give some time to init the deployment session by the thread
               
            tbc.assertTrue("Asserts that the installation was not completed", !installThread.isInstalled());

            //Should throw SecurityException
            tbc.getDeploymentAdmin().cancel();
            
   			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
//        	tbc.pass("SecurityException correctly thrown");  
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
        	test.setRelease(true);
       	 	installThread.uninstallDP(false);
            tbc.uninstall(new DeploymentPackage[] { rp, initialDP, fixDP });
            prepare();
        }
    }
    
	  /**
	  * Asserts that cancle returns false if there was NO active session.
	  * 
	  * @spec DeploymentAdmin.cancel()
	  */
	 private void testCancel004() {
	     tbc.log("#testCancel004");
	     try {
	    	 boolean canceled = tbc.getDeploymentAdmin().cancel();
	         tbc.assertTrue("canceled is false when there is no active session", !canceled);
	     } catch (Exception e) {
        	e.printStackTrace();
        	tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
	     }
	 }
}

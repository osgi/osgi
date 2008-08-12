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
 * Aug 10, 2005  Luiz Felipe Guimaraes
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingRollbackCall;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Class Validates the implementation of
 * <code>uninstall, uninstallForced<code> method, according to MEG reference
 * documentation.
 */
public class UninstallDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;
    public static boolean ROLL_BACK_CALLED = false;

	public UninstallDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testUninstallDeploymentPackage001();
		testUninstallDeploymentPackage002();
		testUninstallDeploymentPackage003();
		testUninstallDeploymentPackage004();
        testUninstallDeploymentPackage005();
        testUninstallDeploymentPackage006();
        testUninstallDeploymentPackage007();
        testUninstallDeploymentPackage008();
        testUninstallDeploymentPackage009();
        testUninstallDeploymentPackage010();
        testUninstallDeploymentPackage011();
        testUninstallDeploymentPackage012();
        testUninstallDeploymentPackage013();
        testUninstallDeploymentPackage014();
        testUninstallDeploymentPackage015();
	}

    /**
     * Sets permission needed and wait for PermissionWorker
     */
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing uninstall Deployment Packages methods");
        }
    }
	
	/**
     * This test case uninstalls a simple deployment package, granting the
     * correct "uninstall" permission.
     * 
     * @spec DeploymentPackage.uninstall()
     */			
	private void testUninstallDeploymentPackage001() {
		tbc.log("#testUninstallDeploymentPackage001");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.UNINSTALL);
			dp.uninstall();

            tbc.assertNull("Asserting that bundle001.jar was uninstalled.", tbc.getBundle("bundle001.jar"));
            tbc.assertNull("Asserting that bundle002.jar was uninstalled.", tbc.getBundle("bundle002.jar"));           
            
			tbc.pass("A DeploymentPackage could be uninstalled using only \"uninstall\" permission");            
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
            // reset permissions
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case uninstalls a simple deployment package,
	 * granting the correct "uninstall_forced" permission.
	 * 
	 * @spec DeploymentPackage.uninstallForced()
	 */			
	private void testUninstallDeploymentPackage002() {
		tbc.log("#testUninstallDeploymentPackage002");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			tbc.assertNotNull("deployment package is not null", dp);
			tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, DeploymentAdminPermission.UNINSTALL_FORCED);
			tbc.assertTrue("UninstallDeploymentPackage forced executed properly", dp.uninstallForced());
            
            tbc.assertNull("Asserting that bundle001.jar was uninstalled.", tbc.getBundle("bundle001.jar"));
            tbc.assertNull("Asserting that bundle002.jar was uninstalled.", tbc.getBundle("bundle002.jar"));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
			tbc.uninstall(dp);
		}
	}
    
    /**
     * This test case asserts that all resource processor services that are
     * owned by this DeploymentPackage has dropAllResources() method called.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage003() {
        tbc.log("#testUninstallDeploymentPackage003");
        DeploymentPackage dp = null;
        try {
        	DeploymentConstants.DROPALLRESOURCES_COUNT = 0;
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());                    
            dp.uninstall();
            tbc.assertEquals("Asserting that dropAllResources() was called two times (once per resource processor).", 2, DeploymentConstants.DROPALLRESOURCES_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            DeploymentConstants.DROPALLRESOURCES_COUNT = 0;
            tbc.uninstall(dp);
        }

    }
    
    /**
     * This test case asserts that only the resource processors
     * that are owned by this deployment package calls dropAllResources.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage004() {
        tbc.log("#testUninstallDeploymentPackage004");
        DeploymentPackage dp1 = null, dp2 = null, dp3 = null;
        try {
        	DeploymentConstants.DROPALLRESOURCES_COUNT = 0;        	
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
            dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
            
            TestingDeploymentPackage testDP3 = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_DP);
            dp3 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP3.getFilename());
            
            dp3.uninstall();
            tbc.assertEquals("Asserting that dropAllResources() was called only one time.", 1, DeploymentConstants.DROPALLRESOURCES_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            DeploymentConstants.DROPALLRESOURCES_COUNT = 0;
            tbc.uninstall(new DeploymentPackage[] { dp1, dp2, dp3 } );
        }

    }
    
    /**
     * This test case asserts that when an error occurs in
     * dropAllResources(), the resource processors must cause a roll back.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage005() {
        tbc.log("#testUninstallDeploymentPackage005");
        DeploymentPackage dp = null, dpError = null;
        ROLL_BACK_CALLED = false;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testDPError = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP);
            dpError = tbc.installDeploymentPackage(tbc.getWebServer() + testDPError.getFilename());            
            dpError.uninstall();
            tbc.fail("dropAllResources() not called");
        } catch (DeploymentException e) {
            tbc.assertTrue("Asserting that rollback was called.", ROLL_BACK_CALLED);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            ROLL_BACK_CALLED = false;
            tbc.uninstall(new DeploymentPackage[] { dp, dpError } );
        }
    }    
    
    /**
     * This test case asserts that when an error occurs in
     * prepare() method, the resource processors must cause a roll back.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage006() {
        tbc.log("#testUninstallDeploymentPackage006");
        DeploymentPackage dp = null, dpError = null;
        TestingRollbackCall trbc = null;
        ROLL_BACK_CALLED = false;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testDPError = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP);
            dpError = tbc.installDeploymentPackage(tbc.getWebServer() + testDPError.getFilename());
            trbc = (TestingRollbackCall)tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR4);
            trbc.setPrepareException(true);
            dpError.uninstall();
            tbc.fail("prepare() not called");
        } catch (DeploymentException e) {
            tbc.assertTrue("Asserting that rollback was called.", ROLL_BACK_CALLED);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            if (trbc != null)
                trbc.setPrepareException(false);
            ROLL_BACK_CALLED = false;
            tbc.uninstall(new DeploymentPackage[] { dp, dpError } );
        }

    }   
    
    /**
     * Asserts that any errors when calling uninstallForced are ignored, they must not cause a roll back.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage007() {
        tbc.log("#testUninstallDeploymentPackage007");
        DeploymentPackage dp = null, dpError = null;
        TestingRollbackCall trbc = null;
        ROLL_BACK_CALLED = false;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testDPError = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP);
            dpError = tbc.installDeploymentPackage(tbc.getWebServer() + testDPError.getFilename());
            trbc = (TestingRollbackCall)tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR4);
            trbc.setPrepareException(true);
            dpError.uninstallForced();
            tbc.pass("Asserts that any errors when calling uninstallForced are ignored, they must not cause a roll back.");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            if (trbc != null)
                trbc.setPrepareException(false);
            ROLL_BACK_CALLED = false;
            tbc.uninstall(new DeploymentPackage[] { dp, dpError } );
        }

    }     
    
    /**
     * This test case asserts that commit is called by the
     * resource processors that joined the session.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage008() {
        tbc.log("#testUninstallDeploymentPackage008");
        DeploymentPackage dp = null, fixDP = null, rp =null;
        try {
    		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
    		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
    		
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
		
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());

			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			
			DeploymentConstants.COMMIT_COUNT = 0;
            dp.uninstall();
            tbc.assertEquals("Asserting that Commit() was called only once.", 1, DeploymentConstants.COMMIT_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            DeploymentConstants.COMMIT_COUNT = 0;
            tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP } );
        }

    }  
    
    /**
     * This test case asserts that commit is called by the
     * resource processors that joined the session. Calling uninstallForced.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage009() {
        tbc.log("#testUninstallDeploymentPackage009");
        DeploymentPackage dp = null, fixDP = null, rp =null;
        try {
    		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
    		TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
    		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
    		
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
		
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());

			fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+testFixDP.getFilename());
			
			DeploymentConstants.COMMIT_COUNT = 0;
            dp.uninstallForced();
            tbc.assertEquals("Asserting that Commit() was called only once.", 1, DeploymentConstants.COMMIT_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            DeploymentConstants.COMMIT_COUNT = 0;
            tbc.uninstall(new DeploymentPackage[] { rp, dp, fixDP } );
        }

    } 
    
    /**
     * This test case asserts that a deployment package that has two resource
     * processors, both must call the commit method, at intallation and
     * uninstallation.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage010() {
        tbc.log("#testUninstallDeploymentPackage010");
        DeploymentPackage dp = null;
        try {
        	DeploymentConstants.COMMIT_COUNT = 0;
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            dp.uninstall();
            tbc.assertEquals("Asserting that Commit() was called four times.", 4, DeploymentConstants.COMMIT_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            DeploymentConstants.COMMIT_COUNT = 0;
            tbc.uninstall(new DeploymentPackage[] { dp } );
        }

    }  
    
    /**
     * This test case asserts that a deployment package that has two
     * resource processors, both must call the commit method. Calling uninstallForced.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */     
    private void testUninstallDeploymentPackage011() {
        tbc.log("#testUninstallDeploymentPackage011");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            DeploymentConstants.COMMIT_COUNT = 0;
            dp.uninstallForced();
            tbc.assertEquals("Asserting that Commit() was called twice", 2, DeploymentConstants.COMMIT_COUNT);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            DeploymentConstants.COMMIT_COUNT = 0;
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that uninstalling a deployment package also uninstalls all owned
     * bundles.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */
    private void testUninstallDeploymentPackage012() {
        tbc.log("#testUninstallDeploymentPackage012");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            TestingBundle[] bundles = testDP.getBundles();
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            Bundle b1 = tbc.getBundle(bundles[0].getName());
            tbc.assertNotNull("Testing bundles 1 was INSTALLED", b1);
            Bundle b2 = tbc.getBundle(bundles[1].getName());
            tbc.assertNotNull("Testing bundles 2 was INSTALLED", b2);
            
            dp.uninstall();
            
            b1 = tbc.getBundle(bundles[0].getName());
            tbc.assertNull("Testing bundles 1 was UNINSTALLED", b1);
            b2 = tbc.getBundle(bundles[1].getName());
            tbc.assertNull("Testing bundles 2 was UNINSTALLED", b2);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
        } finally {
            tbc.cleanUp(dp);
        }
    }
    
    /**
     * Asserts that uninstalling a deployment package also uninstalls all owned
     * bundles. Calling uninstallForced.
     * 
     * @spec 114.9 Uninstalling a Deployment Package
     */
    private void testUninstallDeploymentPackage013() {
        tbc.log("#testUninstallDeploymentPackage013");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            TestingBundle[] bundles = testDP.getBundles();
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            Bundle b1 = tbc.getBundle(bundles[0].getName());
            tbc.assertNotNull("Testing bundles 1 was INSTALLED", b1);
            Bundle b2 = tbc.getBundle(bundles[1].getName());
            tbc.assertNotNull("Testing bundles 2 was INSTALLED", b2);
            
            dp.uninstallForced();
            
            b1 = tbc.getBundle(bundles[0].getName());
            tbc.assertNull("Testing bundles 1 was UNINSTALLED", b1);
            b2 = tbc.getBundle(bundles[1].getName());
            tbc.assertNull("Testing bundles 2 was UNINSTALLED", b2);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
        } finally {
            tbc.cleanUp(dp);
        }
    }
    
    /**
     * This test case installs a simple deployment package and then tries to
     * uninstall without granting this caller the uninstall permission. A
     * SecurityException must be throw.
     * 
     * @spec DeploymentPackage.uninstall()
     */ 
    private void testUninstallDeploymentPackage014() {
        tbc.log("#testUninstallDeploymentPackage014");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.INSTALL);
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            dp.uninstall();
            tbc.failException("#", SecurityException.class);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
        } finally {
            tbc.cleanUp(dp);
        }
    }

    /**
     * This test case installs a simple deployment package and 
     * then tries to uninstall forcefully without granting this caller 
     * the uninstallForceful permission. A SecurityException must be throw.
     * 
     * @spec DeploymentPackage.uninstallForced()
     */     
    private void testUninstallDeploymentPackage015() {
        tbc.log("#testUninstallDeploymentPackage015");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.INSTALL);
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            dp.uninstallForced();
            tbc.failException("#", SecurityException.class);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
        } finally {
            tbc.cleanUp(dp);
        }
    }
}
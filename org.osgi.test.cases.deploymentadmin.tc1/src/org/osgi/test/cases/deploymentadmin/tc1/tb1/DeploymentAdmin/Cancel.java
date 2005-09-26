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

import java.util.Iterator;
import java.util.Vector;
import org.osgi.framework.BundleEvent;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBlockingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

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
    
    private static Barrier cleanupBarrier, releaseBarrier;
    
    private boolean reach, reachTC;
    private boolean clean;

    public Cancel(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testCancel001();
//        testCancel002();
//        testCancel003();
        testCancel004();
    }
    
    /**
     * Tests if <code>cancel</coede> returns true if there was an active 
     * <b>install</b> session and it was successfully cancelled.
     * 
     * @spec DeploymentAdmin.cancel()
     */
    private synchronized void testCancel001() {
        tbc.log("#testCancel001");
        
        SessionWorker worker = null;
        TestingBlockingResourceProcessor testBlockRP = null;
        boolean canceled = false;

        tbc.setDeploymentAdminPermission(
            DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_CANCEL);
        BundleListenerImpl listener = tbc.getBundleListener();
        listener.reset();
        try {
            wait(1000);
            cleanupBarrier = new Barrier();
            releaseBarrier = new Barrier();
            
            int off = 1000;
            TestingDeploymentPackage blockDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            
            worker = new SessionWorker(blockDP);
            worker.start();
            tbc.assertTrue("Installation of blocking session DP not completed", !reach);
            
            // not sure if this is going to be enough
            wait(off);
            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            boolean found = false;
            while (it.hasNext() && !found) {
                BundleEvent event = (BundleEvent)it.next();
                if (event.getBundle().getSymbolicName().equals(
                    DeploymentConstants.PID_RESOURCE_PROCESSOR3)
                    && event.getType() == BundleEvent.STARTED)
                {
                    found = true;
                }
            }
            
            tbc.assertTrue("Found event", found);

            testBlockRP = (TestingBlockingResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
            tbc.assertNotNull("Blocking Resource Processor was registered", testBlockRP);
            
            canceled = tbc.getDeploymentAdmin().cancel();
            tbc.assertTrue("Deployment Admin successfuly cancelled active session", canceled);
            
            testBlockRP.setReleased(true);
            testBlockRP.waitForRelease();
            // release inner threads
            reachTC = true;
            releaseBarrier.waitRelease();
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            worker = null;
            if (!canceled) {
                tbc.getDeploymentAdmin().cancel();
                testBlockRP.setReleased(true);
                testBlockRP.waitForRelease();
            }
            cleanUp(testBlockRP);
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
        
        SessionWorker worker1 = null, worker2 = null;
        TestingBlockingResourceProcessor testBlockRP = null;
        boolean canceled = false;
        DeploymentPackage dp = null;

        tbc.setDeploymentAdminPermission(
            DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_CANCEL);
        BundleListenerImpl listener = tbc.getBundleListener();
        listener.reset();
        try {
            wait(1000);
            cleanupBarrier = new Barrier();
            releaseBarrier = new Barrier();
            
            TestingDeploymentPackage blockDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            int off = (1000 > DeploymentConstants.SESSION_TIMEOUT)?(DeploymentConstants.SESSION_TIMEOUT - 200):1000;
            
            worker1 = new SessionWorker(blockDP);
            worker1.start();
            tbc.assertTrue("Installation of blocking session DP not completed", !reach);
            
            // not sure if this is going to be enough
            wait(off);
            off = (2*off > DeploymentConstants.SESSION_TIMEOUT)?(DeploymentConstants.SESSION_TIMEOUT - off):off;

            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            boolean found = false;
            while (it.hasNext() && !found) {
                BundleEvent event = (BundleEvent)it.next();
                if (event.getBundle().getSymbolicName().equals(
                    DeploymentConstants.PID_RESOURCE_PROCESSOR3)
                    && event.getType() == BundleEvent.STARTED)
                {
                    found = true;
                }
            }
            
            tbc.assertTrue("Found event", found);
            
            testBlockRP = (TestingBlockingResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
            tbc.assertNotNull("Blocking Resource Processor was registered", testBlockRP);
            
            // notifies waiting thread
            testBlockRP.setReleased(true);
            testBlockRP.waitForRelease();
            
            wait(off); // for finishing installation
            dp = worker1.getDp();
            
            // tell threads to wait again
            testBlockRP.setReleased(false);
            worker2 = new SessionWorker(blockDP, false);
            worker2.setDp(dp);
            worker2.start();
            
            // reset off
            off = (1000 > DeploymentConstants.SESSION_TIMEOUT)?(DeploymentConstants.SESSION_TIMEOUT - 200):1000;
            wait(off); // for uninstallation to begin
            canceled = tbc.getDeploymentAdmin().cancel();
            tbc.assertTrue("Deployment Admin successfuly cancelled active session", canceled);
            
            reachTC = true;
            releaseBarrier.waitRelease();
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            worker1 = null;
            worker2 = null;
            if (!canceled) {
                tbc.getDeploymentAdmin().cancel();
                testBlockRP.setReleased(true);
                testBlockRP.waitForRelease();
            }
            tbc.uninstall(dp);
            cleanUp(testBlockRP);
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
        
        SessionWorker worker1 = null;
        TestingBlockingResourceProcessor testBlockRP = null;
        boolean canceled = false;
        DeploymentPackage dp = null;

        tbc.setDeploymentAdminPermission(
            DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL,
            DeploymentAdminPermission.ACTION_INSTALL + "," + DeploymentAdminPermission.ACTION_UNINSTALL + "," + DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
        
        BundleListenerImpl listener = tbc.getBundleListener();
        listener.reset();
        try {
            wait(1000);
            cleanupBarrier = new Barrier();
            releaseBarrier = new Barrier();

            TestingDeploymentPackage blockDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            int off = (1000 > DeploymentConstants.SESSION_TIMEOUT)?(DeploymentConstants.SESSION_TIMEOUT - 200):1000;
            
            worker1 = new SessionWorker(blockDP);
            worker1.start();
            tbc.assertTrue("Installation of blocking session DP not completed", !reach);
            
            // not sure if this is going to be enough
            wait(off);
            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            boolean found = false;
            while (it.hasNext() && !found) {
                BundleEvent event = (BundleEvent)it.next();
                if (event.getBundle().getSymbolicName().equals(
                    DeploymentConstants.PID_RESOURCE_PROCESSOR3)
                    && event.getType() == BundleEvent.STARTED)
                {
                    found = true;
                }
            }
            
            tbc.assertTrue("Found event", found);

            testBlockRP = (TestingBlockingResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
            tbc.assertNotNull("Blocking Resource Processor was registered", testBlockRP);
            
            canceled = tbc.getDeploymentAdmin().cancel();
            tbc.failException("#", SecurityException.class);
            
        } catch (SecurityException e) {
            tbc.pass("SecurityException correctly thrown");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            worker1 = null;
            reachTC = true;
            try {
                releaseBarrier.waitRelease();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (!canceled) {
                tbc.getDeploymentAdmin().cancel();
            }
            tbc.uninstall(dp);
            cleanUp(testBlockRP);
        }
    }
    
    /**
     * Asserts that cancle returns false if there was NO active session.
     * 
     * @spec DeploymentAdmin.cancel()
     */
    private void testCancel004() {
        tbc.log("#testCancel004");
        
        boolean canceled = false;
        tbc.setDeploymentAdminPermission(
            DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        try {
            canceled = tbc.getDeploymentAdmin().cancel();
            tbc.assertTrue("canceled is false when tehere is no active session", !canceled);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        }
    }
    
    /**
     * CleanUp operations
     */
    private void cleanUp(TestingBlockingResourceProcessor testBlockRP) {
        try {
            cleanupBarrier.waitCleanUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testBlockRP.setReleased(false);
        cleanupBarrier.reset();
        cleanupBarrier = null;
    }

    class SessionWorker extends Thread {
        
        private TestingDeploymentPackage testDP;
        private boolean installation;
        DeploymentPackage dp;
        
        protected SessionWorker(TestingDeploymentPackage testDP) {
            this(testDP, true);
        }
        
        protected SessionWorker(TestingDeploymentPackage testDP, boolean installation) {
            this.testDP = testDP;
            this.installation = installation;
        }
        
        public void run() {
            String log = null;
            try {
                if (installation) {
                    dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
                    log = "Installation";
                } else {
                    if (dp != null)
                        dp.uninstall();
                    clean = true;
                    log = "Uninstallation";
                }
                reach = true;
                releaseBarrier.waitRelease();
                tbc.log("#" + log + " session of " + testDP.getName() + " completed successfuly");
            } catch (Exception e) {
                tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
            } finally {
                if (installation) {
                    tbc.uninstall(dp);
                    clean = true;
                    tbc.log("#Clean up successful");
                    try {
                        cleanupBarrier.waitCleanUp();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else
                    ; // do nothing
            }
        }
        /**
         * @return Returns the dp.
         */
        public DeploymentPackage getDp() {
            return dp;
        }
        /**
         * @param dp The dp to set.
         */
        public void setDp(DeploymentPackage dp) {
            this.dp = dp;
        }
    }
    
    class Barrier {
        
        protected synchronized void waitRelease() throws InterruptedException {
            if (reach && reachTC) {
                // if needed do some action
                this.notifyAll();
            } else {
                while (!(reach && reachTC)) {
                    this.wait(DeploymentConstants.TIMEOUT);
                }
            }
        }
        
        protected synchronized void waitCleanUp() throws InterruptedException {
            if (clean && reachTC) {
                // if needed do some action
                this.notifyAll();
            } else {
                while (!(clean && reachTC)) {
                    this.wait(DeploymentConstants.TIMEOUT);
                }
            }
        }
        
        public void reset() {
            reach = false;
            reachTC = false;
            clean = false;
        }
    }

}

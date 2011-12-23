/*
 * Copyright (c) OSGi Alliance (2004, 2011). All Rights Reserved.
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
 * Sep 06, 2005  Andre Assad
 * 179           Implement Review Issues
 * ============  ==============================================================
 */ 
package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentSession;

import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.log.LogEntry;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResource;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingSessionResourceProcessor;
import org.osgi.test.support.log.LogEntryCollector;

/**
 * This test class validates the session operations that happens during a
 * deployment package installation. <br>
 * Note some tests for installation cases may have already been done in API test
 * cases and are not replicated here.
 * 
 * @author Andre Assad
 */
public class InstallSession extends DeploymentTestControl {
    
    
    // shared statics for testing
    public static boolean EXCEPTION_AT_START = false;
    public static boolean ROLL_BACK = false;

    private DeploymentPackage rp;
    
    

    /**
     * Uninstalls testing resource processor
     */
	protected void tearDown() {
		uninstall(rp);
		super.tearDown();
    }

    /**
     * Installs a resource processor to be used in the test cases.
     */
	protected void setUp() {
		super.setUp();
		TestingDeploymentPackage testRP = getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_RP3);
        try {
			rp = installDeploymentPackage(getWebServer() + testRP.getFilename());
        } catch (Exception e) {
        	e.printStackTrace();
			fail("Failed to install testing resource processor");
        }
    }

    /**
     * Validates if Deployment Admin asserts that the Manifest file is the first
     * resource in the Deployment Package JAR file.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession001() {
		log("#testInstallSession001");
        DeploymentPackage dp = null;
        try {
			TestingDeploymentPackage testDP = getTestingDeploymentPackage(DeploymentConstants.MANIFEST_NOT_1ST_FILE);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
			assertEquals("DeploymentException thrown: ",
					DeploymentException.CODE_ORDER_ERROR, e.getCode());
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(dp);
        }
    }
    
    /**
     * Asserts that an exception thrown during the install must roll back the
     * session.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession002() {
		log("#testInstallSession002");
        DeploymentPackage dp = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			TestingDeploymentPackage testDP = getTestingDeploymentPackage(DeploymentConstants.INSTALL_FAIL_DP);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("", DeploymentException.class);
        } catch (DeploymentException e) {
            try {
				tsrp = (TestingSessionResourceProcessor) getService(
						ResourceProcessor.class,
                    "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
                
				assertTrue("Roll back was called during a failed installation",
						tsrp.isRolledback());
            } catch (Exception e1) {
            	e1.printStackTrace();
				fail("Failed to get resource processor from registry");
            }
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(dp);
        }
    }
    
    /**
     * Assert that the installed bundle has the Bundle Symbolic Name as defined
     * by the source manifest. If not, the session must be roll back.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession003() {
		log("#testInstallSession003");
        // cannot assert Resource Processor rollback method
        // resource from DP will not have been processed when Exception is thrown
        DeploymentPackage dp = null;
        TestingDeploymentPackage testDP = null;
        try {
			testDP = getTestingDeploymentPackage(DeploymentConstants.BSN_DIFF_FROM_MANIFEST);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("", DeploymentException.class);
        } catch (DeploymentException e) {
			Bundle b = getBundle("bundles.tb1");
			assertNull(
                    "Roll back was called when BSN is not the same as defined in manifest", b);
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(dp);
        }
    }
    
    /**
     * Assert that the installed bundle has the Bundle version as defined by the
     * source manifest. If not, the session must be roll back.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession004() {
		log("#testInstallSession004");
        // cannot assert Resource Processor rollback method
        // resource from DP will not have been processed when Exception is thrown
        // Asserts only that bundles was uninstalled
        DeploymentPackage dp = null;
        try {
			TestingDeploymentPackage testDP = getTestingDeploymentPackage(DeploymentConstants.BVERSION_DIFF_FROM_MANIFEST);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("", DeploymentException.class);
        } catch (DeploymentException e) {
			Bundle b = getBundle("bundles.tb1");
			assertNull(
					"Roll back was called when BSN is not the same as defined in manifest",
					b);
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(dp);
        }
    }
    
    /**
     * Assert if any Customizer bundles start method throws an exception, the
     * session must be rolled back.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession005() {
		log("#testInstallSession005");
        DeploymentPackage dp = null;
        try {
			TestingDeploymentPackage testDP = getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
            // uninstalls testing resource processor
			uninstall(rp);
            // statically sets exception at start
            EXCEPTION_AT_START = true;
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("", DeploymentException.class);
        } catch (DeploymentException e) {
            try {
				Bundle b1 = getBundle("bundles.tb1");
				assertNull(
						"bundles.tb1 was uninstalled due to session roll back",
						b1);

                Bundle b2 = getBundle("bundles.tb2");
				assertNull(
						"bundles.tb2 was uninstalled due to session roll back",
						b2);
            } catch (Exception e1) {
				fail("Failed to get resource processor from registry");
            }
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	EXCEPTION_AT_START = false;
			uninstall(dp);
        }
    }
    
    /**
     * Asserts that when calling the matched Resource Processor service
     * process(String,Input-Stream) method. The <b>argument is the JAR path </b>
     * of the resource.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession006() {
		log("#testInstallSession006");
        DeploymentPackage dp = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			TestingDeploymentPackage testDP = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_RP3);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
            
            tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
            
            // installed
            String resourceName = tsrp.getResourceName();
            String resourceString = tsrp.getResourceString();
            
            //tested
            String testResource = testDP.getResources()[0].getName();
            
			assertEquals("The name of the resource is correct", testResource,
					resourceName);
            
            log("#" + resourceString);
			assertTrue(
					"The input string correctly returned the right characters",
                (resourceString.indexOf("MEG TCK") != -1));
            
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(dp);
            if (tsrp != null)
                tsrp.reset();
        }
    }
    
    /**
     * Asserts that Deployment Admin calls the matched Resource Processor
     * service process(String,InputStream) method. The argument is the JAR path
     * of the resource. <b>Any Exceptions thrown during this method must abort the
     * installation.</b>
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession007() {
		log("#testInstallSession007");
        DeploymentPackage dp = null;
        TestingSessionResourceProcessor tsrp = null;
        TestingDeploymentPackage testDP = null;
        try {
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
            tsrp.setException(TestingSessionResourceProcessor.PROCESS);

			testDP = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_RP3);
			dp = installDeploymentPackage(getWebServer() + testDP.getFilename());
			failException("", DeploymentException.class);
        } catch (DeploymentException e) {
			DeploymentPackage installed = getDeploymentAdmin()
					.getDeploymentPackage(testDP.getName());
			assertNull("Installation was aborted", installed);
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(dp);
        }
    }
    
    /**
     * This test case asserts the following order is followed: "First the stale
     * resources are dropped and then later the bundles are uninstalled"
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession008() {
		log("#testInstallSession008");
        TestingSessionResourceProcessor tsrp = null;
        DeploymentPackage dp1 = null;
        try {
			TestingDeploymentPackage testDP1 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP1.getFilename());

            tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
			assertNotNull(
					"TestingSessionResourceProcessor installed correctly", tsrp);
            
            TestingDeploymentPackage testDP2 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);

            tsrp.setTest(TestingSessionResourceProcessor.ORDER_OF_UNINSTALLING);
			Bundle b = getBundle("bundles.tb3");
			assertNotNull("Bundle is not null", b);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP2.getFilename());
            
            assertTrue(
					"First the stale resources are dropped and then later the bundles are uninstalled",
            		tsrp.uninstallOrdered() && b.getState()==Bundle.UNINSTALLED);

        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	if (null!=tsrp)
        		tsrp.reset();
			uninstall(dp1);
        }
    }
    
    /**
     * First the stale resources then later the bundles are
     * uninstalled. Asserts that <b>Exceptions are ignored in this phase to
     * allow repairs to always succeed</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession009() {
		log("#testInstallSession009");
        TestingSessionResourceProcessor tsrp = null;
        DeploymentPackage dp1 = null;
        try {
			TestingDeploymentPackage testDP1 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP1.getFilename());

            tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
			assertNotNull(
					"TestingSessionResourceProcessor installed correctly", tsrp);
            
            TestingDeploymentPackage testDP2 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            //Dropped method throws a DeploymentException
            tsrp.setException(TestingSessionResourceProcessor.DROPPED);
            
            tsrp.setTest(TestingSessionResourceProcessor.ORDER_OF_UNINSTALLING);
            
			Bundle b = getBundle("bundles.tb3");
			assertNotNull("Bundle is not null", b);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP2.getFilename());
            
            assertTrue(
					"First the stale resources then later the bundles are uninstalled. "
							+
            		"Asserts that Exceptions are ignored in this phase to allow repairs to always succeed",
            		tsrp.uninstallOrdered() && b.getState()==Bundle.UNINSTALLED);

        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	if (null!=tsrp)
        		tsrp.reset();
			uninstall(dp1);
        }
    }
    
    /**
     * Asserts that Deployment Admin drops all the resources, <b>in reverse
     * target order </b>, that are in the target by calling the matching
     * Resource Processor service dropped(String) method
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession010() {
		log("#testInstallSession010");
        DeploymentPackage dp1 = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			TestingDeploymentPackage target = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ target.getFilename());
            // handles session operations
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");

			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE);
			dp1 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
            
            TestingResource[] testRes = target.getResources();
            
            String[] resourcesDroppedOrder = tsrp.getResourcesDropped();
			assertTrue(
					"Asserts that Deployment Admin drops all the resources in reverse target order",
            		resourcesDroppedOrder[0].equals(testRes[1].getName()) && 
    				resourcesDroppedOrder[1].equals(testRes[0].getName()) && 
					resourcesDroppedOrder[2]==null);
            
            
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(dp1);
            if (tsrp != null)
                tsrp.reset();
        }
    }
    
    /**
     * Uninstall all stale bundles <b>in reverse target order </b>, using the
     * OSGi Framework uninstall method semantics.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession011() {
		log("#testInstallSession011");
        DeploymentPackage dp1 = null, dp2 = null;
        try {
			TestingDeploymentPackage testDP1 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP1.getFilename());
            // listen to bundles events
			BundleListenerImpl listener = getBundleListener();
            listener.reset();

			TestingDeploymentPackage testDP2 = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE);
			dp1 = installDeploymentPackage(getWebServer()
					+ testDP2.getFilename());
            
            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            BundleEvent event = null;
            int i = 2; //there were 3 bundles in DP
            while (it.hasNext() && i >= 0) {
                event = (BundleEvent)it.next();
                if (event.getType() == BundleEvent.UNINSTALLED) {
                    TestingBundle testBundle = testDP1.getBundles()[i--];
					assertEquals(
							"The bundles were uninstalled in reverse target order",
                        testBundle.getName(), event.getBundle().getSymbolicName());
                        
                }
            }
            if (i > 0)
				fail("Not all bundles were uninstalled");
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(new DeploymentPackage[] {dp2, dp1});
        }
    }
    
    /**
     * <b>Any exceptions thrown during ResourceProcessor.dropped(String) method
     * must be ignored</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession012() {
		log("#testInstallSession012");
        DeploymentPackage dp1 = null, dp2 = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			TestingDeploymentPackage target = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ target.getFilename());
            // handles session operations
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
            tsrp.setException(TestingSessionResourceProcessor.DROPPED);

			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE);
			dp2 = installDeploymentPackage(getWebServer()
					+ source.getFilename()); // If must not thrown any
												// exceptions
            
// pass("Asserts that any exceptions thrown during dropped() method must ignored");
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(new DeploymentPackage[] {dp2, dp1});
        }
    }
    
    /**
     * Asserts that processors that have not joined the session(don't have associated
     * resource) <b>must NOT prepare to commit</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession013() {
		log("#testInstallSession013");
        DeploymentPackage dp1 = null, rp = null;
        TestingSessionResourceProcessor tsrp = null;
        TestingResourceProcessor trp = null;
        try {
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                    "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
                
            tsrp.reset();
			TestingDeploymentPackage testRP = getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			rp = installDeploymentPackage(getWebServer() + testRP.getFilename());
            
            TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
            // handles session operations


			assertTrue(DeploymentConstants.PID_RESOURCE_PROCESSOR3
					+ " have NOT to prepared to commit",
                !tsrp.isPrepared());

			trp = (TestingResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR2 + ")");
            
			assertTrue(DeploymentConstants.PID_RESOURCE_PROCESSOR2
					+ " have to prepared to commit",
                trp.sessionPrepareTime()>0);

        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(new DeploymentPackage[] {dp1, rp});
        }
    }
    
    /**
     * All the Resource Processor services that have joined the session must
     * now prepare to commit. <b>If any Resource Processor throws an Exception, the
     * session must roll back</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession014() {
		log("#testInstallSession014");
        DeploymentPackage dp1 = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
            tsrp.setException(TestingSessionResourceProcessor.PREPARE);
            
			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
			failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
			assertTrue("Session was Rolled back", tsrp.isRolledback());
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(dp1);
        }
    }
    
    /**
     * Asserts that DeploymentAdmin start the bundles <b>in the source resource
     * order</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession015() {
		log("#testInstallSession015");
        DeploymentPackage dp1 = null;
        TestingSessionResourceProcessor tsrp = null;
        try {
			tsrp = (TestingSessionResourceProcessor) getService(
					ResourceProcessor.class,
                "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
            
			BundleListenerImpl listener = getBundleListener();
            listener.reset();
            
			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
            TestingBundle[] bundles = source.getBundles();
			dp1 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
            
            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            BundleEvent event = null;
            int i = 0;
            while (it.hasNext() && i < 3) {
                event = (BundleEvent)it.next();
                if (event.getType() == BundleEvent.STARTED) {
                    Bundle b = event.getBundle();
					assertEquals("Bundle " + bundles[i].getName()
                        + " started in the correct order", bundles[i++].getName(), b.getSymbolicName());
                }
            }
            if (i <= 2) {
				fail("Not all bundles were started");
            }
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
			uninstall(dp1);
        }
    }
    
    /**
     * Asserts that Exceptions thrown during the start of deployment package
     * bundles <b>must be logged but do not abort the deployment operation</b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession016() {
		log("#testInstallSession016");
        DeploymentPackage dp1 = null;
        TestingSessionResourceProcessor tsrp = null;
		LogEntryCollector logEntryCollector = new LogEntryCollector();
		getLogReader().addLogListener(logEntryCollector);
        try {
			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.BUNDLE_FAIL_RES_DP);
			dp1 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
			Iterator logsAfter = logEntryCollector.getEntries().iterator();
            // Cannot assume the most recent log is the deployment installation
            boolean found = false;
			while (logsAfter.hasNext() && !found) {
				LogEntry log = (LogEntry) logsAfter.next();
                Bundle b = log.getBundle();
                if (b.getSymbolicName().indexOf("deployment") != -1) {
                    found = true;
					// pass("Deployment Admin bundle logged the start bundle failure");
                }
            }
            if (!found) {
				log("Deployment Admin bundle did not logged the start bundle failure");
            }
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			getLogReader().removeLogListener(logEntryCollector);
            if (tsrp != null)
                tsrp.reset();
			uninstall(dp1);
        }
    }
    
    /**
     * Asserts that all target bundles <b>must be stopped in reverse target
     * resource order </b>.
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testInstallSession017() {
		log("#testInstallSession017");
        DeploymentPackage dp1 = null, dp2 = null;
        try {
			TestingDeploymentPackage target = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_BUNDLE_RES_DP);
            TestingBundle[] bundles = target.getBundles();
			dp1 = installDeploymentPackage(getWebServer()
					+ target.getFilename());
            // listen to bundles events
			BundleListenerImpl listener = getBundleListener();
            listener.reset();

			TestingDeploymentPackage source = getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE);
			dp2 = installDeploymentPackage(getWebServer()
					+ source.getFilename());
            
            Vector events = listener.getEvents();
            Iterator it = events.iterator();
            BundleEvent event = null;
            int i = 2; // there are 3 bundles
            while (it.hasNext() && i >= 0) {
                event = (BundleEvent)it.next();
                if (event.getType() == BundleEvent.STOPPED) {
                    Bundle b = event.getBundle();
					assertEquals("Bundle " + bundles[i].getName()
                        + " stopped in the correct order", bundles[i--].getName(), b.getSymbolicName());
                }
            }
            if (i > 0) {
				fail("Not all bundles were stopped");
            }
        } catch (Exception e) {
			fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
			uninstall(new DeploymentPackage[] {dp2, dp1});
        }
    }
    
}

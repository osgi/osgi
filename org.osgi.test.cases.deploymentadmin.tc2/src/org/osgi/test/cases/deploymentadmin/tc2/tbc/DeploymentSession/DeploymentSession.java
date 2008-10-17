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
 * Abr 28, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK
 * ============  ============================================================== 
 * Jun 10, 2005  Andre Assad
 * 26            After formal inspection
 * ============  ==============================================================
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 * Aug 31, 2005  Andre Assad
 * 179           Implement Review Issues
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentSession;

import java.io.File;
import java.io.FilePermission;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingBlockingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingSessionResourceProcessor;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Class Validates the implementation of
 * <code>getDataFile, getSourceDeploymentPackage, getTargetDeploymentPackage<code> 
 * from <code>DeploymentSession<code> class
 */
public class DeploymentSession {

	private DeploymentTestControl tbc;
    
    public static boolean SIMULATING_EXCEPTION_ON_COMMIT = false;
    public static boolean SIMULATING_EXCEPTION_ON_PREPARE = false;
    
	public DeploymentSession(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}


	public void run() {
		testDeploymentSession001();
		testDeploymentSession002();
		testDeploymentSession003();
		testDeploymentSession004();
		testDeploymentSession005();
		testDeploymentSession006();
		testDeploymentSession007();
		testDeploymentSession008();
		testDeploymentSession009();
		testDeploymentSession010();
		testDeploymentSession011();
        testDeploymentSession012();
        testDeploymentSession013();
	}

		
	/**
	 * Asserts that <code>getDataFile<code> returns the private data area of the bundle in
	 * the source and target deployment packages.'
	 * 
	 * @spec DeploymentSession.getDataFile(Bundle)
	 */
	private void testDeploymentSession001()  {
		tbc.log("#testDeploymentSession001");
		
		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=*)");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		// bundles002.jar version 1.5
		TestingBundle testTargetBundle = testRP.getBundles()[1];
		// bundle001.jar version 1.0
		TestingBundle testSourceBundle = testDP.getBundles()[0];
		
		DeploymentPackage sourceDP = null, targetDP = null;
		try {
            sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);

            TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor) getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
			
			File file1 = testSessionRP.getDataFile(targetDP.getBundle(testTargetBundle.getName()));
            tbc.assertNotNull("The session has access to the private data area of the bundle in the target DP", file1);

			File file2 = testSessionRP.getDataFile(sourceDP.getBundle(testSourceBundle.getName()));
			tbc.assertNotNull("The session has access to the private data area of the bundle in the source DP", file2);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { sourceDP, targetDP });
		}
	}
	
    /**
	 * Asserts that <code>DeploymentCustomizerPermission(" <filter>", "privatearea")<code>
	 * is needed for calling <code>getDataFile<code> method. <code>SecurityException<code> must be
	 * thrown if the caller is not the customizer of the corresponding
	 * deployment package.
	 * 
	 * @spec DeploymentSession.getDataFile(Bundle)
	 */
	private void testDeploymentSession002()  {
		tbc.log("#testDeploymentSession002");
		
		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=bundles.tb4)");
		
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		DeploymentPackage sourceDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			
			TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor)getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
			
			testSessionRP.getDataFile(tbc.getBundle(testRP.getBundles()[0].getName()));
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
		}
	}
	
	/**
	 * Asserts if the deployment action is an install,
	 * <code>getSourceDeploymentPackage<code> returns the <code>DeploymentPackage<code> instance that
	 * corresponds to the deployment package being streamed in for this session.
	 * 
	 * @spec DeploymentSession.getSourceDeploymentPackage()
	 */
	private void testDeploymentSession003()  {
		tbc.log("#testDeploymentSession003");
		
		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=*)");
		
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		DeploymentPackage sourceDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);

			TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor) getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);

			DeploymentPackage dp = testSessionRP.getSourceDeploymentPackage();
			tbc.assertEquals("The target deployment package name is correct", sourceDP.getName(), dp.getName());
			tbc.assertEquals("The target deployment package version is correct", sourceDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
		}
	}
	
	/**
	 * Asserts if the deployment action is an update, <code>getTargetDeploymentPackage<code>
	 * returns the <code>DeploymentPackage<code> instance for the installed deployment
	 * package.
	 * 
	 * @spec DeploymentSession.getTargetDeploymentPackage()
	 */
	private void testDeploymentSession004()  {
		tbc.log("#testDeploymentSession004");

		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=*)");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null, targetDP = null;
		try {
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);

			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			
			TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor)getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);

			DeploymentPackage dp = testSessionRP.getTargetDeploymentPackage();
			tbc.assertEquals("The source deployment package name is correct", targetDP.getName(), dp.getName());
			tbc.assertEquals("The source deployment package version is correct", targetDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { sourceDP, targetDP });
			
		}
	}
	
	
	/**
	 * Asserts if the deployment action is an install,
	 * <code>getTargetDeploymentPackage<code> returns an
	 * empty deploymet package.
	 * 
	 * @spec DeploymentSession.getTargetDeploymentPackage()
	 */
	private void testDeploymentSession005()  {
		tbc.log("#testDeploymentSession005");

		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=*)");

		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		DeploymentPackage sourceDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}), sourceDP);
			
			TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor)getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);
			
			DeploymentPackage dp = testSessionRP.getTargetDeploymentPackage();
			tbc.assertEquals("The target deployment package version is 0.0.0", Version.emptyVersion, dp.getVersion());
			tbc.assertTrue("The target deployment package name is an empty string", dp.getName().equals(""));
			tbc.assertTrue("The target deployment package is stale", dp.isStale());
			tbc.assertTrue("The target deployment package has no bundles", dp.getBundleInfos().length==0);
			tbc.assertTrue("The target deployment package has no resources", dp.getResources().length==0);
			// The mandatory headers, just to check if it really doesnt have headers
			tbc.assertTrue("The target deployment package name has no headers", dp.getHeader("DeploymentPackage-SymbolicName").equals("") && dp.getHeader("DeploymentPackage-Version").equals(Version.emptyVersion.toString()));
			//The tests of IllegalStateException is done at IsStale class. The dp does not have any resources, so getResourceHeader is not checked. 
			


		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
		}
	}
	
	/**
	 * Asserts that Deployment Admin service called the prepare method on all
	 * Resource Processor services that have joined the session, and that
	 * Resource Processor services were called in the reverse order of joining.
	 * 
	 * @spec 114.7 Sessions
	 */
	private void testDeploymentSession006()  {
		tbc.log("#testDeploymentSession006");
		
		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR1, "(name=*)");
        
        setResourceProcessorPermissions(
            DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR2, "(name=*)");
		
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
		DeploymentPackage targetDP = null;
		try {
			DeploymentTestControl.resetResourceProcessorOrder();
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			
			//RP1 joined the session first
			if (DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.BEGIN)) {
				//RP2 prepare method must be called first
				tbc.assertTrue("The Resource Processor prepare method was called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PREPARE));				
			} else {
				//RP2 joined the session first, RP1 prepare method must be called first
				tbc.assertTrue("The Resource Processor prepare method was called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.PREPARE));				
			}
			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * Asserts that the Deployment Admin service called the commit method on all
	 * Resource Processor services that have joined the session, and that
	 * Resource Processor were called in the reverse order of joining.
	 * 
	 * @spec 114.7 Sessions
	 */
	private void testDeploymentSession007()  {
		tbc.log("#testDeploymentSession007");
        
        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
            + DeploymentConstants.PID_RESOURCE_PROCESSOR1, "(name=*)");
        
        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
            + DeploymentConstants.PID_RESOURCE_PROCESSOR2, "(name=*)");

		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
		DeploymentPackage targetDP = null;
		try {
			DeploymentTestControl.resetResourceProcessorOrder();
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			
			//RP1 joined the session first
			if (DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.BEGIN)) {
				//RP2 commit method must be called first
				tbc.assertTrue("The Resource Processors commit methods were called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.COMMIT));				
			} else {
				//RP2 joined the session first, RP1 commit method must be called first
				tbc.assertTrue("The Resource Processors commit methods were called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.COMMIT));				
			}			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * Asserts that any Exceptions thrown in ResourceProcessor commit method is
	 * ignored by the Deployment Admin service.
	 * 
	 * @spec 114.7 Sessions
	 */
	private void testDeploymentSession008()  {
		tbc.log("#testDeploymentSession008");
        
        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
            + DeploymentConstants.PID_RESOURCE_PROCESSOR1, "(name=*)");
        
        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
            + DeploymentConstants.PID_RESOURCE_PROCESSOR2, "(name=*)");
        
        SIMULATING_EXCEPTION_ON_COMMIT = true;

		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
		DeploymentPackage targetDP = null;
		try {
			// to register TestingResourceProcessor
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			
			// to hold testing resource processor service instance
			TestingResourceProcessor rp1 = (TestingResourceProcessor)getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
			
			tbc.assertTrue("DeploymentAdmin service ignored exception throwed on commit method.", rp1.isInstallUpdateOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
            SIMULATING_EXCEPTION_ON_COMMIT = false;
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * Asserts that the Resource Processor services must be called in the
	 * reverse order of joining for the rollback method.
	 * 
	 * @spec 114.7.1 Roll Back
	 */
	private void testDeploymentSession009()  {
		tbc.log("#testDeploymentSession009");
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_TEST_DP);
		DeploymentPackage targetDP = null;
		try {
            // to register TestingResourceProcessor
            targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
            
            SIMULATING_EXCEPTION_ON_PREPARE = true;
			DeploymentTestControl.resetResourceProcessorOrder();            
            targetDP.uninstall();
            tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			//RP1 joined the session first
			if (DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.BEGIN)) {
				//RP2 rollback method must be called first
				tbc.assertTrue("The Resource Processors rollback methods were called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.ROLLBACK));				
			} else {
				//RP2 joined the session first, RP1 rollback method must be called first
				tbc.assertTrue("The Resource Processors rollback methods were called in the reverse order of joining.",
						DeploymentTestControl.isResourceProcessorCallOrdered(DeploymentConstants.PID_RESOURCE_PROCESSOR1,DeploymentConstants.PID_RESOURCE_PROCESSOR2,DeploymentConstants.ROLLBACK));				
			}			

        } catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
            SIMULATING_EXCEPTION_ON_PREPARE = false;
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * Asserts that Bundle events produced by a transactional implementation
	 * must be compatible with the Bundle events produced by a nontransactional
	 * implementation.
	 * 
	 * @spec 114.7.2 Bundle Events During Deployment
	 */
	private synchronized void testDeploymentSession010()  {
		tbc.log("#testDeploymentSession010");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.TRANSACTIONAL_SESSION_DP);
		DeploymentPackage targetDP = null;
		Vector events = null;
		BundleListenerImpl listener = tbc.getBundleListener();
		// makes sure no other test case settings will influence
		listener.reset();
		try {
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
            TestingBundle[] testBundles = testDP.getBundles();
            events = listener.getEvents();
            if (tbc.isTransactionalDA()){
                tbc.assertTrue("In a transactional session no event is sent if the installation fails", events.isEmpty());
            } else { //non-transactional
        		try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
        		
                Iterator it = events.iterator();
                BundleEvent event = null;
                int i = 0;
                // assert INSTALLED events
                while (it.hasNext() && i < 2) {
                    event = (BundleEvent)it.next();
                    if (event.getType() == BundleEvent.INSTALLED) {
                        tbc.assertEquals("Bundle " + i + " INSTALLED",
                            testBundles[i++].getName(), event.getBundle().getSymbolicName());
                    }
                }

                // assert UNINSTALLED events
                it = events.iterator();
                while (it.hasNext() && i >= 0) {
                    event = (BundleEvent)it.next();
                    if (event.getType() == BundleEvent.UNINSTALLED) {
                        tbc.assertEquals("Bundle " + i + " UNINSTALLED",
                            testBundles[--i].getName(), event.getBundle().getSymbolicName());
                    }
                }
            }
        } catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(targetDP);
			listener.reset();
		}
	}
	
    /**
	 * Asserts if the deployment action is an update, <code>getSourceDeploymentPackage<code>
	 * returns the <code>DeploymentPackage<code> instance that corresponds to the deployment 
	 * package being streamed in for this session
	 * 
	 * @spec DeploymentSession.getSourceDeploymentPackage()
	 */
	private void testDeploymentSession011()  {
		tbc.log("#testDeploymentSession011");

		setResourceProcessorPermissions(
				DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, "(name=*)");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null, targetDP = null;
		try {
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);

			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			
			TestingSessionResourceProcessor testSessionRP = (TestingSessionResourceProcessor)getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR3);

			DeploymentPackage dp = testSessionRP.getSourceDeploymentPackage();
			tbc.assertEquals("The source deployment package name is correct", sourceDP.getName(), dp.getName());
			tbc.assertEquals("The source deployment package version is correct", sourceDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { sourceDP, targetDP });
			
		}
	}
    
    	 
    /**
     * Asserts that <code>DeploymentAdmin</code> <b>must only process a single
     * session at a time </b>. When a client requests a new session with an
     * install or uninstall operation, <b>it must block </b> that call until the
     * earlier session is completed. This test case installs a deployment
     * package that blocks the session and checks whether the session was
     * blocked for an uninstallation.
     * 
     * @spec 114.12 Threading
     */
	 private synchronized void testDeploymentSession012() {
	        tbc.log("#testDeploymentSession012");
	        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
	            + DeploymentConstants.PID_RESOURCE_PROCESSOR4, "(name=*)");
	        SessionWorker workerSimpleDP = null,workerBlockDP = null;
	        
	        TestingBlockingResourceProcessor testBlockRP = null;
            TestingDeploymentPackage blockDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);

	        try {
	            workerSimpleDP = new SessionWorker(testDP);
	            workerSimpleDP.start();
	            
	            //Installs the simple.dp
	            while (!workerSimpleDP.isInstalled() && !workerSimpleDP.installFailed) {
	              Thread.sleep(100);
	            }
	            
	            if (workerSimpleDP.installFailed) {
	              tbc.fail("The installation of a simple DP failed!");
	            }
	            
	            workerBlockDP = new SessionWorker(blockDP);
	            workerBlockDP.start();
	            
	            //Before the workerBlockDP installation is blocked, it registers a resource processor 
	            while (null==testBlockRP) {
	              try {
	                Thread.sleep(100);
	              } catch (InterruptedException ie) {}
	            	testBlockRP = (TestingBlockingResourceProcessor) getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR4);
	            }
	            
	            tbc.assertTrue("The installation of Block DP was not completed",!workerBlockDP.isInstalled());
	            
	            workerSimpleDP.uninstallDP();
	            
	            tbc.assertTrue("Uninstallation of test DP was not completed, DeploymentAdmin only processed a single session at a time", 
	            		!workerSimpleDP.isUninstalled());
	            
	            // releases blocking resource processor
	            testBlockRP.setReleased(true);
	            
	        } catch (Exception e) {
	            tbc.fail(MessagesConstants.getMessage(
	                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
	        } finally {
	        	if (null!=testBlockRP) 
	        		testBlockRP.setReleased(true);
	        	if (null!=workerSimpleDP)
	        		workerSimpleDP.uninstallDP();
	        	if (null!=workerBlockDP)
	        		workerBlockDP.uninstallDP();
	            //If the uninstallation was not completed, we need to remove this DP in order to not affect other TCs 
	            cleanUp(testDP);
	        }
	    }
	
    /**
     * The Deployment Admin service <b>must throw a Deployment Exception</b> when the
     * session can not be created after an <b>appropriate time out</b> period.
     * 
     * @spec 114.12 Threading
     */
	 
	 private synchronized void testDeploymentSession013() {
	        tbc.log("#testDeploymentSession013");
	        setResourceProcessorPermissions(DeploymentConstants.OSGI_DP_LOCATION
	            + DeploymentConstants.PID_RESOURCE_PROCESSOR4, "(name=*)");
	        
	        SessionWorker worker1 = null;
	        TestingBlockingResourceProcessor testBlockRP = null;
	        
	        try {
	            TestingDeploymentPackage blockDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
	            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
	            
	            worker1 = new SessionWorker(blockDP);
	            worker1.start();
	            
	            //Just to guarantee that this Thread is executed before the second one
	            while (!worker1.isRunning()) {
                try {
                  Thread.sleep(100);
                } catch (InterruptedException ie) {}
	            }
	          //Before the workerBlockDP installation is blocked, it registers a resource processor 
	            while (null==testBlockRP) {
	            	testBlockRP = (TestingBlockingResourceProcessor) getTestSessionRP(DeploymentConstants.PID_RESOURCE_PROCESSOR4);
                try {
                  Thread.sleep(100);
                } catch (InterruptedException ie) {}
	            }

	            
	            tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
	            tbc.failException("", DeploymentException.class);
	        } catch (DeploymentException e) {
	        	tbc.assertEquals("DeploymentException.CODE_TIMEOUT is thrown when the session cannot be created after an appropriate time out period.", 
	        			DeploymentException.CODE_TIMEOUT,e.getCode());
	        } catch (Exception e) {
	            tbc.fail(MessagesConstants.getMessage(
	                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
	        } finally {
	        	if (null!=testBlockRP) 
	        		testBlockRP.setReleased(true);
	        	if (null!=worker1)
	        		worker1.uninstallDP();
	        }
	    }
	 
	
    private void cleanUp(TestingDeploymentPackage testDP) {
    	DeploymentPackage dp = tbc.getDeploymentAdmin().getDeploymentPackage(testDP.getName());
    	if (null!=dp) {
    		tbc.uninstall(dp);
    	}
    }
	
    /**
	 * @return Returns a TestingSessionResourceProcessor instance.
	 * @throws InvalidSyntaxException 
	 */
	private Object getTestSessionRP(String pid) throws InvalidSyntaxException {
        
        ServiceReference[] sr = tbc.getContext().getServiceReferences(
            ResourceProcessor.class.getName(), "(service.pid=" + pid + ")");
        
		return (sr!=null)?tbc.getContext().getService(sr[0]):null;
        
	}
	
	/**
	 * Sets a PermissionInfo for a resource processor bundle
	 * @param location
	 * @param name filter
	 */
	private void setResourceProcessorPermissions(String location, String filter) {
		PermissionInfo info[] = {
				new PermissionInfo(DeploymentCustomizerPermission.class.getName(), filter,
						DeploymentCustomizerPermission.PRIVATEAREA),
				new PermissionInfo(ServicePermission.class.getName(), "*",ServicePermission.GET + ","
								+ ServicePermission.REGISTER),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
				new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"), };
		
		tbc.setPermissionInfo(location, info);
	}
	
	
	class SessionWorker extends Thread {
		private TestingDeploymentPackage testDP;
		private boolean installed=false;
		private boolean uninstalled=false;
		
		private boolean uninstall=false;
		private boolean running=false;
    boolean installFailed=false;
		
		
		
        protected SessionWorker(TestingDeploymentPackage testDP) {
            this.testDP = testDP;
        }
        
        public void uninstallDP() {
        	if (isAlive()) {
        		uninstall=true;
	        	try {
					this.join(DeploymentConstants.SHORT_TIMEOUT);
				} catch (InterruptedException e) {
					
				}
        	}
        	
        }
        
        public boolean isInstalled() {
        	return installed;
        }
        
        public boolean isRunning() {
        	return running;
        }
        public boolean isUninstalled() {
        	return uninstalled;
        }
		public synchronized void run() {
			DeploymentPackage dp = null;
			try {
				try {
					running=true;
					dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
					installed=(dp==null?false:true);
				} catch (Throwable t) {
				  t.printStackTrace();
          installFailed = true;
				}
			} finally {
				while (!uninstall) {
					try {
						this.wait(100);
					} catch (InterruptedException e) {
					}
				}
				if (isInstalled()) {
					try {
						dp.uninstall();
						uninstalled=true;
					} catch (Exception e) {
					  
					}
				}
			}
			
		}
	}
}

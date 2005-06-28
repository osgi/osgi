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
 */

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentSession;

import java.io.File;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.TestingSessionResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentSession#getDeploymentAction,getSourceDeploymentPackage,getTargetDeploymentPackage
 * @generalDescription This Test Class Validates the implementation of
 *                     DeploymentSession methods
 */
public class DeploymentSession implements TestInterface {

	private DeploymentTestControl tbc;
	
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
	}

		
	/**
	 * @testID testDeploymentSession001
	 * @testDescription Asserts that getDataFile returns the private data area
	 *                  descriptor of the bundle in the source and target
	 *                  deployment packages
	 */
	private void testDeploymentSession001()  {
		tbc.log("#testDeploymentSession001");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		TestingBundle testTargetBundle = testRP.getBundles()[0];
		TestingBundle testSourceBundle = testDP.getBundles()[0];
		
		DeploymentPackage sourceDP = null;
		DeploymentPackage targetDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());
				
			File file = testSessionRP.getDataFile(targetDP.getBundle(testTargetBundle.getName()));
			tbc.assertNotNull("The session has access to the private data area of the bundle in the target DP", file);
			file = testSessionRP.getDataFile(sourceDP.getBundle(testSourceBundle.getName()));
			tbc.assertNotNull("The session has access to the private data area of the bundle in the source DP", file);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * @testID testDeploymentSession002
	 * @testDescription Asserts that getDataFile returns the private data area
	 *                  descriptor of the bundle in the source and target
	 *                  deployment packages
	 */
	private void testDeploymentSession002()  {
		tbc.log("#testDeploymentSession002");
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage targetDP = null;
		try {
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());
				
			testSessionRP.getDataFile(tbc.getBundle("org.osgi.test.cases.deployment.bundles.rp1"));
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * @testID testDeploymentSession003
	 * @testDescription Asserts getSourceDeploymentPackage returns the
	 *                  DeploymentPackage instance that corresponds to the
	 *                  deployment package being streamed in for this session
	 */
	private void testDeploymentSession003()  {
		tbc.log("#testDeploymentSession003");
		
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null;
		DeploymentPackage targetDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());

			DeploymentPackage dp = testSessionRP.getSourceDeploymentPackage();
			tbc.assertEquals("The source deployment package name is correct", sourceDP.getName(), dp.getName());
			tbc.assertEquals("The source deployment package version is correct", sourceDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * @testID testDeploymentSession004
	 * @testDescription Asserts getTargetDeploymentPackage returns the
	 *                  DeploymentPackage instance for the updated deployment
	 *                  package
	 */
	private void testDeploymentSession004()  {
		tbc.log("#testDeploymentSession004");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null;
		DeploymentPackage targetDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());

			DeploymentPackage dp = testSessionRP.getTargetDeploymentPackage();
			tbc.assertEquals("The target deployment package name is correct", targetDP.getName(), dp.getName());
			tbc.assertEquals("The target deployment package version is correct", targetDP.getVersion(), dp.getVersion());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * @testID testDeploymentSession005
	 * @testDescription Asserts getSourceDeploymentPackage returns an empty
	 *                  DeploymentPackage if the deployment action is uninstall
	 */
	private void testDeploymentSession005()  {
		tbc.log("#testDeploymentSession005");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null;
		DeploymentPackage targetDP = null;
		try {
			sourceDP = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),sourceDP);
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			tbc.uninstall(targetDP);
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());

			DeploymentPackage dp = testSessionRP.getSourceDeploymentPackage();
			tbc.assertEquals("The target deployment package name is correct", "", dp.getName());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
			tbc.uninstall(targetDP);
		}
	}
	
	/**
	 * @testID testDeploymentSession006
	 * @testDescription Asserts getTargetDeploymentPackage returns an empty
	 *                  DeploymentPackage if the deployment action is install
	 */
	private void testDeploymentSession006()  {
		tbc.log("#testDeploymentSession006");
		TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SESSION_RESOURCE_PROCESSOR_DP);
		
		DeploymentPackage sourceDP = null;
		DeploymentPackage targetDP = null;
		try {
			targetDP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),targetDP);
			
			TestingSessionResourceProcessor testSessionRP = getTestSessionRP(targetDP, testRP.getResources()[0].getName());

			DeploymentPackage dp = testSessionRP.getTargetDeploymentPackage();
			tbc.assertEquals("The target deployment package version is 0.0.0", new Version(0,0,0), dp.getVersion());
			tbc.assertTrue("The target deployment package has no resources", dp.getResources().length==0);
			tbc.assertTrue("The target deployment package has no bundles", dp.getBundleSymNameVersionPairs().length==0);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(sourceDP);
			tbc.uninstall(targetDP);
		}
	}

	/**
	 * @return Returns a TestingSessionResourceProcessor instance.
	 */
	public TestingSessionResourceProcessor getTestSessionRP(DeploymentPackage dp, String resource) {
		ServiceReference reference = dp.getResourceProcessor(resource);
		return (TestingSessionResourceProcessor) tbc.getContext().getService(reference);
	}
}

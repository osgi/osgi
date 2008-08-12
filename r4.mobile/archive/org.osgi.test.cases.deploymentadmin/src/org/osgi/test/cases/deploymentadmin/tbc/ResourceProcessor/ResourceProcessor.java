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
 * ===========   ==============================================================
 * Jun 10, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tbc.ResourceProcessor;


import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResource;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 * 
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>ResourceProcessor<code> method, according to MEG reference
 *                     documentation (RFC-088).
 */
public class ResourceProcessor {
	
	private DeploymentTestControl tbc;
	private TestingDeploymentPackage testRP;
	private DeploymentPackage dpResourceProcessor, dpInstallResource, dpUpdateResource, dpUninstallResource;
	private TestingResourceProcessor resourceProcessor;
	private String bundleNameFromInstallPackage;
	
	public ResourceProcessor(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		installResourceProcessor();
		installResourceDP();
		testResourceProcessor001();
		testResourceProcessor002();
		updateResourceDP();
		testResourceProcessor003();
		testResourceProcessor004();
		testResourceProcessor005();
		testResourceProcessor006();
	}

		
	private void installResourceProcessor()  {
		tbc.log("#Installing resource processor");
		try {
			testRP = tbc.getTestingDeploymentPackage(DeploymentTestControl.RESOURCE_PROCESSOR_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}

	}

	private void installResourceDP()  {
		try {
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentTestControl.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			TestingBundle testBundle = testDPInstall.getBundles()[0];
			bundleNameFromInstallPackage = testBundle.getName();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}

	}	
	
	/**
	 * @testID testResourceProcessor001
	 * @testDescription Asserts that the action is INSTALL and that DeploymentAdmin calls the 
	 * 					methods of a resource processor in the specified order
	 */
	private void testResourceProcessor001()  {
		tbc.log("#testResourceProcessor001");
		
		tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is installing",resourceProcessor.isInstallUpdateOrdered());			
	}
	
	/**
	 * @testID testResourceProcessor002
	 * @testDescription Asserts that DeploymentException.CODE_PREPARE is thrown when an exception is thrown on prepare() method of the ResourceProcessor
	 * 					It also tests if DeploymentAdmin calls the correct order of methods, including rollback()			
	 */
	private void testResourceProcessor002() {
		tbc.log("#testResourceProcessor002");
		resourceProcessor.setSimulateExceptionOnPrepare(true);
		resourceProcessor.resetCount();
		try {
			TestingDeploymentPackage testDPUpdate = tbc.getTestingDeploymentPackage(DeploymentTestControl.RP_RESOURCE_UPDATE_DP);			
			dpUpdateResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUpdate.getFilename());
			tbc.failException("#",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Asserts that DeploymentException.CODE_PREPARE is thrown when a resource processor cannot commit",DeploymentException.CODE_PREPARE,e.getCode());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the dropped() method",resourceProcessor.exceptionAtPrepareOrdered());
		} finally {
			resourceProcessor.setSimulateExceptionOnPrepare(false);	
		}
	}

	private void updateResourceDP() {
		tbc.log("#Updating resource processor");
		try {
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUpdate = tbc.getTestingDeploymentPackage(DeploymentTestControl.RP_RESOURCE_UPDATE_DP);
			dpUpdateResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUpdate.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that updates a resource"}),dpUpdateResource);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testResourceProcessor003
	 * @testDescription Asserts that the action is UPDATE and that DeploymentAdmin calls the 
	 * 					methods of a resource processor in the specified order
	 */
	private void testResourceProcessor003() {
		tbc.log("#testResourceProcessor003");
		tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is updating",resourceProcessor.isInstallUpdateOrdered());
		
	}

	/**
	 * @testID testResourceProcessor004
	 * @testDescription Asserts that DeploymentException.CODE_NO_SUCH_RESOURCE is thrown when an exception is thrown on dropped() method of the ResourceProcessor
	 * 					It also tests if DeploymentAdmin calls the correct order of methods, including rollback()			
	 */
	private void testResourceProcessor004() {
		try {
			tbc.log("#testResourceProcessor004");
			resourceProcessor.setSimulateExceptionOnDropped(true);
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUninstall = tbc.getTestingDeploymentPackage(DeploymentTestControl.RP_RESOURCE_UNINSTALL_DP);
			dpUninstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUninstall.getFilename());
			tbc.failException("#",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Asserts that DeploymentException.CODE_NO_SUCH_RESOURCE is thrown when the dropped(String resource) method is called on a resource processor that doesn't manage this resource",DeploymentException.CODE_NO_SUCH_RESOURCE,e.getCode());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the dropped() method",resourceProcessor.exceptionAtDroppedOrdered());
		} finally {
			resourceProcessor.setSimulateExceptionOnDropped(false);	
		}
		
	}	
	/**
	 * @testID testResourceProcessor005
	 * @testDescription Uninstall a resource (not the deployment package) 
	 * 					in order no know if the dropped method is called.
	 * 					
	 */
	private void testResourceProcessor005() {
		tbc.log("#testResourceProcessor005");
		resourceProcessor.resetCount();
		TestingDeploymentPackage testDPUninstall = tbc.getTestingDeploymentPackage(DeploymentTestControl.RP_RESOURCE_UNINSTALL_DP);
		try {
			dpUninstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUninstall.getFilename());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is uninstalling",resourceProcessor.isUninstallResourceOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));			
		}
	}
	
	/**
	 * @testID testResourceProcessor006
	 * @testDescription Uninstalls a deployment package containing a resource.
	 *                  Asserts that DeploymentAdmin calls the methods of a
	 *                  resource processor in the specified order
	 */
	private void testResourceProcessor006() {
		tbc.log("#testResourceProcessor006");
		try {
			resourceProcessor.resetCount();
			dpUpdateResource.uninstall();
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is uninstalling",resourceProcessor.isUninstallOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
}

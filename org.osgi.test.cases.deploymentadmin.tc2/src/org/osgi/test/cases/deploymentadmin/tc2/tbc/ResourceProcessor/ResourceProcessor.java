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

package org.osgi.test.cases.deploymentadmin.tc2.tbc.ResourceProcessor;


import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResource;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResourceProcessor;
/**
 * @author Andre Assad
 * 
 * This class tests methods of a ResourceProcessor, according to MEG specification.
 */
public class ResourceProcessor {
	
	private DeploymentTestControl tbc;
	
	public ResourceProcessor(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		testResourceProcessor001();
		testResourceProcessor002();
		testResourceProcessor003();
		testResourceProcessor004();
		testResourceProcessor005();
		testResourceProcessor006();
        testResourceProcessor007();
        testResourceProcessor008();
	}

		
	
    /**
	 * Asserts that the action is INSTALL and that DeploymentAdmin calls the methods of a 
	 * resource processor in the specified order
	 * 
	 * @spec 114.10 Resource Processors
	 */
	private void testResourceProcessor001()  {
		tbc.log("#testResourceProcessor001");
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		try {
			TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			TestingResourceProcessor resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is installing",resourceProcessor.isInstallUpdateOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dpInstallResource,dpResourceProcessor });
		}
	}
	
	/**
	 * Asserts that DeploymentException.CODE_PREPARE is thrown when an exception is thrown on prepare() method of the ResourceProcessor
	 * It also tests if DeploymentAdmin calls the correct order of methods, including rollback()
	 * 
	 * @spec 114.10 Resource Processors			
	 */
	private void testResourceProcessor002() {
		tbc.log("#testResourceProcessor002");
		TestingResourceProcessor resourceProcessor = null;
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		DeploymentPackage dpUpdateResource = null;
		try {
			TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			resourceProcessor.setSimulateExceptionOnPrepare(true);
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUpdate = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UPDATE_DP);			
			dpUpdateResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUpdate.getFilename());
			tbc.failException("#",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the prepare() method",resourceProcessor.exceptionAtPrepareOrdered());
		} finally {
			resourceProcessor.setSimulateExceptionOnPrepare(false);
			tbc.uninstall(new DeploymentPackage[] { dpUpdateResource,dpInstallResource,dpResourceProcessor });
		}
	}

	/**
	 * Asserts that the action is UPDATE and that DeploymentAdmin calls the 
	 * methods of a resource processor in the specified order
	 * 
	 * @spec 114.10 Resource Processors
	 */
	private void testResourceProcessor003() {
		tbc.log("#testResourceProcessor003");
		TestingDeploymentPackage testRP;
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		DeploymentPackage dpUpdateResource = null;
		try {
			testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			TestingResourceProcessor resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUpdate = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UPDATE_DP);
			dpUpdateResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUpdate.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that updates a resource"}),dpUpdateResource);
			
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is updating",resourceProcessor.isInstallUpdateOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dpUpdateResource,dpInstallResource,dpResourceProcessor });
		
		}
	}



	
	/**
     * Uninstall a resource (not the deployment package) in order no know if the
     * dropped method is called.
     * 
     * @spec 114.10 Resource Processors
     */
	private void testResourceProcessor004() {
		tbc.log("#testResourceProcessor004");
		TestingResourceProcessor resourceProcessor = null;
		TestingDeploymentPackage testRP;
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		DeploymentPackage dpUninstallResource = null;
		try {
			testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUninstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UNINSTALL_DP);
			dpUninstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUninstall.getFilename());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is uninstalling",resourceProcessor.isUninstallResourceOrdered());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));			
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dpUninstallResource,dpInstallResource,dpResourceProcessor });
		}
	}
	
	/**
     * Asserts that DeploymentException.CODE_NO_SUCH_RESOURCE is thrown when an
     * exception is thrown on dropped() method of the ResourceProcessor It also
     * tests if DeploymentAdmin calls the correct order of methods, including
     * rollback().
     * 
     * @spec 114.10 Resource Processors
     */
	private void testResourceProcessor005() {
		tbc.log("#testResourceProcessor005");
		TestingResourceProcessor resourceProcessor = null;
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		DeploymentPackage dpUninstallResource = null;
		try {
			TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);
			
			resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			resourceProcessor.setSimulateExceptionOnDropped(true);
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUninstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UNINSTALL_DP);
			dpUninstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUninstall.getFilename());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the dropped() method. It also ensures that no exception is thrown even if the resource does not exist.",resourceProcessor.exceptionAtDroppedOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			resourceProcessor.setSimulateExceptionOnDropped(false);	
			tbc.uninstall(new DeploymentPackage[] { dpUninstallResource,dpInstallResource,dpResourceProcessor });
		}
	}
    
	/**
     * Uninstalls a deployment package containing a resource. Asserts that
     * DeploymentAdmin calls the methods of a resource processor in the
     * specified order
     * 
     * @spec 114.10 Resource Processors
     */
	private void testResourceProcessor006() {
		tbc.log("#testResourceProcessor006");
		TestingDeploymentPackage testRP;
		DeploymentPackage dpResourceProcessor = null;
		DeploymentPackage dpInstallResource = null;
		try {
			testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_2_DP);
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource Processor"}),dpResourceProcessor);
			
			TestingDeploymentPackage testDPInstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_INSTALL_DP);
			TestingResource testResource = testDPInstall.getResources()[0];
			dpInstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPInstall.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Deployment package that installs a resource"}),dpResourceProcessor);
			
			ServiceReference reference = dpInstallResource.getResourceProcessor(testResource.getName());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,new String[] {"Resource processor reference"}),reference);

			TestingResourceProcessor resourceProcessor = (TestingResourceProcessor)tbc.getContext().getService(reference);
			
			resourceProcessor.resetCount();
			dpInstallResource.uninstall();
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it is uninstalling",resourceProcessor.isUninstallOrdered());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dpInstallResource,dpResourceProcessor });
		}
	}
    
    /**
     * Assert that Customizer bundles must never process a resource from another
     * Deployment Package. DeploymentException.CODE_FOREIGN_CUSTOMIZER must be
     * thrown.
     * 
     * @spec 114.5 Customizer
     */
    private void testResourceProcessor007() {
        tbc.log("#testResourceProcessor007");
        
        DeploymentPackage dp1 = null, dp2 = null;
        TestingDeploymentPackage testDP1 = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_CUSTOMIZER);
        TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_FROM_OTHER_DP);
        try {
            dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP1.getFilename());
            tbc.assertNotNull("Deployment Package 1 installed", dp1);
            
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("DeploymentException.CODE_FOREIGN_CUSTOMIZER correctly thrown", DeploymentException.CODE_FOREIGN_CUSTOMIZER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[]{dp1, dp2});
        }
    }
    
    /**
     * Asserts that the Deployment Admin service must execute all its
     * operations, including calls for handling bundles and all calls that are
     * forwarded to a Resource Processor service, inside a doPrivileged block.
     * This privileged block must use an AccessControlContext object that limits
     * the permissions to the security scope. Therefore, a Resource Processor
     * service must assume that it is always running inside the correct security
     * scope. A Resource Processor can of course use its own security scope by
     * doing a local doPrivileged block.
     * 
     * @spec 114.13.3 Permissions During an Install Session
     */
    private void testResourceProcessor008() {
        tbc.log("#testResourceProcessor008");
        
        DeploymentPackage dp1 = null, dp2 = null;
        TestingDeploymentPackage testDP1 = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_CUSTOMIZER);
        TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_FROM_OTHER_DP);
        try {
            dp1 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP1.getFilename());
            tbc.assertNotNull("Deployment Package 1 installed", dp1);
            
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("DeploymentException.CODE_FOREIGN_CUSTOMIZER correctly thrown", DeploymentException.CODE_FOREIGN_CUSTOMIZER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[]{dp1, dp2});
        }
    }
}

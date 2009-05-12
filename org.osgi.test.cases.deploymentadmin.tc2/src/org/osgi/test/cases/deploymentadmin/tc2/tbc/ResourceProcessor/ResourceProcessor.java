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
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingSessionResourceProcessor;
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
	public void testResourceProcessor001()  {
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
			e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(new DeploymentPackage[] { dpInstallResource,dpResourceProcessor });
		}
	}
	
	/**
	 * Asserts that DeploymentException.CODE_COMMIT_ERROR is thrown when an exception is thrown on prepare() method of the ResourceProcessor
	 * It also tests if DeploymentAdmin calls the correct order of methods, including rollback()
	 * 
	 * @spec 114.10 Resource Processors			
	 */
	public void testResourceProcessor002() {
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
			
			resourceProcessor.setSimulateExceptionAtPrepare(true);
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUpdate = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UPDATE_DP);			
			dpUpdateResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUpdate.getFilename());
			tbc.failException("#",DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the prepare() method",resourceProcessor.exceptionAtPrepareOrdered());
			tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_COMMIT_ERROR, e.getCode());
		} finally {
			resourceProcessor.setSimulateExceptionAtPrepare(false);
			tbc.uninstall(new DeploymentPackage[] { dpUpdateResource,dpInstallResource,dpResourceProcessor });
		}
	}

	/**
	 * Asserts that the action is UPDATE and that DeploymentAdmin calls the 
	 * methods of a resource processor in the specified order
	 * 
	 * @spec 114.10 Resource Processors
	 */
	public void testResourceProcessor003() {
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
	public void testResourceProcessor004() {
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
     * Asserts that no exceptionis thrown when an
     * exception is thrown on dropped() method of the ResourceProcessor. 
     * It also tests if DeploymentAdmin calls the correct order of methods.
     * 
     * @spec 114.10 Resource Processors
     */
	public void testResourceProcessor005() {
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
			
			resourceProcessor.setSimulateExceptionAtDropped(true);
			resourceProcessor.resetCount();
			TestingDeploymentPackage testDPUninstall = tbc.getTestingDeploymentPackage(DeploymentConstants.RP_RESOURCE_UNINSTALL_DP);
			dpUninstallResource = tbc.installDeploymentPackage(tbc.getWebServer() + testDPUninstall.getFilename());
			tbc.assertTrue("Asserts that DeploymentAdmin calls the methods of a resource processor in the specified order when it throws an exception in the dropped() method. It also ensures that no exception is thrown even if the resource does not exist.",resourceProcessor.exceptionAtDroppedOrdered());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			resourceProcessor.setSimulateExceptionAtDropped(false);	
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
	public void testResourceProcessor006() {
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
    public void testResourceProcessor007() {
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
        	e.printStackTrace();
            tbc.assertEquals("DeploymentException.CODE_FOREIGN_CUSTOMIZER correctly thrown", DeploymentException.CODE_FOREIGN_CUSTOMIZER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[]{dp1, dp2});
        }
    }
    
    /**
     * It also tests if DeploymentException.CODE_RESOURCE_SHARING_VIOLATION is thrown if
     * the ResourceProcessor throws ResourceProcessorException.CODE_RESOURCE_SHARING_VIOLATION 
     * 
     * @spec 114.8 Installing a Deployment Package
     */
    public void testResourceProcessor008() {
        tbc.log("#testResourceProcessor008");
        DeploymentPackage dp = null, rp=null;
        TestingSessionResourceProcessor tsrp = null;
        TestingDeploymentPackage testDP = null;
        TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_RP3);
            
        try {
        	rp = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
        	
        	ServiceReference[] sr = tbc.getContext().getServiceReferences(org.osgi.service.deploymentadmin.spi.ResourceProcessor.class.getName(), "(service.pid=" + DeploymentConstants.PID_RESOURCE_PROCESSOR3 + ")");
        	
            tsrp = (TestingSessionResourceProcessor) tbc.getContext().getService(sr[0]);
            
            tsrp.setException(TestingSessionResourceProcessor.PROCESS);

            testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_RP3);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("", DeploymentException.class);
        } catch (DeploymentException e) {
        	tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_RESOURCE_SHARING_VIOLATION, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (tsrp != null)
                tsrp.reset();
            tbc.uninstall(new DeploymentPackage[]{rp, dp});
        }
    }
}

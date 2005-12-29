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
 * 14/04/2005    Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * 26 May, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingGetServiceRegistrationResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingResource;

/**
 * @author Luiz Felipe Guimaraes
 *
 * This Test Class Validates the implementation of <code>getResourceProcessor</code> method of DeploymentPackage, 
 * according to MEG specification.
 */

public class GetResourceProcessor implements TestInterface {
	
	private DeploymentTestControl tbc;
	
	private TestingDeploymentPackage testRP;
	private TestingBundle testRPBundle;
	private TestingResource testRPResource;
	
	private DeploymentPackage dpRP;
	
	public GetResourceProcessor(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			prepare();
			installDeploymentPackage();
			testGetResourceProcessor001();
			testGetResourceProcessor002();
			testGetResourceProcessor003();
			testGetResourceProcessor004();
		} finally {
			uninstallDeploymentPackage();
		}
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }
	/**
	 * Installs a DeploymentPackage to be used by the methods below
	 */
	private void installDeploymentPackage() {
		tbc.log("#Installing resource processor");

		testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP);
		testRPBundle = testRP.getBundles()[0];
		testRPResource = testRP.getResources()[0];
		try {
			dpRP = tbc.installDeploymentPackage(tbc.getWebServer() + testRP.getFilename());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * This test case asserts that a deployment package correctly installs a resource processor bundle.
	 * 
	 * @spec DeploymentPackage.getResourceProcessor(String)
	 */
	private void testGetResourceProcessor001() {
		tbc.log("#testGetResourceProcessor001");
		try {
			ServiceReference rp = dpRP.getResourceProcessor(testRPResource.getName());
			Bundle dpBundle = dpRP.getBundle(testRPBundle.getName());

			tbc.assertEquals(
							"Service reference bundle is the same as the deployment package bundle",
							dpBundle, rp.getBundle());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * This test case asserts that getResourceProcessor returns null for an unexistent resource name.
	 * It also tests if only DeploymentAdminPermission with "metadata" is needed.
	 * 
	 * @spec DeploymentPackage.getResourceProcessor(String)
	 */
	private void testGetResourceProcessor002() {
		tbc.log("#testGetResourceProcessor002");
		try {
			tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(dpRP.getName()), DeploymentAdminPermission.METADATA);
			ServiceReference rp = dpRP.getResourceProcessor("UNEXISTENT_RESOURCE.rp");
			tbc.assertNull("Asserts that it returns null when a resource is not part of the deployment package", rp);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			prepare();
		}
	}
	
	/**
     * Asserts that services can be updated after a deployment package has been deployed. In
     * this event, this call will return a reference to the updated service, not
     * to the instance that was used at deployment time. 
     * 
     * @spec DeploymentPackage.getResourceProcessor(String)
     */
	private void testGetResourceProcessor003() {
		tbc.log("#testGetResourceProcessor003");

		String value = "new value";
		
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentConstants.PID_RESOURCE_PROCESSOR1);
		props.put(DeploymentConstants.RESOURCE_PROCESSOR_PROPERTY_KEY, value);
		ServiceReference rp = null;
		try {
			TestingGetServiceRegistrationResourceProcessor test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
			ServiceRegistration registration = test.getServiceRegistration();
			registration.setProperties(props);
			rp = dpRP.getResourceProcessor(testRPResource.getName());
			
			tbc.assertEquals("The properties of the updated service is the same as the deployment package resource service",
							value, (String) rp.getProperty(DeploymentConstants.RESOURCE_PROCESSOR_PROPERTY_KEY));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action 
	 * 
	 * @spec DeploymentPackage.getResourceProcessor(String)
	 */
	private void testGetResourceProcessor004() {
		tbc.log("#testGetResourceProcessor004");
		try {
			tbc.setMininumPermission();
			dpRP.getResourceProcessor(testRPResource.getName());
			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			prepare();
		}
	}
	/**
	 * Uninstalls the deployment packages previously installed.
	 */
	private void uninstallDeploymentPackage() {
		tbc.uninstall(dpRP);
	}
	
}

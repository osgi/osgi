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

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getResourceProcessor
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getResourceProcessor</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetResourceProcessor {
	private DeploymentTestControl tbc;
	private TestingDeploymentPackage testDP;
	private TestingBundle testBundle;
	private DeploymentPackage dpResourceProcessor;
	
	public GetResourceProcessor(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			installDeploymentPackage();
			testGetResourceProcessor001();
			testGetResourceProcessor002();
			testGetResourceProcessor003();
		} finally {
			uninstallDeploymentPackage();
		}
	}
	
	/**
	 * 
	 */
	private void installDeploymentPackage() {
		tbc.log("#Installing resource processor");
		testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.RESOURCE_PROCESSOR_DP);
		testBundle = testDP.getBundles()[0];
		try {
			dpResourceProcessor = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}		
		
	}

	/**
	 * @testID testGetResourceProcessor001
	 * @testDescription This test case asserts that a deployment package
	 *                  correctly installs a resource processor bundle.
	 */
	private void testGetResourceProcessor001() {
		tbc.log("#testGetResourceProcessor001");
		try {
			ServiceReference resourceProcessor = dpResourceProcessor.getResourceProcessor(testBundle.getFilename());
			// safer if we get the symbolic name from the deployment package bundle than getting from test bundle
			String resourceSymName = dpResourceProcessor.getBundle(testBundle.getName()).getSymbolicName();
			tbc.assertEquals("Service reference bundle is the same as the deployment package bundle", resourceSymName, resourceProcessor.getBundle().getSymbolicName());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetResourceProcessor002
	 * @testDescription This test case asserts that getResourceProcessor returns null for an uniexistent resource name
	 */
	private void testGetResourceProcessor002() {
		tbc.log("#testGetResourceProcessor002");
		try {
			ServiceReference resourceProcessor = dpResourceProcessor.getResourceProcessor("UNEXISTENT RESOURCE");
			tbc.assertNull("Asserts that it returns null when a resource is not part of the deployment package",resourceProcessor);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetResourceProcessor003
	 * @testDescription This test cases updates the resource processor reference
	 *                  in the service register and checks if the deployment
	 *                  package returns the updated instance.
	 */
	private void testGetResourceProcessor003() {
		tbc.log("#testGetResourceProcessor003");
		String key = "property.key";
		String value = "property value";
		try {
			Dictionary props = new Hashtable();
			props.put("service.pid", DeploymentTestControl.PID_RESOURCE_PROCESSOR2);
			props.put(key, value);
			ServiceRegistration sr = tbc.getContext().registerService(ResourceProcessor.class.getName(), this, props);
			
			ServiceReference resourceProcessor = dpResourceProcessor.getResourceProcessor(testBundle.getFilename());
			tbc.assertEquals("The properties of the updated service is the same as the deployment package resource service",
							(String) props.get(key), (String) resourceProcessor.getProperty(key));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	private void uninstallDeploymentPackage() {
		tbc.uninstall(dpResourceProcessor);
	}
}

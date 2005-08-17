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
 * 10/05/2005    Andre Assad
 * 76            Implement Test Cases for Deployment Configuration
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedService;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedServiceFactory;

/**
 * @author Andre Assad
 *
 * This class tests Deployment Configuration
 */

public class Configuration {
	
	private DeploymentTestControl tbc;
	private DeploymentPackage dp;
	
	public Configuration(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			prepare();
			testConfiguration001();
			testConfiguration002();
		} finally {
			unprepare();
		}
	}

	/**
	 * Installs the managed service factory and the auto config deployment package
	 */
	private void prepare() {
		installManagedFactoryBundle();
		installAutoConfigDP();
	}

	/**
	 * Installs the auto config deployment package
	 */
	private void installAutoConfigDP() {
		tbc.log("Installing config deployment package");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.AUTO_CONFIG_DP);
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Deployment Package" }), dp);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		}
	}

	/**
	 * Installs the managed service factory bundle with a fixed location
	 */
	private void installManagedFactoryBundle() {
		tbc.log("#Installing Managed Service Factory Bundle");
		URL url = null;
		InputStream is = null;
		try {
			url = new URL(tbc.getWebServer() + "tb2.jar");
			is = url.openStream();
			tbc.getContext().installBundle(DeploymentConstants.MANAGED_BUNDLE_LOCATION, is);
		} catch (MalformedURLException e) {
			tbc.fail("Failed to open an URL connection with Director");
		} catch (IOException e1) {
			tbc.fail("Failed to open bundle InputStream");
		} catch (BundleException e2) {
			tbc.fail("Failed to Install Managed Service Bundle");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		}
	}

	/**
	 * This test case verifies that the Autoconf Resource Processor has
	 * updated ManagedService properties passing the same properties as
	 * extracted from AUTOCONF.xml
	 * 
	 * @spec 116.2 Configuration Data
	 */
	private void testConfiguration001() {
		tbc.log("#testConfiguration001");
		try {
			TestingManagedService managedService = tbc.getManagedService();
			
			tbc.assertEquals("The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
							tbc.getManagedProps(), managedService.getProperties());
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		}
	}
	
	/**
	 * This test case verifies that the Autoconf Resource Processor has
	 * called the updated method of the ManagedServiceFactory passing the
	 * same properties as extracted from AUTOCONF.xml
	 * 
	 * @spec 116.2 Configuration Data
	 */
	private void testConfiguration002() {
		tbc.log("#testConfiguration002");
		 try {
			TestingManagedServiceFactory managedFactory = tbc.getManagedServiceFactory();
			
			tbc.assertEquals("The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
					DeploymentConstants.PID_MANAGED_SERVICE_FACTORY, managedFactory.getPid());
			
			tbc.assertEquals("The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
							tbc.getManagedFactoryProps(), managedFactory.getProperties());
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		}
	}
	
	/**
	 * Uninstalls the Autoconf Deployment Package used in the tests.
	 */
	private void unprepare() {
		tbc.log("#Uninstalling Deployment Package Configurator");
		tbc.uninstall(dp);
	}		
}

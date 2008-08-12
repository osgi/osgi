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

package org.osgi.test.cases.deploymentadmin.tbc.Configuration;

import java.security.AllPermission;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.TestingManagedService;
import org.osgi.test.cases.deploymentadmin.tbc.TestingManagedServiceFactory;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResourceProcessorConfigurator;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 *
 * @generalDescription This class tests Deployment Configuration (rfc0094)
 */

public class Configuration {
	
	private DeploymentTestControl tbc;
	private DeploymentPackage dp;
	private Bundle tb2;
	
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
	 * 
	 */
	private void unprepare() {
		uninstallResourceProcessorConfigurator();
		try {
			if (tb2 != null)
				tbc.uninstallBundle(tb2);
		} catch (Exception e) {
			tbc.fail("Failed to uninstall the Managed Service Factory");
		}
	}		
	

	/**
	 * 
	 */
	private void prepare() {
		try {
			tbc.log("#Installing Managed Service Factory Bundle");
			tb2 = tbc.installBundle("tb2.jar");
		} catch (Exception e) {
			tbc.fail("Failed to install the Managed Service Factory");
		}
		installResourceProcessorConfigurator();
	}

	/**
	 * 
	 */
	private void installResourceProcessorConfigurator() {
		tbc.log("#Installing Resource Processor Configurator");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.RESOURCE_PROCESSOR_CONFIG_DP);
		try {
			// set Permissions to RP configurator

			tbc.getPermissionAdmin().setPermissions("osgi-dp:"+DeploymentTestControl.PID_RESOURCE_PROCESSOR3,
							new PermissionInfo[] {
									new PermissionInfo(AllPermission.class.getName(), "*", "*"),
/*									// to find the Deployment Admin
									new PermissionInfo(ServicePermission.class.getName(), "*", "*"),
									// to manipulate files
									new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
									// to manipulate bundles
									new PermissionInfo(AdminPermission.class.getName(), "*", "*"),*/ 
									});
			dp = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
		} catch (SecurityException e) {
			tbc.fail("Unexpected SecurityException thrown");
		} catch (DeploymentException e) {
			tbc.fail("Unexpected DeploymentException thrown");
		}
	}

	/**
	 * @testID testConfiguration001
	 * @testDescription This test case verifies that the installed resource
	 *                  processor has called the updated method of the
	 *                  ManagedService passing the same properties as
	 *                  extracted from AUTOCONF.xml
	 */
	private void testConfiguration001() {
		tbc.log("#testConfiguration001");
		try {
			TestingResourceProcessorConfigurator resourceConfigurator = tbc.getResourceProcessorConfigurator();
			TestingManagedService managedService = tbc.getManagedService();

			tbc.assertEquals(
							"The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
							resourceConfigurator.getProperties(),
							managedService.getProperties());
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testConfiguration002
	 * @testDescription This test case verifies that the installed resource
	 *                  processor has called the updated method of the
	 *                  ManagedServiceFactory passing the same properties as
	 *                  extracted from AUTOCONF.xml
	 */
	private void testConfiguration002() {
		tbc.log("#testConfiguration002");
		 try {
		 	TestingResourceProcessorConfigurator resourceConfigurator = tbc.getResourceProcessorConfigurator();
			TestingManagedServiceFactory managedFactory = tbc.getManagedServiceFactory();

			tbc.assertEquals(
							"The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
							DeploymentTestControl.PID_MANAGED_SERVICE_FACTORY,
							managedFactory.getPid());
			tbc.assertEquals(
							"The Resource Processor updated the same Dictionary properties as received by ManagedServiceFactory",
							resourceConfigurator.getProperties(),
							managedFactory.getProperties());
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
		}
	}
	
	private void uninstallResourceProcessorConfigurator() {
		tbc.log("#Uninstalling Resource Processor Configurator");
		tbc.uninstall(dp);
	}
}


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
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK 
 * ===========   ==============================================================
 * Mar 30, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 * Apr 28, 2005  Eduardo Oliveira
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.Configuration.Configuration;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission.DeploymentAdminPermissionConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentCustomizerPermission.DeploymentCustomizerPermissionConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentException.DeploymentExceptionConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentSession.DeploymentSession;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.ResourceProcessor.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedService;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedServiceFactory;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingResource;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class DeploymentTestControl extends DefaultTestBundleControl {

	private DeploymentAdmin deploymentAdmin;
	private PermissionAdmin permissionAdmin;
	
	private BundleListenerImpl bundleListener;
	private boolean transactionalDA;
	private HashMap packages = new HashMap();
	private Dictionary managedProps;
	private Dictionary managedFactoryProps;
	
	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot of time
	 * in debugging, clean up all possible persistent remains before the test is
	 * run. Clean up is better don in the prepare because debugging sessions can
	 * easily cause the unprepare never to be called. </remove>
	 * 
	 * @throws InvalidSyntaxException
	 */
	public void prepare() {
		log("#before each run");
		
		permissionAdmin = (PermissionAdmin) getContext().getService(getContext().getServiceReference(PermissionAdmin.class.getName()));
		ServiceReference daServiveReference = getContext().getServiceReference(DeploymentAdmin.class.getName());
		deploymentAdmin = (DeploymentAdmin) getContext().getService(daServiveReference);
		
		createTestingDeploymentPackages();
		setManagedServiceAndFactoryProperties();
		installListener();
		
		//TODO change after conf file implementation
		setTransactionalDA(false);
	}
	
	private void installListener() {
		try {
			bundleListener = new BundleListenerImpl(this);
			getContext().addBundleListener(bundleListener);
		} catch (Exception e) {
			log("#TestControl: Failed starting a Listener");
		}
	}
	
	private void setManagedServiceAndFactoryProperties() {
		managedFactoryProps = new Hashtable();
		managedFactoryProps.put(TestingManagedServiceFactory.ATTRIBUTE_A, TestingManagedServiceFactory.ATTRIBUTE_A_VALUE);
		managedFactoryProps.put(TestingManagedServiceFactory.ATTRIBUTE_B, TestingManagedServiceFactory.ATTRIBUTE_B_VALUE);
		managedFactoryProps.put(TestingManagedServiceFactory.ATTRIBUTE_C, TestingManagedServiceFactory.ATTRIBUTE_C_VALUE);

		managedProps = new Hashtable();
		managedProps.put(TestingManagedService.ATTRIBUTE_A, TestingManagedService.ATTRIBUTE_A_VALUE);
		managedProps.put(TestingManagedService.ATTRIBUTE_B, TestingManagedService.ATTRIBUTE_B_VALUE);
		managedProps.put(TestingManagedService.ATTRIBUTE_C, TestingManagedService.ATTRIBUTE_C_VALUE);
	}

	/**
	 * 
	 */
	private void createTestingDeploymentPackages() {
		TestingDeploymentPackage dp = null;
		for (int i = 0; i < DeploymentConstants.MAP_CODE_TO_DP.length; i++) {
			switch (i) {
			case DeploymentConstants.SIMPLE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0.0", "simple.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.RP_RESOURCE_INSTALL_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				TestingResource[] resources = {new TestingResource("resource_processor_file.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR2)}; 
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "", "rp_resource_install.dp", bundles,resources);
				packages.put(""+i, dp);
				break;
			}	
			case DeploymentConstants.RP_RESOURCE_UPDATE_DP: {
				TestingResource[] resources = {new TestingResource("resource_processor_file.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR2)}; 
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "", "rp_resource_update.dp", null,resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.RP_RESOURCE_UNINSTALL_DP: {
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "", "rp_resource_uninstall.dp", null);
				packages.put(""+i, dp);
				break;
			}
			
			case DeploymentConstants.AUTO_CONFIG_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.tb4", "1.0", "bundle004.jar")};
				TestingResource[] resources = {new TestingResource("AUTOCONF.xml","")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.AUTO_CONFIG_DP], "1.0", "auto_config.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SESSION_RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {
						new TestingBundle("bundles.tb1", "1.5", "bundle001.jar"),
						new TestingBundle("bundles.tb2", "1.5", "bundle002.jar"),
						new TestingBundle("org.osgi.test.cases.deployment.bundles.rp4","1.0", "rp_bundle4.jar") };
				TestingResource[] resources = { 
						new TestingResource("simple_resource.xml", "org.osgi.test.cases.deployment.bundles.rp4") };
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "session_resource_processor.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SESSION_TEST_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
				TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1"), new TestingResource("conf.txt","org.osgi.test.cases.deployment.bundles.rp2")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SESSION_TEST_DP], "1.0", "session_test.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SESSION_UPDATE_TEST_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
				TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1"), new TestingResource("conf.txt","org.osgi.test.cases.deployment.bundles.rp2")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SESSION_UPDATE_TEST_DP], "1.0", "session_update_test.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.TRANSACTIONAL_SESSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
				TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1"), new TestingResource("simple_resource.xml","br.org.unknown.rp")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.TRANSACTIONAL_SESSION_DP], "1.0", "transactional_session.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.RESOURCE_PROCESSOR_2_DP: {
				TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR2, "1.0", "rp_bundle2.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.RESOURCE_PROCESSOR_2_DP], "1.0.0", "resource_processor2.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public TestingDeploymentPackage getTestingDeploymentPackage(int code) {
		return (TestingDeploymentPackage)packages.get(String.valueOf(code));
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can be
	 * executed independently of each other method. Do not keep state between
	 * methods, if possible. This method can be used to clean up any possible
	 * remaining state. </remove>
	 * 
	 */
	public void setState() {
		log("#before each method");
	}

	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
	}

	/**
	 * @return Returns the factory.
	 */
	public DeploymentAdmin getDeploymentAdmin() {
		if (deploymentAdmin == null)
			fail("DeploymentAdmin factory is null");
		return deploymentAdmin;
	}

	// Configuration
	public void testConfiguration() {
		new Configuration(this).run();
	}

	//DeploymentAdminPermission Test Cases
	public void testDeploymentAdminPermission() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission.DeploymentAdminPermission(this).run();
	}
	
	//Equals
	public void testDeploymentAdminPermissionEquals() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission.Equals(this).run();
	}
	
	//Implies
	public void testDeploymentAdminPermissionImplies() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission.Implies(this).run();
	}
	
	//Constants
	public void testDeploymentAdminPermissionConstants() {
		new DeploymentAdminPermissionConstants(this).run();
	}
	
	//DeploymentCustomizerPermission Test Cases
	public void DeploymentCustomizerPermission() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentCustomizerPermission.DeploymentCustomizerPermission(this).run();
	}
	
	//Equals
	public void testDeploymentCustomizerPermissionEquals() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentCustomizerPermission.Equals(this).run();
	}
	
	//Implies
	public void testDeploymentCustomizerPermissionImplies() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentCustomizerPermission.Implies(this).run();
	}
	
	//Constants
	public void testDeploymentCustomizerPermissionConstants() {
		new DeploymentCustomizerPermissionConstants(this).run();
	}
	
	// DeploymentException Test Cases
	// DeploymentException
	public void testDeploymentException() {
		new org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentException.DeploymentException(this).run();
	}
	
	// DeploymentException Constants
	public void testDeploymentExceptionConstants() {
		new DeploymentExceptionConstants(this).run();
	}
	
	//DeploymentSession Test Cases
	public void testDeploymentSession() {
		new DeploymentSession(this).run();
	}	
	
	// ResourceProcessor Test Cases
	public void testResourceProcessor() {
		new ResourceProcessor(this).run();
	}
	/**
	 * @return Returns the permissionAdmin.
	 */
	public PermissionAdmin getPermissionAdmin() {
		if(permissionAdmin==null)
			throw new NullPointerException("PermissionAdmin service reference is null"); 
		return permissionAdmin;
	}
	
	public DeploymentPackage installDeploymentPackage(String urlStr) throws DeploymentException, SecurityException {
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			in = url.openStream();
			return getDeploymentAdmin().installDeploymentPackage(in);
		} catch (MalformedURLException e) {
			fail("Failed to open the URL");
		} catch (IOException e) {
			fail("Failed to open an InputStream");
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e1) {
				}
		}
		return null;
	}
	
	public void uninstall(DeploymentPackage[] dps) {
		for (int i=0;i<dps.length;i++) {
			uninstall(dps[i]);
		}
	}
	public void uninstall(DeploymentPackage dp) {
		if ((dp != null)&&!dp.isStale()) {
			try {
				dp.uninstall();
			} catch (DeploymentException e) {
				log("#Deployment Package could not be uninstalled. Uninstalling forcefuly...");
				dp.uninstallForced();
			} 
		}
	}


	/**
	 * Set the a PermissionInfo for a bundle location for the caller
	 */
	public void setPermissionInfo(String location, PermissionInfo[] info) {
		getPermissionAdmin().setPermissions(location, info);
	}
	
	/**
	 * @return Returns a TestingManagedServiceFactory
	 * @throws InvalidSyntaxException 
	 */
	public TestingManagedServiceFactory getManagedServiceFactory() throws InvalidSyntaxException {
		return (TestingManagedServiceFactory) getContext().getService(getContext().getServiceReferences(
						ManagedServiceFactory.class.getName(),"(service.pid=" + DeploymentConstants.PID_MANAGED_SERVICE_FACTORY + ")")[0]);
	}

	/**
	 * @return Returns a TestingManagedService
	 * @throws InvalidSyntaxException 
	 */
	public TestingManagedService getManagedService() throws InvalidSyntaxException {
		return (TestingManagedService) getContext().getService(getContext().getServiceReferences(
						ManagedService.class.getName(),"(service.pid=" + DeploymentConstants.PID_MANAGED_SERVICE + ")")[0]);
	}
	/**
	 * @return Returns the transactionalDA.
	 */
	public boolean isTransactionalDA() {
		return transactionalDA;
	}
	/**
	 * @param transactionalDA The transactionalDA to set.
	 */
	public void setTransactionalDA(boolean transactionalDA) {
		this.transactionalDA = transactionalDA;
	}
	/**
	 * @return Returns the managedFactoryProps.
	 */
	public Dictionary getManagedFactoryProps() {
		return managedFactoryProps;
	}
	
	/**
	 * @return Returns the managedProps.
	 */
	public Dictionary getManagedProps() {
		return managedProps;
	}
	
	public Bundle getBundle(String name) {
		Bundle bundle = null;
		Bundle[] bundles = getContext().getBundles();
		String str = "";
		boolean found = false;
		int i = 0;
		while ((bundle==null) && (i < bundles.length)) {
			str = bundles[i].getSymbolicName();
			if ((str != null) && (str.equals(name))) {
				bundle = bundles[i];
			}
			i++;
		}
		return bundle;
	}
	/**
	 * @return Returns the bundleEventHandler.
	 */
	public BundleListenerImpl getBundleListener() {
		if (bundleListener==null)
			throw new NullPointerException("BundleListener implementation instance is null");
		return bundleListener;
	}

}

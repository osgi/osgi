/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
 */

package integrationtests;

import integrationtests.api.ITest;
import integrationtests.managedservicefactory1.CTestFactory;

import java.io.FileInputStream;
import java.security.AllPermission;
import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.bundle.autoconf.Autoconf;
import org.osgi.impl.service.policy.integrationtests.IntegratedTest;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.permissionadmin.PermissionInfo;

public class TestAutoconf extends IntegratedTest implements Test {
	public static final ConditionInfo[] SIGNER_SARAH =  new ConditionInfo[] {new ConditionInfo(BundleSignerCondition.class.getName(),new String[] {"CN=Sarah Bar, OU=Informatical Infrastructure Management, O=ConstructionOy, C=HU; CN=People, OU=Informatical Infrastructure Maintenance, O=ConstructionOy, L=Budapest, C=HU; CN=Root1, OU=FAKEDONTUSE, O=CASoft, L=Budapest, C=HU"})};
	public static final PermissionInfo[] ALL_PERMISSION = new PermissionInfo[] { new PermissionInfo(AllPermission.class.getName(),"*","*")};

	public static final String	ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR	= "file:../../org.osgi.impl.service.deploymentadmin/org.osgi.impl.service.deploymentadmin.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_METATYPE_JAR	= "file:../../org.osgi.impl.service.metatype/org.osgi.impl.service.metatype.jar";
	public static final String  ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR = "file:../../org.osgi.impl.bundle.autoconf/org.osgi.impl.bundle.autoconf.jar";
	public static final String	INTEGRATIONTESTS_MANAGEDSERVICE1_JAR = "file:../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.managedservice1.jar";
	public static final String	INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_JAR = "file:../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.managedservicefactory1.jar";
	public static final String	INTEGRATIONTESTS_DP1_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp1.jar";
	public static final String	INTEGRATIONTESTS_DP1_ROLLBACK_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp1_rollback.jar";
	public static final String	INTEGRATIONTESTS_DP2_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp2.jar";
	public static final String	INTEGRATIONTESTS_DP1_UPGRADE1_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp1_upgrade1.jar";
	public static final String	INTEGRATIONTESTS_DP2_UPGRADE1_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp2_upgrade1.jar";
	public static final String	INTEGRATIONTESTS_DP3_JAR = "../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.dp3.jar";

	public Bundle	deploymentAdminBundle;
	public Bundle	autoconf;
	public Bundle	metatypeBundle;
	public MetaTypeService	metaTypeService;
	public ConfigurationAdmin	configurationAdmin;
	public DeploymentAdmin	deploymentAdmin;
	
	public void startFramework(boolean fresh) throws Exception {
		super.startFramework(fresh);
		if (fresh) {
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		}
		deploymentAdminBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
		metatypeBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
		autoconf = systemBundleContext.installBundle(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		deploymentAdminBundle.start();
		metatypeBundle.start();
		autoconf.start();

		ServiceReference sr = systemBundleContext.getServiceReference(MetaTypeService.class.getName());
		metaTypeService = (MetaTypeService) systemBundleContext.getService(sr);

		sr = systemBundleContext.getServiceReference(ConfigurationAdmin.class.getName());
		configurationAdmin = (ConfigurationAdmin) systemBundleContext.getService(sr);

		sr = systemBundleContext.getServiceReference(DeploymentAdmin.class.getName());
		deploymentAdmin = (DeploymentAdmin) systemBundleContext.getService(sr);

		// component service bundle has some race condition issues
		synchronized(this) { this.wait(100); }
	}

	public void stopFramework() throws Exception {
		deploymentAdminBundle = null;
		autoconf = null;
		metatypeBundle = null;
		metaTypeService = null;
		configurationAdmin = null;
		super.stopFramework();
	}

	public void testRegistered() throws Exception {
		startFramework(true);
		ServiceReference[] sr = systemBundleContext.getServiceReferences(ResourceProcessor.class.getName(),"(service.pid=org.osgi.deployment.rp.autoconf)");
		assertEquals(1,sr.length);
		Object sp = systemBundleContext.getService(sr[0]);
		assertEquals(Autoconf.class.getName(),sp.getClass().getName()); // different classloaders!
	}
	
	// this is not really a test, just to make sure we didn't mess up the package
	public void testManagedService1Works() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);
		Bundle managedService1 = systemBundleContext.installBundle(INTEGRATIONTESTS_MANAGEDSERVICE1_JAR);
		managedService1.start();
		synchronized(this) { this.wait(100); }

		ServiceReference sr = systemBundleContext.getServiceReference(ITest.class.getName());
		ITest iTest = (ITest) systemBundleContext.getService(sr);

		Configuration conf = configurationAdmin.getConfiguration("integrationtests.managedservice1.pid");
		conf.setBundleLocation(INTEGRATIONTESTS_MANAGEDSERVICE1_JAR);
		Hashtable props = new Hashtable();
		props.put("increment",new Integer(2));
		conf.update(props);
		
		synchronized(this) { this.wait(100); }

		int i = iTest.succ(3);
		assertEquals(5,i);
	}

	// this is not really a test, just to make sure we didn't mess up the package
	// this is the test of the test, so I don't have to debug in vain
	public void testManagedServiceFactory1Works() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);
		Bundle managedService1 = systemBundleContext.installBundle(INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_JAR);
		managedService1.start();

		ServiceReference[] sr = systemBundleContext.getServiceReferences(ITest.class.getName(),"(increment=5)");
		assertNull(sr);

		Configuration fc5 = configurationAdmin.createFactoryConfiguration(CTestFactory.INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_PID,INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_JAR);
		Dictionary props = new Hashtable();
		props.put("increment",new Integer(5));
		fc5.update(props);

		synchronized(this) { wait(100); }

		sr = systemBundleContext.getServiceReferences(ITest.class.getName(),"(increment=5)");
		assertEquals(1,sr.length);
		ITest i5 = (ITest) systemBundleContext.getService(sr[0]);
		
		int result = i5.succ(4);
		assertEquals(9,result);
		
		Configuration fc3 = configurationAdmin.createFactoryConfiguration(CTestFactory.INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_PID,INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_JAR);
		props = new Hashtable();
		props.put("increment",new Integer(3));
		fc3.update(props);
		props = fc3.getProperties();
		System.out.println("fc3.properties = "+props);

		synchronized(this) { wait(100); }
		
		sr = systemBundleContext.getServiceReferences(ITest.class.getName(),"(increment=3)");
		assertEquals(1,sr.length);
		ITest i3 = (ITest) systemBundleContext.getService(sr[0]);
		
		result = i3.succ(5);
		assertEquals(8,result);
		
		fc5.delete();
		synchronized(this) { wait(100); }
		sr = systemBundleContext.getServiceReferences(ITest.class.getName(),"(increment=5)");
		assertNull(sr);
		
	}
		
	public void testDeployManagedService1() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);
		deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP1_JAR));

		// the deploymentpackage configures "increment" to 3
		Configuration conf = configurationAdmin.getConfiguration("integrationtests.managedservice1.pid");
		Object inc = conf.getProperties().get("increment");
		assertEquals(new Integer(3),inc);
		
		ServiceReference sr = systemBundleContext.getServiceReference(ITest.class.getName());
		ITest iTest = (ITest) systemBundleContext.getService(sr);
		int i = iTest.succ(7);
		assertEquals(10,i);
		
		DeploymentPackage dp = deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP1_UPGRADE1_JAR));
		sr = systemBundleContext.getServiceReference(ITest.class.getName());
		iTest = (ITest) systemBundleContext.getService(sr);
		i = iTest.succ(7);
		assertEquals(11,i);
		
		dp.uninstall();
		sr = systemBundleContext.getServiceReference(ITest.class.getName());
		assertNull(sr);

		// check if the configuration is deleted, too
		conf = configurationAdmin.getConfiguration("integrationtests.managedservice1.pid");
		assertNull(conf.getProperties());
	}

	/**
	 * The deployment package contains a good designate, and then one without
	 * an OCD. Since this second one is not defined, and not marked optional, the 
	 * resource processor should fail, and then roll back the changes.
	 */
	public void testDeployManagedService1Rollback() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);
		
		try {
			deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP1_ROLLBACK_JAR));
			fail();
		} catch (DeploymentException e) {
		}

		Configuration conf = configurationAdmin.getConfiguration("integrationtests.managedservice1.pid");
		Dictionary props = conf.getProperties();
		assertNull(props);
	}

	public void testDeployManagedServiceFactory1() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);
		deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP2_JAR));
		synchronized(this) { wait(100); }

		// the deploymentpackage creates two services, one with increment of 3, the other with 5
		// we look up the configuration with increment 3, and store the generated pid, so
		// that we can later test whether the update happens in-place
		Configuration[] configurations = configurationAdmin.listConfigurations("(&(service.factoryPid=integrationtests.managedservicefactory1.pid)(increment=3))");
		assertEquals(1,configurations.length);
		String pid = configurations[0].getPid();

		// the deploymentpackage has two services, one with increment 3, the other with increment 5
		ServiceReference[] srs = systemBundleContext.getServiceReferences(ITest.class.getName(),null);
		assertEquals(2,srs.length);
		srs = systemBundleContext.getServiceReferences(ITest.class.getName(),"(increment=3)");
		assertEquals(1,srs.length);
		ITest iTest = (ITest) systemBundleContext.getService(srs[0]);
		int i = iTest.succ(7);
		assertEquals(10,i);

		// This changes the 3 to 8, and removes the increment=5.
		DeploymentPackage dp = deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP2_UPGRADE1_JAR));
		synchronized(this) { wait(100); }
		srs = systemBundleContext.getServiceReferences(ITest.class.getName(),null);
		assertEquals(1,srs.length);
		iTest = (ITest) systemBundleContext.getService(srs[0]);
		i = iTest.succ(5);
		assertEquals(13,i);
		
		// uninstall and see what happens
		dp.uninstall();
		srs = systemBundleContext.getServiceReferences(ITest.class.getName(),null);
		assertNull(srs);
		Configuration configuration = configurationAdmin.getConfiguration(pid);
		assertNull(configuration.getProperties());
	}

	public void testConfigureManagedServiceFactoryOutsidePackage() throws Exception {
		// factory configurations can work with bundles outside the package
		startFramework(true);
		
		conditionalPermissionAdmin.addConditionalPermissionInfo(SIGNER_SARAH,ALL_PERMISSION);

		Bundle factory = systemBundleContext.installBundle(INTEGRATIONTESTS_MANAGEDSERVICEFACTORY1_JAR);
		factory.start();
		
		DeploymentPackage dp = deploymentAdmin.installDeploymentPackage(new FileInputStream(INTEGRATIONTESTS_DP3_JAR));
		synchronized(this) { wait(100); }

		// the deploymentpackage has one service with increment of 3
		ServiceReference[] srs = systemBundleContext.getServiceReferences(ITest.class.getName(),null);
		assertEquals(1,srs.length);
		ITest iTest = (ITest) systemBundleContext.getService(srs[0]);
		int i = iTest.succ(7);
		assertEquals(10,i);

		// remove and see if they are removed, too
		dp.uninstall();
		synchronized(this) { wait(100); }
		srs = systemBundleContext.getServiceReferences(ITest.class.getName(),null);
		assertNull(srs);
		
	}
}

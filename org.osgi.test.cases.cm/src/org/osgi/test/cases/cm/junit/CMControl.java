/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.cm.junit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.osgi.test.cases.cm.shared.Util.singletonDictionary;
import static org.osgi.test.support.compatibility.DefaultTestBundleControl.assertException;
import static org.osgi.test.support.compatibility.DefaultTestBundleControl.failException;
import static org.osgi.test.support.compatibility.DefaultTestBundleControl.pass;
import static org.osgi.test.support.compatibility.DefaultTestBundleControl.trace;
import static org.osgi.test.support.sleep.Sleep.sleep;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Capability;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPermission;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.cm.ReadOnlyConfigurationException;
import org.osgi.service.cm.SynchronousConfigurationListener;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.assertj.dictionary.DictionaryAssert;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.SynchronizerImpl;
import org.osgi.test.cases.cm.shared.ModifyPid;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectBundleInstaller;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.bitmaps.BundleEventType;
import org.osgi.test.common.install.BundleInstaller;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.context.InstalledBundleExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import junit.framework.AssertionFailedError;

/**
 * @author Ikuo YAMASAKI, NTT Corporation, added many tests.
 * @author Carsten Ziegeler, Adobe, added ConfigAdmin 1.5/1.6 tests
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class CMControl {
	@InjectService(cardinality = 1, timeout = 500)
	ServiceAware<ConfigurationAdmin>				cmA;
	@InjectService(cardinality = 0)
	ServiceAware<PermissionAdmin>					permAdminA;
	private long					SIGNAL_WAITING_TIME;
	private List<PermissionInfo>					list;
	private boolean permissionFlag;
	private Bundle setAllPermissionBundle;

	@InjectBundleContext BundleContext bc;

	@InjectBundleInstaller BundleInstaller bInstaller;
	private Set<String>								existingConfigs;
	private String									name;

	private static final String SP = ServicePermission.class.getName();
	private static final String PP = PackagePermission.class.getName();
	private static final String AP = AdminPermission.class.getName();
	private static final String CP = ConfigurationPermission.class.getName();
	private static final Dictionary<String,Object>	propsForSync1;
	static {
		propsForSync1 = new Hashtable<String,Object>();
		propsForSync1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync1");
	}
	private static final Dictionary<String,Object> propsForSync2;
	static {
		propsForSync2 = new Hashtable<String,Object>();
		propsForSync2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync2");
	}

	private static final Dictionary<String,Object> propsForSync1_1;
	static {
		propsForSync1_1 = new Hashtable<String,Object>();
		propsForSync1_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync1-1");
	}

	private static final Dictionary<String,Object> propsForSync1_2;
	static {
		propsForSync1_2 = new Hashtable<String,Object>();
		propsForSync1_2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync1-2");
	}

	private static final Dictionary<String,Object> propsForSync2_1;
	static {
		propsForSync2_1 = new Hashtable<String,Object>();
		propsForSync2_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync2-1");
	}
	private static final Dictionary<String,Object> propsForSync3_1;
	static {
		propsForSync3_1 = new Hashtable<String,Object>();
		propsForSync3_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync3-1");
	}
	private static final Dictionary<String,Object> propsForSync3_2;
	static {
		propsForSync3_2 = new Hashtable<String,Object>();
		propsForSync3_2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync3-2");
	}
	private static final Dictionary<String,Object> propsForSync4_1;
	static {
		propsForSync4_1 = new Hashtable<String,Object>();
		propsForSync4_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync4-1");
	}
	private static final Dictionary<String,Object> propsForSync4_2;
	static {
		propsForSync4_2 = new Hashtable<String,Object>();
		propsForSync4_2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync4-2");
	}
	private static final Dictionary<String,Object> propsForSyncF1_1;
	static {
		propsForSyncF1_1 = new Hashtable<String,Object>();
		propsForSyncF1_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncF1-1");
	}

	private static final Dictionary<String,Object> propsForSyncF1_2;
	static {
		propsForSyncF1_2 = new Hashtable<String,Object>();
		propsForSyncF1_2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncF1-2");
	}
	private static final Dictionary<String,Object> propsForSyncF2_1;
	static {
		propsForSyncF2_1 = new Hashtable<String,Object>();
		propsForSyncF2_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncF2-1");
	}
	private static final Dictionary<String,Object> propsForSyncF3_1;
	static {
		propsForSyncF3_1 = new Hashtable<String,Object>();
		propsForSyncF3_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncF3-1");
	}
	private static final Dictionary<String,Object> propsForSyncF3_2;
	static {
		propsForSyncF3_2 = new Hashtable<String,Object>();
		propsForSyncF3_2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncF3-2");
	}

	private static final Dictionary<String,Object> propsForSyncT5_1;
	static {
		propsForSyncT5_1 = new Hashtable<String,Object>();
		propsForSyncT5_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncT5-1");
	}

	private static final Dictionary<String,Object> propsForSyncT6_1;
	static {
		propsForSyncT6_1 = new Hashtable<String,Object>();
		propsForSyncT6_1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"syncT6-1");
	}

	private static final String neverlandLocation = "http://neverneverland/";

	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception {
		name = testInfo.getTestMethod()
				.map(Method::getName)
				.orElse("AutoName_" + System.currentTimeMillis());
		SIGNAL_WAITING_TIME = getLongProperty(
				"org.osgi.test.cases.cm.signal_waiting_time", 4000);

		Bundle cmBundle = cmA.getServiceReference().getBundle();
		if (cmBundle.getState() != Bundle.ACTIVE) {
			startCmBundle(cmBundle);
		}
		// printoutBundleList();
		// populate the created configurations so that
		// listConfigurations can return these configurations
		list = new ArrayList<>(5);

		System.out.println("DEBUG on CI ALL BUNDLES IN BC");
		for (Bundle b : bc.getBundles()) {
			System.out.println(b.getSymbolicName() + " " + b.getVersion() + " "
					+ b.getLocation() + " "
					+ BundleEventType.toString(b.getState()));
		}
		if (System.getSecurityManager() != null) {

			if (setAllPermissionBundle == null) {
				setAllPermissionBundle = bInstaller
					.installBundle("setallpermission.jar", false);
			}

		} else {
			permissionFlag = true;
		}

		// existing configurations
		Configuration[] configs = cmA.waitForService(500l)
				.listConfigurations(null);
		existingConfigs = new HashSet<>();
        if (configs != null) {
            for (int i = 0; i < configs.length; i++) {
                Configuration config = configs[i];
                // log("setUp() -- Register pre-existing config " + config.getPid());
                existingConfigs.add(config.getPid());
            }
        }
	}

	@AfterEach
	void tearDown() throws Exception {
		resetPermissions();
		if (this.setAllPermissionBundle != null) {
			this.setAllPermissionBundle.uninstall();
			this.setAllPermissionBundle = null;
		}

		list = null;

		System.out.println("tearDown()");
		// this.printoutBundleList();
	}

	private void resetPermissions() throws BundleException {
		if (permAdminA.getService() == null)
			return;
		try {
			if (this.setAllPermissionBundle == null) {
				this.setAllPermissionBundle = bInstaller.installBundle(
						"setallpermission.jar", false);
			}
			this.setAllPermissionBundle.start();
			this.setAllPermissionBundle.stop();
		} catch (BundleException e) {
			Exception ise = new IllegalStateException(
					"fail to install or start setallpermission bundle.");
			ise.initCause(e);
			throw e;
		}
		this.printoutPermissions();
	}

	private void printoutPermissions() {
		if (permAdminA.getService() == null)
			return;
		String[] locations = this.permAdminA.getService().getLocations();
		if (locations != null)
			for (int i = 0; i < locations.length; i++) {
				System.out.println("locations[" + i + "]=" + locations[i]);
				PermissionInfo[] pInfos = this.permAdminA.getService()
						.getPermissions(locations[i]);
				for (int j = 0; j < pInfos.length; j++) {
					System.out.println("\t" + pInfos[j]);
				}
			}
		PermissionInfo[] pInfos = this.permAdminA.getService()
				.getDefaultPermissions();
		if (pInfos == null)
			System.out.println("default permission=null");
		else {
			System.out.println("default permission=");
			for (int j = 0; j < pInfos.length; j++) {
				System.out.println("\t" + pInfos[j]);
			}
		}

	}

	private void setBundlePermission(Bundle b, List<PermissionInfo> list) {
		if (permAdminA.getService() == null)
			return;
		PermissionInfo[] pis = new PermissionInfo[list.size()];
		pis = list.toArray(pis);
		permAdminA.getService().setPermissions(b.getLocation(), pis);
		this.printoutPermissions();
	}

	private List<PermissionInfo> getBundlePermission(Bundle b) {
		if (permAdminA.getService() == null)
			return null;
		PermissionInfo[] pis = permAdminA.getService()
				.getPermissions(b.getLocation());
        return Arrays.asList(pis);
    }

	private void add(List<PermissionInfo> permissionsInfos, String clazz,
			String name,
			String actions) {
		permissionsInfos.add(new PermissionInfo(clazz, name, actions));
	}

	/** *** Test methods **** */

	/**
	 * Test that the methods throws IllegalStateException when operating on a
	 * deleted Configuration
	 *
	 * @spec Configuration.delete()
	 * @spec Configuration.getBundleLocation()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.setBundleLocation(String)
	 */

	@Test
	public void testDeletedConfiguration() throws Exception {
		String pid = Util.createPid();
		Configuration conf = null;
		/* Get a brand new configuration and delete it. */
		conf = cmA.waitForService(500l).getConfiguration(pid);
		conf.delete();
		/*
		 * A list of all methodcalls that should be made to the deleted
		 * Configuration object
		 */
		MethodCall[] methods = {
				new MethodCall(Configuration.class, "delete"),
				new MethodCall(Configuration.class, "getBundleLocation"),
				new MethodCall(Configuration.class, "getFactoryPid"),
				new MethodCall(Configuration.class, "getPid"),
				new MethodCall(Configuration.class, "getProperties"),
				new MethodCall(Configuration.class, "getChangeCount"),
				new MethodCall(Configuration.class, "setBundleLocation",
						String.class, "somelocation"),
				new MethodCall(Configuration.class, "update"),
				new MethodCall(Configuration.class, "update", Dictionary.class,
						new Hashtable<>())
		};
		/* Make all the methodcalls in the list */
		for (int i = 0; i < methods.length; i++) {
			try {
				/* Call the method on the Configuration object */
				methods[i].invoke(conf);
				/*
				 * In this case it should always throw an IllegalStateException
				 * so if we end up here, somethings wrong
				 */
				failException(methods[i].getName(), IllegalStateException.class);
			} catch (AssertionFailedError e) {
				throw e;
			} catch (Throwable e) {
				/* Check that we got the correct exception */
				assertException(methods[i].getName(),
						IllegalStateException.class, e);
			}
		}
	}




	/**
	 * TODO comments
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec ConfigurationAdmin.getConfiguration(String,String)
	 * @spec Configuration.getBundleLocation()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.setBundleLocation(String)
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetConfiguration() throws Exception {
		this.setInappropriatePermission();

		String pid = Util.createPid();
		String thisLocation = getLocation();
		Configuration conf = null;
		/* Get a brand new configuration */
		conf = cmA.waitForService(500l).getConfiguration(pid);

		checkConfiguration(conf, "A new Configuration object", pid,
				thisLocation());

		/* Get location of the configuration */
		/* must fail because of inappropriate Permission. */
		String message = "try to get location without appropriate ConfigurationPermission.";
		try {
            conf.getBundleLocation();
            /*
             * A SecurityException should have been thrown if security is
             * enabled
             */
            if (System.getSecurityManager() != null) failException(message, SecurityException.class);
        } catch (AssertionFailedError e) {
            throw e;
        } catch (Throwable e) {
            /* Check that we got the correct exception */
            assertException(message, SecurityException.class, e);
            /*
             * A SecurityException should not have been thrown if security is
             * not enabled
             */
            if (System.getSecurityManager() == null) fail("Security is not enabled", e);
		}

		/* Get the configuration again (should be exactly the same) */
		conf = cmA.waitForService(500l).getConfiguration(pid);
		checkConfiguration(conf, "The same Configuration object", pid,
				thisLocation());

		/*
		 * Change the location of the bundle and then get the Configuration
		 * again. The location should not have been touched.
		 */
		/* must fail because of inappropriate Permission. */
		message = "try to set location without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(neverlandLocation);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message,
						SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message,
					SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		this.setAppropriatePermission();
		conf.setBundleLocation(neverlandLocation);

		conf = cmA.waitForService(500l).getConfiguration(pid);
		assertEquals("Location Neverland", neverlandLocation,
				this.getBundleLocationForCompare(conf));

		this.setInappropriatePermission();
		/* must fail because of inappropriate Permission. */
		message = "try to get configuration whose location is different from the caller bundle without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l).getConfiguration(pid);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		/* Clean up */
		conf.delete();
	}

	/**
	 * TODO comments
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String,String)
	 * @spec Configuration.getBundleLocation()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.getProperties()
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetConfigurationWithLocation() throws Exception {
		final String pid1 = Util.createPid("1");
		final String pid2 = Util.createPid("2");
		final String pid3 = Util.createPid("3");
		// final String thisLocation = getLocation();
		Configuration conf = null;

		this.printoutPermissions();
		this.setInappropriatePermission();

		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration.
		 */
		String message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			fail("Throwable must not be thrown for Configuration#getBundleLocation()",
					e);
		}

		/*
		 * Without appropriate ConfigurationPermission, Get an existing
		 * configuration with thisLocation, but specify the location (which
		 * should then be ignored).
		 */
		// TODO Change the explanation
		message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, neverlandLocation);
			// checkConfiguration(conf, "The same Configuration object",
			// pid1,thisLocation());
			fail("SecurityException must be thrown because cm must check the set location.");
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// fail("Throwable must not be thrown because the configuring bundle has implicit CP for thisLocation",e);
		}

		conf.delete();

		this.setInappropriatePermission();
		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration with a specified location
		 */
		message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l)
					.getConfiguration(pid2, neverlandLocation);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		this.setAppropriatePermission();

		/* Get a brand new configuration with a specified location */
		conf = cmA.waitForService(500l)
				.getConfiguration(pid2, neverlandLocation);
		checkConfiguration(conf, "A new Configuration object", pid2,
				neverlandLocation);
		conf.delete();

		this.setInappropriatePermission();
		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration with no location
		 */
		message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l).getConfiguration(pid3, null);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		this.setAppropriatePermission();
		/* Get a brand new configuration with no location */
		conf = cmA.waitForService(500l).getConfiguration(pid3, null);
		checkConfiguration(conf, "A new Configuration object", pid3, null);
		conf.delete();

	}



	/**
	 * TODO comments
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String,String)
	 * @spec Configuration.getBundleLocation()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.getProperties()
	 *
	 * @throws Exception
	 */
	@Test
	public void testConfigurationWithNullLocation() throws Exception {
		final String bundlePid = Util.createPid("bundle1Pid");
		final String thisLocation = getLocation();
		Configuration conf = null;

		this.setInappropriatePermission();
		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration with no location
		 */
		String message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cmA.waitForService(500l).getConfiguration(bundlePid, null);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		this.setAppropriatePermission();
		/* Get a brand new configuration with no location */
		conf = cmA.waitForService(500l).getConfiguration(bundlePid, null);
		checkConfiguration(conf, "A new Configuration object", bundlePid, null);

		Dictionary<String,Object> props = new Hashtable<>();
		props.put("StringKey", getName());
		conf.update(props);

		/*
		 * Get existing Configuration with neverland location, which should be
		 * ignored
		 */
		Configuration conf3 = cmA.waitForService(500l)
				.getConfiguration(bundlePid, neverlandLocation);
		assertEquals("Pid", bundlePid, conf3.getPid());
		assertNull("FactoryPid", conf3.getFactoryPid());
		assertNull("Location", this.getBundleLocationForCompare(conf3));
		assertEquals("The same Confiuration props", getName(), conf3
				.getProperties().get("StringKey"));

		System.out.println("###########################"
				+ conf3.getBundleLocation());
		// System.out.println("###########################"+cmA.waitForService(500l).getConfiguration(bundlePid).getBundleLocation());

		this.setInappropriatePermission();

		/* Get location of the configuration with null location */
		/* must fail because of inappropriate Permission. */
		message = "try to get location without appropriate ConfigurationPermission.";
		try {
			conf.getBundleLocation();
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}

		message = "try to get configuration with null location without appropriate ConfigurationPermission.";
		/*
		 * try { Configuration conf4
		 * =cmA.waitForService(500l).getConfiguration(bundlePid); A
		 * SecurityException should have been thrown if security is enabled if
		 * (System.getSecurityManager() != null) failException(message,
		 * SecurityException.class); } catch (AssertionFailedError e) { throw e;
		 * } catch (Throwable e) { Check that we got the correct exception
		 * assertException(message, SecurityException.class, e); A
		 * SecurityException should not have been thrown if security is not
		 * enabled if (System.getSecurityManager() == null)
		 * fail("Security is not enabled", e); }
		 */
		Configuration conf4 = cmA.waitForService(500l)
				.getConfiguration(bundlePid);
		/* location MUST be changed to the callers bundle's location. */
		assertEquals("Location", thisLocation(),
				this.getBundleLocationForCompare(conf4));

		/* In order to get Location, appropriate permission is required. */
		this.setAppropriatePermission();
		cmA.waitForService(500l)
				.getConfiguration(bundlePid)
				.setBundleLocation(null);
		Configuration conf5 = cmA.waitForService(500l)
				.getConfiguration(bundlePid);

		/* location MUST be changed to the callers bundle's location. */
		assertEquals("Location", thisLocation(),
				this.getBundleLocationForCompare(conf5));

		// this.setAppropriatePermission();
		// conf3 =cmA.waitForService(500l).getConfiguration(pid3);
		assertEquals("Location", thisLocation(),
				this.getBundleLocationForCompare(conf3));

		this.setInappropriatePermission();
		/* Set location of the configuration to null location */
		/* must fail because of inappropriate Permission. */
		message = "try to set location to null without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(null);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}
		this.setAppropriatePermission();
		conf.setBundleLocation(null);

		this.setInappropriatePermission();
		/* Set location of the configuration with null location to others */
		/* must fail because of inappropriate Permission. */
		message = "try to set location to null without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(thisLocation());
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}
		try {
			conf.setBundleLocation(neverlandLocation);
			/*
			 * A SecurityException should have been thrown if security is
			 * enabled
			 */
			if (System.getSecurityManager() != null)
				failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
			/*
			 * A SecurityException should not have been thrown if security is
			 * not enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		}
		conf.delete();
	}



	/**
	 * Test the change counter.
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testChangeCount() throws Exception {
		trace("Testing change count...");
        // create config with pid
		trace("Create test configuration");
        final String pid = "test_ca_counter_" + ConfigurationListenerImpl.LISTENER_PID_SUFFIX;
		final Configuration config = this.cmA.waitForService(500l)
				.getConfiguration(pid);
        final long startCount = config.getChangeCount();

        // register sync and async listener for change updates
		trace("Create and register configuration listeners");
		// list to check whether the sync listener is called
		final List<ConfigurationEvent> events = new ArrayList<>();
		final SynchronousConfigurationListener scl = new SynchronousConfigurationListener() {

			// the sync listener is called during the update method and the
			// change count should of course already be updated.
			public void configurationEvent(final ConfigurationEvent event) {
		        if (event.getPid() != null
				    && event.getPid().endsWith(ConfigurationListenerImpl.LISTENER_PID_SUFFIX)
				    && event.getFactoryPid() == null) {

					assertEquals("Config event pid match", pid, event.getPid());
					assertEquals("Config event type match",
							ConfigurationEvent.CM_UPDATED, event.getType());
			        assertTrue("Expect second change count to be higher than " + startCount + " : " + config.getChangeCount(),
			        		config.getChangeCount() > startCount);
			        events.add(event);
				}
			}
		};
		ConfigurationListenerImpl cl = null;
		final SynchronizerImpl synchronizer = new SynchronizerImpl();
		bc.registerService(SynchronousConfigurationListener.class.getName(),
				scl, null);
		try {
			cl = createConfigurationListener(synchronizer);
	        // update config with properties
			config.update(singletonDictionary("x", "x"));
	        assertTrue("Sync listener not called.", events.size() == 1);

	        assertTrue("Expect second change count to be higher than " + startCount + " : " + config.getChangeCount(),
	        		config.getChangeCount() > startCount);

			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			trace("Checking configuration event");
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());

			// reget configuration and check count
			trace("Checking regetting configuration");
			final Configuration configNew = this.cmA.waitForService(500l)
					.getConfiguration(pid);
			assertEquals("Configuration change count shouldn't have changed", config.getChangeCount(), configNew.getChangeCount());
	        assertTrue("Expect second change count to be higher than " + startCount + " : " + configNew.getChangeCount(),
	        		configNew.getChangeCount() > startCount);

			trace("Testing change count...finished");
		} finally {
	        // clean up

	        config.delete();
		}
    }

	/**
	 * Test the change counter for factory configuration
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testChangeCountFactory() throws Exception {
		trace("Testing change count factory...");
        // create config with pid
		trace("Create test configuration");
        final String factoryPid = "test_ca_counter_factory_" + ConfigurationListenerImpl.LISTENER_PID_SUFFIX;
		final Configuration config = this.cmA.waitForService(500l)
				.createFactoryConfiguration(factoryPid);
        final String pid = config.getPid();
        final long startCount = config.getChangeCount();

        // register sync and async listener for change updates
		trace("Create and register configuration listeners");
		// list to check whether the sync listener is called
		final List<ConfigurationEvent> events = new ArrayList<>();
		final SynchronousConfigurationListener scl = new SynchronousConfigurationListener() {

			// the sync listener is called during the update method and the
			// change count should of course already be updated.
			public void configurationEvent(final ConfigurationEvent event) {
		        if (event.getPid() != null
				    && event.getFactoryPid().endsWith(ConfigurationListenerImpl.LISTENER_PID_SUFFIX)) {

					assertEquals("Config event factory pid match", factoryPid, event.getFactoryPid());
					assertEquals("Config event pid match", pid, event.getPid());
					assertEquals("Config event type match",
							ConfigurationEvent.CM_UPDATED, event.getType());
			        assertTrue("Expect second change count to be higher than " + startCount + " : " + config.getChangeCount(),
			        		config.getChangeCount() > startCount);
			        events.add(event);
				}
			}
		};
		ConfigurationListenerImpl cl = null;
		final SynchronizerImpl synchronizer = new SynchronizerImpl();
		bc.registerService(SynchronousConfigurationListener.class.getName(),
				scl, null);
		try {
			cl = createConfigurationListener(synchronizer);
	        // update config with properties
			config.update(singletonDictionary("x", "x"));
	        assertTrue("Sync listener not called.", events.size() == 1);

	        assertTrue("Expect second change count to be higher than " + startCount + " : " + config.getChangeCount(),
	        		config.getChangeCount() > startCount);

			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			trace("Checking configuration event");
			assertEquals("Config event factory pid match", factoryPid, cl.getFactoryPid());
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());

			// reget configuration and check count
			trace("Checking regetting configuration");
			final Configuration[] cfs = cmA.waitForService(500l)
					.listConfigurations(
							"(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
                    + factoryPid + ")" );
			final Configuration configNew = cfs[0];
			assertEquals("Configuration change count shouldn't have changed", config.getChangeCount(), configNew.getChangeCount());
	        assertTrue("Expect second change count to be higher than " + startCount + " : " + configNew.getChangeCount(),
	        		configNew.getChangeCount() > startCount);

			trace("Testing change count...finished");
		} finally {
	        // clean up

	        config.delete();
		}
    }

    /**
	 * Test sync listener.
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testSyncListener() throws Exception {
		trace("Testing sync listener...");

		trace("Create and register sync configuration listeners");
		// List of events
		final List<ConfigurationEvent> events = new ArrayList<>();
		// Thread check
		final Thread callerThread = Thread.currentThread();

		final SynchronousConfigurationListener scl = new SynchronousConfigurationListener() {

			// the sync listener is called during the update method and the
			// change count should of course already be updated.
			public void configurationEvent(final ConfigurationEvent event) {
		        if (event.getPid() != null
				    && event.getPid().endsWith(ConfigurationListenerImpl.LISTENER_PID_SUFFIX)
				    && event.getFactoryPid() == null) {
		        	// check thread
		        	if ( Thread.currentThread() != callerThread ) {
		        		fail("Method is not called in sync.");
		        	}
		        	events.add(event);

				}
			}
		};
		bc.registerService(SynchronousConfigurationListener.class.getName(),
				scl, null);
		Configuration config = null;
		try {
	        // create config with pid
			trace("Create test configuration");
	        final String pid = "test_sync_config_" + ConfigurationListenerImpl.LISTENER_PID_SUFFIX;
			config = this.cmA.waitForService(500l).getConfiguration(pid);
			config.update(singletonDictionary("y", "y"));
	        assertEquals("No event received: " + events, 1, events.size());

	        ConfigurationEvent event = events.get(0);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event type match",
						ConfigurationEvent.CM_UPDATED, event.getType());

			// update config
			config.update(singletonDictionary("x", "x"));
	        assertEquals("No event received", 2, events.size());
	        event = events.get(1);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, event.getType());

			// update location
			config.setBundleLocation("location");
	        assertEquals("No event received", 3, events.size());
	        event = events.get(2);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_LOCATION_CHANGED, event.getType());

	        // delete config
	        config.delete();
	        config = null;
	        assertEquals("No event received", 4, events.size());
	        event = events.get(3);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, event.getType());

		} finally {
			if ( config != null ) {
				config.delete();
			}
		}

		trace("Testing sync listener...finished");
    }

    /**
	 * Test sync listener for factory config.
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testSyncListenerFactory() throws Exception {
		trace("Testing sync listener...");

		trace("Create and register sync configuration listeners");
		// List of events
		final List<ConfigurationEvent> events = new ArrayList<>();
		// Thread check
		final Thread callerThread = Thread.currentThread();

		final SynchronousConfigurationListener scl = new SynchronousConfigurationListener() {

			// the sync listener is called during the update method and the
			// change count should of course already be updated.
			public void configurationEvent(final ConfigurationEvent event) {
		        if (event.getPid() != null
				    && event.getFactoryPid().endsWith(ConfigurationListenerImpl.LISTENER_PID_SUFFIX)) {
		        	// check thread
		        	if ( Thread.currentThread() != callerThread ) {
		        		fail("Method is not called in sync.");
		        	}
		        	events.add(event);

				}
			}
		};
		bc.registerService(SynchronousConfigurationListener.class.getName(),
				scl, null);
		Configuration config = null;
		try {
	        // create config with pid
			trace("Create test configuration");
	        final String factoryPid = "test_sync_config_factory_" + ConfigurationListenerImpl.LISTENER_PID_SUFFIX;
			config = this.cmA.waitForService(500l)
					.createFactoryConfiguration(factoryPid);
	        final String pid = config.getPid();
			config.update(singletonDictionary("y", "y"));
	        assertEquals("No event received: " + events, 1, events.size());

	        ConfigurationEvent event = events.get(0);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event factory pid match", factoryPid, event.getFactoryPid());
			assertEquals("Config event type match",
						ConfigurationEvent.CM_UPDATED, event.getType());

			// update config
			config.update(singletonDictionary("x", "x"));
	        assertEquals("No event received", 2, events.size());
	        event = events.get(1);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event factory pid match", factoryPid, event.getFactoryPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, event.getType());

			// update location
			config.setBundleLocation("location");
	        assertEquals("No event received", 3, events.size());
	        event = events.get(2);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event factory pid match", factoryPid, event.getFactoryPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_LOCATION_CHANGED, event.getType());

	        // delete config
	        config.delete();
	        config = null;
	        assertEquals("No event received", 4, events.size());
	        event = events.get(3);
			assertEquals("Config event pid match", pid, event.getPid());
			assertEquals("Config event factory pid match", factoryPid, event.getFactoryPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, event.getType());

		} finally {
			if ( config != null ) {
				config.delete();
			}
		}

		trace("Testing sync listener...finished");
    }

    /**
	 * Test targeted pids
	 * Create configs for the same pid, each new config is either more "specific" than
	 * the previous one or "invalid"
	 * Check if the new config is either bound or ignored
	 * Then delete configs in reverse order and check if either nothing is happening
	 * ("invalid" configs) or rebound to a previous config happens.
	 *
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testTargetedPid() throws Exception {
    	trace("Testing targeted pids...");

		final Bundle bundleT5 = bInstaller.installBundle("bundleT5.jar", false);
		final Bundle bundleT6 = bInstaller.installBundle("bundleT6.jar", false);
		final String pidBase = Util.createPid("pid_targeted1");
		// create a list of configurations
		// the first char is a "bitset":
		// if bit 1 is set, T5 should get the config
		// if bit 2 is set, T6 should get the config
		// so: 0 : no delivery, 1 : T5, 2: T6, 3: T5 + T6
		final String[] pids = new String[] {
				"3" + pidBase,
				"1" + pidBase + "|" + bundleT5.getSymbolicName(),
				"2" + pidBase + "|" + bundleT6.getSymbolicName(),
				"0" + pidBase + "|Not" + bundleT5.getSymbolicName(),
				"1" + pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString(),
				"2" + pidBase + "|" + bundleT6.getSymbolicName() + "|" + bundleT6.getHeaders().get(Constants.BUNDLE_VERSION).toString(),
				"0" + pidBase + "|" + bundleT5.getSymbolicName() + "|555.555.555.Not",
				"0" + pidBase + "|" + bundleT6.getSymbolicName() + "|555.555.555.Not",
				"1" + pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT5.getLocation(),
				"2" + pidBase + "|" + bundleT6.getSymbolicName() + "|" + bundleT6.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT6.getLocation(),
				"0" + pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT5.getLocation() + "Not",
				"0" + pidBase + "|" + bundleT6.getSymbolicName() + "|" + bundleT6.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT6.getLocation() + "Not"
		};
		final List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final List<Configuration> configs = new ArrayList<>();

		try {
			final SynchronizerImpl sync1_1 = new SynchronizerImpl("T5-1");
			list.add(bc.registerService(Synchronizer.class,
					sync1_1, propsForSyncT5_1));
			final SynchronizerImpl sync2_1 = new SynchronizerImpl("T6-1");
			list.add(bc.registerService(Synchronizer.class,
					sync2_1, propsForSyncT6_1));

			this.startTargetBundle(bundleT5);
			this.setCPtoBundle("*", ConfigurationPermission.TARGET, bundleT5, false);
			this.startTargetBundle(bundleT6);
			this.setCPtoBundle("*", ConfigurationPermission.TARGET, bundleT6, false);

			trace("Wait for signal.");
			int count1_1 = 0, count2_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());

			// let's create some configurations
			String previousKeyT5 = null, previousKeyT6 = null;
			for(int i=0; i<pids.length; i++) {
				// key contains marker at char 0 + pid
				final String key = pids[i];
				final String pid = key.substring(1);
				trace("Creating config " + pid);
				final Configuration c = this.cmA.waitForService(500l)
						.getConfiguration(pid, "?*");
				configs.add(c);
				final String propPreviousKeyT5 = previousKeyT5;
				final String propPreviousKeyT6 = previousKeyT6;
				Dictionary<String,Object> newprops = new Hashtable<>();
				newprops.put("test", key);
				if (propPreviousKeyT5 != null) {
					newprops.put("previousT5", propPreviousKeyT5);
				}
				if (propPreviousKeyT6 != null) {
					newprops.put("previousT6", propPreviousKeyT6);
				}
				c.update(newprops);

		        // check T5
		        final char deliveryMarker = key.charAt(0);
		        if ( deliveryMarker == '0' || deliveryMarker == '2') {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
					assertEquals("Pid is wrong", key, sync1_1.getProps().get("test"));
					previousKeyT5 = key;
		        }
		        // check T6
		        if ( deliveryMarker == '0' || deliveryMarker == '1') {
		        	assertNoCallback(sync2_1, count2_1);
		        } else {
		        	count2_1 = assertCallback(sync2_1, count2_1);
					assertEquals("Pid is wrong", key, sync2_1.getProps().get("test"));
					previousKeyT6 = key;
		        }
			}

			// we now delete the configuration in reverse order
			while ( configs.size() > 0 ) {
				final Configuration c = configs.remove(configs.size() - 1);
				final String key = (String) c.getProperties().get("test");
				previousKeyT5 = (String) c.getProperties().get("previousT5");
				previousKeyT6 = (String) c.getProperties().get("previousT6");
				c.delete();

		        // check T5
		        final char deliveryMarker = key.charAt(0);
		        if ( deliveryMarker == '0' || deliveryMarker == '2') {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
		        	if ( configs.size() == 0 ) {
		        		// removed last config, so this is a delete
		        		assertNull(sync1_1.getProps());
		        	} else {
		        		// this is an update = downgrade to a previous config
		        		assertNotNull(sync1_1.getProps());
		        		final String newPid = (String) sync1_1.getProps().get("test");
		        		assertEquals("Pid is wrong", previousKeyT5, newPid);
		        	}
		        }
		        // check T6
		        if ( deliveryMarker == '0' || deliveryMarker == '1') {
		        	assertNoCallback(sync2_1, count2_1);
		        } else {
		        	count2_1 = assertCallback(sync2_1, count2_1);
		        	if ( configs.size() == 0 ) {
		        		// removed last config, so this is a delete
		        		assertNull(sync2_1.getProps());
		        	} else {
		        		// this is an update = downgrade to a previous config
		        		assertNotNull(sync2_1.getProps());
		        		final String newPid = (String) sync2_1.getProps().get("test");
		        		assertEquals("Pid is wrong", previousKeyT6, newPid);
		        	}
		        }
			}


		} finally {
			Iterator<Configuration> i = configs.iterator();
			while ( i.hasNext() ) {
				final Configuration c = i.next();
				c.delete();
			}
		}
    	trace("Testing targeted pids...finished");
    }

    /**
	 * Test targeted factory pids
	 *
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testTargetedFactoryPid() throws Exception {
    	trace("Testing targeted factory pids...");

		final Bundle bundleT5 = bInstaller.installBundle("bundleT5.jar", false);
		final String pidBase = Util.createPid("pid_targeted3");
		final String[] pids = new String[] {
				pidBase,
				pidBase + "|" + bundleT5.getSymbolicName(),
				pidBase + "|Not" + bundleT5.getSymbolicName(),
				pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString(),
				pidBase + "|" + bundleT5.getSymbolicName() + "|555.555.555.Not",
				pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT5.getLocation(),
				pidBase + "|" + bundleT5.getSymbolicName() + "|" + bundleT5.getHeaders().get(Constants.BUNDLE_VERSION).toString() + "|" + bundleT5.getLocation() + "Not"
		};
		final List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final List<Configuration> configs = new ArrayList<>();

		try {
			final SynchronizerImpl sync1_1 = new SynchronizerImpl("T5-1");
			list.add(bc.registerService(Synchronizer.class,
					sync1_1, propsForSyncT5_1));

			this.startTargetBundle(bundleT5);
			this.setCPtoBundle("*", ConfigurationPermission.TARGET, bundleT5, false);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());

			// let's create some configurations
			for(int i=0; i<pids.length; i++) {
				final String factoryPid = pids[i];
				trace("Creating factory config " + factoryPid);
				final Configuration c = this.cmA.waitForService(500l)
						.createFactoryConfiguration(factoryPid, null);
				configs.add(c);
				Dictionary<String,Object> newprops = new Hashtable<>();
				newprops.put("test", c.getPid());
				newprops.put("factoryPid", factoryPid);
				c.update(newprops);

		        if ( factoryPid.indexOf("Not") != -1 ) {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
					assertEquals("Pid is wrong", c.getPid(), sync1_1.getProps().get("test"));
		        }
			}
			// now delete them - order doesn't really matter, but we use reverse order anyway
			while ( configs.size() > 0 ) {
				final Configuration c = configs.remove(configs.size() - 1);
				final String pid = (String) c.getProperties().get("test");
				final String factoryPid = (String) c.getProperties().get("factoryPid");
				c.delete();
		        if ( factoryPid.indexOf("Not") != -1 ) {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
					assertEquals("Pid is wrong", pid, sync1_1.getProps().get("test"));
					assertEquals("Pid is not delete", Boolean.TRUE, sync1_1.getProps().get("_deleted_"));
		        }
			}


		} finally {
			Iterator<Configuration> i = configs.iterator();
			while ( i.hasNext() ) {
				final Configuration c = i.next();
				c.delete();
			}
		}
    	trace("Testing targeted factory pids...finished");
    }

    /**
	 * Test targeted pids
	 * Use a pid with a '|' .
	 *
	 * Enterprise 5.0 - ConfigAdmin 1.5
	 */
	@Test
	public void testNegativeTargetedPid() throws Exception {
    	trace("Testing targeted pids...");

		final Bundle bundleT5 = bInstaller.installBundle("bundleT5.jar", false);
		final String pid1 = Util.createPid("pid");
		final String pid2 = Util.createPid("pid|targeted2");

		final String[] pids = new String[] {pid1, pid2};
		final List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final List<Configuration> configs = new ArrayList<>();

		try {
			final SynchronizerImpl sync1_1 = new SynchronizerImpl("T5-2");
			list.add(bc.registerService(Synchronizer.class,
					sync1_1, propsForSyncT5_1));

			this.startTargetBundle(bundleT5);
			this.setCPtoBundle("*", ConfigurationPermission.TARGET, bundleT5, false);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());

			// let's create some configurations
			for(int i=0; i<pids.length; i++) {
				final String pid = pids[i];
				trace("Creating config " + pid);
				final Configuration c = this.cmA.waitForService(500l)
						.getConfiguration(pid, null);
				configs.add(c);
				c.update(singletonDictionary("test", pid));

		        if ( pid.indexOf("|") == -1 ) {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
					assertEquals("Pid is wrong", pid, sync1_1.getProps().get("test"));
		        }
			}

			// we now delete the configuration in reverse order
			while ( configs.size() > 0 ) {
				final Configuration c = configs.remove(configs.size() - 1);
				final String pid = (String) c.getProperties().get("test");
				c.delete();
		        if ( pid.indexOf("|") == -1 ) {
		        	assertNoCallback(sync1_1, count1_1);
		        } else {
		        	count1_1 = assertCallback(sync1_1, count1_1);
		        	// remove, so no rebind
	        		assertNull(sync1_1.getProps());
		        }
			}


		} finally {
			Iterator<Configuration> i = configs.iterator();
			while ( i.hasNext() ) {
				final Configuration c = i.next();
				c.delete();
			}
		}
    	trace("Testing targeted pids...finished");
    }

    /**
	 * Dynamic binding( configuration with null location and ManagedService)
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String,String)
	 * @spec Configuration.getBundleLocation()
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.getProperties()
	 *
	 * @throws Exception
	 */
	@Test
	public void testDynamicBinding() throws Exception {
		Bundle bundle1 = bInstaller.installBundle("targetb1.jar", false);

		final String bundlePid = Util.createPid("bundlePid1");
		Configuration conf = null;
		ServiceRegistration<Synchronizer> reg = null;
		Dictionary<String,Object> props = null;

		/*
		 * 1. created newly with non-null location and after set to null. After
		 * that, ManagedService is registered.
		 */
		trace("############ 1 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, bundle1.getLocation());

			props = new Hashtable<>();
			props.put("StringKey", getName() + "-1");
			conf.update(props);
			conf.setBundleLocation(null);

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED). Wait for a while.",
					conf.getBundleLocation(), bundle1.getLocation());
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			/*
			 * After the dynamically bound bundle has been uninstalled, the
			 * location must be reset to null.
			 */
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}

		/* 2. created newly with null location.(with properties) */
		trace("############ 2 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			/* props is set. */
			props = new Hashtable<>();
			props.put("StringKey", getName() + "-2");
			conf.update(props);
			reg = null;

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED).Wait for a while.",
					conf.getBundleLocation(), bundle1.getLocation());
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}
		/* 3. created newly with null location. (no properties) */
		trace("############ 3 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED).Wait for a while.",
					conf.getBundleLocation(), bundle1.getLocation());
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}
		/*
		 * 4. created newly with null location and dynamic bound. Then
		 * explicitly set location. --> never dynamically unbound - bound
		 * anymore.
		 */
		trace("############ 4 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			props = new Hashtable<String,Object>();
			props.put("StringKey", getName() + "-4");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			conf.setBundleLocation(bundle1.getLocation());
			bundle1.stop();
			assertEquals("No more Dynamic binding(STOPPED). Wait for a while.",
					conf.getBundleLocation(), bundle1.getLocation());
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			assertEquals("No more Dynamic binding(UNINSTALLED)",
					bundle1.getLocation(), conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}
		/*
		 * 5. dynamic binding and cm restart 1 (with properties).
		 */
		/**
		 * (a)install test bundle. (b)configure test bundle. (c)stop
		 * configuration admin service. (d)start configuration admin service
		 *
		 * ==> configuration is still bound to the target bundle
		 */
		trace("############ 5 testDynamicBinding()");
		Bundle cmBundle = null;
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			props = new Hashtable<String,Object>();
			props.put("StringKey", getName() + "-5");
			conf.update(props);
			reg = null;

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue("ManagedService MUST be called back.", calledback);
			bundle1.stop();

			restartCM();

			sleep(SIGNAL_WAITING_TIME * 2);
			Configuration[] confs =cmA.waitForService(500l).listConfigurations(null);
			assertNotNull("confs must NOT be empty:", confs);
			assertEquals("confs.length must be one", 1, confs.length-existingConfigs.size());
			for (int i = 0; i < confs.length; i++) {
                conf = confs[i];
                if (!existingConfigs.contains(conf.getPid())) {
                    trace("confs[" + i + "].getBundleLocation()=" + confs[i].getBundleLocation());
                    assertEquals("Dynamic binding(UNINSTALLED):confs[" + i
                        + "].getBundleLocation() must be the target bundle", bundle1.getLocation(),
                        confs[i].getBundleLocation());
                }
            }
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			assertEquals(
					"Restarted CM: Must be still bound to the target bundle.",
					bundle1.getLocation(), conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}

		/*
		 * 6. dynamic binding and cm restart 2(with properties).
		 */
		/**
		 * (a)install test bundle. (b)configure test bundle. (c)stop
		 * configuration admin service. (d)uninstall test bundle. (e)start
		 * configuration admin service
		 *
		 * ==> configuration is still bound to the uninstalled bundle
		 */
		trace("############ 6 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			props = new Hashtable<>();
			props.put("StringKey", getName() + "-6");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			cmBundle = stopCmBundle();
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			bundle1.stop();
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			startCmBundle(cmBundle);
			sleep(SIGNAL_WAITING_TIME * 2);
			Configuration[] confs =cmA.waitForService(500l).listConfigurations(null);
			assertNotNull("confs must NOT be empty:", confs);
            assertEquals("confs.length must be one", 1, confs.length - existingConfigs.size());
            for (int i = 0; i < confs.length; i++) {
                conf = confs[i];
                if (!existingConfigs.contains(conf.getPid())) {
                    trace("confs[" + i + "].getBundleLocation()=" + confs[i].getBundleLocation());
                    assertEquals("Dynamic binding(UNINSTALLED):confs[" + i + "].getBundleLocation() must be null",
                        null, confs[i].getBundleLocation());
                }
            }
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			assertEquals("Dynamic binding(UNINSTALLED): Must be Re-bound",
					bc.getBundle().getLocation(),
					conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}

		/*
		 * 7. dynamic binding and cm restart 2 (with no properties).
		 */
		/**
		 * (a)install test bundle. (b)configure test bundle. (c)stop
		 * configuration admin service. (d)uninstall test bundle. (e)start
		 * configuration admin service
		 *
		 * ==> configuration is still bound to the uninstalled bundle
		 */
		trace("############ 7 testDynamicBinding()");
		try {
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			// props = new Hashtable();
			// props.put("StringKey", getName()+"-7");
			// conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			cmBundle = stopCmBundle();
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			bundle1.stop();
			sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			sleep(SIGNAL_WAITING_TIME);
			startCmBundle(cmBundle);
		
			sleep(SIGNAL_WAITING_TIME * 2);
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, null);
			assertEquals("Dynamic binding(UNINSTALLED): Must be reset to null",
					null, conf.getBundleLocation());
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			assertEquals("Dynamic binding(UNINSTALLED): Must be Re-bound",
					bc.getBundle().getLocation(),
					conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}

		/*
		 * [In Progress] 8. After created newly with non-null location and
		 * ManagedService is registered, the location is set to null. What
		 * happens ?
		 */
		trace("############ 8 testDynamicBinding()");
		ServiceRegistration< ? > reg2 = null;
		Bundle bundle2 = null;
		try {
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			conf =cmA.waitForService(500l).getConfiguration(bundlePid, bundle1.getLocation());
			props = new Hashtable<String,Object>();
			props.put("StringKey", getName() + "-8");
			conf.update(props);

			SynchronizerImpl sync = new SynchronizerImpl("ID1");
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			conf.setBundleLocation(null);
			trace("Wait for signal.");
			// calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			// assertFalse("ManagedService MUST NOT be called back.",
			// calledback);
			// assertEquals("Must be still bound to the target bundle.",
			// bundle1.getLocation(), conf.getBundleLocation());

			// TODO: check if the unbound once (updated(null) is called)
			// and bound againg (updated(props) is called ).

			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count++);
			assertTrue("ManagedService MUST be called back.", calledback);
			conf.update(props);
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count++);
			assertTrue("ManagedService MUST be called back.", calledback);
			assertEquals("Must be bound to the target bundle. again.",
					bundle1.getLocation(), conf.getBundleLocation());

			bundle2 = bInstaller.installBundle("targetb2.jar", false);
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = bc.registerService(Synchronizer.class,
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			//assertFalse(
			//		"ManagedService MUST NOT be called back in case conf has no properties.",
			//		calledback2);
			assertTrue(
					"ManagedService MUST be called back with null property.",
					calledback2);

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED). Wait for a while.",
					conf.getBundleLocation(), bundle1.getLocation());
            calledback = sync2.waitForSignal(SIGNAL_WAITING_TIME, count2 + 1);
			assertFalse("ManagedService2 MUST NOT be called back.", calledback);
			bundle1.uninstall();
			trace("Dynamic binding(UNINSTALLED). Wait for a while.");
			calledback2 = sync2
					.waitForSignal(SIGNAL_WAITING_TIME * 2, ++count2);
            assertTrue("ManagedService2 MUST be called back.", calledback2);

            assertEquals("Dynamic binding(UNINSTALLED). Wait for a while.", bundle2.getLocation(),
                conf.getBundleLocation());
			props.put("StringKey", "stringvalue2");
			conf.update(props);
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService MUST be called back.", calledback2);
			conf.delete();
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService MUST be called back.", calledback2);
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertFalse("ManagedService MUST NOT be called back.", calledback);

		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			if (reg2 != null)
				reg2.unregister();
			reg2 = null;
			reg2 = null;
			if (bundle2 != null && bundle2.getState() != Bundle.UNINSTALLED)
				bundle2.uninstall();
			bundle2 = null;
			conf =cmA.waitForService(500l).getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
		}

        /*
         * 9. Test dynamic bindings from getConfiguration(pid) and
         * createConfiguration(pid) (Member Bug 2551)
         */
        trace("############ 9 testDynamicBinding()");
        String dynamicPid1 = Util.createPid("dynamicPid1");
        String dynamicPid2 = Util.createPid("dynamicPid2");
        String dynamicFactoryPid = Util.createPid("dynamicFactoryPid");
        String dynamicFactoryPidInstance = null;
        try {
			props = new Hashtable<String,Object>();
            props.put("StringKey", "String Value");

            // make sure this bundle has enough permissions
            this.setAppropriatePermission();

            // ensure unbound configuration
            conf = this.cmA.waitForService(500l).getConfiguration(dynamicPid1, null);
            assertNotNull("Configuration must exist for " + dynamicPid1, conf);
            assertNull("Configuration must be new for " + dynamicPid1, conf.getProperties());
            assertNull("Configuration for " + dynamicPid1 + " must be unbound",
                this.getBundleLocationForCompare(conf));
            conf.update(props);

            SynchronizerImpl sync = new SynchronizerImpl("ID1");
			reg = bc.registerService(Synchronizer.class, sync,
					propsForSync1);
			bundle1 = bInstaller.installBundle("targetb1.jar", false);
            this.startTargetBundle(bundle1);
            trace("Wait for signal.");

			ServiceReference<ConfigurationAdmin> caref = bundle1
					.getBundleContext()
					.getServiceReference(ConfigurationAdmin.class);
            ConfigurationAdmin ca = bundle1.getBundleContext().getService(caref);

            // ensure configuration 1 is bound to bundle1
            conf = ca.getConfiguration(dynamicPid1);
            assertNotNull("Configuration must exist for " + dynamicPid1, conf);
            assertNotNull("Configuration must not be new for " + dynamicPid1, conf.getProperties());
            assertEquals("Configuration for " + dynamicPid1 + " must be bound to " + bundle1.getLocation(),
                bundle1.getLocation(), this.getBundleLocationForCompare(conf));

            // ensure configuration 2 is bound to bundle1
            conf = ca.getConfiguration(dynamicPid2);
            assertNotNull("Configuration must exist for " + dynamicPid2, conf);
            assertNull("Configuration must be new for " + dynamicPid2, conf.getProperties());
            assertEquals("Configuration for " + dynamicPid2 + " must be bound to " + bundle1.getLocation(),
                bundle1.getLocation(), this.getBundleLocationForCompare(conf));
            conf.update(props);

            // ensure factory configuration bound to bundle1
            conf = ca.createFactoryConfiguration(dynamicFactoryPid);
            dynamicFactoryPidInstance = conf.getPid();
            assertNotNull("Factory Configuration must exist for " + dynamicFactoryPid, conf);
            assertNull("Factory Configuration must be new for " + dynamicFactoryPid, conf.getProperties());
            assertEquals(
                "Factory Configuration for " + dynamicFactoryPid + " must be bound to " + bundle1.getLocation(),
                bundle1.getLocation(), this.getBundleLocationForCompare(conf));
            conf.update(props);

            SynchronizerImpl sync2 = new SynchronizerImpl("SyncListener");
			reg2 = bc.registerService(ConfigurationListener.class,
					new SyncEventListener(sync2),
                null);

            // unsinstall the bundle, make sure configurations are unbound
			bundle1.uninstall();

            // wait for three (CM_LOCATION_CHANGED) events
            boolean threeEvents = sync2.waitForSignal(500, 3);
            assertTrue("Expecting three CM_LOCATION_CHANGED events after bundle uninstallation", threeEvents);

            // ensure configuration 1 is unbound
            conf = this.cmA.waitForService(500l).getConfiguration(dynamicPid1, null);
            assertNotNull("Configuration must exist for " + dynamicPid1, conf);
            assertNotNull("Configuration must not be new for " + dynamicPid1, conf.getProperties());
            assertNull("Configuration for " + dynamicPid1 + " must be unbound", this.getBundleLocationForCompare(conf));

            // ensure configuration 2 is unbound
            conf = this.cmA.waitForService(500l).getConfiguration(dynamicPid2, null);
            assertNotNull("Configuration must exist for " + dynamicPid2, conf);
            assertNotNull("Configuration must not be new for " + dynamicPid2, conf.getProperties());
            assertNull("Configuration for " + dynamicPid2 + " must be unbound", this.getBundleLocationForCompare(conf));

            // ensure factory configuration is unbound
            conf = this.cmA.waitForService(500l).getConfiguration(dynamicFactoryPidInstance, null);
            assertNotNull("Configuration must exist for " + dynamicFactoryPidInstance, conf);
            assertEquals("Configuration " + dynamicFactoryPidInstance + " must be factory configuration for "
                + dynamicFactoryPid, dynamicFactoryPid, conf.getFactoryPid());
            assertNotNull("Configuration must not be new for " + dynamicFactoryPidInstance, conf.getProperties());
            assertNull("Configuration for " + dynamicFactoryPidInstance + " must be unbound",
                this.getBundleLocationForCompare(conf));

        } finally {
            if (reg != null) reg.unregister();
            reg = null;
            if (reg2 != null) reg2.unregister();
            reg2 = null;
            if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED) bundle1.uninstall();
            bundle1 = null;
            conf =cmA.waitForService(500l).getConfiguration(dynamicPid1);
            if (conf != null) conf.delete();
            conf =cmA.waitForService(500l).getConfiguration(dynamicPid2);
            if (conf != null) conf.delete();
            if (dynamicFactoryPidInstance != null) {
                conf =cmA.waitForService(500l).getConfiguration(dynamicFactoryPidInstance);
                if (conf != null) conf.delete();
            }
        }
	}

	private void startTargetBundle(Bundle bundle) throws BundleException {
		if (this.permissionFlag)
			bundle.start();
		else {
			PermissionInfo[] defPis = permAdminA.getService()
					.getDefaultPermissions();
			String[] locations = permAdminA.getService().getLocations();
			Map<String,PermissionInfo[]> bundlePisMap = null;
			if (locations != null) {
				bundlePisMap = new HashMap<>(locations.length);
				for (int i = 0; i < locations.length; i++) {
					bundlePisMap.put(locations[i],
							permAdminA.getService()
									.getPermissions(locations[i]));
				}
			}
			this.resetPermissions();
			bundle.start();
			// this.printoutPermissions();
			this.setPreviousPermissions(defPis, bundlePisMap);
		}
	}

	private void setPreviousPermissions(PermissionInfo[] defPis,
			Map<String,PermissionInfo[]> bundlePisMap) {

		PermissionInfo[] pis = null;
		if (bundlePisMap != null)
			for (Iterator<String> keys = bundlePisMap.keySet().iterator(); keys
					.hasNext();) {
				String location = keys.next();
				if (location.equals(thisLocation()))
					pis = bundlePisMap.get(location);
				else
					permAdminA.getService()
							.setPermissions(location,
							bundlePisMap.get(location));
			}
		permAdminA.getService().setDefaultPermissions(defPis);
		if (pis != null)
			permAdminA.getService().setPermissions(thisLocation(), pis);
	}

	private void setInappropriatePermission() throws BundleException {
		if (permAdminA.getService() == null)
			return;
		this.resetPermissions();
		list.clear();
		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");
		// add(list, CP, "*", "CONFIGURE");
		add(list, AP, "*", "*");
		permissionFlag = false;
		this.setBundlePermission(bc.getBundle(), list);
	}

	private void setAppropriatePermission() throws BundleException {
		if (permAdminA.getService() == null)
			return;
		this.resetPermissions();
		list.clear();
		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");
		add(list, CP, "*", "CONFIGURE");
		add(list, AP, "*", "*");
		permissionFlag = true;
		this.setBundlePermission(bc.getBundle(), list);

	}

	/**
	 * TODO comments
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update()
	 * @spec Configuration.getProperties()
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		String pid = Util.createPid();
		Configuration conf = cmA.waitForService(500l).getConfiguration(pid);
		Dictionary<String,Object> props = conf.getProperties();
		assertNull("Properties in conf", props);

		Hashtable<String,Object> newprops = new Hashtable<String,Object>();
		newprops.put("somekey", "somevalue");
		conf.update(newprops);
		props = conf.getProperties();
		assertNotNull("Properties in conf", props);
		assertEquals("conf property 'somekey'", "somevalue",
				props.get("somekey"));
		Configuration conf2 = cmA.waitForService(500l).getConfiguration(pid);
		Dictionary<String,Object> props2 = conf2.getProperties();
		assertNotNull("Properties in conf2", props2);
		assertEquals("conf2 property 'somekey'", "somevalue",
				props2.get("somekey"));
		// assertSame("Same configurations", conf, conf2);
		// assertEquals("Equal configurations", conf, conf2);
		// assertEquals("Equal pids", conf.getPid(), conf2.getPid());
		assertTrue("Equal configurations", equals(conf, conf2));

		/* check returned properties are copied ones. */
		Dictionary<String,Object> props3 = conf2.getProperties();
		props3.put("KeyOnly3", "ValueOnly3");
		assertTrue("Properties are copied", props2.get("KeyOnly3") == null);

		/* Try to update with illegal configuration types */
		Hashtable<String,Object> illegalprops = new Hashtable<>();
		Collection<Object> v = new ArrayList<>();
		v.add("a string");
		v.add(Locale.getDefault());
		illegalprops.put("somekey", "somevalue");
		illegalprops.put("anotherkey", v);
		String message = "updating with illegal properties";
		try {
			conf2.update(illegalprops);
			/* A IllegalArgumentException should have been thrown */
			failException(message, IllegalArgumentException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, IllegalArgumentException.class, e);
		}

		/* contains case variants of the same key name */
		Hashtable<String,Object> casevariants = new Hashtable<>();
		casevariants.put("somekey", "somevalue");
		casevariants.put("SomeKey", "SomeValue");
		message = "updating with illegal properties (case variants of the same key)";
		try {
			conf2.update(casevariants);
			/* A IllegalArgumentException should have been thrown */
			failException(message, IllegalArgumentException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, IllegalArgumentException.class, e);
		}

	}

	/**
	 * Test the update if different method.
	 * Tests synchronous listeners and managed service.
	 */
	@Test
	public void testUpdateIfDifferent() throws Exception {
		final String pid = Util.createPid("uid");
		final List<ConfigurationEvent> events = new ArrayList<>();
		final List<Dictionary<String, ?>> updates = new ArrayList<>();
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final SynchronousConfigurationListener listener = new SynchronousConfigurationListener() {

			@Override
			public void configurationEvent(final ConfigurationEvent event) {
				if (pid.equals(event.getPid())) {
					synchronized (events) {
						events.add(event);
					}
				}

			}
		};
		final ManagedService ms = new ManagedService() {

			@Override
			public void updated(final Dictionary<String, ?> properties) throws ConfigurationException {
				System.out.println("Updating managed service " + properties);
    			updates.add(properties);
				try {
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					throw new RuntimeException(e);
				}
			}

		};
		bc.registerService(
				SynchronousConfigurationListener.class.getName(),
				listener, null);
	    final Hashtable<String,Object> msProps = new Hashtable<String,Object>();
	    msProps.put(Constants.SERVICE_PID, pid);
		ServiceRegistration< ? > reg = bc.registerService(
				ManagedService.class.getName(),
				ms, msProps);
		// wait for managed service to get configured with null
		barrier.await();
		barrier.reset();

		final Configuration conf = cmA.waitForService(500l)
				.getConfiguration(pid);

			final long startLevel = conf.getChangeCount();
			final Hashtable<String,Object> props1 = new Hashtable<String,Object>();
			props1.put("scalar", "somevalue");
			props1.put("array", new long[] {1, 2});
			props1.put("collection", Arrays.asList(1,2));
			assertTrue(conf.updateIfDifferent(props1));
			assertEquals(startLevel + 1, conf.getChangeCount());

			assertEquals(1, events.size());
			// wait for managed service
			barrier.await();
			barrier.reset();

			// update again with same props
			assertFalse(conf.updateIfDifferent(props1));
			assertEquals(startLevel + 1, conf.getChangeCount());
			assertEquals(1, events.size());
            
			// update scalar prop
			final Hashtable<String,Object> props2 = new Hashtable<String,Object>(props1);
			props2.put("scalar", "newvalue");
			assertTrue(conf.updateIfDifferent(props2));
			assertEquals(startLevel + 2, conf.getChangeCount());

			assertEquals(2, events.size());
			
			// update array prop
			final Hashtable<String,Object> props3 = new Hashtable<String,Object>(props2);
			props3.put("array",  new long[] {3, 4});
			assertTrue(conf.updateIfDifferent(props3));
			assertEquals(startLevel + 3, conf.getChangeCount());
			assertEquals(3, events.size());
			
			// update collection prop
			final Hashtable<String,Object> props4 = new Hashtable<String,Object>(props3);
			props4.put("collection", Arrays.asList(3, 4));
			assertTrue(conf.updateIfDifferent(props4));
			assertEquals(startLevel + 4, conf.getChangeCount());
			assertEquals(4, events.size());
			
			// check updates to managed service
			System.out.println("Waiting for managed service updates");
			// wait for managed service
			barrier.await();

			assertNull(updates.get(0));
			assertEquals("somevalue", updates.get(1).get("scalar"));
			assertEquals("newvalue", updates.get(2).get("scalar"));
			reg.unregister();
			// TODO BJ:WHY MUST THIS BE DONE MANUALLY????


	}

	/**
	 * Tests if we really get the same configuration.
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update()
	 * @spec Configuration.getPid()
	 * @throws Exception
	 */
	@Test
	public void testEquals() throws Exception {
        String pid = Util.createPid();
        try {
			Configuration conf1 = cmA.waitForService(500l)
					.getConfiguration(pid);
			Configuration conf2 = cmA.waitForService(500l)
					.getConfiguration(pid);
            assertEquals("Equal configurations", conf1, conf2);
            assertTrue("Equal configurations", equals(conf1, conf2));
        } finally {
			cmA.waitForService(500l).getConfiguration(pid).delete();
        }
	}

	/**
	 * Tests listing of configurations.
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec ConfigurationAdmin.listConfigurations(String)
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @throws Exception
	 */
	@Test
	public void testListConfigurations() throws Exception {
		this.setAppropriatePermission();
		/* Create configurations */
		/* pid starts with the same prefix */
		final String pid11 = Util.createPid("pid11");
		final String pid12 = Util.createPid("pid12");
		final String pid21 = Util.createPid("pid21");
		final String pid22 = Util.createPid("pid22");
		final String pid31 = Util.createPid("pid31");
		final String pid32 = Util.createPid("pid32");
		/* pid does not start with the same prefix */
		final String otherPid = "otherPid";
		// this.cleanCM();

		List<Configuration> configs = new ArrayList<>(2);
		configs.add(cmA.waitForService(500l).getConfiguration(pid11));
		configs.add(cmA.waitForService(500l).getConfiguration(pid12));

		List<Configuration> updatedConfigs2 = new ArrayList<>(2);
		updatedConfigs2.add(cmA.waitForService(500l).getConfiguration(pid21));
		updatedConfigs2.add(cmA.waitForService(500l).getConfiguration(pid22));

		Configuration otherConf = cmA.waitForService(500l)
				.getConfiguration(otherPid);

		/* location is different */
		List<Configuration> updatedConfigs3 = new ArrayList<>(2);
		updatedConfigs3.add(cmA.waitForService(500l)
				.getConfiguration(pid31, neverlandLocation));
		updatedConfigs3.add(cmA.waitForService(500l)
				.getConfiguration(pid32, neverlandLocation));

		/*
		 * Update properties on some of configurations (to make them "active")
		 */
		for (int i = 0; i < updatedConfigs2.size(); i++) {
			Hashtable<String,Object> props = new Hashtable<>();
			props.put("someprop" + i, "somevalue" + i);
			updatedConfigs2.get(i).update(props);
		}

		for (int i = 0; i < updatedConfigs3.size(); i++) {
			Hashtable<String,Object> props = new Hashtable<>();
			props.put("someprop" + i, "somevalue" + i);
			updatedConfigs3.get(i).update(props);
		}

		try {
			Configuration[] confs = cmA.waitForService(500l)
					.listConfigurations("(service.pid="
					+ Util.createPid() + "*)");
			/*
			 * Returned list must contain all of updateConfigs2 and
			 * updateConfigs3.
			 */
			checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
					updatedConfigs3, null);

			/* Inappropriate Permission */
			this.setInappropriatePermission();
			confs = cmA.waitForService(500l)
					.listConfigurations("(service.pid=" + Util.createPid()
					+ "*)");
			if (System.getSecurityManager() != null)
				/* Returned list must contain all of updateConfigs2. */
				checkIfAllUpdatedConfigs2isListed(confs, updatedConfigs2,
						updatedConfigs3, null);
			else
				/*
				 * Returned list must contain all of updateConfigs2,
				 * updateConfigs3 and otherConf.
				 */
				checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
						updatedConfigs3, otherConf);

			/* Appropriate Permission */
			this.setAppropriatePermission();
			confs = cmA.waitForService(500l).listConfigurations(null);
			/*
			 * Returned list must contain all of updateConfigs2, updateConfigs3
			 * and otherConf.
			 */
			checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
					updatedConfigs3, otherConf);

			/* Inappropriate Permission */
			this.setInappropriatePermission();
			confs = cmA.waitForService(500l).listConfigurations(null);
			if (System.getSecurityManager() != null)
				/*
				 * Returned list must contain all of updateConfigs2 and
				 * otherConf.
				 */
				checkIfAllUpdatedConfigs2isListed(confs, updatedConfigs2,
						updatedConfigs3, otherConf);
			else
				/*
				 * Returned list must contain all of updateConfigs2,
				 * updateConfigs3 and otherConf.
				 */
				checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
						updatedConfigs3, otherConf);

			/* if the filter string is in valid */
			/* must fail because of invalid filter. */
			String message = "try to list configurations by invalid filter string.";
			try {
				cmA.waitForService(500l)
						.listConfigurations(
								"(service.pid=" + Util.createPid() + "*");
				/* A InvalidSyntaxException should have been thrown */
				failException(message, InvalidSyntaxException.class);
			} catch (AssertionFailedError e) {
				throw e;
			} catch (Throwable e) {
				/* Check that we got the correct exception */
				assertException(message, InvalidSyntaxException.class, e);
			}

		}
		/* Delete all used configurations */
		finally {
			for (int i = 0; i < configs.size(); i++) {
				configs.get(i).delete();
			}
			for (int i = 0; i < updatedConfigs2.size(); i++) {
				updatedConfigs2.get(i).delete();
			}
			for (int i = 0; i < updatedConfigs3.size(); i++) {
				updatedConfigs3.get(i).delete();
			}
			otherConf.delete();
		}
		this.setAppropriatePermission();
		/* List all configurations and make sure they are all gone */
		Configuration[] leftConfs = cmA.waitForService(500l)
				.listConfigurations("(|(service.pid=" + pid11
						+ ")(service.pid=" + pid12 + ")(service.pid=" + pid21
						+ ")(service.pid=" + pid22 + ")(service.pid=" + pid31
						+ ")(service.pid=" + pid32 + ")(service.pid="
						+ otherPid + "))");
		assertNull("Left configurations", leftConfs);

	}

	private void checkIfAllUpdatedConfigs2isListed(Configuration[] confs,
			List<Configuration> updatedConfigs2,
			List<Configuration> updatedConfigs3, Configuration otherConf)
			throws IOException, InvalidSyntaxException, InterruptedException {
		boolean otherFlag = false;
		List<Configuration> removedConfigs2 = new ArrayList<>(
				updatedConfigs2.size());
		confs = cmA.waitForService(500l)
				.listConfigurations("(service.pid=" + Util.createPid() + "*)");
		if (confs == null) {
			fail("No configurations returned");
		}
		for (int i = 0; i < confs.length; i++) {
			int index = isPartOf(confs[i], updatedConfigs2);
			if (index != -1) {
				pass(confs[i].getPid() + " was returned");
				removedConfigs2.add(confs[i]);
			} else if (otherConf != null && confs[i].equals(otherConf)) {
				pass(confs[i].getPid() + " was returned");
				otherFlag = true;
			} else {
				fail(confs[i].getPid() + " should not have been listed");
			}
		}
		if (removedConfigs2.size() != updatedConfigs2.size() || otherFlag) {
			fail("All config with nun-null properties and bound to the bundle location cannot be retrieved by listConfigurations().");
		}
	}


	private void checkIfAllUpdatedConfigs2and3isListed(Configuration[] confs,
			List<Configuration> updatedConfigs2,
			List<Configuration> updatedConfigs3, Configuration otherConf)
			throws IOException, InvalidSyntaxException {
		/*
		 * List all configurations and make sure that only the active
		 * configurations are returned
		 */
		List<Configuration> removedConfigs2 = new ArrayList<>(
				updatedConfigs2.size());
		List<Configuration> removedConfigs3 = new ArrayList<>(
				updatedConfigs3.size());
		boolean otherFlag = false;
		if (confs == null) {
			fail("No configurations returned");
		}

		for (int i = 0; i < confs.length; i++) {
			int index = isPartOf(confs[i], updatedConfigs2);
			if (index != -1) {
				pass(confs[i].getPid() + " was returned");
				removedConfigs2.add(confs[i]);
				continue;
			}
			index = isPartOf(confs[i], updatedConfigs3);
			if (index != -1) {
				pass(confs[i].getPid() + " was returned");
				removedConfigs3.add(confs[i]);
			} else if (otherConf != null && equals(confs[i], otherConf)) {
				pass(confs[i].getPid() + " was returned");
				otherFlag = true;
            } else if (!existingConfigs.contains(confs[i].getPid())) {
				fail(confs[i].getPid() + " should not have been listed");
			}
		}
		if (removedConfigs2.size() != updatedConfigs2.size()
				|| removedConfigs3.size() != updatedConfigs3.size()
				|| otherFlag) {
			fail("All config with nun-null properties cannot be retrieved by listConfigurations().");
		}
	}

	/**
	 * Tests to register a ManagedService when a configuration is existing for
	 * it.
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.getBundleLocation()
	 */
	@Test
	public void testManagedServiceRegistration() throws Exception {
		this.setAppropriatePermission();
		final String pid = Util.createPid("somepid");

		/* create a configuration in advance, then register ManagedService */
		Configuration conf = cmA.waitForService(500l).getConfiguration(pid);

		trace("created configuration has null properties.");
		trace("Create and register a new the ManagedService");

		Semaphore semaphore = new Semaphore(0);
		Entry<ServiceRegistration,ManagedServiceImpl> e1 = createManagedService(
				pid, semaphore);
		ManagedServiceImpl ms = e1.getValue();
		ServiceRegistration sreg1 = e1.getKey();
		trace("Wait until the ManagedService has gotten the update");
		boolean calledBack = semaphore.tryAcquire(SIGNAL_WAITING_TIME,
				TimeUnit.MILLISECONDS);
		assertTrue("ManagedService is called back", calledBack);

		trace("Update done!");
		conf.delete();

		final String pid2 = Util.createPid("somepid2");
		Configuration conf2 = cmA.waitForService(500l).getConfiguration(pid2);

		Hashtable<String,Object> props = new Hashtable<>();
		props.put("somekey", "somevalue");
		props.put("CAPITALkey", "CAPITALvalue");
		conf2.update(props);
		trace("created configuration has non-null properties.");
		trace("Create and register a new the ManagedService");
		trace("Wait until the ManagedService has gotten the update");
		semaphore = new Semaphore(0);
		Entry<ServiceRegistration,ManagedServiceImpl> e2 = createManagedService(
				pid2, semaphore);
		ms = e2.getValue();
		ServiceRegistration sreg2 = e2.getKey();
		semaphore.acquire();
		trace("Update done!");

		/*
		 * Add the two properties added by the CM and then check for equality in
		 * the properties
		 */
		props.put(Constants.SERVICE_PID, pid2);
		// props.put(SERVICE_BUNDLE_LOCATION, "cm_TBC"); R3 does not include
		// service.bundleLocation anymore!
		DictionaryAssert.assertThat(props)
				.containsExactlyInAnyOrderEntriesOf(ms.getProperties());

		trace((String) ms.getProperties().get("service.pid"));
		// trace((String) ms.getProperties().get("service.bundleLocation"));
		trace(this.getBundleLocationForCompare(conf2));
		/* OUTSIDE_OF_SPEC */
		// assertNotSame("Properties same?", props, ms.getProperties());
		conf2.delete();
		sreg1.unregister();
		sreg2.unregister();


	}

	private void printoutPropertiesForDebug(SynchronizerImpl sync) {
		Dictionary<String,Object> props1 = sync.getProps();
		if (props1 == null) {
			System.out.println("props = null");
		} else {
			System.out.println("props = ");
			for (Enumeration<String> enums = props1.keys(); enums
					.hasMoreElements();) {
				Object key = enums.nextElement();
				System.out.println("\t(" + key + ", " + props1.get(key) + ")");
			}
		}
	}

	/**
	 * Register ManagedService Test 2.
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistration2() throws Exception {
		this.setAppropriatePermission();
		Bundle bundle = bInstaller.installBundle("targetb1.jar", false);
		final String bundlePid = Util.createPid("bundlePid1");
		ServiceRegistration<Synchronizer> reg = null;
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		trace("###################### A. testManagedServiceRegistration2.");
		try {
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");
			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback);
			assertNull("called back with null props", sync.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-A");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-A", props.get("StringKey"));
			assertNull("bundleLocation must be not included",
					props.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			/* stop and restart target bundle */
			bundle.stop();
			bundle.start();
			trace("The target bundle has been started. Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			// printoutPropertiesForDebug(sync);

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNull("called back with null props", sync.getProps());
			Configuration[] confs = cmA.waitForService(500l)
					.listConfigurations(null);
            assertTrue("confs must be empty:", confs == null
                || (existingConfigs.size() > 0 && confs.length == existingConfigs.size()));
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();
		}

		/*
		 * B1. (1)Register ManagedService in advance. (2)create Configuration
		 * with different location and null props (3)setBundleLocation to the
		 * target bundle.
		 */
		trace("###################### B1. testManagedServiceRegistration2.");
		try {
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");
			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService must be called back even if no configuration.",
					calledback);
			assertNull("called back with null props", sync.getProps());

			trace("The configuration with different location is being created ");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					neverlandLocation);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			this.printoutPropertiesForDebug(sync);
			assertFalse("ManagedService must NOT be called back", calledback);
			/* The conf has null props. */
			trace("The configuration is being set to the target bundle");
			conf.setBundleLocation(bundle.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);

			/*
			 * The spec seems unclear whether the ManagedService registered by
			 * the newly bound bundle should be called back with null or not
			 * called back. The implementator of this TCK (Ikuo) thinks TCK should
			 * accept either case of (a) No callback. (b) callback with null.
			 * Otherwise, it fails. Spec of version 1.3
			 */
			if (calledback) {
				count++;
				assertNull(
						"The props called back MUST be null, if the ManagedService is called back.",
						sync.getProps());
			}
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B1");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-B1", props.get("StringKey"));
			assertNull("bundleLocation must be not included",
					props.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being updated to null.");
			/* props is reset */
			conf.update(new Hashtable<String,Object>(0));
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("Size of props must be 1", 1, props.size());
			conf.update(props);
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNull("called back with null props", sync.getProps());

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertFalse("ManagedService must Not be called back", calledback);
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();
		}
		/*
		 * B2. (1)create Configuration with different location and non-null
		 * props. (2) Register ManagedService. (3)setBundleLocation to the
		 * target bundle.
		 */
		trace("###################### B2. testManagedServiceRegistration2.");
		try {
			trace("The configuration with different location is being created ");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					neverlandLocation);
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B2");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");

			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			//assertFalse(
			//		"ManagedService must NOT be called back even if no configuration.",
			//		calledback);
			assertTrue(
					"ManagedService MUST be called back with null parameter when there is no configuration.",
					calledback);

			trace("The configuration is being updated ");
			props.put("StringKey", "stringvalue2");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			this.printoutPropertiesForDebug(sync);
			assertFalse("ManagedService must NOT be called back", calledback);

			/* The conf has non-null props. */
			trace("The configuration is being set to the target bundle");
			conf.setBundleLocation(bundle.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with the props. However,
			 * BJ thinks no. The implementator of this TCK (Ikuo) thinks TCK
			 * should not check it because it is unclear in the spec of version
			 * 1.3.
			 */
			// assertTrue("ManagedService must be called back", calledback);
			// assertFalse("ManagedService must NOT be called back",
			// calledback);
			if (calledback) {
				++count;
				assertNotNull("null props must be called back", sync.getProps());
				props = sync.getProps();
				assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
				assertEquals("pid", "stringvalue2", props.get("StringKey"));
				assertEquals("Size of props must be 2", 2, props.size());
			}

			trace("The configuration is being updated ");
			props.put("StringKey", "stringvalue3");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", "stringvalue3", props.get("StringKey"));
			assertNull("bundleLocation must be not included",
					props.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with null. However, BJ
			 * thinks no. The implementator of this TCK (Ikuo) thinks TCK should
			 * not check it strictly because it is unclear in the spec of
			 * version 1.3.
			 */
			// assertTrue("ManagedService must be called back", calledback);
			if (calledback) {
				++count;
				assertNull("called back with null props", sync.getProps());
			}
			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertFalse("ManagedService must Not be called back", calledback);

		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();
		}
		/*
		 * B3. (1)create Configuration with different location and null props.
		 * (2) Register ManagedService. (3)setBundleLocation to the target
		 * bundle.
		 */
		trace("###################### B3. testManagedServiceRegistration2.");
		try {
			trace("The configuration with different location is being created ");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					neverlandLocation);
			trace("The configuration is being updated ");
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");

			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			//assertFalse(
			//		"ManagedService must NOT be called back even if no configuration.",
			//		calledback);
			assertTrue(
					"ManagedService MUST be called back with null parameter when there is no configuration.",
					calledback);
			assertNull("called back with null props", sync.getProps());

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B3");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			this.printoutPropertiesForDebug(sync);
			assertFalse("ManagedService must NOT be called back", calledback);

			/* The conf has non-null props. */
			trace("The configuration is being set to the target bundle");
			conf.setBundleLocation(bundle.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-B3", props.get("StringKey"));
			assertNull("bundleLocation must be not included",
					props.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);

			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with null. However, BJ
			 * thinks no. The implementator of this TCK (Ikuo) thinks TCK should
			 * not check it strictly because it is unclear in the spec of
			 * version 1.3.
			 */
			// assertTrue("ManagedService must be called back", calledback);
			if (calledback) {
				++count;
				assertNull("called back with null props", sync.getProps());
			}
			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertFalse("ManagedService must NOT be called back", calledback);

		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();
		}
		/*
		 * C. Configuration Admin Service is stopped once. After a while, it
		 * restarts.
		 *
		 * 1.Register ManagedService in advance. 2.create Configuration with
		 * different location. 3.setBundleLocation to the target bundle.
		 */
		Bundle cmBundle = stopCmBundle();
		try {
			int count = 0;
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle);

			/* restart where no configuration. */
			trace("Wait for restart cm bundle.");
			sleep(SIGNAL_WAITING_TIME);
			startCmBundle(cmBundle);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService is Called back even if no configuration.",
					calledback);



			/* Create configuration and stop/startcmA.waitForService(500l). */
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle.getLocation());
			assertNotNull(conf);
            cmBundle = stopCmBundle();
			trace("Wait for restart cm bundle.");
			sleep(SIGNAL_WAITING_TIME);
			startCmBundle(cmBundle);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService is Called back.", calledback);
			//conf.delete();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();

			/*
			 * Create configuration with null location and
			 * stop/startcmA.waitForService(500l).
			 */
			conf = cmA.waitForService(500l).getConfiguration(bundlePid, null);
			assertNotNull(conf);
			cmBundle = stopCmBundle();
			trace("Wait for restart cm bundle.");
			sleep(SIGNAL_WAITING_TIME);
			startCmBundle(cmBundle);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue(
					"ManagedService is Called back even if configuration with null.",
					calledback);
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
			cmA.waitForService(500l).getConfiguration(bundlePid).delete();
		}
	}

	/**
	 * Register ManagedService in advance. ManagedService has multiple pids.
	 * Then create Configuration.
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistrationWithMultiplPIDs()
			throws Exception {
		this.setAppropriatePermission();

		try {
			trace("########## 1(array) testManagedServiceRegistrationWithMultiplPIDs");
			System.setProperty(
					org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
					org.osgi.test.cases.cm.shared.Constants.MODE_ARRAY);
			internalTestRegisterManagedServiceWithMultiplePIDs();

			trace("########## 1(list) testManagedServiceRegistrationWithMultiplPIDs");
			System.setProperty(
					org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
					org.osgi.test.cases.cm.shared.Constants.MODE_LIST);
			internalTestRegisterManagedServiceWithMultiplePIDs();

			trace("########## 1(set) testManagedServiceRegistrationWithMultiplPIDs");
			System.setProperty(
					org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
					org.osgi.test.cases.cm.shared.Constants.MODE_SET);
			internalTestRegisterManagedServiceWithMultiplePIDs();
		} finally {
			System.setProperty(
					org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
					org.osgi.test.cases.cm.shared.Constants.MODE_UNARY);
		}
	}

	private void internalTestRegisterManagedServiceWithMultiplePIDs()
			throws BundleException, IOException, InterruptedException {

		final String bundlePid1 = Util.createPid("bundlePid1");
		final String bundlePid2 = Util.createPid("bundlePid2");
		ServiceRegistration<Synchronizer> reg = null;
		Bundle bundle1 = null;
		try {
			bundle1 = bInstaller.installBundle("targetb1.jar", false);

			SynchronizerImpl sync = new SynchronizerImpl();
			trace("1 sync.getCount()=" + sync.getCount());
			reg = bc.registerService(Synchronizer.class,
					sync, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback);
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			if (!calledback)
				--count;
			trace("sync.getCount()=" + sync.getCount());
			trace("The configuration1 is being created");
			trace("sync.getCount()=" + sync.getCount());
			Configuration conf1 = cmA.waitForService(500l)
					.getConfiguration(bundlePid1,
					bundle1.getLocation());
			trace("sync.getCount()=" + sync.getCount());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration2 is being created");
			Configuration conf2 = cmA.waitForService(500l)
					.getConfiguration(bundlePid2,
					bundle1.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			trace("sync.getCount()=" + sync.getCount());
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration1 is being updated ");
			Dictionary<String,Object> props1 = new Hashtable<>();
			props1.put("StringKey1", "stringvalue1");
			conf1.update(props1);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			trace("sync.getCount()=" + sync.getCount());
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("null props must be called back", sync.getProps());
			Dictionary<String,Object> props = sync.getProps();
			assertEquals("pid", bundlePid1, props.get(Constants.SERVICE_PID));
			assertEquals("pid", "stringvalue1", props.get("StringKey1"));

			trace("The configuration2 is being updated ");
			Dictionary<String,Object> props2 = new Hashtable<>();
			props2.put("StringKey1", "stringvalue1");
			props2.put("StringKey2", "stringvalue2");
			conf2.update(props2);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			assertNotNull("null props must be called back", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid2, props.get(Constants.SERVICE_PID));
			assertEquals("pid", "stringvalue1", props.get("StringKey1"));
			assertEquals("pid", "stringvalue2", props.get("StringKey2"));
			assertEquals("Size of props must be 3", 3, props.size());

			trace("The configuration1 is being deleted ");
			conf1.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			trace("The configuration2 is being deleted ");
			conf2.delete();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
		}
	}

	/**
	 * Register ManagedService in advance. Then create Configuration.
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistrationDuplicatedTargets()
			throws Exception {
		this.setAppropriatePermission();
		Bundle bundle2 = bInstaller.installBundle("targetb2.jar", false);

		final String bundlePid = Util.createPid("bundlePid1");
		ServiceRegistration<Synchronizer> reg2 = null;

		/*
		 * A. One bundle registers duplicated ManagedService. Both of them must
		 * be called back.
		 */
		trace("################## A testManagedServiceRegistrationDuplicatedTargets()");
		try {
			SynchronizerImpl sync2 = new SynchronizerImpl();
			reg2 = bc.registerService(Synchronizer.class,
					sync2, propsForSync2);
			System.setProperty(
					org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
					"2");
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertTrue(
					"Both ManagedService MUST be called back even if no configuration.",
					calledback2);
			assertNull("called back with null props", sync2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle2.getLocation());
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 3);
			assertFalse("ManagedService must NOT be called back", calledback2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-A");
			conf.update(props);
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 4);
			assertTrue("Both ManagedService must be called back", calledback2);

			/* stop and restart target bundle */
			bundle2.stop();
			bundle2.start();
			trace("The target bundle has been stopped and re-started. Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 6);
			assertTrue("Both ManagedService must be called back", calledback2);
			// printoutPropertiesForDebug(sync);

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 8);
			assertTrue("Both ManagedService must be called back", calledback2);
			assertNull("null props must be called back", sync2.getProps());
		} finally {
			if (reg2 != null)
				reg2.unregister();
			reg2 = null;
			if (bundle2 != null && bundle2.getState() != Bundle.UNINSTALLED)
				bundle2.uninstall();
			bundle2 = null;
		}

		/*
		 * B1. (1)No Configuration. (2)two bundles register duplicated
		 * ManagedService. ==> Only one, firstly registered, must be called
		 * back.
		 */
		trace("################## B1 testManagedServiceRegistrationDuplicatedTargets()");

		reg2 = null;
		ServiceRegistration<Synchronizer> reg1 = null;
		Bundle bundle1 = bInstaller.installBundle("targetb1.jar", false);
		System.setProperty(
				org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
				"1");
		bundle2 = bInstaller.installBundle("targetb2.jar", false);
		try {
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = bc.registerService(Synchronizer.class,
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 1);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback2);
			assertNull("null props must be called back", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = bc.registerService(Synchronizer.class,
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 1);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback1);

			trace("The configuration is being created");
			Configuration[] confs = cmA.waitForService(500l)
					.listConfigurations(null);
            assertTrue("confs must be empty:", confs == null
                || (existingConfigs.size() > 0 && confs.length == existingConfigs.size()));
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle2.getLocation());
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 2);
			this.printoutPropertiesForDebug(sync2);

			assertFalse("ManagedService must NOT be called back", calledback2);
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertFalse("ManagedService must NOT be called back", calledback1);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B1");
			conf.update(props);
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertFalse("ManagedService must NOT be called back", calledback1);

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 3);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			assertNull("null props must be called back", sync2.getProps());
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertFalse("ManagedService must NOT be called back", calledback1);

		} finally {
			if (reg1 != null)
				reg1.unregister();
			reg1 = null;
			if (reg2 != null)
				reg2.unregister();
			reg2 = null;
			if (bundle2 != null && bundle2.getState() != Bundle.UNINSTALLED)
				bundle2.uninstall();
			bundle2 = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;

		}

		/*
		 * B2. (1) Create new Conf with null props. (2)two bundles register
		 * duplicated ManagedService. ==> Only one, firstly registered, must be
		 * called back.
		 */
		trace("################## B2 testManagedServiceRegistrationDuplicatedTargets()");

		reg2 = null;
		reg1 = null;
		bundle1 = bInstaller.installBundle("targetb1.jar", false);
		System.setProperty(
				org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
				"1");
		bundle2 = bInstaller.installBundle("targetb2.jar", false);
		try {
			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle2.getLocation());
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = bc.registerService(Synchronizer.class,
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME,
					++count2);
			assertTrue(
					"ManagedService MUST be called back even if configuration has no props.",
					calledback2);
			assertNull("null props must be called back", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = bc.registerService(Synchronizer.class,
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count1 = 0;
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME,
					++count1);
			//assertFalse("ManagedService MUST NOT be called back even.",
			//		calledback1);
			assertTrue(
					"ManagedService MUST be called back even if deffirent bound location.",
					calledback1);
			assertNull("null props must be called back", sync1.getProps());

			Configuration[] confs = cmA.waitForService(500l)
					.listConfigurations(null);
            assertTrue("confs must be empty:", confs == null
                || (existingConfigs.size() > 0 && confs.length == existingConfigs.size()));

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B2");
			conf.update(props);
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, count1 + 1);
			assertFalse("ManagedService must NOT be called back", calledback1);

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			assertNull("null props must be called back", sync2.getProps());
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, count1 + 1);
			assertFalse("ManagedService must NOT be called back", calledback1);

		} finally {
			if (reg1 != null)
				reg1.unregister();
			reg1 = null;
			if (reg2 != null)
				reg2.unregister();
			reg2 = null;
			if (bundle2 != null && bundle2.getState() != Bundle.UNINSTALLED)
				bundle2.uninstall();
			bundle2 = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
		}

		/*
		 * B3. (1) Create new Conf with non-null props. (2)two bundles register
		 * duplicated ManagedService. ==> Only one, firstly registered, must be
		 * called back.
		 */
		trace("################## B3 testManagedServiceRegistrationDuplicatedTargets()");

		reg2 = null;
		reg1 = null;
		bundle1 = bInstaller.installBundle("targetb1.jar", false);
		System.setProperty(
				org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
				"1");
		bundle2 = bInstaller.installBundle("targetb2.jar", false);
		try {
			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(bundlePid,
					bundle2.getLocation());
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", getName() + "-B3");
			conf.update(props);
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = bc.registerService(Synchronizer.class,
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME,
					++count2);
			assertTrue("ManagedService MUST be called back.", calledback2);
			assertNotNull("called back with null props", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = bc.registerService(Synchronizer.class,
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count1 = 0;
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME,
					++count1);
			//assertFalse("ManagedService MUST NOT be called back.", calledback1);
			assertTrue(
					"ManagedService MUST be called back even if deffirent bound location.",
					calledback1);
			assertNull("null props must be called back", sync1.getProps());

			Configuration[] confs = cmA.waitForService(500l)
					.listConfigurations(null);
			assertNotNull("confs must NOT be empty:", confs);

			trace("The configuration is being updated ");
			props.put("StringKey2", "stringvalue2");
			conf.update(props);
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, count1 + 1);
			assertFalse("ManagedService must NOT be called back", calledback1);

			trace("The configuration is being deleted ");
			conf.delete();
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, ++count2);
			assertTrue("ManagedService firstly registered must be called back",
					calledback2);
			assertNull("null props must be called back", sync2.getProps());
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, count1 + 1);
			assertFalse("ManagedService must NOT be called back", calledback1);

		} finally {
			if (reg1 != null)
				reg1.unregister();
			reg1 = null;
			if (reg2 != null)
				reg2.unregister();
			reg2 = null;
			if (bundle2 != null && bundle2.getState() != Bundle.UNINSTALLED)
				bundle2.uninstall();
			bundle2 = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
		}
	}

    private Bundle stopCmBundle() {
		ServiceReference<ConfigurationAdmin> reference = this.bc
				.getServiceReference(ConfigurationAdmin.class);
        Bundle cmBundle = reference.getBundle();
        try {
            cmBundle.stop();
        } catch (BundleException be) {
            fail("Error stopping CM Bundle: " + be);
        }
        return cmBundle;
    }



    private void startCmBundle(Bundle cmBundle) {
        try {
            cmBundle.start();
        } catch (BundleException be) {
            fail("Error starting CM Bundle: " + be);
        }
    }

	private Dictionary<String,Object> getManagedProperties(String pid)
			throws Exception {
		Semaphore semaphore = new Semaphore(0);
		ManagedServiceImpl ms = createManagedService(pid, semaphore).getValue();
		semaphore.acquire();
		return ms.getProperties();
	}

	/**
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getProperties()
	 * @throws Exception
	 */
	@Test
	public void testManagedProperties() throws Exception {
		String pid = Util.createPid("somepid");
		/* Set up the configuration */
		Configuration conf = cmA.waitForService(500l).getConfiguration(pid);
		/* Put all legal types in the properties and update */
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("StringKey", getName());
		props.put("IntegerKey", Integer.valueOf(12));
		props.put("LongKey", Long.valueOf(-29));
		props.put("FloatKey", Float.valueOf(921.14f));
		props.put("DoubleKey", Double.valueOf(1827.234));
		props.put("ByteKey", Byte.valueOf((byte) 127));
		props.put("ShortKey", Short.valueOf((short) 1));
		// props.put("BigIntegerKey", new BigInteger("123"));
		// props.put("BigDecimalkey", new BigDecimal(9872.7643));
		props.put("CharacterKey", Character.valueOf('N'));
		props.put("BooleanKey", Boolean.valueOf(true));

		Collection<Object> v = new ArrayList<>();
		v.add(getName());
		// ### is now invalid ....
		// v.addElement(Integer.valueOf(12));
		// v.addElement(Long.valueOf(-29));
		// v.addElement(Float.valueOf(921.14));
		// v.addElement(Double.valueOf(1827.234));
		// v.addElement(Byte.valueOf((byte) 127));
		// v.addElement(Short.valueOf((short) 1));
		// v.addElement(new BigInteger("123"));
		// v.addElement(new BigDecimal(9872.7643));
		// v.addElement(Character.valueOf('N'));
		// v.addElement(Boolean.valueOf(true));
		// v.addElement(new String[] {"firststring", "secondstring"});
		// ### end invalid
		props.put("collectionkey", v);
		props.put("StringArray", new String[] { "string1", "string2" });
		props.put("IntegerArray", new Integer[] { Integer.valueOf(1),
				Integer.valueOf(2) });
		props.put("LongArray", new Long[] { Long.valueOf(1), Long.valueOf(2) });
		props.put("FloatArray", new Float[] {
				Float.valueOf(1.1f), Float.valueOf(2.2f)
		});
		props.put("DoubleArray", new Double[] { Double.valueOf(1.1),
				Double.valueOf(2.2) });
		props.put("ByteArray", new Byte[] { Byte.valueOf((byte) -1),
				Byte.valueOf((byte) -2) });
		props.put("ShortArray", new Short[] { Short.valueOf((short) 1),
				Short.valueOf((short) 2) });
		// props.put("BigIntegerArray", new BigInteger[] {
		// new BigInteger("1"), new BigInteger("2")
		// }
		//
		// );
		// props.put("BigDecimalArray", new BigDecimal[] {
		// new BigDecimal(1.1), new BigDecimal(2.2)
		// }
		//
		// );
		props.put("CharacterArray", new Character[] { Character.valueOf('N'),
				Character.valueOf('O') });
		props.put("BooleanArray", new Boolean[] { Boolean.valueOf(true),
				Boolean.valueOf(false) });

		// ### invalid
		// Vector v1 = new Vector();
		// v1.addElement(new Vector());
		// v1.addElement("Anystring");
		// props.put("VectorArray", new Vector[] {v1, new Vector()});

		props.put("CAPITALkey", "CAPITALvalue");
		conf.update(props);
		/* Register a managed service and get the properties */
		Dictionary<String,Object> msprops = getManagedProperties(pid);
		/*
		 * Add the two properties added by the CM and then check for equality in
		 * the properties (including preserved case)
		 */
		props.put(Constants.SERVICE_PID, pid);
		// props.put(SERVICE_BUNDLE_LOCATION, "cm_TBC"); R3 does not include
		// service.bundleLocation anymore!
		DictionaryAssert.assertThat(props)
				.containsExactlyInAnyOrderEntriesOf(msprops);

		/* Check if the properties are case independent */
		String s = (String) msprops.get("sTringkeY");
		assertEquals("case independant properties", getName(), s);
		Hashtable<String,Object> illegalprops = new Hashtable<>();
		illegalprops.put("exception", new Exception());
		String message = "Exception is not a legal type";
		try {
			conf.update(illegalprops);
			/* A IllegalArgumentException should have been thrown */
			failException(message, IllegalArgumentException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, IllegalArgumentException.class, e);
		}

		/* TODO: Add more illegal types (inside collections etc) */
	}

	// @Test
	// public void testUpdatedProperties() throws Exception {
	// /* Put all legal types in the properties and update */
	// /* Get the properties again */
	// /* Check if the properties are equals */
	// /* Check if the properties have preserved the case */
	// /* Check if the properties are case independent */
	// }

	/**
	 * Test created Factory configuration without location.
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getPid()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.getBundleLocation()
	 * @throws Exception
	 */
	@Test
	public void testCreateFactoryConfiguration() throws Exception {
		commonTestCreateFactoryConfiguration(false, getLocation());
	}

	/**
	 * Test created Factory configuration with location.
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String,String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getPid()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.getBundleLocation()
	 * @throws Exception
	 */
	@Test
	public void testCreateFactoryConfigurationWithLocation() throws Exception {
		commonTestCreateFactoryConfiguration(true, neverlandLocation);
	}

	/**
	 * Test created Factory configuration with null location.
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String,String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getPid()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getProperties()
	 * @spec Configuration.getBundleLocation()
	 * @throws Exception
	 */
	@Test
	public void testCreateFactoryConfigurationWithNullLocation()
			throws Exception {
		commonTestCreateFactoryConfiguration(true, null);
	}

	/**
	 * Test creating a factory configuration with a name
	 * 
	 * @throws Exception
	 * @since 1.6
	 */
	@Test
	public void testGetFactoryConfigurationWithName() throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		final String factorypid = Util.createPid("factorypidforname");
		final List<String> pids = new ArrayList<>();
		final List<Configuration> configs = new ArrayList<>();

		this.setAppropriatePermission();
		pids.add(factorypid);
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = cmA.waitForService(500l)
					.getFactoryConfiguration(factorypid,
					String.valueOf(i), null);
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("val", String.valueOf(i));
			conf.update(props);
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid,
					conf.getFactoryPid());
			assertEquals("Correct pid", factorypid + "~" + String.valueOf(i),
					conf.getPid());
			assertEquals("Correct location", null,
					getBundleLocationForCompare(conf));

			pids.add(conf.getPid());
		}
		assertEquals(NUMBER_OF_CONFIGS, cmA.waitForService(500l)
				.listConfigurations(
						"(service.factoryPid=" + factorypid + ")").length);
		for (int i = 0; i < configs.size(); i++) {
			Configuration[] cfgs = cmA.waitForService(500l)
					.listConfigurations(
					"(service.pid=" + configs.get(i).getPid() + ")");
			assertEquals(1, cfgs.length);
			assertEquals(configs.get(i).getFactoryPid(),
					cfgs[0].getFactoryPid());
			assertEquals(configs.get(i).getPid(), cfgs[0].getPid());
		}
		// get factory configuration with same name again
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = cmA.waitForService(500l)
					.getFactoryConfiguration(factorypid,
					String.valueOf(i), null);
			assertEquals("Correct factory pid", factorypid,
					conf.getFactoryPid());
			assertEquals("Correct pid", factorypid + "~" + String.valueOf(i),
					conf.getPid());
			assertEquals(String.valueOf(i), conf.getProperties().get("val"));
		}
		// and verify count of factory configurations
		// (should still be NUMBER_OF_CONFIGS)
		assertEquals(NUMBER_OF_CONFIGS, cmA.waitForService(500l)
				.listConfigurations(
				"(service.factoryPid=" + factorypid + ")").length);

		// delete configurations
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = configs.get(i);
			conf.delete();
		}
		// ensure that no factory configurations are available anymore
		for (final String p : pids) {
			Configuration[] cfgs = cmA.waitForService(500l)
					.listConfigurations(
					"(service.pid=" + p + ")");
			assertNull(cfgs);
		}
		assertNull(cmA.waitForService(500l)
				.listConfigurations("(service.factoryPid=" + factorypid + ")"));
	}

	/**
	 * Test whether repeated gets of a factory config returns the same object
	 * 
	 * @throws Exception
	 * @since 1.6
	 */
	@Test
	public void testRepeatedGetFactoryConfigurationWithName() throws Exception {
		final String factorypid = Util.createPid("factorypidforname");
		final String factoryname = "aname";

		this.setAppropriatePermission();

		final Configuration conf1 = cmA.waitForService(500l)
				.getFactoryConfiguration(factorypid,
				factoryname, null);
		final Configuration conf2 = cmA.waitForService(500l)
				.getFactoryConfiguration(factorypid,
				factoryname, null);
		final Dictionary<String,Object> props1 = new Hashtable<>();
		props1.put("val", "1");
		conf1.update(props1);

		final Dictionary<String,Object> props2 = new Hashtable<>();
		props2.put("val", "2");
		conf2.update(props2);

		assertEquals("2",
				cmA.waitForService(500l)
						.getFactoryConfiguration(factorypid, factoryname, null)
						.getProperties()
						.get("val"));

		final Dictionary<String,Object> props22 = new Hashtable<>();
		props22.put("val", "22");
		conf2.update(props22);

		assertEquals("22",
				cmA.waitForService(500l)
						.getFactoryConfiguration(factorypid, factoryname, null)
						.getProperties()
						.get("val"));
	}

	private void commonTestCreateFactoryConfiguration(boolean withLocation,
			String location) throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		final String factorypid = Util.createPid("somefactorypid");
		final List<String> pids = new ArrayList<>();
		final List<Configuration> configs = new ArrayList<>();

		// Inappropriate Permission
		this.setInappropriatePermission();
		pids.add(factorypid);
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = null;
			if (withLocation) {
				/*
				 * Without appropriate ConfigurationPermission, create
				 * FactoryConfiguration with specfied location.
				 */
				String message = "try to create factory configuration without appropriate ConfigurationPermission.";
				try {
					conf = cmA.waitForService(500l)
							.createFactoryConfiguration(factorypid, location);
					/*
					 * A SecurityException should have been thrown if security
					 * is enabled
					 */
					if (System.getSecurityManager() != null)
						failException(message, SecurityException.class);
				} catch (AssertionFailedError e) {
					throw e;
				} catch (Throwable e) {
					/* Check that we got the correct exception */
					assertException(message, SecurityException.class, e);
					/*
					 * A SecurityException should not have been thrown if
					 * security is not enabled
					 */
					if (System.getSecurityManager() == null)
						fail("Security is not enabled", e);
				}
				continue;
			} else {
				/*
				 * Even appropriate ConfigurationPermission,
				 * createFactoryConfiguration(factorypid) must be succeed
				 */
				conf = cmA.waitForService(500l)
						.createFactoryConfiguration(factorypid);
			}
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid,
					conf.getFactoryPid());
			assertNull("No properties", conf.getProperties());
			assertEquals("Correct location", location,
					getBundleLocationForCompare(conf));
			/* Add the pid to the list */
			pids.add(conf.getPid());
		}
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = configs.get(i);
			conf.delete();
		}

		// Appropriate Permission
		pids.clear();
		configs.clear();
		this.setAppropriatePermission();
		pids.add(factorypid);
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = null;
			if (withLocation) {
				conf = cmA.waitForService(500l)
						.createFactoryConfiguration(factorypid, location);
			} else {
				conf = cmA.waitForService(500l)
						.createFactoryConfiguration(factorypid);
			}
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid,
					conf.getFactoryPid());
			assertNull("No properties", conf.getProperties());
			assertEquals("Correct location", location,
					this.getBundleLocationForCompare(conf));
			/* Add the pid to the list */
			pids.add(conf.getPid());
		}
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = configs.get(i);
			conf.delete();
		}
	}

	private String getBundleLocationForCompare(Configuration conf)
			throws BundleException {
		String location = null;
        if (this.permissionFlag) {
            try {
                location = conf.getBundleLocation();
            } catch (SecurityException se) {
                // Bug 2539: Need to be hard on granting appropriate permission
				System.out.println("Temporarily grant CONFIGURE("
						+ thisLocation()
                    + ") to get location of configuration " + conf.getPid());
				List<PermissionInfo> perms = getBundlePermission(thisBundle());
                try {
					setCPtoBundle("*", ConfigurationPermission.CONFIGURE,
							thisBundle());
                    location = conf.getBundleLocation();
                } catch (SecurityException se2) {
                    throw se;
                } finally {
					System.out.println("Resetting permissions for "
							+ thisLocation() + " to: " + perms);
					resetBundlePermission(thisBundle(), perms);
                }
            }
        } else {
			this.setAppropriatePermission();
			location = conf.getBundleLocation();
			this.setInappropriatePermission();
		}
		return location;

	}

	@Test
	public void testFactoryConfigurationCollision()
			throws IOException, InvalidSyntaxException, BundleException,
			InterruptedException {
        final String factoryPid = Util.createPid("factoryPid1");

		final Configuration cf = cmA.waitForService(500l)
				.createFactoryConfiguration(factoryPid, null);
        assertNotNull( cf );
        final String pid = cf.getPid();

		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		Bundle bundle = bInstaller.installBundle("bundleT1.jar", false);
        try
        {
            SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class, sync1_1,
					propsForSyncF1_1));

            this.startTargetBundle(bundle);
			trace("Wait for signal.");
			int count1_1 = 0;
			assertNoCallback(sync1_1, count1_1);

            assertNotNull( "Configuration must have PID", pid );
            assertEquals( "Factory configuration must have requested factory PID", factoryPid, cf.getFactoryPid() );

            // assert getConfiguration returns the same configurtion
			final Configuration c1 = cmA.waitForService(500l)
					.getConfiguration(pid, null);
            assertEquals( "getConfiguration must retrieve required PID", pid, c1.getPid() );
            assertEquals( "getConfiguration must retrieve new factory configuration", factoryPid, c1.getFactoryPid() );
            assertNull( "Configuration must not have properties", c1.getProperties() );

            assertNoCallback(sync1_1, count1_1);

            // restart config admin and verify getConfiguration persisted
            // the new factory configuration as such
            restartCM();
			assertNotNull("Config Admin Service missing", cmA.getService());

			assertNoCallback(sync1_1, count1_1);

			final Configuration c2 = cmA.waitForService(500l)
					.getConfiguration(pid, null);
            assertEquals( "getConfiguration must retrieve required PID", pid, c2.getPid() );
            assertEquals( "getConfiguration must retrieve new factory configuration from persistence", factoryPid, c2.getFactoryPid() );
            assertNull( "Configuration must not have properties", c2.getProperties() );

			Dictionary<String,Object> props = new Hashtable<>();
            props.put("StringKey", "stringvalue");
            c2.update( props );

            count1_1 = assertCallback(sync1_1, count1_1);
			props = sync1_1.getProps();

            assertEquals( "stringvalue", props.get( "StringKey" ) );

			final Configuration[] cfs = cmA.waitForService(500l)
					.listConfigurations(
							"(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
                + factoryPid + ")" );
            assertNotNull( "Expect at least one configuration", cfs );
            assertEquals( "Expect exactly one configuration", 1, cfs.length );
            assertEquals( cf.getPid(), cfs[0].getPid() );
            assertEquals( cf.getFactoryPid(), cfs[0].getFactoryPid() );
        }
        finally
        {
            // make sure no configuration survives ...
			cmA.waitForService(500l).getConfiguration(pid, null).delete();
        }
    }

	/**
	 * Test Managed Service Factory.
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getPid()
	 * @spec Configuration.getFactoryPid()
	 * @spec Configuration.getProperties()
	 * @spec ManagedServiceFactory.updated(String,Dictionary)
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceFactory() throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		String factorypid = Util.createPid("somefactorypid");
		Hashtable<String,Object> configs = new Hashtable<>();
		// Create some factory configurations
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(factorypid);
			Hashtable<String,Object> ht = new Hashtable<>();
			ht.put("test.field", i + "");
			conf.update(ht);
			trace("pid: " + conf.getPid());
			configs.put(conf.getPid(), conf);
		}
		// Create some more factory configurations using names
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = cmA.waitForService(500l)
					.getFactoryConfiguration(factorypid,
					String.valueOf(i));
			Hashtable<String,Object> ht = new Hashtable<>();
			ht.put("test.field", String.valueOf(NUMBER_OF_CONFIGS + i) + "");
			conf.update(ht);
			trace("pid: " + conf.getPid());
			configs.put(conf.getPid(), conf);
		}
		try {
			Semaphore semaphore = new Semaphore(0);
			// Register a factory
			ManagedServiceFactoryImpl msf = new ManagedServiceFactoryImpl(
					"msf", "testprop", semaphore);
			Hashtable<String,Object> properties = new Hashtable<>();
			properties.put(Constants.SERVICE_PID, factorypid);
			properties.put(ConfigurationAdmin.SERVICE_FACTORYPID, factorypid);
			bc.registerService(ManagedServiceFactory.class.getName(), msf,
					properties);
			for (int i = 0; i < 2 * NUMBER_OF_CONFIGS; i++) {
				trace("Wait for signal #" + i);
				semaphore.acquire();
				trace("Signal #" + i + " arrived");
			}
			trace("All signals have arrived");
		} finally {
			Enumeration<String> keys = configs.keys();
			while (keys.hasMoreElements()) {
				Configuration conf = (Configuration) configs.get(keys
						.nextElement());
				conf.delete();
			}
		}

	}

	/**
	 * Tests a configuration listener update event notification from a
	 * configuration service. The event data should match the data that
	 * originated the event (pid, factorypid...).
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationEvent.getPid()
	 * @spec ConfigurationEvent.getFactoryPid()
	 * @spec ConfigurationEvent.getReference()
	 * @spec ConfigurationEvent.getType()
	 * @spec Configuration.update(Dictionary)
	 * @throws Exception
	 */
	@Test
	public void testUpdateConfigEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cmA.waitForService(500l).getConfiguration(pid);
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);

			conf.update(props);
			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());
			assertNull("Config Factory event pid null", cl.getFactoryPid());
			assertNotNull("Config event reference null", cl.getReference());
			ConfigurationAdmin admin = bc
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration config = admin.getConfiguration(cl.getPid());
				assertNotNull("Configuration from event", config);
				DictionaryAssert.assertThat(conf.getProperties())
						.containsExactlyInAnyOrderEntriesOf(
								config.getProperties());

			} finally {
				bc.ungetService(cl.getReference());
			}

	}

	/**
	 * Tests a configuration listener update event notification from a
	 * configuration service factory. The event data should match the data that
	 * originated the event (pid, factorypid...).
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.getPid()
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationEvent.getPid()
	 * @spec ConfigurationEvent.getFactoryPid()
	 * @spec ConfigurationEvent.getReference()
	 * @spec ConfigurationEvent.getType()
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testUpdateConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cmA.waitForService(500l)
				.createFactoryConfiguration(factorypid);
		String pid = conf.getPid();
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten"
				+ "the config factory update");

			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());
			assertEquals("Config Factory event pid match", factorypid,
					cl.getFactoryPid());
			assertNotNull("Config Factory event reference null",
					cl.getReference());
			ConfigurationAdmin admin = bc
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration config = admin.getConfiguration(cl.getPid());
				assertNotNull("Configuration from event", config);
				DictionaryAssert.assertThat(conf.getProperties())
						.containsExactlyInAnyOrderEntriesOf(
								config.getProperties());
			} finally {
				bc.ungetService(cl.getReference());
			}

	}

	/**
	 * Tests a configuration listener delete event notification from a
	 * configuration service. The deleted <code>Configuration</code> should be
	 * empty (<code>ConfigurationAdmin.listConfigurations(null)</code> must not
	 * contain the deleted <code>Configuration</code>).
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationEvent.getPid()
	 * @spec ConfigurationEvent.getFactoryPid()
	 * @spec ConfigurationEvent.getReference()
	 * @spec ConfigurationEvent.getType()
	 * @spec ConfigurationAdmin.listConfigurations(String)
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testDeleteConfigEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cmA.waitForService(500l).getConfiguration(pid);
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");

			cl = createConfigurationListener(synchronizer, 2);
			conf.update(props);
			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));

			conf.delete();

			trace("Wait until the ConfigurationListener has gotten the delete");

			assertTrue("Delete done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME, 2));
			assertEquals("Config event pid match", pid, cl.getPid(2));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(2));
			assertNull("Config Factory event pid null", cl.getFactoryPid(2));
			assertNotNull("Config Factory event reference null",
					cl.getReference(2));
			try {
				ConfigurationAdmin admin = bc
						.getService(cl.getReference(2));
				assertNotNull("Configuration Admin from event", admin);
				Configuration[] configs = admin
						.listConfigurations("(service.pid=" + pid + ")");
				assertNull("The configuration exists in CM!", configs);
			} finally {
				bc.ungetService(cl.getReference(2));
			}

	}

	/**
	 * Tests a configuration listener delete event notification from a
	 * configuration service factory. The deleted <code>Configuration</code>
	 * should be empty (
	 * <code>ConfigurationAdmin.listConfigurations(null)</code> must not contain
	 * the deleted <code>Configuration</code>).
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationEvent.getPid()
	 * @spec ConfigurationEvent.getFactoryPid()
	 * @spec ConfigurationEvent.getReference()
	 * @spec ConfigurationEvent.getType()
	 * @spec ConfigurationAdmin.listConfigurations(String)
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testDeleteConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cmA.waitForService(500l)
				.createFactoryConfiguration(factorypid);
		String pid = conf.getPid();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		conf.delete();
		trace("Wait until the ConfigurationListener has gotten"
				+ "the config factory delete");

			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType());
			assertEquals("Config Factory event pid match", factorypid,
					cl.getFactoryPid());
			assertNotNull("Config Factory event reference null",
					cl.getReference());
			ConfigurationAdmin admin = bc
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration[] configs = admin
						.listConfigurations("(service.factoryPid=" + factorypid
								+ ")");
				assertNull("The configuration exists in CM!", configs);
			} finally {
				bc.ungetService(cl.getReference());
			}

	}

	/**
	 * Tests a configuration listener permission. The bundle does not have
	 * <code>ServicePermission[ConfigurationListener,REGISTER]</code> and will
	 * try to register a <code>ConfigurationListener</code>. An exception must
	 * be thrown.
	 *
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 *
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testConfigListenerPermission() throws Exception {
		Bundle tb = bInstaller.installBundle("tb1.jar", false);
		String message = "registering config listener without permission";
		try {
			tb.start();
			/* A BundleException should have been thrown if security is enabled */
			if (System.getSecurityManager() != null)
				failException(message, BundleException.class);
		} catch (BundleException e) {
			/* Check that we got the correct exception */
			assertException(message, BundleException.class, e);
			/*
			 * A BundleException should not have been thrown if security is not
			 * enabled
			 */
			if (System.getSecurityManager() == null)
				fail("Security is not enabled", e);
		} finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests an event from a different bundle. The
	 * <code>ConfigurationListener</code> should get the event even if it was
	 * generated from a different bundle.
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.getPid()
	 * @spec Configuration.delete()
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationEvent.getPid()
	 * @spec ConfigurationEvent.getFactoryPid()
	 * @spec ConfigurationEvent.getReference()
	 * @spec ConfigurationEvent.getType()
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testConfigEventFromDifferentBundle() throws Exception {
		trace("Create and register a new ConfigurationListener");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		ConfigurationListenerImpl cl = createConfigurationListener(
				synchronizer, 4);
		Bundle tb = bInstaller.installBundle("tb2.jar", false);
		tb.start();
		trace("Wait until the ConfigurationListener has gotten the update");

		try {
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", PACKAGE + ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX,
					cl.getPid(1));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(1));
			assertNull("Config Factory event pid null", cl.getFactoryPid(1));

			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME, 2));
			assertEquals("Config event pid match", PACKAGE + ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX,
					cl.getPid(2));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(2));
			assertNull("Config Factory event pid null", cl.getFactoryPid(2));

			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME, 3));
			assertEquals("Config event facotory pid match", PACKAGE
					+ ".tb2factorypid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX,
					cl.getFactoryPid(3));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(3));

			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME, 4));
			assertEquals("Config event factory pid match", PACKAGE
					+ ".tb2factorypid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX,
					cl.getFactoryPid(4));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(4));

		} finally {
			tb.uninstall();
		}
	}



	/** *** Helper methods **** */
	/**
	 * creates and registers a configuration listener that expects just one
	 * event.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			SynchronizerImpl synchronizer) throws Exception {
		return createConfigurationListener(synchronizer, 1);
	}

	/**
	 * creates and registers a configuration listener that expects eventCount
	 * events.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			SynchronizerImpl synchronizer, int eventCount) throws Exception {
		ConfigurationListenerImpl listener = new ConfigurationListenerImpl(
				synchronizer, eventCount);
		bc.registerService(ConfigurationListener.class.getName(), listener,
				null);
		return listener;
	}

	private Entry<ServiceRegistration,ManagedServiceImpl> createManagedService(
			String pid, Semaphore s)
			throws Exception {
		ManagedServiceImpl ms = new ManagedServiceImpl(s);
		Hashtable<String,Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_PID, pid);
		/* TODO: Testa registered service.pid with other String */
		ServiceRegistration< ? > sreg = bc
				.registerService(ManagedService.class.getName(), ms, props);
		trace("ManagedService is registered with pid:" + pid);
		return new AbstractMap.SimpleEntry(sreg, ms);
	}

	private void checkConfiguration(Configuration conf, String message,
			String pid, String location) throws BundleException {
		assertNotNull(message, conf);
		assertEquals("Pid", pid, conf.getPid());
		assertNull("FactoryPid", conf.getFactoryPid());
		assertNull("Properties", conf.getProperties());
		assertEquals("Location", location,
				this.getBundleLocationForCompare(conf));

	}

	/**
	 * See if a configuration is part of a list.
	 *
	 * @return index of the list if the configuration is a part of the
	 *         list.Otherwise, return -1nd.
	 */
	private int isPartOf(Configuration theConf, List<Configuration> configs) {

		for (int i = 0; i < configs.size(); i++)
			if (equals(configs.get(i), theConf))
				return i;

		return -1;
	}

	/**
	 * Compares two Configurations for equality. Configuration.equals() is not
	 * specified in the spec, so this is a helper method that compares pids.
	 * <p>
	 * Two Configurations are considered equal if the got the same pid or if
	 * both are null.
	 */
	private boolean equals(Configuration c1, Configuration c2) {
		boolean result = false;
		/* If both are null, they are equal */
		if ((c1 == null) && (c2 == null)) {
			result = true;
		}
		/* If none of them is null, and got the same pid, they are equal */
		if ((c1 != null) && (c2 != null)) {
			result = c1.getPid().equals(c2.getPid());
		}
		return result;
	}

	public final static String PACKAGE = "org.osgi.test.cases.cmA.waitForService(500l).tbc";

	private String getLocation() {
		return bc.getBundle().getLocation();
	}


    class SyncEventListener implements SynchronousConfigurationListener {

        private final Synchronizer sync;

        public SyncEventListener(final Synchronizer sync) {
            this.sync = sync;
        }

        public void configurationEvent(ConfigurationEvent event) {
            this.sync.signal();
        }

    }

	/*
	 * Shigekuni KONDO, Ikuo YAMASAKI, (Yushi Kuroda), NTT Corporation adds
	 * tests for specification version 1.4
	 */
	private final String locationA = "location.a";
	private final String locationB = "location.b";
	private final String regionA = "?RegionA";
	private String regionB = "?RegionB";

	@Test
	public void testGetConfigurationWithLocation_2_01() throws Exception {
		final String locationOld = null;
		this.internalGetConfigurationWithLocation_2_02To08(1, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_02() throws Exception {
		final String locationOld = locationA;
		this.internalGetConfigurationWithLocation_2_02To08(2, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_03() throws Exception {
		final String locationOld = locationA + "*";
		this.internalGetConfigurationWithLocation_2_02To08(3, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_04() throws Exception {
		final String locationOld = locationB;
		this.internalGetConfigurationWithLocation_2_02To08(4, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_06() throws Exception {
		final String locationOld = "?*";
		this.internalGetConfigurationWithLocation_2_02To08(6, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_07() throws Exception {
		final String locationOld = regionA;
		this.internalGetConfigurationWithLocation_2_02To08(7, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_08() throws Exception {
		final String locationOld = regionB;
		this.internalGetConfigurationWithLocation_2_02To08(8, locationOld);
	}

	public void internalGetConfigurationWithLocation_2_02To08(final int minor,
			final String locationOld)
			throws BundleException, IOException, InterruptedException {

		final String header = "testGetConfigurationWithLocation_2_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		this.setAppropriatePermission();
		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		Dictionary<String,Object> props = new Hashtable<>();

		// Get a brand new configuration
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
		assertEquals("Location", locationOld,
				this.getBundleLocationForCompare(conf));
		assertNull("Configuration props MUST be null", conf.getProperties());

		// micro 2
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
		assertEquals("Location", locationOld,
				this.getBundleLocationForCompare(conf));
		assertNull("Configuration props MUST be null", conf.getProperties());

		// micro 1
		testId = traceTestId(header, ++micro);
		props.put("StringKey", "stringvalue");
		conf.update(props);
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
		assertEquals("Location", locationOld,
				this.getBundleLocationForCompare(conf));
		assertEquals("Check Configuration props", 2, conf.getProperties()
				.size());
		assertEquals("Check Configuration props", "stringvalue", conf
				.getProperties().get("StringKey"));
		conf.delete();

		resetPermissions();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
		assertEquals("Location", locationOld,
				this.getBundleLocationForCompare(conf));
		assertNull("Configuration props MUST be null", conf.getProperties());

		// micro 4
		testId = traceTestId(header, ++micro);
		setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		if (minor == 2) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}

		// micro 3
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 2) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			assertEquals("Check Configuration props", "stringvalue", conf
					.getProperties().get("StringKey"));
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}
		conf.delete();

		resetPermissions();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// micro 8
		testId = traceTestId(header, ++micro);
		setCPtoBundle("?*", ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 6 || minor == 7 || minor == 8) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}

		// micro 7
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 6 || minor == 7 || minor == 8) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			assertEquals("Check Configuration props", "stringvalue", conf
					.getProperties().get("StringKey"));
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}
		conf.delete();

		resetPermissions();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// micro 10
		testId = traceTestId(header, ++micro);
		setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 7) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}

		// micro 9
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 7) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			assertEquals("Check Configuration props", "stringvalue", conf
					.getProperties().get("StringKey"));
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1,
					locationOld);
		}
		conf.delete();

		resetPermissions();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// micro 12
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
		assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, locationOld);

		// micro 11
		testId = traceTestId(header, ++micro);
		conf.update(props);
		assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, locationOld);
		conf.delete();

		resetPermissions();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// micro 13
		testId = traceTestId(header, ++micro);
		List<String> cList = new ArrayList<>();
		cList.add("?");
		cList.add(regionA);
		setCPListtoBundle(cList, null, thisBundle());
		conf.update(props);
		if (minor == 7) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, "?");
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			assertEquals("Check Configuration props", "stringvalue", conf
					.getProperties().get("StringKey"));
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, "?");
		}
		conf.delete();

		if (minor == 2) {
			resetPermissions();
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());

			// micro 16
			testId = traceTestId(header, ++micro);
			setCPtoBundle(null, null, thisBundle());
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());

			// micro 15
			testId = traceTestId(header, ++micro);
			conf.update(props);
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			assertEquals("Check Configuration props", "stringvalue", conf
					.getProperties().get("StringKey"));
			conf.delete();
		}
	}

	// TODO confirm
	@Test
	public void testGetConfigurationWithLocation_2_09() throws Exception {
		final String locationOld = null;
		this.internalGetConfigurationWithLocation_2_09To13(9, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_10() throws Exception {
		final String locationOld = locationA;
		this.internalGetConfigurationWithLocation_2_09To13(10, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_12() throws Exception {
		final String locationOld = regionA;
		this.internalGetConfigurationWithLocation_2_09To13(12, locationOld);
	}

	@Test
	public void testGetConfigurationWithLocation_2_13() throws Exception {
		final String locationOld = regionA + "*";
		this.internalGetConfigurationWithLocation_2_09To13(13, locationOld);
	}

	public void internalGetConfigurationWithLocation_2_09To13(final int minor,
			final String location)
			throws BundleException, IOException, InterruptedException {
		final String header = "testGetConfigurationWithLocation_2_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		this.setAppropriatePermission();
		final String pid1 = Util.createPid("1");
		Configuration conf = null;

		// 1
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
		conf = cmA.waitForService(500l).getConfiguration(pid1, location);
		assertEquals("Location", location,
				this.getBundleLocationForCompare(conf));
		assertNull("Configuration props MUST be null", conf.getProperties());
		conf.delete();

		// 2
		testId = traceTestId(header, ++micro);
		setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		if (minor == 10) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, location);
			assertEquals("Location", location,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);
		}

		// 3
		testId = traceTestId(header, ++micro);
		setCPtoBundle(locationB, ConfigurationPermission.CONFIGURE,
				thisBundle());
		assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);

		// 4
		testId = traceTestId(header, ++micro);
		setCPtoBundle("?", ConfigurationPermission.CONFIGURE, thisBundle());
		assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);

		// 5
		testId = traceTestId(header, ++micro);
		setCPtoBundle("?*", ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 12 || minor == 13) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, location);
			assertEquals("Location", location,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);
		}

		// 6
		testId = traceTestId(header, ++micro);
		setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 12) {
			conf = cmA.waitForService(500l).getConfiguration(pid1, location);
			assertEquals("Location", location,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();
		} else {
			assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);
		}

		// 7
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
		assertThrowsSEbyGetConfigurationWithLocation(testId, pid1, location);

		if (minor == 10) {
			// thisLocation no permission
			testId = traceTestId(header, ++micro);
			setCPtoBundle(null, null, thisBundle());
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();

			// thisLocation with CONFIGURE
			testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();

			// thisLocation with TARGET
			testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
			conf.delete();
		}
	}

	private void setCPtoBundle(String name, String action, Bundle bundle)
			throws BundleException {
		this.setCPtoBundle(name, action, bundle, true);
	}

	private void setCPtoBundle(String name, String action, Bundle bundle,
			boolean resetAll) throws BundleException {
		if (resetAll)
			this.resetPermissions();
		list.clear();

		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");

		if (name != null && action != null)
			add(list, CP, name, action);
		add(list, AP, "*", "*");
		permissionFlag = true;
		this.setBundlePermission(bundle, list);
	}

	private void resetBundlePermission(Bundle b, List<PermissionInfo> list)
			throws BundleException {
        this.resetPermissions();
        if (list != null) {
            this.setBundlePermission(b, list);
        }
    }

	private String traceTestId(final String header, int micro) {
		String testId = header + String.valueOf(micro);
		trace(testId);
		return testId;
	}

	private void assertThrowsSEbyGetConfigurationWithLocation(String testId,
			String pid, String location) {
		String message = testId
				+ ":try to get configuration without appropriate ConfigurationPermission.";
		try {
			cmA.waitForService(500l).getConfiguration(pid, location);
			// A SecurityException should have been thrown
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// Check that we got the correct exception
			assertException(message, SecurityException.class, e);
		}

	}

	@Test
	public void testGetConfiguration_3_01() throws Exception {
		final String locationOld = null;
		this.internalGetConfiguration_3_01To07(1, locationOld);
	}

	@Test
	public void testGetConfiguration_3_02() throws Exception {
		final String locationOld = locationA;
		this.internalGetConfiguration_3_01To07(2, locationOld);
	}

	@Test
	public void testGetConfiguration_3_03() throws Exception {
		final String locationOld = locationA + "*";
		this.internalGetConfiguration_3_01To07(3, locationOld);
	}

	@Test
	public void testGetConfiguration_3_04() throws Exception {
		final String locationOld = locationB;
		this.internalGetConfiguration_3_01To07(4, locationOld);
	}

	@Test
	public void testGetConfiguration_3_06() throws Exception {
		final String locationOld = regionA;
		this.internalGetConfiguration_3_01To07(6, locationOld);
	}

	@Test
	public void testGetConfiguration_3_07() throws Exception {
		final String locationOld = regionA + "*";
		this.internalGetConfiguration_3_01To07(7, locationOld);
	}

	public void internalGetConfiguration_3_01To07(int minor, String locationOld)
			throws BundleException, IOException, InterruptedException {
		final String header = "testGetConfiguration_3_" + String.valueOf(minor)
				+ "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		Dictionary<String,Object> props = new Hashtable<>();

		this.setAppropriatePermission();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
		String message = testId
				+ ":try to get configuration with appropriate ConfigurationPermission.";

		// 2
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
		conf = cmA.waitForService(500l).getConfiguration(pid1);
		if (minor == 1) {
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		}

		// 1
		testId = traceTestId(header, ++micro);
		props.put("StringKey", "stringvalue");
		conf.update(props);
		conf = cmA.waitForService(500l).getConfiguration(pid1);
		if (minor == 1) {
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else {
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		}
		conf.delete();

		this.setAppropriatePermission();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// 4
		testId = traceTestId(header, ++micro);
		setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else if (minor == 2) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}

		// 3
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else if (minor == 2) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}
		conf.delete();

		this.setAppropriatePermission();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// 8
		testId = traceTestId(header, ++micro);
		setCPtoBundle("?*", ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else if (minor == 6 || minor == 7) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}

		// 7
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else if (minor == 6 || minor == 7) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}
		conf.delete();

		this.setAppropriatePermission();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// 10
		testId = traceTestId(header, ++micro);
		setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else if (minor == 6) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}

		// 9
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else if (minor == 6) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", locationOld,
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}
		conf.delete();

		this.setAppropriatePermission();
		conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

		// 12
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}

		// 11
		testId = traceTestId(header, ++micro);
		conf.update(props);
		if (minor == 1) {
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
		} else {
			assertThrowsSEbyGetConfiguration(pid1, message);
		}
		conf.delete();

		if (minor == 1) {
			this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

			// 15
			testId = traceTestId(header, ++micro);
			setCPtoBundle(null, null, thisBundle());
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());

			// 14
			testId = traceTestId(header, ++micro);
			conf.update(props);
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			conf.delete();
		}

		if (minor == 2) {
			this.setAppropriatePermission();
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());

			// 15
			testId = traceTestId(header, ++micro);
			setCPtoBundle(null, null, thisBundle());
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertNull("Configuration props MUST be null", conf.getProperties());

			// 14
			testId = traceTestId(header, ++micro);
			conf.update(props);
			conf = cmA.waitForService(500l).getConfiguration(pid1);
			assertEquals("Location", thisLocation(),
					this.getBundleLocationForCompare(conf));
			assertEquals("Check Configuration props", 2, conf.getProperties()
					.size());
			conf.delete();
		}
	}

	private void assertThrowsSEbyGetConfiguration(final String pid,
			String message) throws AssertionFailedError {
		try {
			cmA.waitForService(500l).getConfiguration(pid);
			// A SecurityException should have been thrown
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// Check that we got the correct exception
			assertException(message, SecurityException.class, e);
		}
	}

	// TODO confirm
	@Test
	public void testCreateFactoryConfiguration_4_01() throws Exception {
		String location = null;
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(1,
				location);
	}

	@Test
	public void testCreateFactoryConfiguration_4_02() throws Exception {
		String location = locationA;
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(2,
				location);
	}

	@Test
	public void testCreateFactoryConfiguration_4_03() throws Exception {
		String location = locationA + "*";
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(3,
				location);
	}

	@Test
	public void testCreateFactoryConfiguration_4_04() throws Exception {
		String location = locationB;
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(4,
				location);
	}

	@Test
	public void testCreateFactoryConfiguration_4_06() throws Exception {
		String location = regionA;
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(6,
				location);
	}

	@Test
	public void testCreateFactoryConfiguration_4_07() throws Exception {
		String location = regionA + "*";
		this.internalCreateFactoryConfigurationWithLocation_4_01To07(7,
				location);
	}

	public void internalCreateFactoryConfigurationWithLocation_4_01To07(
			int minor, String location)
			throws BundleException, IOException, InterruptedException {
		final String header = "testCreateFactoryConfigurationWithLocation_4_"
				+ String.valueOf(minor) + "_";
		;

		String fpid = Util.createPid("factory1");
		String testId = null;
		int micro = 0;

		String message = testId
				+ ":try to create factory configuration with location with inappropriate ConfigurationPermission.";

		// 1
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
		Configuration conf1 = cmA.waitForService(500l)
				.createFactoryConfiguration(fpid, location);
		Configuration conf2 = cmA.waitForService(500l)
				.createFactoryConfiguration(fpid, location);
		assertEquals("Check conf fpid.", conf1.getFactoryPid(),
				conf2.getFactoryPid());
		assertFalse("Check conf pid does not same.",
				conf1.getPid().equals(conf2.getPid()));
		assertEquals("Check conf location.", location,
				this.getBundleLocationForCompare(conf1));
		assertEquals("Check conf location.", location,
				this.getBundleLocationForCompare(conf2));

		// 2
		testId = traceTestId(header, ++micro);
		setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		if (minor == 2) {
			conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			assertEquals("Check conf fpid.", conf1.getFactoryPid(),
					conf2.getFactoryPid());
			assertFalse("Check conf pid does not same.",
					conf1.getPid().equals(conf2.getPid()));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf1));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf2));
		} else {
			this.assertThrowsSEbyCreateFactoryConf(fpid, location, message);
		}

		// 4
		testId = traceTestId(header, ++micro);
		setCPtoBundle("?*", ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 6 || minor == 7) {
			conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			assertEquals("Check conf fpid.", conf1.getFactoryPid(),
					conf2.getFactoryPid());
			assertFalse("Check conf pid does not same.",
					conf1.getPid().equals(conf2.getPid()));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf1));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf2));
		} else {
			this.assertThrowsSEbyCreateFactoryConf(fpid, location, message);
		}

		// 5
		testId = traceTestId(header, ++micro);
		setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE, thisBundle());
		if (minor == 6) {
			conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, location);
			assertEquals("Check conf fpid.", conf1.getFactoryPid(),
					conf2.getFactoryPid());
			assertFalse("Check conf pid does not same.",
					conf1.getPid().equals(conf2.getPid()));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf1));
			assertEquals("Check conf location.", location,
					this.getBundleLocationForCompare(conf2));
		} else {
			this.assertThrowsSEbyCreateFactoryConf(fpid, location, message);
		}

		// 6
		testId = traceTestId(header, ++micro);
		setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
		this.assertThrowsSEbyCreateFactoryConf(fpid, location, message);

		// 7
		if (minor == 6) {
			testId = traceTestId(header, ++micro);
			setCPtoBundle(regionB, ConfigurationPermission.CONFIGURE,
					thisBundle());
			this.assertThrowsSEbyCreateFactoryConf(fpid, location, message);
		}

		// 8
		if (minor == 2) {
			traceTestId(header, ++micro);
			this.resetPermissions();
			setCPtoBundle(null, null, thisBundle());
			conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, thisLocation());
			conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid, thisLocation());
			assertEquals("Check conf fpid.", conf1.getFactoryPid(),
					conf2.getFactoryPid());
			assertFalse("Check conf pid does not same.",
					conf1.getPid().equals(conf2.getPid()));
			assertEquals("Check conf location.", thisLocation(),
					this.getBundleLocationForCompare(conf1));
			assertEquals("Check conf location.", thisLocation(),
					this.getBundleLocationForCompare(conf2));
		}
	}

	private void assertThrowsSEbyCreateFactoryConf(final String fPid,
			final String location, String message) throws AssertionFailedError {
		try {
			cmA.waitForService(500l).createFactoryConfiguration(fPid, location);
			// A SecurityException should have been thrown
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// Check that we got the correct exception
			assertException(message, SecurityException.class, e);
		}
	}

	@Test
	public void testListConfigurations_6_01() throws Exception {
		String filter = null;
		this.internalListConfigurations(1, filter);
	}

	@Test
	public void testListConfigurations_6_02() throws Exception {
		String filter = "(service.pid=pid1)";
		this.internalListConfigurations(2, filter);
	}

	@Test
	public void testListConfigurations_6_03() throws Exception {
		String filter = "(service.bundleLocation=location.a)";
		this.internalListConfigurations(3, filter);
	}

	@Test
	public void testListConfigurations_6_04() throws Exception {
		String filter = "(service.bundleLocation=?RegionA)";
		this.internalListConfigurations(4, filter);
	}

	@Test
	public void testListConfigurations_6_05() throws Exception {
		String filter = "(&(service.bundleLocation=?RegionA)(service.pid=pid2))";
		this.internalListConfigurations(5, filter);
	}

	public void internalListConfigurations(int minor, String filter)
			throws IOException, InvalidSyntaxException, BundleException,
			InterruptedException {
		final String header = "testListConfigurations_6_"
				+ String.valueOf(minor) + "_";
		int micro = 0;

		String testId = null;
		String pid1 = "pid1";
		String pid2 = "pid2";
		String pid3 = "pid3";
		Configuration conf1 = null;
		Configuration conf2 = null;
		Configuration conf3 = null;

		Dictionary<String,Object> prop = new Hashtable<>();
		prop.put("StringKey", "Stringvalue");

		String message = testId + ":try listConfigurations ";
		this.setCPtoBundle("*", ConfigurationPermission.CONFIGURE,
				thisBundle());
		conf1 = cmA.waitForService(500l).getConfiguration(pid1, locationA);
		conf1.update(prop);
		conf2 = cmA.waitForService(500l).getConfiguration(pid2, regionA);
		conf2.update(prop);
		conf3 = cmA.waitForService(500l).getConfiguration(pid3, "?");
		conf3.update(prop);

		testId = traceTestId(header, ++micro);
		this.setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		Configuration[] conflist = cmA.waitForService(500l)
				.listConfigurations(filter);
		if (minor == 4 || minor == 5) {
			assertNull("Returned list of configuration MUST be null.", conflist);
		} else {
			assertEquals("number of Configuration Object", 1, conflist.length);
			assertEquals(message, conflist[0], conf1);
		}

		testId = traceTestId(header, ++micro);
		this.setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
				thisBundle());
		conflist = cmA.waitForService(500l).listConfigurations(filter);
		if (minor == 1 || minor == 4 || minor == 5) {
			assertEquals("number of Configuration Object", 1, conflist.length);
			assertEquals(message, conflist[0], conf2);
		} else {
			assertNull("Returned list of configuration MUST be null.", conflist);
		}
	}

	// TODO
	@Test
	public void testGetBundleLocation_7_01() throws Exception {
		String locationOld = null;
		this.internalGetBundleLocation_7_01to08(1, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_02() throws Exception {
		String locationOld = locationA;
		this.internalGetBundleLocation_7_01to08(2, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_03() throws Exception {
		String locationOld = locationA + "*";
		this.internalGetBundleLocation_7_01to08(3, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_05() throws Exception {
		String locationOld = "?*";
		this.internalGetBundleLocation_7_01to08(5, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_06() throws Exception {
		String locationOld = regionA;
		this.internalGetBundleLocation_7_01to08(6, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_07() throws Exception {
		String locationOld = regionA + "*";
		this.internalGetBundleLocation_7_01to08(7, locationOld);
	}

	@Test
	public void testGetBundleLocation_7_08() throws Exception {
		String locationOld = thisLocation();
		this.internalGetBundleLocation_7_01to08(8, locationOld);
	}

	public void internalGetBundleLocation_7_01to08(final int minor,
			final String locationOld) throws Exception {

		final String header = "testGetBundleLocation_7_"
				+ String.valueOf(minor) + "_";

		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;

		try {
			this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);

			// 1
			testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
			String loc = conf.getBundleLocation();
			assertEquals("Check conf location", locationOld, loc);

			// 2
			testId = traceTestId(header, ++micro);
			setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
					thisBundle());
            if (minor == 2) {
				loc = conf.getBundleLocation();
				assertEquals("Check conf location", locationOld, loc);
			} else {
				this.assertThrowsSEbyGetLocation(conf, testId);
			}

			// 3
			testId = traceTestId(header, ++micro);
			setCPtoBundle("?", ConfigurationPermission.CONFIGURE, thisBundle());
            this.assertThrowsSEbyGetLocation(conf, testId);

			// 4
			testId = traceTestId(header, ++micro);
			setCPtoBundle("?*", ConfigurationPermission.CONFIGURE,
					thisBundle());
            if (minor == 5 || minor == 6 || minor == 7) {
				loc = conf.getBundleLocation();
				assertEquals("Check conf location", locationOld, loc);
			} else {
				this.assertThrowsSEbyGetLocation(conf, testId);
			}

			// 5
			testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
            if (minor == 6) {
				loc = conf.getBundleLocation();
				assertEquals("Check conf location", locationOld, loc);
			} else {
				this.assertThrowsSEbyGetLocation(conf, testId);
			}

			// 6
			testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
            this.assertThrowsSEbyGetLocation(conf, testId);

			// 7
			testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.CONFIGURE + ","
					+ ConfigurationPermission.TARGET, thisBundle());
			loc = conf.getBundleLocation();
			assertEquals("Check conf location", locationOld, loc);

			// 8
			if (minor == 6 || minor == 7) {
				testId = traceTestId(header, ++micro);
				setCPtoBundle(regionB, ConfigurationPermission.CONFIGURE,
						thisBundle());
				this.assertThrowsSEbyGetLocation(conf, testId);
			}
			++micro;

			// 9
			if (minor == 6) {
				testId = traceTestId(header, ++micro);
				setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE + ","
						+ ConfigurationPermission.TARGET, thisBundle());
				loc = conf.getBundleLocation();
				assertEquals("Check conf location", locationOld, loc);
			}
			++micro;

			// 10
			testId = traceTestId(header, ++micro);
			conf.delete();
			resetPermissions();
			conf = cmA.waitForService(500l)
					.getConfiguration(pid1, thisLocation());
			// Bug2539: need to have CONFIGURE(thisLocation())
			setCPtoBundle(thisLocation(), ConfigurationPermission.CONFIGURE,
					thisBundle());
            loc = conf.getBundleLocation();
			assertEquals("Check conf location", thisLocation(), loc);
		} finally {
			this.resetPermissions();
			if (conf != null)
				conf.delete();
		}
	}

	private void assertThrowsSEbyGetLocation(final Configuration conf,
			String testId) throws AssertionFailedError {
		String message = testId
				+ ":try to get bundle location without appropriate ConfigurationPermission.";
		try {
			conf.getBundleLocation();
			// A SecurityException should have been thrown
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// Check that we got the correct exception
			assertException(message, SecurityException.class, e);
		}
	}

	@Test
	public void testSetBundleLocation_8_01() throws Exception {
		String locationOld = null;
		String location = null;
		this.internalSetBundleLocation_8_01to07(1, locationOld, location);
	}

	@Test
	public void testSetBundleLocation_8_02() throws Exception {
		String locationOld = null;
		String location = locationA;
		this.internalSetBundleLocation_8_01to07(2, locationOld, location);
	}

	@Test
	public void testSetBundleLocation_8_04() throws Exception {
		String locationOld = null;
		String location = "?*";
		this.internalSetBundleLocation_8_01to07(4, locationOld, location);
	}

	@Test
	public void testSetBundleLocation_8_05() throws Exception {
		String locationOld = null;
		String location = regionA;
		this.internalSetBundleLocation_8_01to07(5, locationOld, location);
	}

	@Test
	public void testSetBundleLocation_8_06() throws Exception {
		String locationOld = locationA;
		String location = null;
		this.internalSetBundleLocation_8_01to07(6, locationOld, location);
	}

	@Test
	public void testSetBundleLocation_8_07() throws Exception {
		String locationOld = locationA;
		String location = locationA;
		this.internalSetBundleLocation_8_01to07(7, locationOld, location);
	}

	public void internalSetBundleLocation_8_01to07(final int minor,
			final String locationOld, final String location) throws Exception {

		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";

		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 2
    		testId = traceTestId(header, ++micro);
    		if (minor == 6 || minor == 7) {
    			setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
						thisBundle());
    			if (minor == 7) {
    				conf.setBundleLocation(location);
    				assertEquals("Check Conf location.", location,
    						this.getBundleLocationForCompare(conf));
    			} else
    				assertThrowsSEbySetLocation(conf, location, testId);
    		} else {
				setCPtoBundle("?*", ConfigurationPermission.CONFIGURE,
						thisBundle());
    			assertThrowsSEbySetLocation(conf, location, testId);
    		}

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
		} finally {
		    if (conf != null) {
		        conf.delete();
		    }
		}
	}

	@Test
	public void testSetBundleLocation_8_08() throws Exception {

		int minor = 8;
		String locationOld = locationA;
		String location = locationB;
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			List<String> cList = new ArrayList<>();
    		cList.add(locationA);
    		cList.add(locationB);
			setCPListtoBundle(cList, null, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(locationOld);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(locationOld);

    		// 4
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(locationB, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(locationOld);

    		// 5
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(locationA, ConfigurationPermission.TARGET,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_10() throws Exception {

		int minor = 10;
		String locationOld = locationA;
		String location = regionA;
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			List<String> cList = new ArrayList<>();
    		cList.add(locationA);
    		cList.add(regionA);
			setCPListtoBundle(cList, null, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 4
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_15() throws Exception {

		int minor = 15;
		String locationOld = regionA;
		String location = null;
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.CONFIGURE, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_16() throws Exception {

		int minor = 16;
		String locationOld = regionA;
		String location = locationA;
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			List<String> cList = new ArrayList<>();
    		cList.add(locationA);
    		cList.add(regionA);
			setCPListtoBundle(cList, null, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(locationA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 4
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_17() throws Exception {

		int minor = 17;
		String locationOld = regionA;
		String location = "?";
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			List<String> cList = new ArrayList<>();
    		cList.add("?");
    		cList.add(regionA);
			setCPListtoBundle(cList, null, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("?", ConfigurationPermission.CONFIGURE, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		this.setAppropriatePermission();
    		conf.setBundleLocation(locationOld);

    		// 4
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_18() throws Exception {

		int minor = 18;
		String locationOld = regionA;
		String location = regionA;
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			setCPtoBundle(regionA, ConfigurationPermission.CONFIGURE,
					thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		// 2
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("?", ConfigurationPermission.CONFIGURE, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	@Test
	public void testSetBundleLocation_8_19() throws Exception {

		int minor = 19;
		String locationOld = locationA + "*";
		String location = regionA + "*";
		final String header = "testSetBundleLocation_8_"
				+ String.valueOf(minor) + "_";
		String testId = null;
		int micro = 0;

		final String pid1 = Util.createPid("1");
		Configuration conf = null;
		try {
    		this.setAppropriatePermission();
			conf = cmA.waitForService(500l).getConfiguration(pid1, locationOld);
            assertEquals("Check Conf location.", locationOld,
                this.getBundleLocationForCompare(conf));

    		// 1
    		testId = traceTestId(header, ++micro);
			List<String> cList = new ArrayList<>();
    		cList.add(locationA + "*");
    		cList.add(regionA + "*");
			setCPListtoBundle(cList, null, thisBundle());
    		conf.setBundleLocation(location);
    		assertEquals("Check Conf location.", location,
    				this.getBundleLocationForCompare(conf));

    		// 2
    		testId = traceTestId(header, ++micro);
			cList = new ArrayList<>();
    		cList.add(locationA + ".com");
    		cList.add(regionA + ".com");
			setCPListtoBundle(cList, null, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);

    		// 3
    		testId = traceTestId(header, ++micro);
			setCPtoBundle("*", ConfigurationPermission.TARGET, thisBundle());
    		assertThrowsSEbySetLocation(conf, location, testId);
        } finally {
            if (conf != null) {
                conf.delete();
            }
        }
	}

	private void setCPListtoBundle(List<String> nameforConfigure,
			List<String> nameForTarget,
			Bundle bundle) throws BundleException {
		this.resetPermissions();
		list.clear();

		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");
		add(list, AP, "*", "*");

		if (nameforConfigure != null)
			if (!nameforConfigure.isEmpty()) {
				for (Iterator<String> itc = nameforConfigure.iterator(); itc
						.hasNext();) {
					add(list, CP, itc.next(),
							ConfigurationPermission.CONFIGURE);
				}
			}
		if (nameForTarget != null)
			if (!nameForTarget.isEmpty()) {
				for (Iterator<String> itt = nameforConfigure.iterator(); itt
						.hasNext();) {
					add(list, CP, itt.next(),
							ConfigurationPermission.TARGET);
				}
			}
		permissionFlag = true;
		this.setBundlePermission(bundle, list);
	}

	private void assertThrowsSEbySetLocation(final Configuration conf,
			final String location, String testId) throws AssertionFailedError {
		String message = testId
				+ ":try to set bundle location without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(location);
			// A SecurityException should have been thrown
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			// Check that we got the correct exception
			assertException(message, SecurityException.class, e);
		}
	}

	/* Ikuo YAMASAKI */

	private int assertCallback(SynchronizerImpl sync, int count) {
		boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
		assertTrue(
				"ManagedService#updated(props)/ManagedServiceFactory#updated(pid,props) must be called back",
				calledback);
		return count;
	}

	private void assertNoCallback(SynchronizerImpl sync, int count) {
		boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
		assertFalse(
				"ManagedService#updated(props)/ManagedServiceFactory#updated(pid,props) must NOT be called back",
				calledback);
	}

	private int assertDeletedCallback(SynchronizerImpl sync, int count) {
		boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count,
				true);
		assertTrue("ManagedServiceFactory#deleted(pid) must be called back",
				calledback);
		return count;
	}

	private void assertDeletedNoCallback(SynchronizerImpl sync, int count) {
		boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1,
				true);
		assertFalse(
				"ManagedServiceFactory#deleted(pid) must NOT be called back",
				calledback);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistration9_1_1() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testSetBundleLocation_9_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.setCPtoBundle(null, null, bundleT1, false);

			this.startTargetBundle(bundleT1);
			// this.setInappropriatePermission();

			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());

			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");
			this.setAppropriatePermission();
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1,
					bundleT1.getLocation());
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_1);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("Conf is being deleted.");
			conf.delete();
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

	}

	private void restartCM() throws BundleException {
		Bundle cmBundle = stopCmBundle();
		cmBundle.stop();
		trace("CM has been stopped.");
		startCmBundle(cmBundle);
		trace("CM has been started.");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistration9_1_2() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		/*
		 * A. create Configuration in advance, Then Register ManagedService .
		 */
		final String header = "testSetBundleLocation_9_1_2";

		int micro = 0;
		traceTestId(header, ++micro);
		trace("The configuration is being created");
		Configuration conf = cmA.waitForService(500l)
				.getConfiguration(pid1, bundleT1.getLocation());
		trace("The configuration is being updated ");
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("StringKey", "stringvalue");
		conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class,
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.setCPtoBundle(null, null, bundleT1, false);

			this.startTargetBundle(bundleT1);

			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("null props must be called back", sync1_1.getProps());
			assertEquals("Check props", "stringvalue",
					sync1_1.getProps().get("StringKey"));
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("Conf is being deleted.");
			conf.delete();
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

	}

	/**
	 *
	 * @throws Exception
	 */
	// 	@Test
	// public void testManagedServiceRegistration9_2_1() throws Exception {
	// final Bundle bundleT1 = bInstaller.installBundle(
	// getWebServer() + "bundleT1.jar");
	// final Bundle bundleT2 = bInstaller.installBundle(
	// getWebServer() + "bundleT2.jar");
	// final String locationT2 = bundleT2.getLocation();
	// final String pid1 = Util.createPid("pid1");
	// List list = new ArrayList(3);
	// /*
	// * A. Register ManagedService in advance. Then create Configuration.
	// */
	// final String header = "testSetBundleLocation_9_2_1";
	//
	// String testId = null;
	// int micro = 0;
	// testId = traceTestId(header, ++micro);
	//
	// this.setCPtoBundle(locationT2, "target", bundleT1);
	//
	// try {
	// SynchronizerImpl sync1_1 = new SynchronizerImpl();
	// list.add(bc.registerService(Synchronizer.class.getName(),
	// sync1_1, propsForSync1_1));
	// SynchronizerImpl sync1_2 = new SynchronizerImpl();
	// list.add(bc.registerService(Synchronizer.class.getName(),
	// sync1_2, propsForSync1_2));
	// this.startTargetBundle(bundleT1);
	// trace("Wait for signal.");
	// int count1_1 = 0;
	// count1_1 = assertCallback(sync1_1, count1_1);
	// assertNull("called back with null props", sync1_1.getProps());
	// int count1_2 = 0;
	// count1_2 = assertCallback(sync1_2, count1_2);
	// assertNull("called back with null props", sync1_2.getProps());
	//
	// trace("The configuration is being created");
	//
	// Configuration conf =cmA.waitForService(500l).getConfiguration(pid1,
	// bundleT2
	// .getLocation());
	// trace("Wait for signal.");
	// assertNoCallback(sync1_1, count1_1);
	//
	// trace("The configuration is being updated ");
	// Dictionary<String,Object> props = new Hashtable<>();
	// props.put("StringKey", "stringvalue");
	// conf.update(props);
	// trace("Wait for signal.");
	// assertNoCallback(sync1_1, count1_1);
	// assertNoCallback(sync1_2, count1_2);
	//
	// // restartCM();
	// // conf =cmA.waitForService(500l).getConfiguration(pid1,
	// bundleT2.getLocation());
	// // trace("Wait for signal.");
	// // count1_1 = assertCallback(sync1_1, count1_1);
	// // count1_2 = assertCallback(sync1_2, count1_2);
	// // assertNull("called back with null props", sync1_2.getProps());
	// //
	// // trace("conf is going to be deleted.");
	// // conf.delete();
	// // count1_1 = assertCallback(sync1_1, count1_1);
	// // assertNull("called back with null props", sync1_1.getProps());
	// // this.assertNoCallback(sync1_2, count1_2);
	//
	// } finally {
	// this.cleanUpForCallbackTest(bundleT1, null, null, list);
	// }
	// }
	//

	@Test
	public void testManagedServiceRegistration9_2_1() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		internalManagedServiceRegistration9_2_1to2(2, bundleT2.getLocation(),
				ConfigurationPermission.TARGET, bundleT1, bundleT2);
	}

	@Test
	public void testManagedServiceRegistration9_2_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		internalManagedServiceRegistration9_2_1to2(3, "*",
				ConfigurationPermission.TARGET, bundleT1, bundleT2);
	}

	private void internalManagedServiceRegistration9_2_1to2(final int micro,
			final String target, final String actions, final Bundle bundleT1,
			final Bundle bundleT2) throws Exception {

		bundleT2.getLocation();
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testSetBundleLocation_9_2_"
				+ String.valueOf(micro);

		int micro1 = 0;
		traceTestId(header, ++micro1);

		this.setCPtoBundle(actions, target, bundleT1);

			SynchronizerImpl sync1_1 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl();
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");

			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1,
					bundleT2.getLocation());
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("conf is going to be deleted.");
			conf.delete();
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistration9_2_4() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		internalManagedServiceRegistration9_2_4to5(4, bundleT2.getLocation(),
				"target", bundleT1, bundleT2);
	}

	@Test
	public void testManagedServiceRegistration9_2_5() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		internalManagedServiceRegistration9_2_4to5(5, "*", "target", bundleT1,
				bundleT2);
	}

	public void internalManagedServiceRegistration9_2_4to5(final int micro,
			final String target, final String actions, final Bundle bundleT1,
			final Bundle bundleT2) throws Exception {

		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testSetBundleLocation_9_2_"
				+ String.valueOf(micro);

		int micro1 = 0;
		traceTestId(header, ++micro1);

		trace("The configuration is being created");
		Configuration conf = cmA.waitForService(500l)
				.getConfiguration(pid1, bundleT2.getLocation());

		trace("The configuration is being updated ");
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("StringKey", "stringvalue");
		conf.update(props);
		this.setCPtoBundle(target, actions, bundleT1);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("Conf is being deleted.");
			conf.delete();
			this.assertNoCallback(sync1_1, count1_1);
			this.assertNoCallback(sync1_2, count1_2);
	}

	/**
	 * Register ManagedService in advance. Then create Configuration.
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_1_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final Bundle bundleT4 = bInstaller.installBundle("bundleT4.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(7);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testManagedServiceRegistrationMultipleTargets_10_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSync2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSync3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSync3_2));
			SynchronizerImpl sync4_1 = new SynchronizerImpl("4-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_1, propsForSync4_1));
			SynchronizerImpl sync4_2 = new SynchronizerImpl("4-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_2, propsForSync4_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?*", "target", bundleT2, false);
			this.setCPtoBundle("?RegionA", "target", bundleT3, false);
			this.setCPtoBundle("?RegionA", "target", bundleT4, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			this.startTargetBundle(bundleT2);
			int count2_1 = 0;
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());
			this.startTargetBundle(bundleT3);
			int count3_1 = 0;
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNull("called back with null props", sync3_1.getProps());
			int count3_2 = 0;
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNull("called back with null props", sync3_2.getProps());
			this.startTargetBundle(bundleT4);
			// TODO Called back twice each pid.
			// int count4_1 = 0;
			int count4_1 = 1;
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNull("called back with null props", sync4_1.getProps());
			// count4_1 = 2
			// int count4_2 = 0;
			int count4_2 = 1;
			count4_2 = assertCallback(sync4_2, count4_2);
			assertNull("called back with null props", sync4_2.getProps());
			// count4_2 = 2

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			trace("Wait for signal.");

			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);
			assertNoCallback(sync4_1, count4_1);
			assertNoCallback(sync4_2, count4_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNotNull("called back with Non-null props", sync2_1.getProps());
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNotNull("called back with Non-null props", sync2_1.getProps());
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNotNull("called back with Non-null props", sync4_1.getProps());
			assertNoCallback(sync4_2, count4_2);

			trace("conf is going to be deleted.");
			conf.delete();
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNull("called back with null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNull("called back with null props", sync3_2.getProps());
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNull("called back with null props", sync4_1.getProps());
			assertNoCallback(sync4_2, count4_2);

	}

	/**
	 * Register ManagedService in advance. Then create Configuration.
	 *
	 * @throws Exception
	 */
	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_1_2()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final Bundle bundleT4 = bInstaller.installBundle("bundleT4.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testManagedServiceRegistrationMultipleTargets_10_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSync2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSync3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSync3_2));
			SynchronizerImpl sync4_1 = new SynchronizerImpl("4-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_1, propsForSync4_1));
			SynchronizerImpl sync4_2 = new SynchronizerImpl("4-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_2, propsForSync4_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?RegionB", "target", bundleT2, false);
			this.setCPtoBundle("?RegionB", "target", bundleT3, false);
			this.setCPtoBundle("?RegionA", "target", bundleT4, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			this.startTargetBundle(bundleT2);
			int count2_1 = 0;
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());
			this.startTargetBundle(bundleT3);
			int count3_1 = 0;
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNull("called back with null props", sync3_1.getProps());
			int count3_2 = 0;
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNull("called back with null props", sync3_2.getProps());
			this.startTargetBundle(bundleT4);
			// int count4_1 = 0;
			int count4_1 = 1;
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNull("called back with null props", sync4_1.getProps());
			// int count4_2 = 0;
			int count4_2 = 1;
			count4_2 = assertCallback(sync4_2, count4_2);
			assertNull("called back with null props", sync4_2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);
			assertNoCallback(sync4_1, count4_1);
			assertNoCallback(sync4_2, count4_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNotNull("called back with Non-null props", sync4_1.getProps());
			assertNoCallback(sync4_2, count4_2);

			trace("conf is going to be deleted.");
			conf.delete();
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNull("called back with null props", sync4_1.getProps());
			assertNoCallback(sync4_2, count4_2);

	}

	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_2_1()
			throws Exception {
		System.setProperty(
				"org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode",
				"Array");
	    try {
	        this.internalTestManagedServiceRegistrationMultipleTargets_10_2_1to3();
	    } finally {
			System.getProperties()
					.remove("org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode");
	    }
	}

	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_2_2()
			throws Exception {
		System.setProperty(
				"org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode",
				"Vector");
		try {
    		this.internalTestManagedServiceRegistrationMultipleTargets_10_2_1to3();
        } finally {
			System.getProperties()
					.remove("org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode");
        }
	}

	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_2_3()
			throws Exception {
		System.setProperty(
				"org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode",
				"List");
		try {
		    this.internalTestManagedServiceRegistrationMultipleTargets_10_2_1to3();
        } finally {
			System.getProperties()
					.remove("org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode");
        }
	}

	private void internalTestManagedServiceRegistrationMultipleTargets_10_2_1to3()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final Bundle bundleT4 = bInstaller.installBundle("bundleT4.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceRegistrationMultipleTargets_10_2_1";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSync2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSync3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSync3_2));
			SynchronizerImpl sync4_1 = new SynchronizerImpl("4-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_1, propsForSync4_1));
			SynchronizerImpl sync4_2 = new SynchronizerImpl("4-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_2, propsForSync4_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?*", "target", bundleT2, false);
			this.setCPtoBundle("?RegionA", "target", bundleT3, false);
			this.setCPtoBundle("?RegionA", "target", bundleT4, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			this.startTargetBundle(bundleT2);
			int count2_1 = 0;
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNotNull("called back with NON-null props", sync2_1.getProps());
			this.startTargetBundle(bundleT3);
			int count3_1 = 0;
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with NON-null props", sync3_1.getProps());
			int count3_2 = 0;
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with NON-null props", sync3_2.getProps());
			this.startTargetBundle(bundleT4);
			trace("Wait for signal.");

			// MS for pid2, pid1; two callbacks expected
			int count4_1 = 0;
			count4_1 = assertCallback(sync4_1, count4_1+1);
			assertEquals("expect two callbacks", count4_1, 2);
			assertNotNull("called back with NON-null props", sync4_1.getProps());

	 		// MS for pid2, pid3; two callbacks expected
			int count4_2 = 0;
			count4_2 = assertCallback(sync4_2, count4_2+1);
			assertEquals("expect two callbacks", count4_2, 2);
			assertNull("called back with null props", sync4_2.getProps());

			// trace("conf is going to be deleted.");
			// conf.delete();
			// assertNoCallback(sync1_1, count1_1);
			// assertNoCallback(sync1_2, count1_2);
			// count2_1 = assertCallback(sync2_1, count2_1);
			// assertNull("called back with null props", sync2_1.getProps());
			// count3_1 = assertCallback(sync3_1, count3_1);
			// assertNull("called back with null props", sync3_1.getProps());
			// count3_2 = assertCallback(sync3_2, count3_2);
			// assertNull("called back with null props", sync3_2.getProps());

	}

	@Test
	public void testManagedServiceRegistrationMultipleTargets_10_2_4()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final Bundle bundleT4 = bInstaller.installBundle("bundleT4.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testManagedServiceRegistrationMultipleTargets_10_2_1";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSync2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSync3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSync3_2));
			SynchronizerImpl sync4_1 = new SynchronizerImpl("4-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_1, propsForSync4_1));
			SynchronizerImpl sync4_2 = new SynchronizerImpl("4-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync4_2, propsForSync4_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?RegionB", "target", bundleT2, false);
			this.setCPtoBundle("?RegionB", "target", bundleT3, false);
			this.setCPtoBundle("?RegionA", "target", bundleT4, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			this.startTargetBundle(bundleT2);
			trace("Wait for signal.");
			int count2_1 = 0;
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());
			this.startTargetBundle(bundleT3);
			trace("Wait for signal.");
			int count3_1 = 0;
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNull("called back with null props", sync3_1.getProps());
			// assertNotNull("called back with NON-null props",
			// sync3_1.getProps());
			int count3_2 = 0;
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNull("called back with null props", sync3_2.getProps());

			String mode = System
					.getProperty(
							"org.osgi.test.cases.cmA.waitForService(500l).bundleT4.mode");
			System.out.println("##########################" + mode);

			this.startTargetBundle(bundleT4);
			trace("Wait for signal.");
			int count4_1 = 0;
			count4_1 = assertCallback(sync4_1, count4_1);
			// ignore actual call back properties, might already have been overwritten
			count4_1 = assertCallback(sync4_1, count4_1);
			assertNotNull("called back with NON-null props", sync4_1.getProps());
			int count4_2 = 0;
			count4_2 = assertCallback(sync4_2, count4_2);
			// ignore actual call back properties, might already have been overwritten
			count4_2 = assertCallback(sync4_2, count4_2);
			assertNull("called back with null props", sync4_2.getProps());

			// trace("conf is going to be deleted.");
			// conf.delete();
			// assertNoCallback(sync1_1, count1_1);
			// assertNoCallback(sync1_2, count1_2);
			// count2_1 = assertCallback(sync2_1, count2_1);
			// assertNull("called back with null props", sync2_1.getProps());
			// count3_1 = assertCallback(sync3_1, count3_1);
			// assertNull("called back with null props", sync3_1.getProps());
			// count3_2 = assertCallback(sync3_2, count3_2);
			// assertNull("called back with null props", sync3_2.getProps());

	}

	@Test
	public void testManagedServiceRegistrationMultipleTargets_11_1_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		final String header = "testManagedServiceRegistrationMultipleTargets_11_1_1";

		int micro = 0;
		traceTestId(header, ++micro);


			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSync2_1));

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			this.startTargetBundle(bundleT2);
			int count2_1 = 0;
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNull("called back with null props", sync2_1.getProps());

			this.printoutPermissions();
			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("*", "target", bundleT2, false);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, null);
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);
			sleep(3000);

			if(conf.getBundleLocation().equals(bundleT1.getLocation())){
				count1_1 = assertCallback(sync1_1, count1_1);
				assertNotNull("called back with NON-null props",
						sync1_1.getProps());
				assertNoCallback(sync1_2, count1_2);
				assertNoCallback(sync2_1, count2_1);
				assertEquals("conf.location must be dynamically set to location of T1.",
						conf.getBundleLocation(),bundleT1.getLocation());
				bundleT1.stop();
				bundleT1.uninstall();
				props.put("StringKey", "stringvalueNew");
				trace("The configuration is being updated ");
				conf.update(props);
				count2_1 = assertCallback(sync2_1, count2_1);
				assertNotNull("called back with NON-null props",sync2_1.getProps());
				bundleT2.stop();
				bundleT2.uninstall();
			}else if(conf.getBundleLocation().equals(bundleT2.getLocation())){
				assertNoCallback(sync1_1, count1_1);
				assertNoCallback(sync1_2, count1_2);
				count2_1 = assertCallback(sync2_1, count2_1);
				assertNotNull("called back with NON-null props",
						sync2_1.getProps());
				assertEquals("conf.location must be dynamically set to location of T2.",
						conf.getBundleLocation(),bundleT2.getLocation());
				trace("The bound bundleT2 is going to unregister MS.");
				bundleT2.stop();
				bundleT2.uninstall();
				props.put("StringKey", "stringvalueNew");
				trace("The configuration is being updated ");
				conf.update(props);
				count1_1 = assertCallback(sync1_1, count1_1);
				assertNotNull("called back with NON-null props",sync1_1.getProps());
				assertNoCallback(sync1_2, count1_2);
				bundleT1.stop();
				bundleT1.uninstall();
			}else{
				fail();
			}
	}

	@Test
	public void testManagedServiceChangeLocation_12_1_1() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceChangeLocation_12_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1,
					bundleT2.getLocation());
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("?RegionA", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("conf.setBundleLocation() is being called.");
			conf.setBundleLocation("?RegionA");

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			// count1_2 = assertCallback(sync1_2, count1_2);
			// assertNull("called back with null props", sync1_2.getProps());

	}

	@Test
	public void testManagedServiceChangeLocation_12_1_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceChangeLocation_12_1_2";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("?RegionA", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			assertEquals("conf.location must be set to \"?RegionA\".",
					conf.getBundleLocation(), "?RegionA");

			trace("conf.setBundleLocation() is being called.");
			conf.setBundleLocation(bundleT2.getLocation());

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
	}

	@Test
	public void testManagedServiceChangeLocation_12_1_3() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceChangeLocation_12_1_2";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("?RegionA", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
			assertEquals("conf.location must be set to \"?RegionA\".",
					conf.getBundleLocation(), "?RegionA");

			trace("conf.setBundleLocation() is being called.");
			conf.setBundleLocation("?RegionB");

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
	}

	@Test
	public void testManagedServiceChangeCP_12_2_1() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceChangeCP_12_2_1";

		int micro = 0;
		traceTestId(header, ++micro);


			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			this.setCPtoBundle("?RegionA", "target", bundleT1, false);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			props.put("StringKey", "stringvalueNew");
			trace("The configuration is being updated ");
			conf.update(props);
			//count1_1 = assertCallback(sync1_1, count1_1);
			//assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

	}

	@Test
	public void testManagedServiceChangeCP_12_2_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceChangeCP_12_2_1";

		int micro = 0;
		traceTestId(header, ++micro);


			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("?RegionA", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			this.setCPtoBundle("*", "target", bundleT1, false);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			props.put("StringKey", "stringvalueNew");
			trace("The configuration is being updated ");
			conf.update(props);
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
	}

	@Test
	public void testManagedServiceStartCM_12_3_1() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceStartCM_12_3_1";

		int micro = 0;
		traceTestId(header, ++micro);

			Bundle cmBundle = this.stopCmBundle();

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			int count1_2 = 0;
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("CM is going to start");
			startCmBundle(cmBundle);
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

	}

	@Test
	public void testManagedServiceStartCM_12_3_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceStartCM_12_3_2";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			assertNotNull(conf);
			// Dictionary<String,Object> props = new Hashtable<>();
			// props.put("StringKey", "stringvalue");
			// trace("The configuration is being updated ");
			// conf.update(props);
            Bundle cmBundle = this.stopCmBundle();

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			int count1_2 = 0;
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("CM is going to start");
			startCmBundle(cmBundle);
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
	}

	@Test
	public void testManagedServiceStartCM_12_3_3() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceStartCM_12_3_3";

		int micro = 0;
		traceTestId(header, ++micro);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?RegionA");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);
            Bundle cmBundle = this.stopCmBundle();

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			int count1_2 = 0;
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("CM is going to start");
			startCmBundle(cmBundle);
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());
	}

	@Test
	public void testManagedServiceModifyPid_12_4_1() throws Exception {
		assertTrue(cmA.waitForService(1000) != null);
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		final String pid2 = Util.createPid("pid2");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceModifyPid_12_4_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			// modify PID
			trace("modify PID from " + pid1 + " to " + pid2);
			modifyPid(ManagedService.class.getName(), pid1, pid2);
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

	}

	@Test
	public void testManagedServiceModifyPid_12_4_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String pid1 = Util.createPid("pid1");
		final String pid2 = Util.createPid("pid2");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);

		final String header = "testManagedServiceModifyPid_12_4_1";

		int micro = 0;
		traceTestId(header, ++micro);


			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSync1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSync1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNull("called back with null props", sync1_1.getProps());
			int count1_2 = 0;
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNull("called back with null props", sync1_2.getProps());

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.getConfiguration(pid1, "?");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			trace("The configuration is being updated ");
			conf.update(props);

			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			// modify PID
			trace("modify PID from " + pid2 + " to " + pid1);
			modifyPid(ManagedService.class.getName(), pid2, pid1);
			assertNoCallback(sync1_1, count1_1);
			count1_1 = assertCallback(sync1_2, count1_2);
			assertNotNull("called back with NON-null props", sync1_2.getProps());
	}

	private void modifyPid(String clazz, String oldPid, String newPid) {
		ServiceReference< ? > reference1 = this.bc
				.getServiceReference(
				ModifyPid.class.getName());
		ModifyPid modifyPid = (ModifyPid) this.bc.getService(
				reference1);

		ServiceReference< ? >[] references = null;
		try {
			references = this.bc.getServiceReferences(clazz,
					"(service.pid=" + oldPid + ")");
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trace("The pid of MS1-1 is being changed from " + oldPid + " to "
				+ newPid);
		modifyPid.changeMsPid(
				(Long) (references[0].getProperty(Constants.SERVICE_ID)),
				newPid);
	}

	@Test
	public void testManagedServiceFactoryRegistration13_1_1() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);

		final String header = "testManagedServiceFactoryRegistration13_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

		this.setCPtoBundle(null, null, bundleT1, false);

		checkMsf_FirstRegThenCreateConf(bundleT1, fpid1, list,
				bundleT1.getLocation(), true);
	}

	private void checkMsf_FirstRegThenCreateConf(Bundle bundleT1,
			final String fpid1, List<ServiceRegistration< ? >> list,
			final String location,
			boolean toBeCalledBack)
			throws BundleException, IOException, InterruptedException {
			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			int count1_1 = 0;
			int count1_2 = 0;
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, location);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			if (toBeCalledBack) {
				count1_1 = assertCallback(sync1_1, count1_1);
				props = sync1_1.getProps();
				assertNotNull("null props must be called back", props);
				assertNotNull("pid", props.get(Constants.SERVICE_PID));
				assertFalse("pid",
						props.get(Constants.SERVICE_PID).equals(fpid1));
				assertEquals("fpid", fpid1,
						props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
				assertEquals("value", "stringvalue", props.get("stringkey"));
				assertNull("bundleLocation must be not included",
						props.get("service.bundleLocation"));
				assertEquals("Size of props must be 3", 3, props.size());
			} else {
				assertNoCallback(sync1_1, count1_1);
			}
			assertNoCallback(sync1_2, count1_2);

			restartCM();
			if (toBeCalledBack) {
				count1_1 = assertCallback(sync1_1, count1_1);
				props = sync1_1.getProps();
				assertNotNull("null props must be called back", props);
				assertNotNull("pid", props.get(Constants.SERVICE_PID));
				assertFalse("pid",
						props.get(Constants.SERVICE_PID).equals(fpid1));
				assertEquals("fpid", fpid1,
						props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
				assertEquals("value", "stringvalue", props.get("stringkey"));
				assertNull("bundleLocation must be not included",
						props.get("service.bundleLocation"));
				assertEquals("Size of props must be 3", 3, props.size());
			} else {
				assertNoCallback(sync1_1, count1_1);
			}
			assertNoCallback(sync1_2, count1_2);

	}

	@Test
	public void testManagedServiceFactoryRegistration13_1_2() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);

		final String header = "testManagedServiceFactoryRegistration11_1_2";

		int micro = 0;
		traceTestId(header, ++micro);

		this.setCPtoBundle(null, null, bundleT1, false);
			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					bundleT1.getLocation());

			int count1_1 = 0;
			int count1_2 = 0;

			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("Bundle is going to stop.");
			bundleT1.stop();
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			props = sync1_1.getProps();
			assertNotNull("null props must be called back", props);
			assertNotNull("pid", props.get(Constants.SERVICE_PID));
			assertFalse("pid", props.get(Constants.SERVICE_PID).equals(fpid1));
			assertEquals("fpid", fpid1,
					props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
			assertEquals("value", "stringvalue", props.get("stringkey"));
			assertNull("bundleLocation must be not included",
					props.get("service.bundleLocation"));
			assertEquals("Size of props must be 3", 3, props.size());

			trace("The configuration is being deleted. Wait for signal.");
			conf.delete();
			this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);
	}

	@Test
	public void testManagedServiceFactoryRegistration13_2_2() throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		/*
		 * A. Register in advance. Then create Configuration.
		 */
		final String header = "testManagedServiceFactoryRegistration13_2_2";

		int micro = 0;
		traceTestId(header, ++micro);

		this.setCPtoBundle("*", "target", bundleT1, false);

		checkMsf_FirstRegThenCreateConf(bundleT1, fpid1, list,
				bundleT2.getLocation(), false);
	}

	@Test
	public void testManagedServiceFactoryRegistration13_2_5() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);

		final String header = "testManagedServiceFactoryRegistration13_2_5";

		int micro = 0;
		traceTestId(header, ++micro);

		this.setCPtoBundle(null, null, bundleT1, false);
			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					bundleT2.getLocation());
			int count1_1 = 0;
			int count1_2 = 0;
			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1);
			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("Bundle is going to stop.");
			bundleT1.stop();
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			// count1_1 = assertCallback(sync1_1, count1_1);
			// assertNotNull("called back with NON-null props",
			// sync1_1.getProps());
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			// props = sync1_1.getProps();
			// assertNotNull("null props must be called back", props);
			// assertNotNull("pid", props.get(Constants.SERVICE_PID));
			// assertFalse("pid",
			// props.get(Constants.SERVICE_PID).equals(fpid1));
			// assertEquals("fpid", fpid1,
			// props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
			// assertEquals("value", "stringvalue", props.get("stringkey"));
			// assertNull("bundleLocation must be not included",
			// props.get("service.bundleLocation"));
			// assertEquals("Size of props must be 3", 3, props.size());

			trace("The configuration is being deleted. Wait for signal.");
			conf.delete();
			this.assertDeletedNoCallback(sync1_1, 0);
			// count1_1 = this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);

	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_14_1_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		// final Bundle bundleT4 = bInstaller.installBundle(
		// getWebServer() + "bundleT4.jar");
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_14_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?*", "target", bundleT2, false);
			this.setCPtoBundle("?RegionA", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNotNull("called back with Non-null props", sync2_1.getProps());
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());

			trace("conf is going to be deleted.");
			conf.delete();
			this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);
			this.assertDeletedCallback(sync2_1, 0);
			this.assertDeletedCallback(sync3_1, 0);
			this.assertDeletedCallback(sync3_2, 0);
	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_14_1_2()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		// final Bundle bundleT4 = bInstaller.installBundle(
		// getWebServer() + "bundleT4.jar");
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_14_1_2";

		int micro = 0;
		traceTestId(header, ++micro);
			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?RegionB", "target", bundleT2, false);
			this.setCPtoBundle("?RegionB", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("conf is going to be deleted.");
			conf.delete();
			this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);
			this.assertDeletedNoCallback(sync2_1, 0);
			this.assertDeletedNoCallback(sync3_1, 0);
			this.assertDeletedNoCallback(sync3_2, 0);
	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_14_2_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_14_2_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?*", "target", bundleT2, false);
			this.setCPtoBundle("?RegionA", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			this.startTargetBundle(bundleT1);
			sleep(1000);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNotNull("called back with Non-null props", sync2_1.getProps());
			this.startTargetBundle(bundleT3);
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());

			trace("conf is going to be deleted.");
			conf.delete();
			this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);
			this.assertDeletedCallback(sync2_1, 0);
			this.assertDeletedCallback(sync3_1, 0);
			this.assertDeletedCallback(sync3_2, 0);
	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_14_2_2()
			throws Exception {

		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_14_2_2";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("?RegionB", "target", bundleT2, false);
			this.setCPtoBundle("?RegionB", "target", bundleT3, false);
			// this.setCPtoBundle("?*", "target", bundleT2, false);
			// this.setCPtoBundle("?RegionA", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			this.startTargetBundle(bundleT1);
			sleep(1000);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("conf is going to be deleted.");
			conf.delete();
			this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);
			this.assertDeletedNoCallback(sync2_1, 0);
			this.assertDeletedNoCallback(sync3_1, 0);
			this.assertDeletedNoCallback(sync3_2, 0);

	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_15_1_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		// final Bundle bundleT3 = bInstaller.installBundle(
		//		getWebServer() + "bundleT3.jar");
		// final Bundle bundleT4 = bInstaller.installBundle(
		// getWebServer() + "bundleT4.jar");
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_15_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			//SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			// list.add(bc.registerService(Synchronizer.class.getName(),
			//		sync3_1, propsForSyncF3_1));
			//SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			// list.add(bc.registerService(Synchronizer.class.getName(),
			//		sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle("*", "target", bundleT2, false);
			//this.setCPtoBundle("*", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			//int count3_1 = 0;
			//int count3_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			//this.startTargetBundle(bundleT3);
			//assertNoCallback(sync3_1, count3_1);
			//assertNoCallback(sync3_2, count3_2);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, null);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			//assertNoCallback(sync3_1, count3_1);
			//assertNoCallback(sync3_2, count3_2);
//			assertEquals(
//					"The location of the conf must be dynamically bound to bundleT2",
//					bundleT2.getLocation(), conf.getBundleLocation());

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			sleep(1000);
			if(conf.getBundleLocation().equals(bundleT1.getLocation())){
				trace("Wait for signal.");
				count1_1 = assertCallback(sync1_1, count1_1);
				assertNotNull("called back with Non-null props", sync1_1.getProps());
				assertNoCallback(sync1_2, count1_2);
				assertNoCallback(sync2_1, count2_1);
				assertEquals(
						"The location of the conf must be dynamically bound to bundleT1",
						bundleT1.getLocation(), conf.getBundleLocation());
				trace("Bound bundleT1 is going to be stopped.");
				bundleT1.stop();
				bundleT1.uninstall();
				sleep(1000);
				assertEquals(
						"The location of the conf must be dynamically bound to bundleT2",
						bundleT2.getLocation(), conf.getBundleLocation());
				count2_1 = assertCallback(sync2_1, count2_1);
				trace("conf is going to be deleted.");
				conf.delete();
				this.assertDeletedCallback(sync2_1, 0);
				bundleT2.stop();
				bundleT2.uninstall();
			}else if(conf.getBundleLocation().equals(bundleT2.getLocation())){
				trace("Wait for signal.");
				assertNoCallback(sync1_1, count1_1);
				assertNoCallback(sync1_2, count1_2);
				count2_1 = assertCallback(sync2_1, count2_1);
				assertNotNull("called back with Non-null props", sync2_1.getProps());
				assertEquals(
						"The location of the conf must be dynamically bound to bundleT2",
						bundleT2.getLocation(), conf.getBundleLocation());
				trace("Bound bundleT2 is going to be stopped.");
				bundleT2.stop();
				bundleT2.uninstall();
				sleep(1000);
				assertEquals(
						"The location of the conf must be dynamically bound to bundleT1",
						bundleT1.getLocation(), conf.getBundleLocation());
				count1_1 = assertCallback(sync1_1, count1_1);
				assertNotNull("called back with Non-null props", sync1_1.getProps());
				assertNoCallback(sync1_2, count1_2);
				trace("conf is going to be deleted.");
				conf.delete();
				this.assertDeletedCallback(sync1_1, 0);
				this.assertDeletedNoCallback(sync1_2, 0);
				bundleT1.stop();
				bundleT1.uninstall();
			}else{
				fail();
			}

			//trace("Wait for signal.");
			//assertNoCallback(sync1_1, count1_1);
			//assertNoCallback(sync1_2, count1_2);
			//count2_1 = assertCallback(sync2_1, count2_1);
			//assertNotNull("called back with Non-null props", sync2_1.getProps());
			//assertNoCallback(sync3_1, count3_1);
			//assertNoCallback(sync3_2, count3_2);

			//trace("Bound bundleT2 is going to be stopped.");
			//bundleT2.stop();
			//assertEquals(
			//		"The location of the conf must be dynamically bound to bundleT3",
			//		bundleT3.getLocation(), conf.getBundleLocation());

			// TODO confirm the behavior when the MSF gets unvisible. Will
			// deleted(pid) be called back ?
			//assertNoCallback(sync1_1, count1_1);
			//assertNoCallback(sync1_2, count1_2);
			//int countDeleted2_1 = this.assertDeletedCallback(sync2_1, 0);
			//count3_1 = assertCallback(sync3_1, count3_1);
			//assertNotNull("called back with Non-null props", sync3_1.getProps());
			//count3_1 = assertCallback(sync3_2, count3_2);
			//assertNotNull("called back with Non-null props", sync3_2.getProps());

			//trace("conf is going to be deleted.");
			//conf.delete();
			//this.assertDeletedNoCallback(sync1_1, 0);
			//this.assertDeletedNoCallback(sync1_2, 0);
			//this.assertDeletedNoCallback(sync2_1, countDeleted2_1);
			//this.assertDeletedCallback(sync3_1, 0);
			//this.assertDeletedCallback(sync3_2, 0);

	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_15_1_2()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_15_1_2";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			int count1_1 = 0;
			int count1_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, null);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertEquals(
					"The location of the conf must be dynamically bound to bundleT1",
					bundleT1.getLocation(), conf.getBundleLocation());

			trace("Bound bundleT1 is going to be stopped.");
			bundleT1.stop();
			bundleT1.uninstall();
			sleep(1000);
			assertNull(
					"The location of the conf must be dynamically unbound to null.",
					conf.getBundleLocation());

	}


	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_15_2_1()
			throws Exception {
		// TODO impl
	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleTargets_15_3_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		// final Bundle bundleT4 = bInstaller.installBundle(
		// getWebServer() + "bundleT4.jar");
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleTargets_15_3_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("*", "target", bundleT1, false);
			this.setCPtoBundle(bundleT2.getLocation(), "target", bundleT2,
					false);
			this.setCPtoBundle("?", "target", bundleT3, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, "?");
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			this.printoutPermissions();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			this.startTargetBundle(bundleT1);
			sleep(1000);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			sleep(1000);
			trace("Wait for signal.");
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			sleep(1000);
			trace("Wait for signal.");
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());


	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleCF_16_1_1()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleCF_16_1_1";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("?RegionA", "target", bundleT1, false);
			this.setCPtoBundle("?RegionB", "target", bundleT2, false);
			this.setCPtoBundle(null, null, bundleT3, false);
			// this.setCPtoBundle(null, null, bundleT2, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configurations are being created");
			Configuration conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			Configuration conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, "?");
			Configuration conf3 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					bundleT3.getLocation());
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf1 is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf1.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf2 is being updated ");
			conf2.update(props);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			// count2_1 = assertCallback(sync2_1, count2_1);
			// assertNotNull("called back with Non-null props",
			// sync2_1.getProps());
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf3 is being updated ");
			conf3.update(props);
			trace("Wait for signal.");
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());

	}

	@Test
	public void testManagedServiceFactoryRegistrationMultipleCF_16_1_2()
			throws Exception {
		final Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final Bundle bundleT2 = bInstaller.installBundle("bundleT2.jar", false);
		final Bundle bundleT3 = bInstaller.installBundle("bundleT3.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(5);
		final String header = "testManagedServiceFactoryRegistrationMultipleCF_16_1_2";

		int micro = 0;
		traceTestId(header, ++micro);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));
			SynchronizerImpl sync2_1 = new SynchronizerImpl("2-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync2_1, propsForSyncF2_1));

			SynchronizerImpl sync3_1 = new SynchronizerImpl("3-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_1, propsForSyncF3_1));
			SynchronizerImpl sync3_2 = new SynchronizerImpl("3-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync3_2, propsForSyncF3_2));

			this.resetPermissions();
			this.setCPtoBundle("?*", "target", bundleT1, false);
			this.setCPtoBundle("?", "target", bundleT2, false);
			this.setCPtoBundle(null, null, bundleT3, false);
			// this.setCPtoBundle(null, null, bundleT2, false);
			int count1_1 = 0;
			int count1_2 = 0;
			int count2_1 = 0;
			int count3_1 = 0;
			int count3_2 = 0;

			this.startTargetBundle(bundleT1);
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			this.startTargetBundle(bundleT2);
			assertNoCallback(sync2_1, count2_1);
			this.startTargetBundle(bundleT3);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The configurations are being created");
			Configuration conf1 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			Configuration conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, "?");
			Configuration conf3 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					bundleT3.getLocation());
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf1 is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf1.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);
			assertNoCallback(sync2_1, count2_1);
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf2 is being updated ");
			conf2.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with Non-null props", sync1_1.getProps());
			// assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);
			count2_1 = assertCallback(sync2_1, count2_1);
			assertNotNull("called back with Non-null props", sync2_1.getProps());
			assertNoCallback(sync3_1, count3_1);
			assertNoCallback(sync3_2, count3_2);

			trace("The conf3 is being updated ");
			conf3.update(props);
			trace("Wait for signal.");
			count3_1 = assertCallback(sync3_1, count3_1);
			assertNotNull("called back with Non-null props", sync3_1.getProps());
			count3_2 = assertCallback(sync3_2, count3_2);
			assertNotNull("called back with Non-null props", sync3_2.getProps());

	}

	@Test
	public void testManagedServiceFactoryCmRestart18_3_2() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);

		final String header = "testManagedServiceFactoryCmRestart18_3_2";

		int micro = 0;
		traceTestId(header, ++micro);


			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					bundleT1.getLocation());
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			trace("CM Bundle is going to start.");
			Bundle cmBundle = stopCmBundle();

			this.setCPtoBundle("*", "target", bundleT1, false);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			int count1_1 = 0;
			int count1_2 = 0;

			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			assertNoCallback(sync1_2, count1_2);

			trace("CM Bundle is going to start.");
			startCmBundle(cmBundle);

			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);


	}

	@Test
	public void testManagedServiceFactoryModifyPid18_4_2() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		final String fpid2 = Util.createPid("factoryPid2");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);
		System.out.println(bundleT1.getState());
		final String header = "testManagedServiceFactoryModifyPid18_4_2";

		int micro = 0;
		traceTestId(header, ++micro);


		System.out.println(bundleT1.getState());
			this.setCPtoBundle("*", "target", bundleT1, false);
		System.out.println(bundleT1.getState());
			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			int count1_1 = 0;
			int count1_2 = 0;

			trace("Bundle is going to start.");
			System.out.println(bundleT1.getState());
			this.startTargetBundle(bundleT1);
			System.out.println(bundleT1.getState());
			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1, "?");
			// Configuration conf =
			// cmA.waitForService(500l).createFactoryConfiguration(fpid1,bundleT1.getLocation());
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			// modify PID
			modifyPid(ManagedServiceFactory.class.getName(), fpid2, fpid1);

			// ServiceReference[] references = this.bc
			// .getServiceReferences(
			// ManagedServiceFactory.class.getName(), null);

			trace("Wait for signal.");
			assertNoCallback(sync1_1, count1_1);
			count1_2 = assertCallback(sync1_2, count1_2);
			assertNotNull("called back with NON-null props", sync1_2.getProps());
			System.out.println(bundleT1.getState());

	}

	@Test
	public void testManagedServiceFactoryDeletion18_5_2() throws Exception {
		Bundle bundleT1 = bInstaller.installBundle("bundleT1.jar", false);
		final String fpid1 = Util.createPid("factoryPid1");
		List<ServiceRegistration< ? >> list = new ArrayList<>(3);

		final String header = "testManagedServiceFactoryDeletion18_5_2";

		int micro = 0;
		traceTestId(header, ++micro);


			trace("The configuration is being created");
			Configuration conf = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("The configuration is being updated ");
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("StringKey", "stringvalue");
			conf.update(props);

			this.setCPtoBundle("*", "target", bundleT1, false);

			SynchronizerImpl sync1_1 = new SynchronizerImpl("F1-1");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_1, propsForSyncF1_1));
			SynchronizerImpl sync1_2 = new SynchronizerImpl("F1-2");
			list.add(bc.registerService(Synchronizer.class.getName(),
					sync1_2, propsForSyncF1_2));

			int count1_1 = 0;
			int count1_2 = 0;

			trace("Bundle is going to start.");
			this.startTargetBundle(bundleT1);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			trace("The configuration is being deleted. Wait for signal.");
			conf.delete();

			int countDeteted1_1 = this.assertDeletedCallback(sync1_1, 0);
			this.assertDeletedNoCallback(sync1_2, 0);

			trace("The configuration is being created");
			Configuration conf2 = cmA.waitForService(500l)
					.createFactoryConfiguration(fpid1,
					"?RegionA");
			trace("The configuration is being updated ");
			conf2.update(props);
			trace("Wait for signal.");
			count1_1 = assertCallback(sync1_1, count1_1);
			assertNotNull("called back with NON-null props", sync1_1.getProps());
			assertNoCallback(sync1_2, count1_2);

			trace("Change permission of bundleT1 ");
			this.setCPtoBundle(null, null, bundleT1, false);
			trace("The configuration is being deleted. Wait for signal.");
			conf2.delete();
			this.assertDeletedNoCallback(sync1_1, countDeteted1_1);
			this.assertDeletedNoCallback(sync1_2, 0);

	}

	/**
	 * Configuration attributes - locking
	 * @since 1.6
	 */
	@Test
	public void testConfigurationLocking() throws Exception {
		final String pid = Util.createPid("locking");
		final List<ConfigurationEvent> events = new ArrayList<>();
		final List<Dictionary<String, ?>> updates = new ArrayList<>();
		final CyclicBarrier barrier = new CyclicBarrier(2);

		final SynchronousConfigurationListener listener = new SynchronousConfigurationListener() {

			@Override
			public void configurationEvent(final ConfigurationEvent event) {
				if (pid.equals(event.getPid())) {
					synchronized (events) {
						events.add(event);
					}
				}

			}
		};
		final ManagedService ms = new ManagedService() {

			@Override
			public void updated(final Dictionary<String, ?> properties) throws ConfigurationException {
    			updates.add(properties);
				try {
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					throw new RuntimeException(e);
				}
			}

		};
		bc.registerService(
				SynchronousConfigurationListener.class.getName(),
				listener, null);
	    final Hashtable<String,Object> msProps = new Hashtable<String,Object>();
	    msProps.put(Constants.SERVICE_PID, pid);
		ServiceRegistration< ? > regms = bc.registerService(
				ManagedService.class.getName(),
				ms, msProps);
		// wait for managed service to get null
		barrier.await();
		barrier.reset();

		final Configuration conf = cmA.waitForService(500l)
				.getConfiguration(pid);
		cmA.waitForService(500l).getConfiguration(pid);
			final long startLevel = conf.getChangeCount();
			final Hashtable<String,Object> newprops = new Hashtable<String,Object>();
			newprops.put("somekey", "somevalue");
			conf.update(newprops);
			assertEquals(startLevel + 1, conf.getChangeCount());
			assertEquals(1, events.size());

			// wait for managed service
			barrier.await();
			barrier.reset();

			// check attributes
			assertTrue(conf.getAttributes().isEmpty());
			
			// lock configuration
			conf.addAttributes(Configuration.ConfigurationAttribute.READ_ONLY);
			assertEquals(1, conf.getAttributes().size());
			assertTrue(conf.getAttributes().contains(Configuration.ConfigurationAttribute.READ_ONLY));

			// try to update
			try {
				conf.update(newprops);
				fail();
			} catch ( final ReadOnlyConfigurationException e) {
				// expected
			}
			// nothing should have changed
			assertEquals(startLevel + 1, conf.getChangeCount());
			assertEquals(1, events.size());

            // unlock
			conf.removeAttributes(Configuration.ConfigurationAttribute.READ_ONLY);
			assertTrue(conf.getAttributes().isEmpty());
			
            // try update 
			conf.update(newprops);
			assertEquals(startLevel + 2, conf.getChangeCount());
			assertEquals(2, events.size());
			
			// check updates to managed service
			barrier.await();

			assertNull(updates.get(0));
			assertNotNull(updates.get(1));
			assertNotNull(updates.get(2));
			// TODO BJ:WHY MUST THIS BE DONE MANUALLY????
			regms.unregister();
	}


	@Test
	public void testProvideImplementationCapability() throws Exception {
	    // get the bundle revision for the CA bundle by obtaining the CA service
		final BundleRevision rev = cmA.getServiceReference()
				.getBundle()
				.adapt(BundleRevision.class);

		final List<Capability> capabilities = rev.getCapabilities(ImplementationNamespace.IMPLEMENTATION_NAMESPACE);

		boolean found = false;

		for (final Capability capability : capabilities) {
			final Map<String, Object> attributes = capability.getAttributes();
			final String name = (String) attributes.get(ImplementationNamespace.IMPLEMENTATION_NAMESPACE);

			if ("osgi.cm".equals(name) ) {
				final Version version = (Version)attributes.get("version");

				if (version != null && version.equals(new Version("1.6.0"))) {
    				final Map<String, String> directives = capability.getDirectives();
	    			final List<String> packages = Arrays.asList(directives.get("uses").split(","));

					assertTrue(packages.contains("org.osgi.service.cm"));

					found = true;
				}
			}
		}

		assertTrue(found);
	}

	@Test
	public void testProvideServiceCapability() throws Exception {
	    // get the bundle revision for the CA bundle by obtaining the CA service
		final ServiceReference<ConfigurationAdmin> srA = bc
				.getServiceReference(ConfigurationAdmin.class);
		final BundleRevision rev = srA.getBundle().adapt(BundleRevision.class);
		final List<Capability> capabilities = rev.getCapabilities(ServiceNamespace.SERVICE_NAMESPACE);

		boolean found = false;

		for (final Capability capability : capabilities) {
			final Map<String, Object> attributes = capability.getAttributes();
			@SuppressWarnings("unchecked")
			final List<String> objectClasses = (List<String>) attributes.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);

			if ((objectClasses != null) && objectClasses.contains(ConfigurationAdmin.class.getName())) {
				final Map<String, String> directives = capability.getDirectives();
				final List<String> packages = Arrays.asList(directives.get("uses").split(","));

				assertTrue(packages.contains("org.osgi.service.cm"));

				found = true;
			}
		}

		assertTrue(found);
	}
	
	public Bundle thisBundle() {
		return bc.getBundle();
	}

	public String thisLocation() {
		return thisBundle().getLocation();
	}
	
	private long getLongProperty(String string, int i) {
		return i;
	}

	private String getName() {
		return name;
	}

}

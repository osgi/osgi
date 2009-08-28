/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.cm.junit;

// import java.math.*;
import java.io.IOException;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.PropertyPermission;

import junit.framework.AssertionFailedError;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPermission;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.SynchronizerImpl;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author Ikuo YAMASAKI, NTT Corporation, added many tests.
 * 
 */
public class CMControl extends DefaultTestBundleControl {
	private ConfigurationAdmin cm;
	private PermissionAdmin permAdmin;
	private static final long SIGNAL_WAITING_TIME = 5000;
	private List list;
	private boolean permissionFlag;
	private Bundle setAllPermissionBundle;

	private static final String SP = ServicePermission.class.getName();
	private static final String PP = PackagePermission.class.getName();
	private static final String AP = AdminPermission.class.getName();
	private static final String CP = ConfigurationPermission.class.getName();
	private static final Dictionary propsForSync1;
	static {
		propsForSync1 = new Hashtable();
		propsForSync1.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync1");
	}
	private static final Dictionary propsForSync2;
	static {
		propsForSync2 = new Hashtable();
		propsForSync2.put(
				org.osgi.test.cases.cm.shared.Constants.SERVICEPROP_KEY_SYNCID,
				"sync2");
	}
	private static final String neverlandLocation = "http://neverneverland/";

	protected void setUp() throws Exception {
		cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
		// populate the created configurations so that
		// listConfigurations can return these configurations
		list = new ArrayList(5);

		permAdmin = (PermissionAdmin) getService(PermissionAdmin.class);
		setAllPermissionBundle = getContext().installBundle(
				getWebServer() + "setallpermission.jar");

	}

	protected void tearDown() throws Exception {
		resetPermissions();
		if (this.setAllPermissionBundle != null) {
			this.setAllPermissionBundle.uninstall();
			this.setAllPermissionBundle = null;
		}
		ungetService(permAdmin);
		list = null;

		cleanCM();
		unregisterAllServices();
		ungetService(cm);
	}

	private void resetPermissions() throws BundleException {
		try {
			if (this.setAllPermissionBundle == null)
				this.setAllPermissionBundle = getContext().installBundle(
						getWebServer() + "setallpermission.jar");
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

	private void setAllpermission(String bundleLocation) {
		ServiceReference ref = getContext().getServiceReference(
				PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) getContext().getService(ref);
		PermissionInfo[] pisAllPerm = new PermissionInfo[1];
		pisAllPerm[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin.setPermissions(bundleLocation, pisAllPerm);
	}

	private void printoutPermissions() {
		String[] locations = this.permAdmin.getLocations();
		if (locations != null)
			for (int i = 0; i < locations.length; i++) {
				System.out.println("locations[" + i + "]=" + locations[i]);
				PermissionInfo[] pInfos = this.permAdmin
						.getPermissions(locations[i]);
				for (int j = 0; j < pInfos.length; j++) {
					System.out.println("\t" + pInfos[j]);
				}
			}
		PermissionInfo[] pInfos = this.permAdmin.getDefaultPermissions();
		if (pInfos == null)
			System.out.println("default permission=null");
		else {
			System.out.println("default permission=");
			for (int j = 0; j < pInfos.length; j++) {
				System.out.println("\t" + pInfos[j]);
			}
		}

	}

	private void setBundlePermission(Bundle b, List list) {
		PermissionInfo[] pis = new PermissionInfo[list.size()];
		pis = (PermissionInfo[]) list.toArray(pis);
		permAdmin.setPermissions(b.getLocation(), pis);
		this.printoutPermissions();
	}

	private void add(List permissionsInfos, String clazz, String name,
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
	public void testDeletedConfiguration() throws Exception {
		String pid = Util.createPid();
		Configuration conf = null;
		/* Get a brand new configuration and delete it. */
		conf = cm.getConfiguration(pid);
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
				new MethodCall(Configuration.class, "setBundleLocation",
						String.class, "somelocation"),
				new MethodCall(Configuration.class, "update"),
				new MethodCall(Configuration.class, "update", Dictionary.class,
						new Hashtable()) };
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
	public void testGetConfiguration() throws Exception {
		this.setInappropriatePermission();

		String pid = Util.createPid();
		String thisLocation = getLocation();
		Configuration conf = null;
		/* Get a brand new configuration */
		conf = cm.getConfiguration(pid);

		checkConfiguration(conf, "A new Configuration object", pid,
				thisLocation);

		/* Get location of the configuration */
		/* must fail because of inappropriate Permission. */
		String message = "try to get location without appropriate ConfigurationPermission.";
		try {
			conf.getBundleLocation();
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		/* Get the configuration again (should be exactly the same) */
		conf = cm.getConfiguration(pid);
		checkConfiguration(conf, "The same Configuration object", pid,
				thisLocation);

		/*
		 * Change the location of the bundle and then get the Configuration
		 * again. The location should not have been touched.
		 */
		/* must fail because of inappropriate Permission. */
		message = "try to set location without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(neverlandLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();
		conf.setBundleLocation(neverlandLocation);

		conf = cm.getConfiguration(pid);
		assertEquals("Location Neverland", neverlandLocation, this
				.getBundleLocationForCompare(conf));

		this.setInappropriatePermission();
		/* must fail because of inappropriate Permission. */
		message = "try to get configuration whose location is different from the caller bundle without appropriate ConfigurationPermission.";
		try {
			conf = cm.getConfiguration(pid);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
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
	public void testGetConfigurationWithLocation() throws Exception {
		final String pid1 = Util.createPid("1");
		final String pid2 = Util.createPid("2");
		final String pid3 = Util.createPid("3");
		final String thisLocation = getLocation();
		Configuration conf = null;

		this.printoutPermissions();
		this.setInappropriatePermission();

		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration.
		 */
		String message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cm.getConfiguration(pid1, thisLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();

		/* Get a brand new configuration */
		conf = cm.getConfiguration(pid1, thisLocation);
		checkConfiguration(conf, "A new Configuration object", pid1,
				thisLocation);

		this.setInappropriatePermission();
		/*
		 * Without appropriate ConfigurationPermission, Get an existing
		 * configuration, but specify the location (which should then be
		 * ignored) without appropriate ConfigurationPermission.
		 */
		message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cm.getConfiguration(pid1, neverlandLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();
		/*
		 * Get an existing configuration, but specify the location (which should
		 * then be ignored)
		 */
		conf = cm.getConfiguration(pid1, neverlandLocation);
		checkConfiguration(conf, "The same Configuration object", pid1,
				thisLocation);
		conf.delete();

		this.setInappropriatePermission();
		/*
		 * Without appropriate ConfigurationPermission, Get a brand new
		 * configuration with a specified location
		 */
		message = "try to get configuration without appropriate ConfigurationPermission.";
		try {
			conf = cm.getConfiguration(pid2, neverlandLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();

		/* Get a brand new configuration with a specified location */
		conf = cm.getConfiguration(pid2, neverlandLocation);
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
			conf = cm.getConfiguration(pid3, null);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();
		/* Get a brand new configuration with no location */
		conf = cm.getConfiguration(pid3, null);
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
			conf = cm.getConfiguration(bundlePid, null);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		this.setAppropriatePermission();
		/* Get a brand new configuration with no location */
		conf = cm.getConfiguration(bundlePid, null);
		checkConfiguration(conf, "A new Configuration object", bundlePid, null);

		Dictionary props = new Hashtable();
		props.put("StringKey", getName());
		conf.update(props);

		/* Get existing Configuration with null location */
		Configuration conf3 = cm.getConfiguration(bundlePid, null);
		assertEquals("Pid", bundlePid, conf3.getPid());
		assertNull("FactoryPid", conf3.getFactoryPid());
		assertNull("Location", this.getBundleLocationForCompare(conf3));
		assertEquals("The same Confiuration props", getName(), conf3
				.getProperties().get("StringKey"));

		this.setInappropriatePermission();

		/* Get location of the configuration with null location */
		/* must fail because of inappropriate Permission. */
		message = "try to get location without appropriate ConfigurationPermission.";
		try {
			conf.getBundleLocation();
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}

		message = "try to get configuration with null location without appropriate ConfigurationPermission.";
		Configuration conf4 = cm.getConfiguration(bundlePid);
		/* In order to get Location, appropriate permission is required. */
		this.setAppropriatePermission();

		/* location MUST be changed to the callers bundle's location. */
		assertEquals("Location", thisLocation, this
				.getBundleLocationForCompare(conf4));

		// this.setAppropriatePermission();
		// conf3 = cm.getConfiguration(pid3);
		assertEquals("Location", thisLocation, this
				.getBundleLocationForCompare(conf3));

		this.setInappropriatePermission();
		/* Set location of the configuration to null location */
		/* must fail because of inappropriate Permission. */
		message = "try to set location to null without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(null);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}
		this.setInappropriatePermission();
		this.setAppropriatePermission();
		conf.setBundleLocation(null);

		this.setInappropriatePermission();
		/* Set location of the configuration with null location to others */
		/* must fail because of inappropriate Permission. */
		message = "try to set location to null without appropriate ConfigurationPermission.";
		try {
			conf.setBundleLocation(thisLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}
		try {
			conf.setBundleLocation(neverlandLocation);
			/* A SecurityException should have been thrown */
			failException(message, SecurityException.class);
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, SecurityException.class, e);
		}
		conf.delete();
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
	public void testDynamicBinding() throws Exception {
		this.setAppropriatePermission();
		this.cleanCM();

		Bundle bundle1 = getContext().installBundle(
				getWebServer() + "targetb1.jar");

		final String bundlePid = Util.createPid("bundlePid1");
		Configuration conf = null;
		ServiceRegistration reg = null;
		Dictionary props = null;

		/*
		 * 1. created newly with non-null location and after set to null. After
		 * that, ManagedService is registered.
		 */
		trace("############ 1 testDynamicBinding()");
		try {
			conf = cm.getConfiguration(bundlePid, bundle1.getLocation());

			props = new Hashtable();
			props.put("StringKey", getName() + "-1");
			conf.update(props);
			conf.setBundleLocation(null);

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
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
			assertEquals("Dynamic binding(STOPPED). Wait for a while.", conf
					.getBundleLocation(), bundle1.getLocation());
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			/*
			 * After the dynamically bound bundle has been uninstalled, the
			 * location must be reset to null.
			 */
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf = cm.getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}

		/* 2. created newly with null location.(with properties) */
		trace("############ 2 testDynamicBinding()");
		try {
			conf = cm.getConfiguration(bundlePid, null);
			/* props is set. */
			props = new Hashtable();
			props.put("StringKey", getName() + "-2");
			conf.update(props);
			reg = null;

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED).Wait for a while.", conf
					.getBundleLocation(), bundle1.getLocation());
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf = cm.getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
			conf = null;
		}
		/* 3. created newly with null location. (no properties) */
		trace("############ 3 testDynamicBinding()");
		try {
			conf = cm.getConfiguration(bundlePid, null);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			assertEquals("Dynamic binding(STARTED)", bundle1.getLocation(),
					conf.getBundleLocation());

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED).Wait for a while.", conf
					.getBundleLocation(), bundle1.getLocation());
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			assertNull("Dynamic binding(UNINSTALLED)", conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf = cm.getConfiguration(bundlePid);
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
			conf = cm.getConfiguration(bundlePid, null);
			props = new Hashtable();
			props.put("StringKey", getName() + "-4");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
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
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			assertEquals("No more Dynamic binding(UNINSTALLED)", bundle1
					.getLocation(), conf.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			// conf = cm.getConfiguration(bundlePid);
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
			conf = cm.getConfiguration(bundlePid, null);
			props = new Hashtable();
			props.put("StringKey", getName() + "-5");
			conf.update(props);
			reg = null;

			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			assertTrue("ManagedService MUST be called back.", calledback);
			cmBundle = getCmBundle();
			bundle1.stop();

			cmBundle.stop();

			cmBundle.start();
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			Thread.sleep(SIGNAL_WAITING_TIME * 2);
			Configuration[] confs = cm.listConfigurations(null);
			assertNotNull("confs must NOT be empty:", confs);
			assertEquals("confs.lenght must be one", 1, confs.length);
			trace("confs[0].getBundleLocation()="
					+ confs[0].getBundleLocation());
			assertEquals(
					"Dynamic binding(UNINSTALLED):confs[0].getBundleLocation() must be the target bundle",
					bundle1.getLocation(), confs[0].getBundleLocation());
			conf = cm.getConfiguration(bundlePid);
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
			cmBundle.start();
			cmBundle = null;
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			conf = cm.getConfiguration(bundlePid);
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
			conf = cm.getConfiguration(bundlePid, null);
			props = new Hashtable();
			props.put("StringKey", getName() + "-6");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			cmBundle = getCmBundle();
			cmBundle.stop();
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			bundle1.stop();
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			cmBundle.start();
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			Thread.sleep(SIGNAL_WAITING_TIME * 2);
			Configuration[] confs = cm.listConfigurations(null);
			assertNotNull("confs must NOT be empty:", confs);
			assertEquals("confs.lenght must be one", 1, confs.length);
			trace("confs[0].getBundleLocation()="
					+ confs[0].getBundleLocation());
			assertEquals(
					"Dynamic binding(UNINSTALLED):confs[0].getBundleLocation() must be null",
					null, confs[0].getBundleLocation());
			conf = cm.getConfiguration(bundlePid);
			assertEquals("Dynamic binding(UNINSTALLED): Must be Re-bound",
					getContext().getBundle().getLocation(), conf
							.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			cmBundle.start();
			cmBundle = null;
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			conf = cm.getConfiguration(bundlePid);
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
			conf = cm.getConfiguration(bundlePid, null);
			// props = new Hashtable();
			// props.put("StringKey", getName()+"-7");
			// conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME);
			cmBundle = getCmBundle();
			cmBundle.stop();
			assertTrue(
					"ManagedService MUST be called back in case conf has no properties.",
					calledback);
			bundle1.stop();
			Thread.sleep(SIGNAL_WAITING_TIME);
			bundle1.uninstall();
			trace("Target Bundle is uninstalled. Wait for a while to check unbound.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			cmBundle.start();
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			Thread.sleep(SIGNAL_WAITING_TIME * 2);
			conf = cm.getConfiguration(bundlePid, null);
			assertEquals("Dynamic binding(UNINSTALLED): Must be reset to null",
					null, conf.getBundleLocation());
			conf = cm.getConfiguration(bundlePid);
			assertEquals("Dynamic binding(UNINSTALLED): Must be Re-bound",
					getContext().getBundle().getLocation(), conf
							.getBundleLocation());
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			if (bundle1 != null && bundle1.getState() != Bundle.UNINSTALLED)
				bundle1.uninstall();
			bundle1 = null;
			cmBundle.start();
			cmBundle = null;
			cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			conf = cm.getConfiguration(bundlePid);
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
		ServiceRegistration reg2 = null;
		Bundle bundle2 = null;
		try {
			conf = cm.getConfiguration(bundlePid, getWebServer()
					+ "targetb1.jar");
			props = new Hashtable();
			props.put("StringKey", getName() + "-8");
			conf.update(props);

			SynchronizerImpl sync = new SynchronizerImpl("ID1");
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");
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
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			assertFalse("ManagedService MUST NOT be called back.", calledback);
			assertEquals("Must be still bound to the target bundle.", bundle1
					.getLocation(), conf.getBundleLocation());

			bundle2 = getContext().installBundle(
					getWebServer() + "targetb2.jar");
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = getContext().registerService(Synchronizer.class.getName(),
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME,
					count2 + 1);
			assertFalse(
					"ManagedService MUST NOT be called back in case conf has no properties.",
					calledback2);

			bundle1.stop();
			assertEquals("Dynamic binding(STOPPED). Wait for a while.", conf
					.getBundleLocation(), bundle1.getLocation());
			calledback = sync2.waitForSignal(SIGNAL_WAITING_TIME, count2 + 1);
			assertFalse("ManagedService2 MUST NOT be called back.", calledback);
			bundle1.uninstall();
			trace("Dynamic binding(UNINSTALLED). Wait for a while.");
			calledback2 = sync2
					.waitForSignal(SIGNAL_WAITING_TIME * 2, ++count2);
			/*
			 * Open issue. Should the newly dynamically bound ManagedService be
			 * called back ? Ikuo thinks yes while BJ thinks no. The
			 * implementator of this CT (Ikuo) thinks CT should not be strict in
			 * this point because it is not clear in the spec of version 1.3.
			 */
			// assertTrue("ManagedService MUST be called back.", calledback2);
			if (!calledback2) {
				count2--;
			}

			/*
			 * Open Issue: Ikuo thinks the Conf which got unbound from bundle1
			 * should get bound to any of other target bundles if other target
			 * bundle exists. However Felix thinks not (when Conf#update(props)
			 * is called, it will be bound. The implementator of this CT (Ikuo)
			 * thinks CT should not be strict in this point because it is not
			 * clear in the spec of version 1.3.
			 */

			// assertEquals("Dynamic binding(UNINSTALLED). Wait for a while.",
			// bundle2.getLocation(), conf.getBundleLocation());
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
			conf = cm.getConfiguration(bundlePid);
			if (conf != null)
				conf.delete();
		}

	}

	private void startTargetBundle(Bundle bundle) throws BundleException {
		if (this.permissionFlag)
			bundle.start();
		else {
			this.setAppropriatePermission();
			bundle.start();
			this.setInappropriatePermission();
		}
	}

	private void setInappropriatePermission() throws BundleException {
		this.resetPermissions();
		list.clear();
		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");
		// add(list, CP, "*", "CONFIGURE");
		add(list, AP, "*", "*");
		permissionFlag = false;
		this.setBundlePermission(super.getContext().getBundle(), list);
	}

	private void setAppropriatePermission() throws BundleException {
		this.resetPermissions();
		list.clear();
		add(list, PropertyPermission.class.getName(), "*", "READ,WRITE");
		add(list, PP, "*", "IMPORT,EXPORTONLY");
		add(list, SP, "*", "GET,REGISTER");
		add(list, CP, "*", "CONFIGURE");
		add(list, AP, "*", "*");
		permissionFlag = true;
		this.setBundlePermission(super.getContext().getBundle(), list);

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
	public void testUpdate() throws Exception {
		String pid = Util.createPid();
		Configuration conf = cm.getConfiguration(pid);
		Dictionary props = conf.getProperties();
		assertNull("Properties in conf", props);

		Hashtable newprops = new Hashtable();
		newprops.put("somekey", "somevalue");
		conf.update(newprops);
		props = conf.getProperties();
		assertNotNull("Properties in conf", props);
		assertEquals("conf property 'somekey'", "somevalue", props
				.get("somekey"));
		Configuration conf2 = cm.getConfiguration(pid);
		Dictionary props2 = conf2.getProperties();
		assertNotNull("Properties in conf2", props2);
		assertEquals("conf2 property 'somekey'", "somevalue", props2
				.get("somekey"));
		// assertSame("Same configurations", conf, conf2);
		// assertEquals("Equal configurations", conf, conf2);
		// assertEquals("Equal pids", conf.getPid(), conf2.getPid());
		assertTrue("Equal configurations", equals(conf, conf2));

		/* check returned properties are copied ones. */
		Dictionary props3 = conf2.getProperties();
		props3.put("KeyOnly3", "ValueOnly3");
		assertTrue("Properties are copied", props2.get("KeyOnly3") == null);

		/* Try to update with illegal configuration types */
		Hashtable illegalprops = new Hashtable();
		Collection v = new ArrayList();
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
		props2.put("SomeKey", "SomeValue");
		message = "updating with illegal properties (case variants of the same key)";
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

	}

	/**
	 * Tests if we really get the same configuration.
	 * 
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update()
	 * @spec Configuration.getPid()
	 * @throws Exception
	 */
	public void testEquals() throws Exception {
		String pid = Util.createPid();
		Configuration conf1 = cm.getConfiguration(pid);
		Configuration conf2 = cm.getConfiguration(pid);
		assertEquals("Equal configurations", conf1, conf2);
		assertTrue("Equal configurations", equals(conf1, conf2));
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

		List configs = new ArrayList(2);
		configs.add(cm.getConfiguration(pid11));
		configs.add(cm.getConfiguration(pid12));

		List updatedConfigs2 = new ArrayList(2);
		updatedConfigs2.add(cm.getConfiguration(pid21));
		updatedConfigs2.add(cm.getConfiguration(pid22));

		Configuration otherConf = cm.getConfiguration(otherPid);

		/* location is different */
		List updatedConfigs3 = new ArrayList(2);
		updatedConfigs3.add(cm.getConfiguration(pid31, neverlandLocation));
		updatedConfigs3.add(cm.getConfiguration(pid32, neverlandLocation));

		/*
		 * Update properties on some of configurations (to make them "active")
		 */
		for (int i = 0; i < updatedConfigs2.size(); i++) {
			Hashtable props = new Hashtable();
			props.put("someprop" + i, "somevalue" + i);
			((Configuration) updatedConfigs2.get(i)).update(props);
		}

		for (int i = 0; i < updatedConfigs3.size(); i++) {
			Hashtable props = new Hashtable();
			props.put("someprop" + i, "somevalue" + i);
			((Configuration) updatedConfigs3.get(i)).update(props);
		}

		try {
			Configuration[] confs = cm.listConfigurations("(service.pid="
					+ Util.createPid() + "*)");
			/*
			 * Returned list must contain all of updateConfigs2 and
			 * updateConfigs3.
			 */
			checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
					updatedConfigs3, null);

			/* Inappropriate Permission */
			this.setInappropriatePermission();
			confs = cm.listConfigurations("(service.pid=" + Util.createPid()
					+ "*)");
			/* Returned list must contain all of updateConfigs2. */
			checkIfAllUpdatedConfigs2isListed(confs, updatedConfigs2,
					updatedConfigs3, null);

			/* Appropriate Permission */
			this.setAppropriatePermission();
			confs = cm.listConfigurations(null);
			/*
			 * Returned list must contain all of updateConfigs2, updateConfigs3
			 * and otherConf.
			 */
			checkIfAllUpdatedConfigs2and3isListed(confs, updatedConfigs2,
					updatedConfigs3, otherConf);

			/* Inappropriate Permission */
			this.setInappropriatePermission();
			confs = cm.listConfigurations(null);
			/* Returned list must contain all of updateConfigs2 and otherConf. */
			checkIfAllUpdatedConfigs2isListed(confs, updatedConfigs2,
					updatedConfigs3, otherConf);

			/* if the filter string is in valid */
			/* must fail because of inappropriate Permission. */
			String message = "try to list configurations by invalid filter string.";
			try {
				cm.listConfigurations("(service.pid=" + Util.createPid() + "*");
				/* A SecurityException should have been thrown */
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
				((Configuration) configs.get(i)).delete();
			}
			for (int i = 0; i < updatedConfigs2.size(); i++) {
				((Configuration) updatedConfigs2.get(i)).delete();
			}
			for (int i = 0; i < updatedConfigs3.size(); i++) {
				((Configuration) updatedConfigs3.get(i)).delete();
			}
			otherConf.delete();
		}
		this.setAppropriatePermission();
		/* List all configurations and make sure they are all gone */
		Configuration[] leftConfs = cm
				.listConfigurations("(|(service.pid=" + pid11
						+ ")(service.pid=" + pid12 + ")(service.pid=" + pid21
						+ ")(service.pid=" + pid22 + ")(service.pid=" + pid31
						+ ")(service.pid=" + pid32 + ")(service.pid="
						+ otherPid + "))");
		assertNull("Left configurations", leftConfs);

	}

	private void checkIfAllUpdatedConfigs2isListed(Configuration[] confs,
			List updatedConfigs2, List updatedConfigs3, Configuration otherConf)
			throws IOException, InvalidSyntaxException {
		boolean otherFlag = false;
		List removedConfigs2 = new ArrayList(updatedConfigs2.size());
		confs = cm
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
			List updatedConfigs2, List updatedConfigs3, Configuration otherConf)
			throws IOException, InvalidSyntaxException {
		/*
		 * List all configurations and make sure that only the active
		 * configurations are returned
		 */
		List removedConfigs2 = new ArrayList(updatedConfigs2.size());
		List removedConfigs3 = new ArrayList(updatedConfigs3.size());
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
			} else {
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
	public void testManagedServiceRegistration() throws Exception {
		this.setAppropriatePermission();
		this.cleanCM();
		final String pid = Util.createPid("somepid");

		/* create a configuration in advance, then register ManagedService */
		Configuration conf = cm.getConfiguration(pid);

		trace("created configuration has null properties.");
		trace("Create and register a new the ManagedService");

		Semaphore semaphore = new Semaphore();
		ManagedServiceImpl ms = createManagedService(pid, semaphore);
		trace("Wait until the ManagedService has gotten the update");
		boolean calledBack = semaphore.waitForSignal(SIGNAL_WAITING_TIME);
		assertTrue("ManagedService is called back", calledBack);

		trace("Update done!");
		conf.delete();

		final String pid2 = Util.createPid("somepid2");
		Configuration conf2 = cm.getConfiguration(pid2);

		Hashtable props = new Hashtable();
		props.put("somekey", "somevalue");
		props.put("CAPITALkey", "CAPITALvalue");
		conf2.update(props);
		trace("created configuration has non-null properties.");
		trace("Create and register a new the ManagedService");
		trace("Wait until the ManagedService has gotten the update");
		semaphore = new Semaphore();
		ms = createManagedService(pid2, semaphore);
		semaphore.waitForSignal();
		trace("Update done!");

		/*
		 * Add the two properties added by the CM and then check for equality in
		 * the properties
		 */
		props.put(Constants.SERVICE_PID, pid2);
		// props.put(SERVICE_BUNDLE_LOCATION, "cm_TBC"); R3 does not include
		// service.bundleLocation anymore!
		assertEqualProperties("Properties equal?", props, ms.getProperties());
		trace((String) ms.getProperties().get("service.pid"));
		// trace((String) ms.getProperties().get("service.bundleLocation"));
		trace(this.getBundleLocationForCompare(conf2));
		/* OUTSIDE_OF_SPEC */
		// assertNotSame("Properties same?", props, ms.getProperties());
		conf2.delete();

	}

	private void printoutPropertiesForDebug(SynchronizerImpl sync) {
		Dictionary props1 = sync.getProps();
		if (props1 == null) {
			System.out.println("props = null");
		} else {
			System.out.println("props = ");
			for (Enumeration enums = props1.keys(); enums.hasMoreElements();) {
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
	public void testManagedServiceRegistration2() throws Exception {
		this.setAppropriatePermission();
		this.cleanCM();
		Bundle bundle = getContext().installBundle(
				getWebServer() + "targetb1.jar");
		final String bundlePid = Util.createPid("bundlePid1");
		ServiceRegistration reg = null;
		/*
		 * A. Register ManagedService in advance. Then create Configuration.
		 */
		trace("###################### A. testManagedServiceRegistration2.");
		try {
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
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
			Configuration conf = cm.getConfiguration(bundlePid, bundle
					.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
			props.put("StringKey", getName() + "-A");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			assertNotNull("called back with non-null props", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-A", props.get("StringKey"));
			assertNull("bundleLocation must be not included", props
					.get("service.bundleLocation"));
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
			Configuration[] confs = cm.listConfigurations(null);
			assertNull("confs must be empty:", confs);
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
		}

		/*
		 * B1. (1)Register ManagedService in advance. (2)create Configuration
		 * with different location and null props (3)setBundleLocation to the
		 * target bundle.
		 */
		trace("###################### B1. testManagedServiceRegistration2.");
		try {
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
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
			Configuration conf = cm.getConfiguration(bundlePid,
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
			 * called back. The implementator of this CT (Ikuo) thinks CT should
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
			Dictionary props = new Hashtable();
			props.put("StringKey", getName() + "-B1");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("called back with non-null props", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-B1", props.get("StringKey"));
			assertNull("bundleLocation must be not included", props
					.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being updated to null.");
			/* props is reset */
			conf.update(new Hashtable(0));
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("called back with non-null props", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("Size of props must be 1", 1, props.size());
			conf.update(props);
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with null. However, BJ
			 * thinks no. The implementator of this CT (Ikuo) thinks CT should
			 * not check it because it is unclear in the spec of version 1.3.
			 */
			// trace("Wait for signal.");
			// calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, 5);
			// assertTrue("ManagedService must be called back", calledback);
			// assertNull("called back with null props", sync.getProps());
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
		}
		/*
		 * B2. (1)create Configuration with different location and non-null
		 * props. (2) Register ManagedService. (3)setBundleLocation to the
		 * target bundle.
		 */
		trace("###################### B2. testManagedServiceRegistration2.");
		try {
			trace("The configuration with different location is being created ");
			Configuration conf = cm.getConfiguration(bundlePid,
					neverlandLocation);
			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
			props.put("StringKey", getName() + "-B2");
			conf.update(props);
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");

			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					count + 1);
			assertFalse(
					"ManagedService must NOT be called back even if no configuration.",
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
			 * BJ thinks no. The implementator of this CT (Ikuo) thinks CT
			 * should not check it because it is unclear in the spec of version
			 * 1.3.
			 */
			// assertTrue("ManagedService must be called back", calledback);
			// assertFalse("ManagedService must NOT be called back",
			// calledback);
			if (calledback) {
				++count;
				assertNotNull("called back with non-null props", sync
						.getProps());
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
			assertNotNull("called back with non-null props", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", "stringvalue3", props.get("StringKey"));
			assertNull("bundleLocation must be not included", props
					.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with null. However, BJ
			 * thinks no. The implementator of this CT (Ikuo) thinks CT should
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
		}
		/*
		 * B3. (1)create Configuration with different location and null props.
		 * (2) Register ManagedService. (3)setBundleLocation to the target
		 * bundle.
		 */
		trace("###################### B3. testManagedServiceRegistration2.");
		try {
			trace("The configuration with different location is being created ");
			Configuration conf = cm.getConfiguration(bundlePid,
					neverlandLocation);
			trace("The configuration is being updated ");
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			this.startTargetBundle(bundle);
			trace("Wait for signal.");

			int count = 0;
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					count + 1);
			assertFalse(
					"ManagedService must NOT be called back even if no configuration.",
					calledback);
			assertNull("called back with null props", sync.getProps());

			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
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
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with the props. However,
			 * BJ thinks no. The implementator of this CT (Ikuo) thinks CT
			 * should not check it because it is unclear in the spec of version
			 * 1.3.
			 */
			// assertTrue("ManagedService must be called back", calledback);
			// assertFalse("ManagedService must NOT be called back",
			// calledback);
			if (calledback) {
				++count;
				assertNotNull("called back with non-null props", sync
						.getProps());
				props = sync.getProps();
				assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
				assertEquals("pid", "stringvalue2", props.get("StringKey"));
				assertEquals("Size of props must be 2", 2, props.size());
			}

			trace("The configuration is being updated ");
			conf.update(props);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("called back with non-null props", sync.getProps());
			props = sync.getProps();
			assertEquals("pid", bundlePid, props.get(Constants.SERVICE_PID));
			assertEquals("pid", getName() + "-B3", props.get("StringKey"));
			assertNull("bundleLocation must be not included", props
					.get("service.bundleLocation"));
			assertEquals("Size of props must be 2", 2, props.size());

			trace("The configuration is being set to different location");
			conf.setBundleLocation(neverlandLocation);

			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			/*
			 * XXX Felix and Ikuo think the ManagedService registered by the
			 * newly bound bundle should be called back with null. However, BJ
			 * thinks no. The implementator of this CT (Ikuo) thinks CT should
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
		}
		/*
		 * C. Configuration Admin Service is stopped once. After a while, it
		 * restarts.
		 * 
		 * 1.Register ManagedService in advance. 2.create Configuration with
		 * different location. 3.setBundleLocation to the target bundle.
		 */
		Bundle cmBundle = getCmBundle();
		cmBundle.stop();
		try {
			int count = 0;
			SynchronizerImpl sync = new SynchronizerImpl();
			reg = getContext().registerService(Synchronizer.class.getName(),
					sync, propsForSync1);
			this.startTargetBundle(bundle);

			/* restart where no configuration. */
			trace("Wait for restart cm bundle.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			cmBundle.start();
			trace("Wait for signal.");
			boolean calledback = sync.waitForSignal(SIGNAL_WAITING_TIME,
					++count);
			assertTrue(
					"ManagedService is Called back even if no configuration.",
					calledback);

			this.cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);

			/* Create configuration and stop/start cm. */
			Configuration conf = cm.getConfiguration(bundlePid, bundle
					.getLocation());
			cmBundle.stop();
			trace("Wait for restart cm bundle.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			cmBundle.start();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService is Called back.", calledback);
			this.cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
			conf.delete();

			/* Create configuration with null location and stop/start cm. */
			conf = cm.getConfiguration(bundlePid, null);
			cmBundle.stop();
			trace("Wait for restart cm bundle.");
			Thread.sleep(SIGNAL_WAITING_TIME);
			cmBundle.start();
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue(
					"ManagedService is Called back even if configuration with null.",
					calledback);
			this.cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
		} finally {
			if (reg != null)
				reg.unregister();
			reg = null;
			bundle.stop();
		}
	}

	/**
	 * Register ManagedService in advance. ManagedService has multiple pids.
	 * Then create Configuration.
	 * 
	 * @throws Exception
	 */
	public void testManagedServiceRegistrationWithMultiplPIDs()
			throws Exception {
		this.setAppropriatePermission();
		this.cleanCM();

		try {
			trace("########## 1(array) testManagedServiceRegistrationWithMultiplPIDs");
			System
					.setProperty(
							org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
							org.osgi.test.cases.cm.shared.Constants.MODE_ARRAY);
			internalTestRegisterManagedServiceWithMultiplePIDs();

			trace("########## 1(list) testManagedServiceRegistrationWithMultiplPIDs");
			System
					.setProperty(
							org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
							org.osgi.test.cases.cm.shared.Constants.MODE_LIST);
			internalTestRegisterManagedServiceWithMultiplePIDs();

			trace("########## 1(set) testManagedServiceRegistrationWithMultiplPIDs");
			System
					.setProperty(
							org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
							org.osgi.test.cases.cm.shared.Constants.MODE_SET);
			internalTestRegisterManagedServiceWithMultiplePIDs();
		} finally {
			System
					.setProperty(
							org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_MODE,
							org.osgi.test.cases.cm.shared.Constants.MODE_UNARY);
		}
	}

	private void internalTestRegisterManagedServiceWithMultiplePIDs()
			throws BundleException, IOException {

		final String bundlePid1 = Util.createPid("bundlePid1");
		final String bundlePid2 = Util.createPid("bundlePid2");
		ServiceRegistration reg = null;
		Bundle bundle1 = null;
		try {
			bundle1 = getContext().installBundle(
					getWebServer() + "targetb1.jar");

			SynchronizerImpl sync = new SynchronizerImpl();
			trace("1 sync.getCount()=" + sync.getCount());
			reg = getContext().registerService(Synchronizer.class.getName(),
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
			Configuration conf1 = cm.getConfiguration(bundlePid1, bundle1
					.getLocation());
			trace("sync.getCount()=" + sync.getCount());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration2 is being created");
			Configuration conf2 = cm.getConfiguration(bundlePid2, bundle1
					.getLocation());
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, count + 1);
			trace("sync.getCount()=" + sync.getCount());
			assertFalse("ManagedService must NOT be called back", calledback);

			trace("The configuration1 is being updated ");
			Dictionary props1 = new Hashtable();
			props1.put("StringKey1", "stringvalue1");
			conf1.update(props1);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			trace("sync.getCount()=" + sync.getCount());
			assertTrue("ManagedService must be called back", calledback);
			assertNotNull("called back with non-null props", sync.getProps());
			Dictionary props = sync.getProps();
			assertEquals("pid", bundlePid1, props.get(Constants.SERVICE_PID));
			assertEquals("pid", "stringvalue1", props.get("StringKey1"));

			trace("The configuration2 is being updated ");
			Dictionary props2 = new Hashtable();
			props2.put("StringKey1", "stringvalue1");
			props2.put("StringKey2", "stringvalue2");
			conf2.update(props2);
			trace("Wait for signal.");
			calledback = sync.waitForSignal(SIGNAL_WAITING_TIME, ++count);
			assertTrue("ManagedService must be called back", calledback);

			assertNotNull("called back with non-null props", sync.getProps());
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
	public void testManagedServiceRegistrationDuplicatedTargets()
			throws Exception {
		this.setAppropriatePermission();
		this.cleanCM();
		Bundle bundle2 = getContext().installBundle(
				getWebServer() + "targetb2.jar");

		final String bundlePid = Util.createPid("bundlePid1");
		ServiceRegistration reg2 = null;

		/*
		 * A. One bundle registers duplicated ManagedService. Both of them must
		 * be called back.
		 */
		trace("################## A testManagedServiceRegistrationDuplicatedTargets()");
		try {
			SynchronizerImpl sync2 = new SynchronizerImpl();
			reg2 = getContext().registerService(Synchronizer.class.getName(),
					sync2, propsForSync2);
			System
					.setProperty(
							org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
							"2");
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			boolean calledback2 = sync2
					.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertTrue(
					"Both ManagedService MUST be called back even if no configuration.",
					calledback2);
			assertNull("called back with null props", sync2.getProps());

			trace("The configuration is being created");
			Configuration conf = cm.getConfiguration(bundlePid, bundle2
					.getLocation());
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 3);
			assertFalse("ManagedService must NOT be called back", calledback2);

			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
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
			assertNull("called back with non-null props", sync2.getProps());
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
		ServiceRegistration reg1 = null;
		Bundle bundle1 = getContext().installBundle(
				getWebServer() + "targetb1.jar");
		System
				.setProperty(
						org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
						"1");
		bundle2 = getContext().installBundle(getWebServer() + "targetb2.jar");
		try {
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = getContext().registerService(Synchronizer.class.getName(),
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 1);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback2);
			assertNull("called back with non-null props", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = getContext().registerService(Synchronizer.class.getName(),
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 1);
			assertTrue(
					"ManagedService MUST be called back even if no configuration.",
					calledback1);

			trace("The configuration is being created");
			Configuration[] confs = cm.listConfigurations(null);
			assertNull("confs must be empty:", confs);
			Configuration conf = cm.getConfiguration(bundlePid, bundle2
					.getLocation());
			trace("Wait for signal.");
			calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME, 2);
			this.printoutPropertiesForDebug(sync2);

			assertFalse("ManagedService must NOT be called back", calledback2);
			calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME, 2);
			assertFalse("ManagedService must NOT be called back", calledback1);

			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
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
			assertNull("called back with non-null props", sync2.getProps());
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
		bundle1 = getContext().installBundle(getWebServer() + "targetb1.jar");
		System
				.setProperty(
						org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
						"1");
		bundle2 = getContext().installBundle(getWebServer() + "targetb2.jar");
		try {
			trace("The configuration is being created");
			Configuration conf = cm.getConfiguration(bundlePid, bundle2
					.getLocation());
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = getContext().registerService(Synchronizer.class.getName(),
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME,
					++count2);
			assertTrue(
					"ManagedService MUST be called back even if configuration has no props.",
					calledback2);
			assertNull("called back with non-null props", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = getContext().registerService(Synchronizer.class.getName(),
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count1 = 0;
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME,
					count1 + 1);
			assertFalse("ManagedService MUST NOT be called back even.",
					calledback1);

			Configuration[] confs = cm.listConfigurations(null);
			assertNull("confs must be empty:", confs);

			trace("The configuration is being updated ");
			Dictionary props = new Hashtable();
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
			assertNull("called back with non-null props", sync2.getProps());
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
		bundle1 = getContext().installBundle(getWebServer() + "targetb1.jar");
		System
				.setProperty(
						org.osgi.test.cases.cm.shared.Constants.SYSTEMPROP_KEY_DUPCOUNT,
						"1");
		bundle2 = getContext().installBundle(getWebServer() + "targetb2.jar");
		try {
			trace("The configuration is being created");
			Configuration conf = cm.getConfiguration(bundlePid, bundle2
					.getLocation());
			Dictionary props = new Hashtable();
			props.put("StringKey", getName() + "-B3");
			conf.update(props);
			SynchronizerImpl sync2 = new SynchronizerImpl("ID2");
			reg2 = getContext().registerService(Synchronizer.class.getName(),
					sync2, propsForSync2);
			this.startTargetBundle(bundle2);
			trace("Wait for signal.");
			int count2 = 0;
			boolean calledback2 = sync2.waitForSignal(SIGNAL_WAITING_TIME,
					++count2);
			assertTrue("ManagedService MUST be called back.", calledback2);
			assertNotNull("called back with null props", sync2.getProps());

			SynchronizerImpl sync1 = new SynchronizerImpl("ID1");
			reg1 = getContext().registerService(Synchronizer.class.getName(),
					sync1, propsForSync1);
			this.startTargetBundle(bundle1);
			trace("Wait for signal.");
			int count1 = 0;
			boolean calledback1 = sync1.waitForSignal(SIGNAL_WAITING_TIME,
					count1 + 1);
			assertFalse("ManagedService MUST NOT be called back.", calledback1);

			Configuration[] confs = cm.listConfigurations(null);
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
			assertNull("called back with non-null props", sync2.getProps());
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

	private Bundle getCmBundle() {
		ServiceReference reference = this.getContext().getServiceReference(
				ConfigurationAdmin.class.getName());
		Bundle cmBundle = reference.getBundle();
		return cmBundle;
	}

	private Dictionary getManagedProperties(String pid) throws Exception {
		Semaphore semaphore = new Semaphore();
		ManagedServiceImpl ms = createManagedService(pid, semaphore);
		semaphore.waitForSignal();
		return ms.getProperties();
	}

	/**
	 * 
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec Configuration.getProperties()
	 * @throws Exception
	 */
	public void testManagedProperties() throws Exception {
		String pid = Util.createPid("somepid");
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		/* Put all legal types in the properties and update */
		Hashtable props = new Hashtable();
		props.put("StringKey", getName());
		props.put("IntegerKey", new Integer(12));
		props.put("LongKey", new Long(-29));
		props.put("FloatKey", new Float(921.14));
		props.put("DoubleKey", new Double(1827.234));
		props.put("ByteKey", new Byte((byte) 127));
		props.put("ShortKey", new Short((short) 1));
		// props.put("BigIntegerKey", new BigInteger("123"));
		// props.put("BigDecimalkey", new BigDecimal(9872.7643));
		props.put("CharacterKey", new Character('N'));
		props.put("BooleanKey", new Boolean(true));

		Collection v = new ArrayList();
		v.add(getName());
		// ### is now invalid ....
		// v.addElement(new Integer(12));
		// v.addElement(new Long(-29));
		// v.addElement(new Float(921.14));
		// v.addElement(new Double(1827.234));
		// v.addElement(new Byte((byte) 127));
		// v.addElement(new Short((short) 1));
		// v.addElement(new BigInteger("123"));
		// v.addElement(new BigDecimal(9872.7643));
		// v.addElement(new Character('N'));
		// v.addElement(new Boolean(true));
		// v.addElement(new String[] {"firststring", "secondstring"});
		// ### end invalid
		props.put("collectionkey", v);
		props.put("StringArray", new String[] { "string1", "string2" });
		props.put("IntegerArray", new Integer[] { new Integer(1),
				new Integer(2) });
		props.put("LongArray", new Long[] { new Long(1), new Long(2) });
		props.put("FloatArray", new Float[] { new Float(1.1), new Float(2.2) });
		props.put("DoubleArray", new Double[] { new Double(1.1),
				new Double(2.2) });
		props.put("ByteArray", new Byte[] { new Byte((byte) -1),
				new Byte((byte) -2) });
		props.put("ShortArray", new Short[] { new Short((short) 1),
				new Short((short) 2) });
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
		props.put("CharacterArray", new Character[] { new Character('N'),
				new Character('O') });
		props.put("BooleanArray", new Boolean[] { new Boolean(true),
				new Boolean(false) });

		// ### invalid
		// Vector v1 = new Vector();
		// v1.addElement(new Vector());
		// v1.addElement("Anystring");
		// props.put("VectorArray", new Vector[] {v1, new Vector()});

		props.put("CAPITALkey", "CAPITALvalue");
		conf.update(props);
		/* Register a managed service and get the properties */
		Dictionary msprops = getManagedProperties(pid);
		/*
		 * Add the two properties added by the CM and then check for equality in
		 * the properties (including preserved case)
		 */
		props.put(Constants.SERVICE_PID, pid);
		// props.put(SERVICE_BUNDLE_LOCATION, "cm_TBC"); R3 does not include
		// service.bundleLocation anymore!
		assertEqualProperties("Properties equal?", props, msprops);

		/* Check if the properties are case independent */
		String s = (String) msprops.get("sTringkeY");
		assertEquals("case independant properties", getName(), s);
		Hashtable illegalprops = new Hashtable();
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
	public void testCreateFactoryConfigurationWithNullLocation()
			throws Exception {
		commonTestCreateFactoryConfiguration(true, null);
	}

	private void commonTestCreateFactoryConfiguration(boolean withLocation,
			String location) throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		final String factorypid = Util.createPid("somefactorypid");
		final List pids = new ArrayList();
		final List configs = new ArrayList();

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
					conf = cm.createFactoryConfiguration(factorypid, location);
					/* A SecurityException should have been thrown */
					failException(message, SecurityException.class);
				} catch (AssertionFailedError e) {
					throw e;
				} catch (Throwable e) {
					/* Check that we got the correct exception */
					assertException(message, SecurityException.class, e);
				}
				continue;
			} else {
				/*
				 * Even appropriate ConfigurationPermission,
				 * createFactoryConfiguration(factorypid) must be succeed
				 */
				conf = cm.createFactoryConfiguration(factorypid);
			}
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid, conf
					.getFactoryPid());
			assertNull("No properties", conf.getProperties());
			assertEquals("Correct location", location,
					getBundleLocationForCompare(conf));
			/* Add the pid to the list */
			pids.add(conf.getPid());
		}
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = (Configuration) configs.get(i);
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
				conf = cm.createFactoryConfiguration(factorypid, location);
			} else {
				conf = cm.createFactoryConfiguration(factorypid);
			}
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid, conf
					.getFactoryPid());
			assertNull("No properties", conf.getProperties());
			assertEquals("Correct location", location, this
					.getBundleLocationForCompare(conf));
			/* Add the pid to the list */
			pids.add(conf.getPid());
		}
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = (Configuration) configs.get(i);
			conf.delete();
		}
	}

	private String getBundleLocationForCompare(Configuration conf)
			throws BundleException {
		String location = null;
		if (this.permissionFlag)
			location = conf.getBundleLocation();
		else {
			this.setAppropriatePermission();
			location = conf.getBundleLocation();
			this.setInappropriatePermission();
		}
		return location;

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
	public void testManagedServiceFactory() throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		String factorypid = Util.createPid("somefactorypid");
		Hashtable configs = new Hashtable();
		/* Create some factory configurations */
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = cm.createFactoryConfiguration(factorypid);
			Hashtable ht = new Hashtable();
			ht.put("test.field", i + "");
			conf.update(ht);
			trace("pid: " + conf.getPid());
			configs.put(conf.getPid(), conf);
		}
		try {
			Semaphore semaphore = new Semaphore();
			/* Register a factory */
			ManagedServiceFactoryImpl msf = new ManagedServiceFactoryImpl(
					"msf", "testprop", semaphore);
			Hashtable properties = new Hashtable();
			properties.put(Constants.SERVICE_PID, factorypid);
			properties.put(ConfigurationAdmin.SERVICE_FACTORYPID, factorypid);
			registerService(ManagedServiceFactory.class.getName(), msf,
					properties);
			for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
				trace("Wait for signal #" + i);
				semaphore.waitForSignal();
				trace("Signal #" + i + " arrived");
			}
			trace("All signals have arrived");
		} finally {
			Enumeration keys = configs.keys();
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
	public void testUpdateConfigEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		try {
			conf.update(props);
			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());
			assertNull("Config Factory event pid null", cl.getFactoryPid());
			assertNotNull("Config event reference null", cl.getReference());
			ConfigurationAdmin admin = (ConfigurationAdmin) getContext()
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration config = admin.getConfiguration(cl.getPid());
				assertNotNull("Configuration from event", config);
				assertEqualProperties("Properties match", conf.getProperties(),
						config.getProperties());
			} finally {
				getContext().ungetService(cl.getReference());
			}
		} finally {
			removeConfigurationListener(cl);
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
	public void testUpdateConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cm.createFactoryConfiguration(factorypid);
		String pid = conf.getPid();
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten"
				+ "the config factory update");
		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType());
			assertEquals("Config Factory event pid match", factorypid, cl
					.getFactoryPid());
			assertNotNull("Config Factory event reference null", cl
					.getReference());
			ConfigurationAdmin admin = (ConfigurationAdmin) getContext()
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration config = admin.getConfiguration(cl.getPid());
				assertNotNull("Configuration from event", config);
				assertEqualProperties("Properties match", conf.getProperties(),
						config.getProperties());
			} finally {
				getContext().ungetService(cl.getReference());
			}
		} finally {
			removeConfigurationListener(cl);
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
	public void testDeleteConfigEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		trace("Create and register a new ConfigurationListener");
		try {
			cl = createConfigurationListener(synchronizer, 2);
			conf.update(props);
			trace("Wait until the ConfigurationListener has gotten the update");
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));

			conf.delete();

			trace("Wait until the ConfigurationListener has gotten the delete");

			assertTrue("Delete done", synchronizer.waitForSignal(
					SIGNAL_WAITING_TIME, 2));
			assertEquals("Config event pid match", pid, cl.getPid(2));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(2));
			assertNull("Config Factory event pid null", cl.getFactoryPid(2));
			assertNotNull("Config Factory event reference null", cl
					.getReference(2));
			try {
				ConfigurationAdmin admin = (ConfigurationAdmin) getContext()
						.getService(cl.getReference(2));
				assertNotNull("Configuration Admin from event", admin);
				Configuration[] configs = admin
						.listConfigurations("(service.pid=" + pid + ")");
				assertNull("The configuration exists in CM!", configs);
			} finally {
				getContext().ungetService(cl.getReference(2));
			}
		} finally {
			removeConfigurationListener(cl);
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
	public void testDeleteConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		/* Set up the configuration */
		Configuration conf = cm.createFactoryConfiguration(factorypid);
		String pid = conf.getPid();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		conf.delete();
		trace("Wait until the ConfigurationListener has gotten"
				+ "the config factory delete");
		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", pid, cl.getPid());
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType());
			assertEquals("Config Factory event pid match", factorypid, cl
					.getFactoryPid());
			assertNotNull("Config Factory event reference null", cl
					.getReference());
			ConfigurationAdmin admin = (ConfigurationAdmin) getContext()
					.getService(cl.getReference());
			try {
				assertNotNull("Configuration Admin from event", admin);
				Configuration[] configs = admin
						.listConfigurations("(service.factoryPid=" + factorypid
								+ ")");
				assertNull("The configuration exists in CM!", configs);
			} finally {
				getContext().ungetService(cl.getReference());
			}
		} finally {
			removeConfigurationListener(cl);
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
	public void testConfigListenerPermission() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "tb1.jar");
		String message = "registering config listener without permission";
		try {
			tb.start();
			failException(message, BundleException.class);
		} catch (BundleException e) {
			/* Check that we got the correct exception */
			assertException(message, BundleException.class, e);
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
	public void testConfigEventFromDifferentBundle() throws Exception {
		trace("Create and register a new ConfigurationListener");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		ConfigurationListenerImpl cl = createConfigurationListener(
				synchronizer, 4);
		Bundle tb = getContext().installBundle(getWebServer() + "tb2.jar");
		tb.start();
		trace("Wait until the ConfigurationListener has gotten the update");

		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match", PACKAGE + ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX, cl
					.getPid(1));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(1));
			assertNull("Config Factory event pid null", cl.getFactoryPid(1));

			assertTrue("Update done", synchronizer.waitForSignal(
					SIGNAL_WAITING_TIME, 2));
			assertEquals("Config event pid match", PACKAGE + ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX, cl
					.getPid(2));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(2));
			assertNull("Config Factory event pid null", cl.getFactoryPid(2));

			assertTrue("Update done", synchronizer.waitForSignal(
					SIGNAL_WAITING_TIME, 3));
			assertEquals("Config event facotory pid match", PACKAGE
					+ ".tb2factorypid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX, cl
					.getFactoryPid(3));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(3));

			assertTrue("Update done", synchronizer.waitForSignal(
					SIGNAL_WAITING_TIME, 4));
			assertEquals("Config event factory pid match", PACKAGE
					+ ".tb2factorypid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX, cl
					.getFactoryPid(4));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(4));

		} finally {
			removeConfigurationListener(cl);
			tb.uninstall();
		}
	}

	/**
	 * Tests if a configuration plugin is invoked when only a configuration
	 * listener is registered (no managed service). It should not be invoked.
	 * 
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec 
	 *       ConfigurationPlugin.modifyConfiguration(ServiceReference,Dictionary)
	 * 
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	public void testConfigurationPluginService() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		plugin = createConfigurationPlugin();
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");
		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
		} finally {
			removeConfigurationListener(cl);
			removeConfigurationPlugin(plugin);
		}
	}

	/**
	 * Tests if a configuration plugin is invoked when only a configuration
	 * listener is registered (managed service factory). It should not be
	 * invoked.
	 * 
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec 
	 *       ConfigurationPlugin.modifyConfiguration(ServiceReference,Dictionary)
	 * 
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	public void testConfigurationPluginServiceFactory() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		/* Set up the configuration */
		Configuration conf = cm.createFactoryConfiguration(factorypid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		plugin = createConfigurationPlugin();
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");
		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
		} finally {
			removeConfigurationListener(cl);
			removeConfigurationPlugin(plugin);
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
		registerService(ConfigurationListener.class.getName(), listener, null);
		return listener;
	}

	/**
	 * creates and registers a configuration plugin.
	 */
	private NotVisitablePlugin createConfigurationPlugin() throws Exception {
		NotVisitablePlugin plugin = new NotVisitablePlugin();
		registerService(ConfigurationPlugin.class.getName(), plugin, null);
		return plugin;
	}

	/**
	 * unregisters a configuration listener.
	 */
	private void removeConfigurationListener(ConfigurationListener cl)
			throws Exception {
		unregisterService(cl);
	}

	/**
	 * unregisters a configuration plugin.
	 */
	private void removeConfigurationPlugin(ConfigurationPlugin plugin)
			throws Exception {
		unregisterService(plugin);
	}

	private ManagedServiceImpl createManagedService(String pid, Semaphore s)
			throws Exception {
		ManagedServiceImpl ms = new ManagedServiceImpl(s);
		Hashtable props = new Hashtable();
		props.put(Constants.SERVICE_PID, pid);
		/* TODO: Testa registered service.pid with other String */
		registerService(ManagedService.class.getName(), ms, props);
		trace("ManagedService is registered with pid:" + pid);
		return ms;
	}

	private void checkConfiguration(Configuration conf, String message,
			String pid, String location) throws BundleException {
		assertNotNull(message, conf);
		assertEquals("Pid", pid, conf.getPid());
		assertNull("FactoryPid", conf.getFactoryPid());
		assertNull("Properties", conf.getProperties());
		assertEquals("Location", location, this
				.getBundleLocationForCompare(conf));

	}

	/**
	 * See if a configuration is part of a list.
	 * 
	 * @return index of the list if the configuration is a part of the
	 *         list.Otherwise, return -1nd.
	 */
	private int isPartOf(Configuration theConf, List configs) {

		for (int i = 0; i < configs.size(); i++)
			if (equals((Configuration) configs.get(i), theConf))
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

	public final static String PACKAGE = "org.osgi.test.cases.cm.tbc";

	private String getLocation() {
		return getContext().getBundle().getLocation();
	}

	private String getFilter() {
		// return "(|(service.pid=" + PACKAGE + ".*)" + "(service.factoryPid="
		// + PACKAGE + ".*))";
		return null;
	}

	/**
	 * Removes any configurations made by this bundle.
	 */
	private void cleanCM() throws Exception {
		// Configuration[] configs = cm.listConfigurations(getFilter());
		Configuration[] configs = cm.listConfigurations(getFilter());
		if (configs != null) {
			// log("Clearing " + configs.length + " Configurations");
			for (int i = 0; i < configs.length; i++) {
				configs[i].delete();
			}
		} else {
			// log("No Configurations to clear");
		}
	}

	class Plugin implements ConfigurationPlugin {
		private int index;

		Plugin(int x) {
			index = x;
		}

		public void modifyConfiguration(ServiceReference ref, Dictionary props) {
			trace("Calling plugin with cmRanking=" + (index * 10));
			String[] types = (String[]) ref.getProperty("objectClass");
			for (int i = 0; i < types.length; i++) {
				if ("org.osgi.service.cm.ManagedService".equals(types[i])) {
					props.put("plugin.ms." + index, "added by plugin#" + index);
					break;
				} else if ("org.osgi.service.cm.ManagedServiceFactory"
						.equals(types[i])) {
					props.put("plugin.factory." + index, "added by plugin#"
							+ index);
					break;
				}
			}
		}
	}

	/**
	 * <code>ConfigurationPlugin</code> implementation to be used in the
	 * <code>ConfigurationListener</code> test. The plugin should NOT be invoked
	 * when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	class NotVisitablePlugin implements ConfigurationPlugin {
		private boolean visited;

		/**
		 * Creates a <code>ConfigurationPlugin</code> instance that has not been
		 * invoked (visited) by a <code>Configuration</code> update event.
		 * 
		 */
		public NotVisitablePlugin() {
			visited = false;
		}

		/**
		 * <p>
		 * Callback method when a <code>Configuration</code> update is being
		 * delivered to a registered <code>ManagedService</code> or
		 * <code>ManagedServiceFactory</code> instance.
		 * </p>
		 * <p>
		 * Set plugin to visited (<code>visited = true</code>) when this method
		 * is invoked. If this happens, the <code>ConfigurationListener</code>
		 * tests failed.
		 * </p>
		 * 
		 * @param ref
		 *            the <code>ConfigurationAdmin</code> that generated the
		 *            update.
		 * @param props
		 *            the <code>Dictionary</code> containing the properties of
		 *            the <code>
		 * @see org.osgi.service.cm.ConfigurationPlugin#modifyConfiguration(org.osgi.framework.ServiceReference,
		 *      java.util.Dictionary)
		 */
		public void modifyConfiguration(ServiceReference ref, Dictionary props) {
			visited = true;
		}

		/**
		 * Checks if the plugin has not been invoked by a <code>Configuration
		 * </code> update event.
		 * 
		 * @return <code>true</code> if plugin has not been visited (invoked).
		 *         <code>false</code>, otherwise.
		 */
		public boolean notVisited() {
			return !visited;
		}
	}

	/**
	 * <code>ConfigurationPlugin</code> implementation to be used in the
	 * <code>ConfigurationListener</code> test. The plugin should NOT be invoked
	 * when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	class RunnableImpl implements Runnable {
		private final String pid;
		private String location;
		private Configuration conf;
		private Object lock = new Object();

		RunnableImpl(String pid, String location) {
			this.pid = pid;
			this.location = location;
		}

		Configuration getConfiguration() {
			return conf;
		}

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				conf = cm.getConfiguration(pid, location);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			conf.setBundleLocation(location);

		}

		void unlock() {
			synchronized (lock) {
				lock.notifyAll();
			}
		}

		void unlock(String newLocation) {
			location = newLocation;
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}
}

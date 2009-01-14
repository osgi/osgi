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
package org.osgi.test.cases.cm.tbc;

// import java.math.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.Synchronizer;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

public class CMControl extends DefaultTestBundleControl {
	private ConfigurationAdmin	cm;
	private Hashtable			confProps;
	/* Two constants that are lacking in the spec */
	public static final String	SERVICE_FACTORY_PID	= "service.factoryPid";
	private static final long	SIGNAL_WAITING_TIME	= 25000;

	// public static final String SERVICE_BUNDLE_LOCATION =
	// "service.bundleLocation";

	protected void setUp() throws Exception {
		assertTrue(serviceAvailable(ConfigurationAdmin.class));
		cm = (ConfigurationAdmin) getService(ConfigurationAdmin.class);
		// populate the created configurations so that
		// listConfigurations can return these configurations
		confProps = new Hashtable();
		confProps.put("prop1", "val1");
		confProps.put("prop2", "val2");
		cleanCM();
	}

	protected void tearDown() throws Exception {
		cleanCM();
		unregisterAllServices();
		ungetService(cm);
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
		String pid = getPackage();
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
						new Hashtable())};
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
			}
			catch (AssertionFailedError e) {
				throw e;
			}
			catch (Throwable e) {
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
		String pid = getPackage();
		String thisLocation = getLocation();
		Configuration conf = null;
		/* Get a brand new configuration */
		conf = cm.getConfiguration(pid);
		checkConfiguration(conf, "A new Configuration object", pid,
				thisLocation);
		/* Get the configuration again (should be exactly the same) */
		conf = cm.getConfiguration(pid);
		checkConfiguration(conf, "The same Configuration object", pid,
				thisLocation);
		/*
		 * Change the location of the bundle and then get the Configuration
		 * again. The location should not have been touched.
		 */
		String neverlandLocation = "http://neverneverland/";
		conf.setBundleLocation(neverlandLocation);
		conf = cm.getConfiguration(pid);
		assertEquals("Location Neverland", neverlandLocation, conf
				.getBundleLocation());
		/*
		 * Clear the location of the bundle and then get the Configuration
		 * again. The location should have been set again
		 */
		// conf.setBundleLocation(null);
		// assertNull("Location null", conf.getBundleLocation());
		// conf = cm.getConfiguration(pid);
		// assertEquals("Location set again", getLocation(),
		// conf.getBundleLocation());
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
		String pid1 = getPackage() + ".1";
		String pid2 = getPackage() + ".2";
		String pid3 = getPackage() + ".2";
		String thisLocation = getLocation();
		String neverlandLocation = "http://neverneverland/";
		Configuration conf = null;
		/* Get a brand new configuration */
		conf = cm.getConfiguration(pid1, thisLocation);
		checkConfiguration(conf, "A new Configuration object", pid1,
				thisLocation);
		/*
		 * Get an existing configuration, but specify the location (which should
		 * then be ignored)
		 */
		conf = cm.getConfiguration(pid1, neverlandLocation);
		checkConfiguration(conf, "The same Configuration object", pid1,
				thisLocation);
		conf.delete();
		/* Get a brand new configuration with a specified location */
		conf = cm.getConfiguration(pid2, neverlandLocation);
		checkConfiguration(conf, "A new Configuration object", pid2,
				neverlandLocation);
		conf.delete();
		/* Get a brand new configuration with no location */
		conf = cm.getConfiguration(pid3, null);
		checkConfiguration(conf, "A new Configuration object", pid3, null);
		conf.delete();
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
		String pid = "pidX";
		Configuration conf = cm.getConfiguration(pid);
		Hashtable newprops = new Hashtable();
		newprops.put("somekey", "somevalue");
		conf.update(newprops);
		Dictionary props = conf.getProperties();
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
		}
		catch (AssertionFailedError e) {
			throw e;
		}
		catch (Throwable e) {
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
		Configuration conf1 = cm.getConfiguration("pidA");
		Configuration conf2 = cm.getConfiguration("pidA");
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
		/* Create configurations */
		Configuration[] configs = {cm.getConfiguration("pid1")};
		Configuration[] updatedConfigs = {cm.getConfiguration("pid2")};
		try {
			/*
			 * Update properties on some of configurations (to make them
			 * "active")
			 */
			for (int i = 0; i < updatedConfigs.length; i++) {
				Hashtable props = new Hashtable();
				props.put("someprop" + i, "somevalue" + i);
				updatedConfigs[i].update(props);
			}
			/*
			 * List all configurations and make sure that only the active
			 * configurations are returned
			 */
			Configuration[] confs = cm.listConfigurations("(service.pid=pid2)");
			if (confs == null) {
				fail("No configurations returned");
			}
			for (int i = 0; i < confs.length; i++) {
				if (isPartOf(confs[i], updatedConfigs)) {
					pass(confs[i].getPid() + " was returned");
				}
				else {
					fail(confs[i].getPid() + " should not have been listed");
				}
			}
		}
		/* Delete all used configurations */
		finally {
			for (int i = 0; i < configs.length; i++) {
				configs[i].delete();
			}
			for (int i = 0; i < updatedConfigs.length; i++) {
				updatedConfigs[i].delete();
			}
		}
		/* List all configurations and make sure they are all gone */
		Configuration[] leftConfs = cm.listConfigurations("(|(service.pid=pid1)(service.pid=pid2))");
		assertNull("Left configurations", leftConfs);
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
		String pid = "somepid";
		Semaphore semaphore = new Semaphore();
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable props = new Hashtable();
		props.put("somekey", "somevalue");
		props.put("CAPITALkey", "CAPITALvalue");
		conf.update(props);
		trace("Create and register a new the ManagedService");
		ManagedServiceImpl ms = createManagedService(pid, semaphore);
		trace("Wait until the ManagedService has gotten the update");
		semaphore.waitForSignal();
		trace("Update done!");
		/*
		 * Add the two properties added by the CM and then check for equality in
		 * the properties
		 */
		props.put(Constants.SERVICE_PID, pid);
		// props.put(SERVICE_BUNDLE_LOCATION, "cm_TBC"); R3 does not include
		// service.bundleLocation anymore!
		assertEqualProperties("Properties equal?", props, ms.getProperties());
		trace((String) ms.getProperties().get("service.pid"));
		// trace((String) ms.getProperties().get("service.bundleLocation"));
		trace((String) conf.getBundleLocation());
		/* OUTSIDE_OF_SPEC */
		// assertNotSame("Properties same?", props, ms.getProperties());
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
		String pid = "somepid";
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		/* Put all legal types in the properties and update */
		Hashtable props = new Hashtable();
		props.put("StringKey", "stringvalue");
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
		v.add("stringvalue");
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
		props.put("StringArray", new String[] {"string1", "string2"});
		props.put("IntegerArray",
				new Integer[] {new Integer(1), new Integer(2)});
		props.put("LongArray", new Long[] {new Long(1), new Long(2)});
		props.put("FloatArray", new Float[] {new Float(1.1), new Float(2.2)});
		props.put("DoubleArray",
				new Double[] {new Double(1.1), new Double(2.2)});
		props.put("ByteArray", new Byte[] {new Byte((byte) -1),
				new Byte((byte) -2)});
		props.put("ShortArray", new Short[] {new Short((short) 1),
				new Short((short) 2)});
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
		props.put("CharacterArray", new Character[] {new Character('N'),
				new Character('O')});
		props.put("BooleanArray", new Boolean[] {new Boolean(true),
				new Boolean(false)});

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
		assertEquals("case independant properties", "stringvalue", s);
		Hashtable illegalprops = new Hashtable();
		illegalprops.put("exception", new Exception());
		String message = "Exception is not a legal type";
		try {
			conf.update(illegalprops);
			/* A IllegalArgumentException should have been thrown */
			failException(message, IllegalArgumentException.class);
		}
		catch (AssertionFailedError e) {
			throw e;
		}
		catch (Throwable e) {
			/* Check that we got the correct exception */
			assertException(message, IllegalArgumentException.class, e);
		}
		/* TODO: Add more illegal types (inside collections etc) */
	}

	public void testUpdatedProperties() throws Exception {
		/* Put all legal types in the properties and update */
		/* Get the properties again */
		/* Check if the properties are equals */
		/* Check if the properties have preserved the case */
		/* Check if the properties are case independent */
	}

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
		commonTestCreateFactoryConfiguration(true, "http://neverneverland/");
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
		String factorypid = "somefactorypid";
		List pids = new ArrayList();
		List configs = new ArrayList();
		pids.add(factorypid);
		for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
			Configuration conf = null;
			if (withLocation) {
				conf = cm.createFactoryConfiguration(factorypid, location);
			}
			else {
				conf = cm.createFactoryConfiguration(factorypid);
			}
			configs.add(conf);
			trace("pid: " + conf.getPid());
			assertTrue("Unique pid", !pids.contains(conf.getPid()));
			assertEquals("Correct factory pid", factorypid, conf
					.getFactoryPid());
			assertNull("No properties", conf.getProperties());
			assertEquals("Correct location", location, conf.getBundleLocation());
			/* Add the pid to the list */
			pids.add(conf.getPid());
		}
		for (int i = 0; i < configs.size(); i++) {
			Configuration conf = (Configuration) configs.get(i);
			conf.delete();
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
	public void testManagedServiceFactory() throws Exception {
		final int NUMBER_OF_CONFIGS = 3;
		String factorypid = "somefactorypid";
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
			ManagedServiceFactoryImpl msf = new ManagedServiceFactoryImpl("msf",
					"testprop", semaphore);
			Hashtable properties = new Hashtable();
			properties.put(Constants.SERVICE_PID, factorypid);
			properties.put(SERVICE_FACTORY_PID, factorypid);
			registerService(ManagedServiceFactory.class.getName(), msf, properties);
			for (int i = 0; i < NUMBER_OF_CONFIGS; i++) {
				trace("Wait for signal #" + i);
				semaphore.waitForSignal();
				trace("Signal #" + i + " arrived");
			}
			trace("All signals have arrived");
		} finally {
	      Enumeration keys = configs.keys();
	      while (keys.hasMoreElements()) {
    			Configuration conf = (Configuration)configs.get(keys.nextElement());
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
		String pid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "updateConfigEvent";
		Synchronizer synchronizer = new Synchronizer();
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
			}
			finally {
				getContext().ungetService(cl.getReference());
			}
		}
		finally {
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
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testUpdateConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "updateFactoryEvent";
		Synchronizer synchronizer = new Synchronizer();
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
			}
			finally {
				getContext().ungetService(cl.getReference());
			}
		}
		finally {
			removeConfigurationListener(cl);
		}
	}

	/**
	 * Tests a configuration listener delete event notification from a
	 * configuration service. The deleted <code>Configuration</code> should be
	 * empty (<code>ConfigurationAdmin.listConfigurations(null)</code> must
	 * not contain the deleted <code>Configuration</code>).
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
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testDeleteConfigEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String pid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "deleteConfigEvent";
		Synchronizer synchronizer = new Synchronizer();
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
			
			assertTrue("Delete done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME, 2));
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
			}
			finally {
				getContext().ungetService(cl.getReference(2));
			}
		}
		finally {
			removeConfigurationListener(cl);
		}
	}

	/**
	 * Tests a configuration listener delete event notification from a
	 * configuration service factory. The deleted <code>Configuration</code>
	 * should be empty (
	 * <code>ConfigurationAdmin.listConfigurations(null)</code> must not
	 * contain the deleted <code>Configuration</code>).
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
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testDeleteConfigFactoryEvent() throws Exception {
		ConfigurationListenerImpl cl = null;
		String factorypid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "deleteFactoryEvent";
		Synchronizer synchronizer = new Synchronizer();
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
			}
			finally {
				getContext().ungetService(cl.getReference());
			}
		}
		finally {
			removeConfigurationListener(cl);
		}
	}

	/**
	 * Tests a configuration listener permission. The bundle does not have
	 * <code>ServicePermission[ConfigurationListener,REGISTER]</code> and will
	 * try to register a <code>ConfigurationListener</code>. An exception
	 * must be thrown.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testConfigListenerPermission() throws Exception {
		Bundle tb;
		tb = getContext().installBundle(getWebServer() + "tb1.jar");
		String message = "registering config listener without permission";
		try {
			tb.start();
			failException(message, BundleException.class);
		}
		catch (BundleException e) {
			/* Check that we got the correct exception */
			assertException(message, BundleException.class, e);
		}
		finally {
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
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testConfigEventFromDifferentBundle() throws Exception {
		Bundle tb = null;
		ConfigurationListenerImpl cl = null;
		trace("Create and register a new ConfigurationListener");
		Synchronizer synchronizer = new Synchronizer();
		cl = createConfigurationListener(synchronizer, 4);
		tb = getContext().installBundle(getWebServer() + "tb2.jar");
		tb.start();
		trace("Wait until the ConfigurationListener has gotten the update");

		try {
			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME));
			assertEquals("Config event pid match",
					ConfigurationListenerImpl.RFC_0103_PID_PREFIX + "tb2pid",
					cl.getPid(1));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(1));
			assertNull("Config Factory event pid null", cl.getFactoryPid(1));

			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME, 2));
			assertEquals("Config event pid match",
					ConfigurationListenerImpl.RFC_0103_PID_PREFIX + "tb2pid",
					cl.getPid(2));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(2));
			assertNull("Config Factory event pid null", cl.getFactoryPid(2));

			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME, 3));
			assertEquals("Config event facotory pid match",
					ConfigurationListenerImpl.RFC_0103_PID_PREFIX
							+ "tb2factorypid", cl.getFactoryPid(3));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_UPDATED, cl.getType(3));

			assertTrue("Update done", synchronizer
					.waitForSignal(SIGNAL_WAITING_TIME, 4));
			assertEquals("Config event factory pid match",
					ConfigurationListenerImpl.RFC_0103_PID_PREFIX
							+ "tb2factorypid", cl.getFactoryPid(4));
			assertEquals("Config event type match",
					ConfigurationEvent.CM_DELETED, cl.getType(4));

		}
		finally {
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
	 * @spec ConfigurationPlugin.modifyConfiguration(ServiceReference,Dictionary)
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testConfigurationPluginService() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String pid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "configPluginService";
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		Synchronizer synchronizer = new Synchronizer();
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
		}
		finally {
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
	 * @spec ConfigurationPlugin.modifyConfiguration(ServiceReference,Dictionary)
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */
	public void testConfigurationPluginServiceFactory() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String factorypid = ConfigurationListenerImpl.RFC_0103_PID_PREFIX
				+ "configPluginServiceFatory";
		/* Set up the configuration */
		Configuration conf = cm.createFactoryConfiguration(factorypid);
		Hashtable props = new Hashtable();
		props.put("key", "value1");
		Synchronizer synchronizer = new Synchronizer();
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
		}
		finally {
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
			Synchronizer synchronizer) throws Exception {
		return createConfigurationListener(synchronizer, 1);
	}

	/**
	 * creates and registers a configuration listener that expects eventCount
	 * events.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			Synchronizer synchronizer, int eventCount) throws Exception {
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
		return ms;
	}

	private void checkConfiguration(Configuration conf, String message,
			String pid, String location) {
		assertNotNull(message, conf);
		assertEquals("Pid", pid, conf.getPid());
		assertNull("FactoryPid", conf.getFactoryPid());
		assertNull("Properties", conf.getProperties());
		assertEquals("Location", location, conf.getBundleLocation());
	}

	/**
	 * See if a configuration is part of a list.
	 */
	private boolean isPartOf(Configuration theConf, Configuration[] configs) {
		boolean found = false;
		int i = 0;
		while (!found && (i < configs.length)) {
			if (equals(configs[i], theConf)) {
				found = true;
			}
			i++;
		}
		return found;
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

	private String getPackage() {
		return "org.osgi.test.cases.cm.tbc";
	}

	private String getLocation() {
		return getContext().getBundle().getLocation();
	}

	private String getFilter() {
		return "(|(service.pid=" + getPackage() + ".factory.*)"
				+ "(service.factoryPid=" + getPackage() + ".factory.*))";
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
		}
		else {
			// log("No Configurations to clear");
		}
	}

	class Plugin implements ConfigurationPlugin {
		private int	index;

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
				}
				else
					if ("org.osgi.service.cm.ManagedServiceFactory"
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
	 * <code>ConfigurationListener</code> test. The plugin should NOT be
	 * invoked when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	class NotVisitablePlugin implements ConfigurationPlugin {
		private boolean	visited;

		/**
		 * Creates a <code>ConfigurationPlugin</code> instance that has not
		 * been invoked (visited) by a <code>Configuration</code> update
		 * event.
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
		 * Set plugin to visited (<code>visited = true</code>) when this
		 * method is invoked. If this happens, the
		 * <code>ConfigurationListener</code> tests failed.
		 * </p>
		 * 
		 * @param ref the <code>ConfigurationAdmin</code> that generated the
		 *        update.
		 * @param props the <code>Dictionary</code> containing the properties
		 *        of the <code>
		 * @see org.osgi.service.cm.ConfigurationPlugin#modifyConfiguration(org.osgi.framework.ServiceReference, java.util.Dictionary)
		 */
		public void modifyConfiguration(ServiceReference ref, Dictionary props) {
			visited = true;
		}

		/**
		 * Checks if the plugin has not been invoked by a
		 * <code>Configuration</code> update event.
		 * 
		 * @return <code>true</code> if plugin has not been visited (invoked).
		 *         <code>false</code>, otherwise.
		 */
		public boolean notVisited() {
			return !visited;
		}
	}
}

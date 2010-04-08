package org.osgi.test.cases.jmx.junit;

import java.io.*;
import java.util.*;

import javax.management.*;
import javax.management.openmbean.*;

import org.osgi.framework.*;
import org.osgi.jmx.service.cm.*;
import org.osgi.service.cm.*;

public class ConfigurationAdminMBeanTestCase extends MBeanGeneralTestCase {
	private ConfigurationAdmin		configAdminService;
	private ConfigurationAdminMBean	configAdminMBean;

	private BundleContext			bundleContext;
	private Bundle					testBundle2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();

		super
				.waitForRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));

		configAdminMBean = super.getMBeanFromServer(
				ConfigurationAdminMBean.OBJECTNAME,
				ConfigurationAdminMBean.class);
		bundleContext = super.getContext();
		configAdminService = (ConfigurationAdmin) bundleContext
				.getService(bundleContext
						.getServiceReference(ConfigurationAdmin.class.getName()));

		assertNotNull(configAdminMBean);
		assertNotNull(bundleContext);
		assertNotNull(configAdminService);
	}

	public void testConfigurationGetBundleLocation() throws Exception {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_get_bundle_location");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		String bundleLocation = configAdminMBean
				.getBundleLocation(configurationPid);
		assertTrue("bundle location is wrong " + bundleLocation + " "
				+ testBundle2.getLocation(), testBundle2.getLocation().equals(
				bundleLocation));
	}

	public void testConfigurationSetBundleLocation() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_set_bundle_location");
		configAdminMBean.setBundleLocation(configurationPid, testBundle2
				.getLocation());
		configAdminMBean.update(configurationPid, OSGiProperties
				.tableFrom(props));
		String bundleLocation = configAdminMBean
				.getBundleLocation(configurationPid);
		assertTrue("bundle location is wrong " + bundleLocation + " "
				+ testBundle2.getLocation(), testBundle2.getLocation().equals(
				bundleLocation));
	}

	public void testConfigurationUpdate() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_update");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		assertTrue("property was not updated", "test_update"
				.equals(OSGiProperties.propertiesFrom(result).get("test_key")));
	}

	public void testConfigurationUpdateForLocation() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_update_for_location");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getPropertiesForLocation(
				configurationPid, testBundle2.getLocation());
		assertNotNull(result);
		assertTrue("property was not updated for location",
				"test_update_for_location".equals(OSGiProperties
						.propertiesFrom(result).get("test_key")));
	}

	public void testConfigurationGetProperties() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key1", "test_get_properties1");
		props.setProperty("test_key2", "test_get_properties2");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		OSGiProperties.propertiesFrom(result).get("test_key");
		assertTrue("properties were not retrieved properly",
				"test_get_properties1".equals(OSGiProperties.propertiesFrom(
						result).get("test_key1"))
						&& "test_get_properties2".equals(OSGiProperties
								.propertiesFrom(result).get("test_key2")));
	}

	public void testConfigurationGetPropertiesForLocation() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key1", "test_get_properties_for_location1");
		props.setProperty("test_key2", "test_get_properties_for_location2");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getPropertiesForLocation(
				configurationPid, testBundle2.getLocation());
		assertNotNull(result);
		OSGiProperties.propertiesFrom(result).get("test_key");
		assertTrue("properties were not retrieved properly",
				"test_get_properties_for_location1".equals(OSGiProperties
						.propertiesFrom(result).get("test_key1"))
						&& "test_get_properties_for_location2"
								.equals(OSGiProperties.propertiesFrom(result)
										.get("test_key2")));
	}

	public void testConfigurationDelete() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_delete");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		configAdminMBean.delete(configurationPid);
		result = configAdminMBean.getProperties(configurationPid);
		assertNull(result);
	}

	public void testConfigurationDeleteForLocation() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_delete_for_location");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		configAdminMBean.deleteForLocation(configurationPid, testBundle2
				.getLocation());
		result = configAdminMBean.getProperties(configurationPid);
		assertNull(result);
	}

	public void testDeleteConfigurationsFilter() throws Exception {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_delete_for_filter");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		configAdminMBean
				.deleteConfigurations("(&(test_key=test_delete_for_filter))");
		result = configAdminMBean.getProperties(configurationPid);
		assertNull(result);
	}

	public void testGetFactoryPid() throws Exception {
		String test2factory = testBundle2.getBundleId() + ".factory";
		Configuration configuration = configAdminService
				.createFactoryConfiguration(test2factory, null);
		Dictionary properties = new Properties();
		properties.put("test_key", "test_factory_pid");
		properties.put(Constants.SERVICE_PID, test2factory);
		configuration.update(properties);

		assertTrue("get factory pid doesn't work", test2factory
				.equals(configAdminMBean.getFactoryPid(configuration.getPid())));
	}

	public void testGetFactoryPidForLocation() throws Exception {
		String test2factory = testBundle2.getBundleId() + ".factory";
		Configuration configuration = configAdminService
				.createFactoryConfiguration(test2factory, testBundle2
						.getLocation());
		Dictionary properties = new Properties();
		properties.put("test_key", "test_factory_pid");
		properties.put(Constants.SERVICE_PID, test2factory);
		configuration.update(properties);

		assertTrue("get factory pid doesn't work", test2factory
				.equals(configAdminMBean.getFactoryPidForLocation(configuration
						.getPid(), testBundle2.getLocation())));
	}

	public void testGetConfigurationsFilter() throws Exception {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key_filter", "test_get_cfg_filter");
		configAdminMBean.updateForLocation(configurationPid, testBundle2
				.getLocation(), OSGiProperties.tableFrom(props));

		String[][] result = configAdminMBean
				.getConfigurations("(&(test_key_filter=test_get_cfg_filter))");
		assertTrue("wrong filtering of configurations", (result != null)
				&& (result.length == 1) && (result[0].length == 2)
				&& configurationPid.equals(result[0][0])
				&& testBundle2.getLocation().equals(result[0][1]));
	}

	public void testCreateFactoryConfigurationForLocation() throws IOException {
		String fpid = testBundle2.getBundleId() + ".factory";
		String location = testBundle2.getLocation();

		// do the test using configuration admin service
		Dictionary<String,Object> p = new Hashtable<String,Object>();
		p.put("test_key", "test_create_factory_configuration_service");

		Configuration c = configAdminService.createFactoryConfiguration(fpid,
				location);
		// commmenting next line makes the next test fail
		c.update(p);
		Configuration cAgain = configAdminService.getConfiguration(c.getPid(),
				location);
		cAgain.update(p);
		assertEquals("Check pids", cAgain.getPid(), c.getPid());
		assertEquals("Factory pids", cAgain.getFactoryPid(), fpid);

		// Matches configAdminMBean's view
		String fpidMBean = configAdminMBean.getFactoryPidForLocation(cAgain
				.getPid(), location);
		assertEquals("Fpid from configAdminMBean", fpid, fpidMBean);

		// do the test using configuration admin mbean
		String pid = configAdminMBean.createFactoryConfigurationForLocation(
				fpid, location);
		assertNotNull(pid);
		configAdminMBean.update(pid, OSGiProperties.tableFrom(p));

		c = configAdminService.getConfiguration(pid, location);
		assertEquals("Pid does not match", pid, c.getPid());
		assertEquals("Factory does not match", fpid, c.getFactoryPid());
		assertEquals("Location does not match", location, c.getBundleLocation());

		String fmpid = configAdminMBean.getFactoryPidForLocation(pid, location);

		assertEquals(
				"test doesn't work using config admin mbean; wrong factory pid",
				fpid, fmpid);
		/*
		 * Bug report for this method is
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1527#c8
		 * 
		 */
	}

	public void testCreateFactoryConfiguration() throws IOException {
		String testFactoryId = testBundle2.getBundleId() + ".factory";

		// do the test using configuration admin service
		Dictionary properties = new Properties();
		properties.put("test_key", "test_create_factory_configuration_service");

		Configuration test2factoryServiceCfg = configAdminService
				.createFactoryConfiguration(testFactoryId);
		test2factoryServiceCfg.update(properties);
		Configuration cfg = configAdminService
				.getConfiguration(test2factoryServiceCfg.getPid());
		assertTrue("test doesn't work using config admin service", cfg
				.getFactoryPid().equals(testFactoryId));

		// do the test using configuration admin mbean
		properties = new Properties();
		properties.put("test_key", "test_create_factory_configuration_mbean");

		String test2factoryMBean = configAdminMBean
				.createFactoryConfiguration(testFactoryId);
		assertNotNull(test2factoryMBean);
		configAdminMBean.update(test2factoryMBean, OSGiProperties
				.tableFrom(properties));
		assertTrue(
				"test doesn't work using config admin mbean; wrong factory pid",
				testFactoryId.equals(configAdminMBean
						.getFactoryPid(test2factoryMBean)));
		/*
		 * Bug report for this method is
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1527#c8
		 * 
		 */
	}

	public void testExceptions() {
		/*
		 * Bug report for this method is
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1605
		 */

		assertNotNull(configAdminMBean);

		// test createFactoryConfiguration method
		try {
			configAdminMBean.createFactoryConfiguration(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfiguration throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.createFactoryConfiguration(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfiguration throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.createFactoryConfiguration(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfiguration throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test createFactoryConfigurationForLocation method
		try {
			configAdminMBean.createFactoryConfigurationForLocation(STRING_NULL,
					STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfigurationForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.createFactoryConfigurationForLocation(
					STRING_EMPTY, STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfigurationForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.createFactoryConfigurationForLocation(
					STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method createFactoryConfigurationForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test delete method
		try {
			configAdminMBean.delete(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method delete throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.delete(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method delete throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.delete(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method delete throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test deleteConfigurations method
		try {
			configAdminMBean.deleteConfigurations(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			configAdminMBean.deleteConfigurations(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			configAdminMBean.deleteConfigurations(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}

		// test deleteForLocation method
		try {
			configAdminMBean.deleteForLocation(STRING_NULL, STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method deleteForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.deleteForLocation(STRING_EMPTY, STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method deleteForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.deleteForLocation(STRING_SPECIAL_SYMBOLS,
					STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method deleteForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test getBundleLocation method
		try {
			configAdminMBean.getBundleLocation(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getBundleLocation(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getBundleLocation(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test getConfigurations method
		try {
			configAdminMBean.getConfigurations(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			configAdminMBean.getConfigurations(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			configAdminMBean.getConfigurations(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeMBeanException e) {
			// spec describes this method could throw IllegalArgumentException;
			// let's check
			assertRootCauseIllegalArgumentException(e);
		}

		// test getFactoryPid method
		try {
			configAdminMBean.getFactoryPid(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPid throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getFactoryPid(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPid throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getFactoryPid(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPid throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test getFactoryPidForLocation method
		try {
			configAdminMBean.getFactoryPidForLocation(STRING_NULL, STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPidForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getFactoryPidForLocation(STRING_EMPTY,
					STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPidForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getFactoryPidForLocation(STRING_SPECIAL_SYMBOLS,
					STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getFactoryPidForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test getProperties method
		try {
			configAdminMBean.getProperties(STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getProperties throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getProperties(STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getProperties throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getProperties(STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getProperties throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test getPropertiesForLocation method
		try {
			configAdminMBean.getPropertiesForLocation(STRING_NULL, STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getPropertiesForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getPropertiesForLocation(STRING_EMPTY,
					STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getPropertiesForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.getPropertiesForLocation(STRING_SPECIAL_SYMBOLS,
					STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method getPropertiesForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test setBundleLocation method
		try {
			configAdminMBean.setBundleLocation(STRING_NULL, STRING_NULL);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method setBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.setBundleLocation(STRING_EMPTY, STRING_EMPTY);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method setBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.setBundleLocation(STRING_SPECIAL_SYMBOLS,
					STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method setBundleLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test update method
		try {
			configAdminMBean.update(STRING_NULL, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method update throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.update(STRING_EMPTY, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method update throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.update(STRING_SPECIAL_SYMBOLS, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method update throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}

		// test updateForLocation method
		try {
			configAdminMBean.updateForLocation(STRING_NULL, STRING_NULL, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method updateForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean
					.updateForLocation(STRING_EMPTY, STRING_EMPTY, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method updateForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
		try {
			configAdminMBean.updateForLocation(STRING_SPECIAL_SYMBOLS,
					STRING_SPECIAL_SYMBOLS, null);
		}
		catch (IOException ioException) {
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			assertTrue(
					"method updateForLocation throws runtime exception, but only IOException is allowed; runtime exception is "
							+ e.toString(), false);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super
				.waitForUnRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));
		if (testBundle2 != null) {
			try {
				super.uninstallBundle(testBundle2);
			}
			catch (Exception io) {
			}
		}
	}
}

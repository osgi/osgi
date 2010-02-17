package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.jmx.service.cm.ConfigurationAdminMBean;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigurationAdminMBeanTestCase extends MBeanGeneralTestCase {
	private ConfigurationAdmin configAdminService;
	private ConfigurationAdminMBean configAdminMBean;
	
	private BundleContext bundleContext;
	private Bundle testBundle2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();
		
		super.waitForRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));
		
		configAdminMBean = super.getMBeanFromServer(ConfigurationAdminMBean.OBJECTNAME,	ConfigurationAdminMBean.class);
		bundleContext = super.getContext();
		configAdminService = (ConfigurationAdmin) bundleContext.getService(bundleContext.getServiceReference(ConfigurationAdmin.class.getName()));
		
		assertNotNull(configAdminMBean);
		assertNotNull(bundleContext);
		assertNotNull(configAdminService);
	}

	public void testConfigurationGetBundleLocation() throws Exception {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_get_bundle_location");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		String bundleLocation = configAdminMBean.getBundleLocation(configurationPid);
		assertTrue("bundle location is wrong " + bundleLocation + " " + testBundle2.getLocation(), testBundle2.getLocation().equals(bundleLocation));
	}
	
	public void testConfigurationSetBundleLocation() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_set_bundle_location");
		configAdminMBean.setBundleLocation(configurationPid, testBundle2.getLocation());		
		configAdminMBean.update(configurationPid, OSGiProperties.tableFrom(props));
		String bundleLocation = configAdminMBean.getBundleLocation(configurationPid);
		assertTrue("bundle location is wrong " + bundleLocation + " " + testBundle2.getLocation(), testBundle2.getLocation().equals(bundleLocation));
	}

	public void testConfigurationUpdate() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_update");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		assertTrue("property was not updated", "test_update".equals(OSGiProperties.propertiesFrom(result).get("test_key")));
	}
	
	public void testConfigurationUpdateForLocation() throws IOException {	
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_update_for_location");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getPropertiesForLocation(configurationPid, testBundle2.getLocation());
		assertNotNull(result);
		assertTrue("property was not updated for location", "test_update_for_location".equals(OSGiProperties.propertiesFrom(result).get("test_key")));
	}

	public void testConfigurationGetProperties() throws IOException {	
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key1", "test_get_properties1");
		props.setProperty("test_key2", "test_get_properties2");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		OSGiProperties.propertiesFrom(result).get("test_key");
		assertTrue("properties were not retrieved properly", "test_get_properties1".equals(OSGiProperties.propertiesFrom(result).get("test_key1")) &&
															 "test_get_properties2".equals(OSGiProperties.propertiesFrom(result).get("test_key2")));		
	}

	public void testConfigurationGetPropertiesForLocation() throws IOException {	
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key1", "test_get_properties_for_location1");
		props.setProperty("test_key2", "test_get_properties_for_location2");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getPropertiesForLocation(configurationPid, testBundle2.getLocation());
		assertNotNull(result);
		OSGiProperties.propertiesFrom(result).get("test_key");
		assertTrue("properties were not retrieved properly", "test_get_properties_for_location1".equals(OSGiProperties.propertiesFrom(result).get("test_key1")) &&
															 "test_get_properties_for_location2".equals(OSGiProperties.propertiesFrom(result).get("test_key2")));		
	}
	
	public void testConfigurationDelete() throws IOException {
		String configurationPid = testBundle2.getBundleId() + ".1";
		Properties props = new Properties();
		props.setProperty("test_key", "test_delete");
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
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
		configAdminMBean.updateForLocation(configurationPid, testBundle2.getLocation(), OSGiProperties.tableFrom(props));
		TabularData result = configAdminMBean.getProperties(configurationPid);
		assertNotNull(result);
		configAdminMBean.deleteForLocation(configurationPid, testBundle2.getLocation());
		result = configAdminMBean.getProperties(configurationPid);
		assertNull(result);
	}

	public void testGetFactoryPid() throws Exception {
		String test2factory = testBundle2.getBundleId() + ".factory";		
        Configuration configuration = configAdminService.createFactoryConfiguration(test2factory, null);
        Dictionary properties = new Properties();
        properties.put("test_key", "test_factory_pid");
        properties.put(Constants.SERVICE_PID, test2factory);
        configuration.update(properties);  
         
        assertTrue("get factory pid doesn't work", test2factory.equals(configAdminMBean.getFactoryPid(configuration.getPid())));
	}

	public void testGetFactoryPidForLocation() throws Exception {
		String test2factory = testBundle2.getBundleId() + ".factory";		
        Configuration configuration = configAdminService.createFactoryConfiguration(test2factory, null);
        Dictionary properties = new Properties();
        properties.put("test_key", "test_factory_pid");
        properties.put(Constants.SERVICE_PID, test2factory);
        configuration.update(properties);  
         
        assertTrue("get factory pid doesn't work", test2factory.equals(configAdminMBean.getFactoryPidForLocation(configuration.getPid(), testBundle2.getLocation())));
	}

	public void testCreateFactoryConfigurationForLocation() throws IOException {
		String testFactoryId = testBundle2.getBundleId() + ".factory";
		
		//do the test using configuration admin service 
        Dictionary properties = new Properties();
        properties.put("test_key", "test_create_factory_configuration_service");
		
        Configuration test2factoryServiceCfg = configAdminService.createFactoryConfiguration(testFactoryId, testBundle2.getLocation());
        test2factoryServiceCfg.update(properties);        
        Configuration cfg = configAdminService.getConfiguration(test2factoryServiceCfg.getPid());
        assertTrue("test doesn't work using config admin service", cfg.getFactoryPid().equals(testFactoryId));

		//do the test using configuration admin mbean
        properties = new Properties();
        properties.put("test_key", "test_create_factory_configuration_mbean");
        
		String test2factoryMBean = configAdminMBean.createFactoryConfigurationForLocation(testFactoryId, testBundle2.getLocation());
		assertNotNull(test2factoryMBean);
        configAdminMBean.update(test2factoryMBean, OSGiProperties.tableFrom(properties));
        assertTrue("test doesn't work using config admin mbean; wrong factory pid", testFactoryId.equals(configAdminMBean.getFactoryPid(test2factoryMBean)));
	}

	public void testCreateFactoryConfiguration() throws IOException {
		String testFactoryId = testBundle2.getBundleId() + ".factory";
		
		//do the test using configuration admin service 
        Dictionary properties = new Properties();
        properties.put("test_key", "test_create_factory_configuration_service");
		
        Configuration test2factoryServiceCfg = configAdminService.createFactoryConfiguration(testFactoryId);
        test2factoryServiceCfg.update(properties);        
        Configuration cfg = configAdminService.getConfiguration(test2factoryServiceCfg.getPid());
        assertTrue("test doesn't work using config admin service", cfg.getFactoryPid().equals(testFactoryId));

		//do the test using configuration admin mbean
        properties = new Properties();
        properties.put("test_key", "test_create_factory_configuration_mbean");
        
		String test2factoryMBean = configAdminMBean.createFactoryConfiguration(testFactoryId);
		assertNotNull(test2factoryMBean);
        configAdminMBean.update(test2factoryMBean, OSGiProperties.tableFrom(properties));
        assertTrue("test doesn't work using config admin mbean; wrong factory pid", testFactoryId.equals(configAdminMBean.getFactoryPid(test2factoryMBean)));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));
		if (testBundle2 != null) {
			try {
				super.uninstallBundle(testBundle2);
			} catch (Exception io) {}
		}		
	}
}

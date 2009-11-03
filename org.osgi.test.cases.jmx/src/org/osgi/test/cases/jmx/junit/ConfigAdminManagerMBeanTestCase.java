package org.osgi.test.cases.jmx.junit;

import java.io.*;

import org.osgi.framework.*;
import org.osgi.jmx.service.cm.*;
import org.osgi.service.cm.*;

public class ConfigAdminManagerMBeanTestCase extends MBeanGeneralTestCase {
	private ConfigurationAdmin configAdminService;
	private ConfigurationAdminMBean configAdminMBean;
	private BundleContext bundleContext;
	private Bundle testBundle;
	private String bundleName;
	private String key = "key";
	private String value = "value";
	private String type = "String";
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBundle = super.install("tb1.jar");
		bundleName = testBundle.getSymbolicName();
		super.waitForRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));
		configAdminMBean = super.getMBeanFromServer(ConfigurationAdminMBean.OBJECTNAME,
				ConfigurationAdminMBean.class);
		bundleContext = super.getContext();
		configAdminService = (ConfigurationAdmin) bundleContext
				.getService(bundleContext
						.getServiceReference(ConfigurationAdmin.class.getName()));
		assertNotNull(configAdminMBean);
		assertNotNull(bundleContext);
		assertNotNull(configAdminService);
	}

	public void testAddAndGetProperty() throws IOException {
		String bundleName = testBundle.getSymbolicName();
		String key = "key";
		String value = "value";
		String type = "String";

		// TODO addProperty is gone
//		configAdminMBean.addProperty(bundleName, key, value, type);
//
//		assertNotNull("failed to retrieve previously stored configuration "
//				+ bundleName, configAdminMBean.getProperties(bundleName));
//		Hashtable<String, Object> props = OSGiProperties
//				.propertiesFrom(configAdminMBean.getProperties(bundleName));
//		assertTrue("failed to find key " + key + " in returned configuration.",
//				props.containsKey(key));
//		assertTrue("returned value " + props.get(key)
//				+ "does not equal the previously store value " + value, props
//				.get(key).equals(value));

	}

	public void testDeleteProperty() throws IOException {
		// TODO addProperty is gone
//		configAdminMBean.addProperty(bundleName, key, value, type);
//		configAdminMBean.deleteProperty(bundleName, key);
//		Hashtable<String, Object> props = OSGiProperties.propertiesFrom(configAdminMBean.getProperties(bundleName));
//		assertNull("failed to delete property " +  key + "from ", props.get(key));
	}

	public void testDeleteConfiguration() throws IOException {
		// TODO addProperty is gone
//		configAdminMBean.addProperty(bundleName, key, value, type);
//		configAdminMBean.delete(bundleName);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(ConfigurationAdminMBean.OBJECTNAME));
	}

}

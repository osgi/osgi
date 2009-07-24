package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.cm.ConfigAdminManagerMBean;
import org.osgi.jmx.codec.OSGiProperties;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigAdminManagerMBeanTestCase extends MBeanGeneralTestCase {
	private ConfigurationAdmin configAdminService;
	private ConfigAdminManagerMBean configAdminMBean;
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
		super.waitForRegistering(createObjectName(JmxConstants.CM_SERVICE));
		configAdminMBean = super.getMBeanFromServer(JmxConstants.CM_SERVICE,
				ConfigAdminManagerMBean.class);
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

		configAdminMBean.addProperty(bundleName, key, value, type);

		assertNotNull("failed to retrieve previously stored configuration "
				+ bundleName, configAdminMBean.getProperties(bundleName));
		Hashtable<String, Object> props = OSGiProperties
				.propertiesFrom(configAdminMBean.getProperties(bundleName));
		assertTrue("failed to find key " + key + " in returned configuration.",
				props.containsKey(key));
		assertTrue("returned value " + props.get(key)
				+ "does not equal the previously store value " + value, props
				.get(key).equals(value));

	}

	public void testDeleteProperty() throws IOException {
		configAdminMBean.addProperty(bundleName, key, value, type);
		configAdminMBean.deleteProperty(bundleName, key);
		Hashtable<String, Object> props = OSGiProperties.propertiesFrom(configAdminMBean.getProperties(bundleName));
		assertNull("failed to delete property " +  key + "from ", props.get(key));
	}

	public void testDeleteConfiguration() throws IOException {
		configAdminMBean.addProperty(bundleName, key, value, type);
		configAdminMBean.delete(bundleName);
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		super.waitForUnRegistering(createObjectName(JmxConstants.CM_SERVICE));
	}

}

package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.codec.OSGiProperties;
import org.osgi.jmx.service.provisioning.ProvisioningMBean;
import org.osgi.service.provisioning.ProvisioningService;

public class ProvisioningMBeanTestCase extends MBeanGeneralTestCase {
	private ProvisioningMBean pMBean;
	private ProvisioningService pService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		super.waitForRegistering(createObjectName(JmxConstants.PS_SERVICE));
		pMBean = getMBeanFromServer(JmxConstants.PS_SERVICE,
				ProvisioningMBean.class);
		pService = (ProvisioningService) getContext().getService(
				getContext().getServiceReference(
						ProvisioningService.class.getName()));
	}

	@SuppressWarnings("unchecked")
	public void testGetInformation() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pService);

		assertNotNull("Failed to retrieve provisioning information from MBean",
				pMBean.getInformation());

		Hashtable<String, Object> table = OSGiProperties.propertiesFrom(pMBean
				.getInformation());

		Dictionary dict = pService.getInformation();
		compareDictAndTable(dict, table);
	}

	public void testAddInformation() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pService);

		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("foo.info", "bar.value");
		dict.put("one.key", "another.value");
		pMBean.addInformation(OSGiProperties.tableFrom(dict));
		Hashtable<String, Object> table = OSGiProperties.propertiesFrom(pMBean
				.getInformation());
		compareDictAndTable(dict, table);
	}

	@SuppressWarnings("unchecked")
	private void compareDictAndTable(Dictionary<String, String> dict,
			Hashtable<String, Object> table) {
		Enumeration keyEnumeration = dict.keys();
		while (keyEnumeration.hasMoreElements()) {
			Object key = keyEnumeration.nextElement();
			assertTrue("failed to verify key "+key+" in the dictionary.", table.containsKey(key));
			assertTrue("failed to verify key "+key+" in the dictionary.", table.get(key).equals(dict.get(key)));
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(JmxConstants.PS_SERVICE));
	}
}

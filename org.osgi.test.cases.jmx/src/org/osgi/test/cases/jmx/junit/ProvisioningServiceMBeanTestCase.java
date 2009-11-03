package org.osgi.test.cases.jmx.junit;

import java.io.*;
import java.util.*;

import org.osgi.jmx.service.provisioning.*;
import org.osgi.service.provisioning.*;

public class ProvisioningServiceMBeanTestCase extends MBeanGeneralTestCase {
	private ProvisioningServiceMBean pMBean;
	private ProvisioningService pService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		super.waitForRegistering(createObjectName(ProvisioningServiceMBean.OBJECTNAME));
		pMBean = getMBeanFromServer(ProvisioningServiceMBean.OBJECTNAME,
				ProvisioningServiceMBean.class);
		pService = (ProvisioningService) getContext().getService(
				getContext().getServiceReference(
						ProvisioningService.class.getName()));
	}

	@SuppressWarnings("unchecked")
	public void testGetInformation() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pService);

		assertNotNull("Failed to retrieve provisioning information from MBean",
				pMBean.listInformation());

		Hashtable<String, Object> table = OSGiProperties.propertiesFrom(pMBean
				.listInformation());

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
				.listInformation());
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
		super.waitForUnRegistering(createObjectName(ProvisioningServiceMBean.OBJECTNAME));
	}
}

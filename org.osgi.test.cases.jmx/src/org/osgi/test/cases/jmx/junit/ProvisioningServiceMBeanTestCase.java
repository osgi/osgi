package org.osgi.test.cases.jmx.junit;

import java.io.*;
import java.util.*;

import javax.management.RuntimeMBeanException;

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

	public void testSetInformation() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pService);

		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("foo.info", "bar.value");
		dict.put("one.key", "another.value");
		pMBean.addInformation(OSGiProperties.tableFrom(dict));
		Hashtable<String, Object> table = OSGiProperties.propertiesFrom(pMBean.listInformation());
		compareDictAndTable(dict, table);

		dict = new Hashtable<String, String>();
		dict.put("one.key", "another.value.new");
		pMBean.setInformation(OSGiProperties.tableFrom(dict));
		table = OSGiProperties.propertiesFrom(pMBean.listInformation());
		assertTrue("set information doesn't work", table.size() == 2);
		compareDictAndTable(dict, table);		
	}
	
	public void testAddInformationFromZip() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pService);

		pMBean.addInformationFromZip(getContext().getBundle().getEntry("tb2.jar").toString());
		Hashtable<String, Object> table = OSGiProperties.propertiesFrom(pMBean.listInformation());
		assertTrue("add information from zip doesn't work", table.keySet().contains("org/osgi/test/cases/jmx/tb2/impl/ConfiguratorImpl.class"));
	}
	
	public void testExceptions() {
		assertNotNull(pMBean);
		
		//test listInformation method
		try {
			pMBean.listInformation();			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method listInformation throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test addInformationFromZip method
		try {
			pMBean.addInformationFromZip(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addInformationFromZip throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			pMBean.addInformationFromZip(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addInformationFromZip throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			pMBean.addInformationFromZip(STRING_URL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addInformationFromZip throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}

		//test addInformation method
		try {
			pMBean.addInformation(null);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addInformation throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}

		//test setInformation method
		try {
			pMBean.setInformation(null);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method setInformation throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
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

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
package org.osgi.test.cases.jmx.provisioning.junit;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.jmx.service.provisioning.ProvisioningServiceMBean;
import org.osgi.service.provisioning.ProvisioningService;

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

		Dictionary<String,Object> dict = pService.getInformation();
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
		assertTrue(
				"add information from zip doesn't work",
				table.keySet()
						.contains(
								"org/osgi/test/cases/jmx/provisioning/tb2/impl/ConfiguratorImpl.class"));
	}
	
	public void testExceptions() {
		assertNotNull(pMBean);
		
		//test listInformation method
		try {
			pMBean.listInformation();			
		}
		catch (IOException e) {
			// expected
		}
		
		//test addInformationFromZip method
		try {
			pMBean.addInformationFromZip(STRING_NULL);			
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.addInformationFromZip(STRING_EMPTY);			
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			pMBean.addInformationFromZip(STRING_URL);			
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test addInformation method
		try {
			pMBean.addInformation(null);			
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test setInformation method
		try {
			pMBean.setInformation(null);			
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
	}
	
	private void compareDictAndTable(Dictionary<String, ? > dict,
			Hashtable<String, ? > table) {
		Enumeration<String> keyEnumeration = dict.keys();
		while (keyEnumeration.hasMoreElements()) {
			String key = keyEnumeration.nextElement();
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

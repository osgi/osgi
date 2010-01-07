/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.jpa.junit;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 *	Test cases for registering and interacting with persistence units
 *
 * 
 * @version $Rev$ $Date$
 */

public class PersistenceUnitTests extends DefaultTestBundleControl {

	public void testDefaultPersistenceLocation() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		EntityManagerFactoryBuilder persistenceUnit = null;
		
		try {
			persistenceUnit = (EntityManagerFactoryBuilder) getService (EntityManagerFactoryBuilder.class, "(osgi.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			} 
		} finally {
			if (persistenceUnit != null) {
				ungetService(persistenceUnit);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testNonStandardPersistenceLocation() throws Exception {
		Bundle persistenceBundle = installBundle("nonStandardPersistenceLocation.jar");
		EntityManagerFactoryBuilder persistenceUnit = null;
		
		try {
			persistenceUnit = (EntityManagerFactoryBuilder) getService (EntityManagerFactoryBuilder.class, "(osgi.unit.name=testUnit2)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			} 
		} finally {
			if (persistenceUnit != null) {
				ungetService(persistenceUnit);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testMultiplePersistenceLocations() throws Exception {
		Bundle persistenceBundle = installBundle("multiplePersistenceLocations.jar");
		EntityManagerFactoryBuilder persistenceUnit1 = null;
		EntityManagerFactoryBuilder persistenceUnit2 = null;
		
		try {
			persistenceUnit1 = (EntityManagerFactoryBuilder) getService (EntityManagerFactoryBuilder.class, "(osgi.unit.name=testUnit3)");
			if (persistenceUnit1 == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			persistenceUnit2 = (EntityManagerFactoryBuilder) getService (EntityManagerFactoryBuilder.class, "(osgi.unit.name=testUnit4)");
			if (persistenceUnit2 == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
		} finally {
			if (persistenceUnit1 != null) {
				ungetService(persistenceUnit1);
			}
			
			if (persistenceUnit2 != null) {
				ungetService(persistenceUnit2);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testNestedJarPersistenceLocation() throws Exception {
		Bundle persistenceBundle = installBundle("nestedJarPersistenceLocation.jar");
		EntityManagerFactoryBuilder persistenceUnit = null;
		
		try { 
			persistenceUnit = (EntityManagerFactoryBuilder) getService (EntityManagerFactoryBuilder.class, "(osgi.unit.name=testUnit5)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
		} finally {
			if (persistenceUnit != null) {
				ungetService(persistenceUnit);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPesistenceUnitServiceProperties() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		
		try {
			ServiceReference unitRef = getContext().getServiceReference(EntityManagerFactoryBuilder.class.getName());
			String unitName = (String) unitRef.getProperty("osgi.unit.name");
			String unitVersion = (String) unitRef.getProperty("osgi.unit.version");
			String providerName = (String) unitRef.getProperty("osgi.provider");
			
			if (unitName == null) {
				fail("The osgi.unit.name property is not set.");
			} else if (!unitName.equals("testUnit1")) {
				fail("The osgi.unit.name property is not set correctly.  Received osgi.unit.name=" + unitName + " but expected osgi.unit.name=testUnit1");
			}
			
			if (unitVersion == null) {
				fail("The osgi.unit.version property is not set.");
			} else if (!unitVersion.equals(persistenceBundle.getVersion().toString())) {
				fail("The osgi.unit.version property is not set correctly.  Received osgi.unit.version=" + unitVersion + " but expected osgi.unit.version=" + persistenceBundle.getVersion().toString());
			}
			
			ServiceReference providerRef = (ServiceReference) getServiceReference(PersistenceProvider.class);
			if (providerName == null) {
				fail("The osgi.provider property is not set.");
			} else if (!providerName.equals(providerRef.getProperty("javax.persistence.provider"))) {
				fail("The osgi.provider property is not set correctly.  Received osgi.provider=" + providerName + " but expected osgi.provider=" + providerRef.getProperty("javax.persistence.provider"));
			}
		} catch (Exception ex) {
			fail("Unable to verify PersistenctUnitInfoService service properties.", ex);
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
}

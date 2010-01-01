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

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jpa.PersistenceUnitInfoService;
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
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService (PersistenceUnitInfoService.class, "(osgi.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			} 
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testNonStandardPersistenceLocation() throws Exception {
		Bundle persistenceBundle = installBundle("nonStandardPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService (PersistenceUnitInfoService.class, "(osgi.unit.name=testUnit2)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			} 
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testMultiplePersistenceLocations() throws Exception {
		Bundle persistenceBundle = installBundle("multiplePersistenceLocations.jar");
		try {
			PersistenceUnitInfoService persistenceUnit1 = (PersistenceUnitInfoService) getService (PersistenceUnitInfoService.class, "(osgi.unit.name=testUnit3)");
			if (persistenceUnit1 == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			PersistenceUnitInfoService persistenceUnit2 = (PersistenceUnitInfoService) getService (PersistenceUnitInfoService.class, "(osgi.unit.name=testUnit4)");
			if (persistenceUnit2 == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testNestedJarPersistenceLocation() throws Exception {
		Bundle persistenceBundle = installBundle("nestedJarPersistenceLocation.jar");
		try { 
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService (PersistenceUnitInfoService.class, "(osgi.unit.name=testUnit5)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testMetadata() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			Map<String,Object> testMetadata = (Map<String,Object>) persistenceUnit.getPersistenceXmlMetadata();
			Properties testProperties = (Properties) testMetadata.get(PersistenceUnitInfoService.PROPERTIES);
			if (!testProperties.containsKey("testMetadata")) {
				fail("The PersistenceUnitInfoService metadata does not contain the properties defined in the persistence.xml file.");
			}
			
		} catch (Exception ex) {
			fail("Failed to retrieve the PersistenceUnitInfoService metadata.", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testProviderReference() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
 		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			ServiceReference provider = (ServiceReference) persistenceUnit.getProviderReference();
			if (provider == null) {
				fail("Unable to retrieve the persistence provider service reference from the PersistenceUnitInfoService instance.");
			} 
			
		} catch (Exception ex) {
				fail("Unable to retrieve the persistence provider service reference from the PersistenceUnitInfoService instance.", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPersistenceXmlUrl() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			URL persistenceXmlUrl = persistenceUnit.getPersistenceXmlLocation();
			if (persistenceXmlUrl == null) {
				fail("Unable to retrieve the persistence.xml url from the PersistenceUnitInfoService instance");
			}	
			
		} catch (Exception ex) {
			fail("Unable to retrieve the persistence.xml url from the PersistenceUnitInfoService instance", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPersistenceRootUrl() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			URL persistenceRootUrl = persistenceUnit.getPersistenceUnitRoot();
			if (persistenceRootUrl == null) {
				fail("Unable to retrieve the persistence.xml root url from the PersistenceUnitInfoService instance");
			}
			
		} catch (Exception ex) {
			fail("Unable to retrieve the persistence.xml root url from the PersistenceUnitInfoService instance", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPersistenceUnitBundle() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			Bundle persistenceUnitBundle = persistenceUnit.getDefiningBundle();
			if (!(persistenceUnitBundle == persistenceBundle)) {
				fail("Unable to retrieve the persistence unit bundle from the PersistenceUnitInfoService instance");
			}
			
		} catch (Exception ex) {
			fail("Unable to retrieve the persistence unit bundle from the PersistenceUnitInfoService instance", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPersistenceUnitClassloader() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class, "(osgi.jpa.persistence.unit.name=testUnit1)");
			if (persistenceUnit == null) {
				fail("Failed to retrieve the specified persistence unit.");
			}
			
			ClassLoader persistenceClassloader = persistenceUnit.getClassLoader();
			if (persistenceClassloader == null) {
				fail("Unable to retrieve the persistence classloader from the PersistenceUnitInfoService instance");
			}
			
		} catch (Exception ex) {
			fail("Unable to retrieve the persistence classloader from the PersistenceUnitInfoService instance", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testPesistenceServiceProperties() throws Exception {
		Bundle persistenceBundle = installBundle("defaultPersistenceLocation.jar");
		
		// TODO: JPA - Replace with appropriate PersistenceUnitInfoService variables when the interface is updated.
		try {
			ServiceReference unitRef = getContext().getServiceReference(PersistenceUnitInfoService.class.getName());
			String unitName = (String) unitRef.getProperty(PersistenceUnitInfoService.PERSISTENCE_UNIT_NAME);
			String bundleName = (String) unitRef.getProperty(PersistenceUnitInfoService.PERSISTENCE_BUNDLE_SYMBOLIC_NAME);
			String bundleVersion = (String) unitRef.getProperty(PersistenceUnitInfoService.PERSISTENCE_BUNDLE_SYMBOLIC_NAME);
			
			if (unitName == null) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_UNIT_NAME + " property is not set.");
			} else if (!unitName.equals("testUnit1")) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_UNIT_NAME + " property is not set correctly.  Received unitName=" + unitName + " but expected unitName=testUnit1");
			}
			
			if (bundleName == null) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_BUNDLE_SYMBOLIC_NAME + " property is not set.");
			} else if (!bundleName.equals(persistenceBundle.getSymbolicName())) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_BUNDLE_SYMBOLIC_NAME + " property is not set correctly.  Received bundleName=" + bundleName + " but expected bundleName=" + persistenceBundle.getSymbolicName());
			}
			
			if (bundleVersion == null) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_BUNDLE_VERSION + " property is not set.");
			} else if (!bundleVersion.equals(persistenceBundle.getVersion())) {
				fail("The " + PersistenceUnitInfoService.PERSISTENCE_BUNDLE_VERSION + " property is not set correctly.  Received bundleVersion=" + bundleVersion + " but expected bundleVersion=" + persistenceBundle.getVersion());
			}
		} catch (Exception ex) {
			fail("Unable to verify PersistenctUnitInfoService service properties.", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
}

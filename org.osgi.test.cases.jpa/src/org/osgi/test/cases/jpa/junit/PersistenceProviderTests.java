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
import org.osgi.service.jpa.PersistenceUnitInfoService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 *	Test cases for registering and interacting with a persistence provider
 *
 * 
 * @version $Rev$ $Date$
 */

public class PersistenceProviderTests extends DefaultTestBundleControl {
	
	public void testProviderRegistration() throws Exception {		
		try {
			PersistenceProvider provider = (PersistenceProvider) getService(javax.persistence.spi.PersistenceProvider.class, "(osgi.jpa.provider.name=org.apache.openjpa.persistence.PersistenceProviderImpl)");
			if (provider == null) {
				fail("The specified persistence provider was not found registered in the service registry");
			} 
		} catch (Exception ex) {
			fail("The specified persistence provider was not found registered in the service registry", ex.getCause());
		}
	}
	
	public void testSpecificProviderVersion() throws Exception {
		Bundle persistenceBundle = installBundle("specificVersionVersionedPersistenceBundle.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class,"(osgi.jpa.persistence.unit.name=testUnit5)");
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
	
	public void testSpecificProviderVersionRange() throws Exception {
		Bundle persistenceBundle = installBundle("validRangeVersionedPersistenceBundle.jar");
		try {
			PersistenceUnitInfoService persistenceUnit = (PersistenceUnitInfoService) getService(PersistenceUnitInfoService.class,"(osgi.jpa.persistence.unit.name=testUnit6)");
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
	
	public void testSpecificProviderVersionOutOfRange() throws Exception {
		Bundle persistenceBundle = installBundle("invalidRangeVersionedPersistenceBundle.jar");
		try {		
			ServiceReference[] refs = getContext().getServiceReferences(PersistenceUnitInfoService.class.getName(),"(osgi.jpa.pesistence.unit.name=testUnit7)");
			if (!(refs == null)) {
				fail("This persistence unit should not have been registered.");
			} 
			
		} catch (Exception ex) {
				fail("", ex.getCause());
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
}

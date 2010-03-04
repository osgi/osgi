/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class JPATestCase extends DefaultTestBundleControl {

	public void testPersistenceClass() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		
		try {
			emf = Persistence.createEntityManagerFactory("emfTestUnit");
			assertNotNull("Unable to create the specified EntityManagerFactory", emf);
			EntityManager em = emf.createEntityManager();
		} finally {
			if (emf != null) {
				emf.close();
				ungetService(emf);
			}
			uninstallBundle(persistenceBundle);
		}
	
	}
	
	public void testPersistenceClassWithMap() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactory emf = null;
		
		try {
			ServiceReference dsfRef = getServiceReference(DataSourceFactory.class.getName());
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			emf = Persistence.createEntityManagerFactory("emfBuilderTestUnit", props);
			assertNotNull("Unable to create the specified EntityManagerFactory", emf);
			EntityManager em = emf.createEntityManager();
		} finally {
			if (emf != null) {
				emf.close();
				ungetService(emf);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testEntityManagerFactory() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
			EntityManager em = emf.createEntityManager();
		} finally {
			if (emf != null) {
				emf.close();
				ungetService(emf);
			}
			uninstallBundle(persistenceBundle);
		}
		
	}
	
	public void testEntityManagerFactoryWithIncompletePersistenceUnit() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactory emf = null;
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfBuilderTestUnit)");
			assertNull("There should be no EntityManagerFactory registered since this persistence unit is incomplete", emf);
		} finally {
			if (emf != null) {
				emf.close();
				ungetService(emf);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testEntityManagerFactoryBuilder() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		EntityManagerFactory emf = null;
		
		try {
			emfBuilder = (EntityManagerFactoryBuilder) getService(EntityManagerFactoryBuilder.class, "(osgi.unit.name=emfBuilderTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactoryBuilder", emfBuilder);
			ServiceReference dsfRef = getServiceReference(DataSourceFactory.class.getName());
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			emf = emfBuilder.createEntityManagerFactory(props);
			EntityManager em = emf.createEntityManager();
		} finally {
			if (emfBuilder != null) {
				ungetService(emfBuilder);
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testEntityManagerFactoryRebinding() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
			ServiceReference dsfRef = getServiceReference(DataSourceFactory.class.getName());
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			EntityManager em = emf.createEntityManager(props);
		} catch (java.lang.IllegalArgumentException ex) {
			pass("java.lang.IllegalArgumentException caught in testEntityManagerFactoryRebinding: SUCCESS");
			return;
		} finally {
			if (emf != null) {
				emf.close();
				ungetService(emf);
			}
			uninstallBundle(persistenceBundle);
		}
		failException("testEntityManagerFactoryRebinding failed", java.lang.IllegalArgumentException.class);
	}
	
	public void testPersistenceBundleStopping() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
			EntityManager em = emf.createEntityManager();
			uninstallBundle(persistenceBundle);
			if (em.isOpen()) {
				fail("The EntityManager should have been closed when the persistence bundle was uninstalled");
			}
			if (emf.isOpen()) {
				fail("The EntityManagerFactory should have been closed when the persistence bundle was uninstalled");
			}
		} finally {
			if (persistenceBundle.getState() != Bundle.UNINSTALLED) {
				uninstallBundle(persistenceBundle);
			}
		}
	}
	
	public void testPersistenceProviderRegistration() throws Exception {
		// We should already have a provider present in the registry.  Make sure we can grab it.
		PersistenceProvider provider = (PersistenceProvider) getService(PersistenceProvider.class);
		assertNotNull("The PersistenceProvider service should be registered when the JPA Provider is installed", provider);
		// The javax.persistence.provider property should have been registered alongside the PersistenceProvider service
		ServiceReference providerRef = getServiceReference(provider);
		String javaxPersistenceProvider = (String) providerRef.getProperty("javax.persistence.provider");
		assertNotNull("The javax.persistence.provider service property should be registered alongside the PersistenceProvider service", javaxPersistenceProvider);
	}
}

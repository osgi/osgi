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
import org.osgi.util.tracker.ServiceTracker;

public class JPATestCase extends DefaultTestBundleControl {

    public static final long SERVICE_WAIT_TIME = 5000;
    
	public void testPersistenceClass() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactory.class);
		try {
			emf = Persistence.createEntityManagerFactory("emfTestUnit");
			assertNotNull("Unable to create the specified EntityManagerFactory", emf);
		} finally {
			if (emf != null) {
				emf.close();
			}
			uninstallBundle(persistenceBundle);
		}
	
	}
	
	public void testPersistenceClassWithMap() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactoryBuilder.class);
		try {
			DataSourceFactory dsf = (DataSourceFactory) getService(DataSourceFactory.class);
			ServiceReference dsfRef = getServiceReference(dsf);
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			emf = Persistence.createEntityManagerFactory("emfBuilderTestUnit", props);
			assertNotNull("Unable to create the specified EntityManagerFactory", emf);
		} finally {
			if (emf != null) {
				emf.close();
			}
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testEntityManagerFactory() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactory.class);
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
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
		waitForService(EntityManagerFactoryBuilder.class);
		try {
			ServiceReference[] emfRefs = getContext().getServiceReferences(EntityManagerFactory.class.getName(), "(osgi.unit.name=emfBuilderTestUnit)");
			assertNull("There should be no EntityManagerFactory registered since this persistence unit is incomplete", emfRefs);
		} finally {
			uninstallBundle(persistenceBundle);
		}
	}
	
	public void testEntityManagerFactoryBuilder() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactoryBuilder.class);
		try {
			emfBuilder = (EntityManagerFactoryBuilder) getService(EntityManagerFactoryBuilder.class, "(osgi.unit.name=emfBuilderTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactoryBuilder", emfBuilder);
			DataSourceFactory dsf = (DataSourceFactory) getService(DataSourceFactory.class);
			ServiceReference dsfRef = getServiceReference(dsf);
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			emf = emfBuilder.createEntityManagerFactory(props);
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
		waitForService(EntityManagerFactory.class);
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
			DataSourceFactory dsf = (DataSourceFactory) getService(DataSourceFactory.class);
			ServiceReference dsfRef = getServiceReference(dsf);
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
	
	public void testEntityManagerFactoryBuilderRebinding() throws Exception {
		// Install the bundle necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		EntityManagerFactory emf1 = null;
		EntityManagerFactory emf2 = null;
		waitForService(EntityManagerFactoryBuilder.class);
		try {
			emfBuilder = (EntityManagerFactoryBuilder) getService(EntityManagerFactoryBuilder.class, "(osgi.unit.name=emfBuilderTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactroyBuilder", emfBuilder);
			DataSourceFactory dsf = (DataSourceFactory) getService(DataSourceFactory.class);
			ServiceReference dsfRef = getServiceReference(dsf);
			assertNotNull("Unable to retrieve a reference for the DataSourceFactory service", dsfRef);
			Map props1 = new HashMap();
			props1.put("javax.persistence.jdbc.driver", dsfRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS));
			emf1 = emfBuilder.createEntityManagerFactory(props1);
			EntityManager em = emf1.createEntityManager();
			Map props2 = new HashMap();
			props2.put("javax.persistence.jdbc.driver", "fake.driver.class");
			emf2 = emfBuilder.createEntityManagerFactory(props2);
		} catch (java.lang.IllegalArgumentException ex) {
			pass("java.lang.IllegalArgumentException caught in testEntityManagerFactoryBuilderRebinding: SUCCESS");
			return;
		} finally {
			if (emfBuilder != null) {
				ungetService(emfBuilder);
			}
			uninstallBundle(persistenceBundle);
		}
		failException("testEntityManagerFactoryBuilderRebinding failed", java.lang.IllegalArgumentException.class);
	}
	
	public void testEntityManagerFactoryRebindingWithBuilder() throws Exception {
		// Install the bundle necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		EntityManagerFactory emf1 = null;
		EntityManagerFactory emf2 = null;
		waitForService(EntityManagerFactory.class);
		waitForService(EntityManagerFactoryBuilder.class);
		try {
			emf1 = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf1);
			EntityManager em = emf1.createEntityManager();
			emfBuilder = (EntityManagerFactoryBuilder) getService(EntityManagerFactoryBuilder.class, "(osgi.unit.name=emfTestUnit)");
			Map props = new HashMap();
			props.put("javax.persistence.jdbc.driver", "fake.driver.class");
			emf2 = emfBuilder.createEntityManagerFactory(props);
		} catch (java.lang.IllegalArgumentException ex) {
			pass("java.lang.IllegalArgumentException caught in testEntityManagerFactoryRebindingWithBuilder: SUCCESS");
			return;
		} finally {
			if (emf1 != null) {
				emf1.close();
				ungetService(emf1);
			}
			if (emf2 != null) {
				emf2.close();
			}
			if (emfBuilder != null) {
				ungetService(emfBuilder);
			}
			uninstallBundle(persistenceBundle);
		}
		failException("testEntityManagerFactoryRebindingWithBuilder failed", java.lang.IllegalArgumentException.class);
		
	}
	
	public void testPersistenceBundleStopping() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBundle.jar");
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactory.class);
		try {
			emf = (EntityManagerFactory) getService(EntityManagerFactory.class, "(osgi.unit.name=emfTestUnit)");
			assertNotNull("Unable to retrieve the specified EntityManagerFactory", emf);
			uninstallBundle(persistenceBundle);
			try {
				if (emf.isOpen()) {
					fail("The EntityManagerFactory should have been closed when the persistence bundle was uninstalled");
				}
			} catch (NullPointerException npe) {
				// Do nothing.  An NPE is expected if the bundle performs all the appropriate steps when stopping.
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
	
    public void waitForService(Class cls) {
        ServiceTracker tracker = new ServiceTracker(getContext(), cls.getName(), null);
        tracker.open();
        Object service = null;
        long start = System.currentTimeMillis();
        do {
        	try { 
        		service = tracker.waitForService(SERVICE_WAIT_TIME);
        	} catch (InterruptedException intEx) {
        		// service will be null
        	}
        } while (System.currentTimeMillis() - start < SERVICE_WAIT_TIME);
        tracker.close();
        assertNotNull("Service for " + cls.getName() + " was not registered after waiting " +
            SERVICE_WAIT_TIME + " milliseconds", service);
    }
}

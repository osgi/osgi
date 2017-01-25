/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.jpa.junit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @version $Rev$ $Date$
 */
public class JPAService_1_1_TestCase extends DefaultTestBundleControl {

	public static final long SERVICE_WAIT_TIME = 5000;

	/**
	 * See JPA Service Spec 127.3.4.1
	 * 
	 * @throws Exception
	 */
	public void testUseStandardDataSourceConfig() throws Exception {

		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		EntityManagerFactory emf = null;
		waitForService(EntityManagerFactoryBuilder.class, true);
		try {
			emfBuilder = getService(EntityManagerFactoryBuilder.class,
					"(osgi.unit.name=emfBuilderTestUnit)");
			assertNotNull(
					"Unable to retrieve the specified EntityManagerFactoryBuilder",
					emfBuilder);

			DataSourceFactory dsf = getService(DataSourceFactory.class);

			Properties props = new Properties();
			props.put(DataSourceFactory.JDBC_URL, "jdbc:h2:mem:");
			DataSource ds = dsf.createDataSource(props);

			Map<String,Object> emfProps = new HashMap<>();
			emfProps.put("javax.persistence.dataSource", ds);
			emf = emfBuilder.createEntityManagerFactory(emfProps);

			EntityManager em = emf.createEntityManager();
			try {
				assertEquals("1", em
						.createNativeQuery("SELECT X FROM SYSTEM_RANGE(1, 1);")
						.getSingleResult()
						.toString());
			} finally {
				em.close();
			}
		} catch (java.lang.IllegalArgumentException ex) {
			fail("Unknown properties should be ignored and not result in an IllegalArgumentException.");
		} finally {
			if (emfBuilder != null) {
				ungetService(emfBuilder);
			}
			uninstallBundle(persistenceBundle);
		}
	}

	/**
	 * See JPA Service Spec 127.4.7
	 * 
	 * @throws Exception
	 */
	public void testUpdateEMFProperties() throws Exception {
		// Install the bundles necessary for this test
		Bundle persistenceBundle = installBundle("emfBuilderBundle.jar");
		EntityManagerFactoryBuilder emfBuilder = null;
		waitForService(EntityManagerFactoryBuilder.class, true);
		try {
			emfBuilder = getService(EntityManagerFactoryBuilder.class,
					"(osgi.unit.name=emfBuilderTestUnit)");
			assertNotNull(
					"Unable to retrieve the specified EntityManagerFactoryBuilder",
					emfBuilder);

			assertNull("There should not be an EntityManagerFactory",
					getContext()
							.getServiceReference(EntityManagerFactory.class));

			Map<String,Object> props = new HashMap<>();
			props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
			props.put("say.hello", "Hello!");
			props.put("javax.persistence.jdbc.password", "Secret");

			emfBuilder.createEntityManagerFactory(props);

			waitForService(EntityManagerFactory.class, true);
			EntityManagerFactory emfService = getService(
					EntityManagerFactory.class);

			ServiceReference< ? > serviceRef = getServiceReference(emfService);
			assertEquals("Hello!", serviceRef.getProperty("say.hello"));
			assertFalse(Arrays.asList(serviceRef.getPropertyKeys())
					.contains("javax.persistence.jdbc.password"));

			props.put("say.hello", "overridden");
			emfBuilder.createEntityManagerFactory(props);

			assertNull(serviceRef.getBundle());
			ungetService(emfService);

			emfService = getService(EntityManagerFactory.class);
			serviceRef = getServiceReference(emfService);
			assertEquals("overridden", serviceRef.getProperty("say.hello"));

		} catch (java.lang.IllegalArgumentException ex) {
			fail("Unknown properties should be ignored and not result in an IllegalArgumentException.");
		} finally {
			if (emfBuilder != null) {
				ungetService(emfBuilder);
			}
			uninstallBundle(persistenceBundle);
		}
	}

	public <T> void waitForService(Class<T> cls, boolean expected) {
		ServiceTracker<T,T> tracker = new ServiceTracker<>(getContext(),
				cls.getName(), null);
		tracker.open();
		Object service = null;
		try {
			service = Tracker.waitForService(tracker, SERVICE_WAIT_TIME);
		} catch (InterruptedException intEx) {
			// service will be null
		}
		tracker.close();

		if (expected) {
			assertNotNull("Service for " + cls.getName()
					+ " was not registered after waiting " + SERVICE_WAIT_TIME
					+ " milliseconds", service);
		} else {
			assertNull(
					"Service for " + cls.getName()
							+ " was registered despite not being expected",
					service);
		}
	}
}

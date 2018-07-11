package org.osgi.test.cases.cdi.junit;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.cases.cdi.interfaces.Pojo;
import org.osgi.util.tracker.ServiceTracker;

public class OptionalReluctantReferenceTests extends AbstractTestCase {

	@Override
	@Before
	public void setUp() throws Exception {
		testHeader();

		cdiRuntime = runtimeTracker.waitForService(timeout);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void applicationScoped() throws Exception {
		Bundle tb = installBundle("tb11.jar");

		try {
			ServiceTracker<Pojo, Pojo> tracker = track("(&(objectClass=%s)(bean.id=as))", Pojo.class.getName());

			Pojo pojo = tracker.waitForService(timeout);

			assertEquals(-1, pojo.getCount());
			assertEquals("-1", pojo.foo(""));

			ContainerDTO containerDTO = getContainerDTO(cdiRuntime, tb);

			long changeCount = containerDTO.changeCount;

			ServiceRegistration<Integer> int1 = bundleContext.registerService(
				Integer.class, new Integer(12),
				new Hashtable<>(Collections.singletonMap("bean.id", "as")));

			try {
				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));

				tb.stop();
				tb.start();

				pojo = tracker.waitForService(timeout);

				assertEquals(12, pojo.getCount());
				assertEquals("12", pojo.foo(""));
			}
			finally {
				changeCount = getContainerDTO(cdiRuntime, tb).changeCount;

				int1.unregister();

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				pojo = tracker.waitForService(timeout);

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));
			}
		}
		finally {
			tb.uninstall();
		}
	}

	@Test
	public void singleComponent() throws Exception {
		Bundle tb = installBundle("tb11.jar");

		try {
			ServiceTracker<Pojo, Pojo> tracker = track("(&(objectClass=%s)(bean.id=sc))", Pojo.class.getName());

			Pojo pojo = tracker.waitForService(timeout);

			assertEquals(-1, pojo.getCount());
			assertEquals("-1", pojo.foo(""));

			ContainerDTO containerDTO = getContainerDTO(cdiRuntime, tb);

			long changeCount = containerDTO.changeCount;

			ServiceRegistration<Integer> int1 = bundleContext.registerService(
				Integer.class, new Integer(12),
				new Hashtable<>(Collections.singletonMap("bean.id", "sc")));

			try {
				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));

				tb.stop();
				tb.start();

				pojo = tracker.waitForService(timeout);

				assertEquals(12, pojo.getCount());
				assertEquals("12", pojo.foo(""));
			}
			finally {
				changeCount = getContainerDTO(cdiRuntime, tb).changeCount;

				int1.unregister();

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				pojo = tracker.waitForService(timeout);

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));
			}
		}
		finally {
			tb.uninstall();
		}
	}

	@Test
	public void factoryComponent() throws Exception {
		adminTracker = new ServiceTracker<>(bundleContext, ConfigurationAdmin.class, null);
		adminTracker.open();
		configurationAdmin = adminTracker.getService();

		Bundle tb = installBundle("tb11.jar");

		try {
			ServiceTracker<Pojo, Pojo> tracker = track("(&(objectClass=%s)(bean.id=fc))", Pojo.class.getName());

			Pojo pojo = tracker.waitForService(timeout);

			assertNull(pojo);

			int trackingCount = tracker.getTrackingCount();

			Configuration configuration = configurationAdmin.createFactoryConfiguration("optionalReference_FC");
			configuration.update(new Hashtable<>(Collections.singletonMap("foo", "bar")));

			for (long i = 10; i > 0 && (tracker.getTrackingCount() == trackingCount); i--) {
				Thread.sleep(20);
			}

			pojo = tracker.waitForService(timeout);

			assertEquals(-1, pojo.getCount());
			assertEquals("-1", pojo.foo(""));

			ContainerDTO containerDTO = getContainerDTO(cdiRuntime, tb);

			long changeCount = containerDTO.changeCount;

			ServiceRegistration<Integer> int1 = bundleContext.registerService(
				Integer.class, new Integer(12),
				new Hashtable<>(Collections.singletonMap("bean.id", "fc")));

			try {
				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));

				tb.stop();
				tb.start();

				pojo = tracker.waitForService(timeout);

				assertEquals(12, pojo.getCount());
				assertEquals("12", pojo.foo(""));
			}
			finally {
				changeCount = getContainerDTO(cdiRuntime, tb).changeCount;

				int1.unregister();

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				pojo = tracker.waitForService(timeout);

				assertEquals(-1, pojo.getCount());
				assertEquals("-1", pojo.foo(""));

				configuration.delete();
			}
		}
		finally {
			tb.uninstall();
			adminTracker.close();
		}
	}

	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> adminTracker;
	private ConfigurationAdmin configurationAdmin;

}

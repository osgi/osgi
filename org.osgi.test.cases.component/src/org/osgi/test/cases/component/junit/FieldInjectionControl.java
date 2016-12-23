/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
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
package org.osgi.test.cases.component.junit;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 * This is the bundle initially installed and started by the TestCase. It
 * performs the test methods of the declarative services test cases for field
 * injection.
 *
 * @author $Id$
 */
@SuppressWarnings("unchecked")
public class FieldInjectionControl extends DefaultTestBundleControl {

	private static int			SLEEP			= 1000;


	protected void setUp() throws Exception {
		String sleepTimeString = getProperty("osgi.tc.component.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Error while parsing sleep value! The default one will be used : "
								+ SLEEP);
			}
			if (sleepTime < 100) {
				System.out.println("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			}
			else {
				SLEEP = sleepTime;
			}
		}
	}

	/**
	 * Simple field injection test. Static unary reference
	 * 
	 * @throws Exception
	 */
	public void testFIStaticUnaryReference() throws Exception {
		final TestObject service = new TestObject();

		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			this.registerService(TestObject.class.getName(), service, null);

			tb.start();

			final BaseService bs = this.getService(
					BaseService.class, "(type=static)");
			assertNotNull(bs.getProperties());
			assertEquals(service, bs.getProperties().get("service"));
			this.ungetService(bs);

			this.unregisterService(service);

			Sleep.sleep(SLEEP * 3);

			assertNull(getContext().getServiceReferences(
					BaseService.class.getName(), "(type=static)"));
		} finally {
			uninstallBundle(tb);
			this.unregisterAllServices();
		}
	}

	/**
	 * Simple field injection test. Dynamic unary reference
	 * 
	 * @throws Exception
	 */
	public void testFIDynamicUnaryReference() throws Exception {
		final Bundle tb = installBundle("tbf1.jar", true);
		try {
			final TestObject service = new TestObject();

			// ref not available
			BaseService bs = this.getService(
					BaseService.class, "(type=dynamic)");
			assertNotNull(bs.getProperties());
			assertNull(bs.getProperties().get("service"));
			ungetService(bs);

			// register ref and wait
			this.registerService(TestObject.class.getName(), service, null);
			Sleep.sleep(SLEEP * 3);

			bs = this.getService(BaseService.class,
					"(type=dynamic)");
			assertNotNull(bs.getProperties());
			assertEquals(service, bs.getProperties().get("service"));

			// unregister ref again and wait
			this.unregisterService(service);
			Sleep.sleep(SLEEP * 3);

			assertNotNull(bs.getProperties());
			assertNull(bs.getProperties().get("service"));
		} finally {
			uninstallBundle(tb);
			this.unregisterAllServices();
		}
	}

	/**
	 * Simple field injection test. Dynamic unary reference where field is not
	 * marked volatile TODO I think we can remove this test as
	 * {@link DeclarativeServicesControl#testDynamicNonVoltaileFieldReference130}
	 * checks the same
	 * 
	 * @throws Exception
	 */
	public void testFIFailingUnaryReference() throws Exception {
		final Bundle tb = installBundle("tbf1.jar", true);
		try {
			// we get the reference just to be sure that
			// component.xml is processed
			getService(BaseService.class, "(type=dynamic)");

			// we get the reference and the service as it's not activated
			// due to a non volatile field
			final Collection<ServiceReference<BaseService>> refs = getContext()
					.getServiceReferences(BaseService.class, "(type=failed)");
			assertNotNull(refs);
			assertFalse(refs.isEmpty());
			final Object service = getContext()
					.getService(refs.iterator().next());
			assertNotNull(service);
		} finally {
			ungetAllServices();
			uninstallBundle(tb);
		}
	}

	/**
	 * Simple field injection test. Field type test
	 * 
	 * @throws Exception
	 */
	public void testFITypeUnaryReference() throws Exception {
		final TestObject service = new TestObject();
		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			this.registerService(TestObject.class.getName(), service, null);
			final ServiceReference<TestObject> ref = this.getContext()
					.getServiceReference(TestObject.class);

			tb.start();

			final BaseService bs = this.getService(
					BaseService.class, "(type=type)");
			assertNotNull(bs.getProperties());

			// service
			assertEquals(service, bs.getProperties().get("service"));

			// service reference
			assertEquals(0, ref.compareTo(bs.getProperties().get("ref")));

			// for the properties map we just check the service id
			final Map<String,Object> props = (Map<String,Object>) bs
					.getProperties().get("map");
			assertNotNull(props);
			assertEquals(ref.getProperty(Constants.SERVICE_ID),
					props.get(Constants.SERVICE_ID));

			// tuple
			final Map.Entry<Map<String,Object>,TestObject> tuple = (Map.Entry<Map<String,Object>,TestObject>) bs
					.getProperties().get("tuple");
			final Map<String,Object> serviceProps = tuple.getKey();
			assertEquals(ref.getProperty(Constants.SERVICE_ID),
					serviceProps.get(Constants.SERVICE_ID));
			assertEquals(service, tuple.getValue());

			// service objects
			final ComponentServiceObjects<TestObject> objects = (ComponentServiceObjects<TestObject>) bs
					.getProperties()
					.get("objects");
			assertNotNull(objects);
			assertEquals(ref.getProperty(Constants.SERVICE_ID), objects
					.getServiceReference().getProperty(Constants.SERVICE_ID));
		} finally {
			ungetAllServices();
			unregisterAllServices();
			uninstallBundle(tb);
		}
	}

	/**
	 * Simple field injection test. Static multiple reference
	 * 
	 * @throws Exception
	 */
	public void testFIStaticMultipleReference() throws Exception {
		final TestObject service = new TestObject();

		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			this.registerService(TestObject.class.getName(), service, null);

			tb.start();

			final BaseService bs = this.getService(
					BaseService.class, "(type=multiple-required)");
			assertNotNull(bs.getProperties());
			assertNotNull(bs.getProperties().get("services"));
			assertEquals(1,
					((List<TestObject>) bs.getProperties().get("services"))
							.size());
			assertEquals(service,
					((List<TestObject>) bs.getProperties().get("services"))
							.get(0));

			this.ungetService(bs);

			this.unregisterService(service);

			Sleep.sleep(SLEEP * 3);

			assertNull(getContext().getServiceReferences(
					BaseService.class.getName(), "(type=multiple-required)"));
		} finally {
			uninstallBundle(tb);
			this.unregisterAllServices();
		}
	}

	/**
	 * Simple field injection test. Dynamic multiple reference
	 * 
	 * @throws Exception
	 */
	public void testFIDynamicMultipleReference() throws Exception {
		final TestObject service1 = new TestObject();
		final TestObject service2 = new TestObject();
		final TestObject service3 = new TestObject();

		final Dictionary<String,Object> props1 = new Hashtable<>();
		props1.put(Constants.SERVICE_RANKING, new Integer(5));
		final Dictionary<String,Object> props2 = new Hashtable<>();
		props2.put(Constants.SERVICE_RANKING, new Integer(10));
		final Dictionary<String,Object> props3 = new Hashtable<>();
		props3.put(Constants.SERVICE_RANKING, new Integer(15));

		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			tb.start();

			final BaseService bs = this.getService(
					BaseService.class, "(type=multiple-dynamic)");
			assertNotNull(bs.getProperties());
			assertNotNull(bs.getProperties().get("services"));
			assertEquals(0,
					((List<TestObject>) bs.getProperties().get("services"))
							.size());

			this.registerService(TestObject.class.getName(), service1, props1);
			this.registerService(TestObject.class.getName(), service3, props3);
			this.registerService(TestObject.class.getName(), service2, props2);

			// unfortunately there is no event we can wait for
			Sleep.sleep(SLEEP);

			assertEquals(3,
					((List<TestObject>) bs.getProperties().get("services"))
							.size());
			assertEquals(service1,
					((List<TestObject>) bs.getProperties().get("services"))
							.get(0));
			assertEquals(service2,
					((List<TestObject>) bs.getProperties().get("services"))
							.get(1));
			assertEquals(service3,
					((List<TestObject>) bs.getProperties().get("services"))
							.get(2));

			this.ungetService(bs);

			this.unregisterService(service3);
			this.unregisterService(service2);
			this.unregisterService(service1);

		} finally {
			uninstallBundle(tb);
			this.unregisterAllServices();
		}
	}
}

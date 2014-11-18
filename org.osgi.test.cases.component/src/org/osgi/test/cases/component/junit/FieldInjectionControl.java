/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
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

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
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
	 * Simple field injection test.
	 *
	 * Static unary reference
	 */
	public void testFIStaticUnaryReference() throws Exception {
		final TestObject service = new TestObject();

		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			this.registerService(TestObject.class.getName(), service, null);

			tb.start();

			final BaseService bs = (BaseService) this.getService(
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
	 * Simple field injection test.
	 *
	 * Dynamic unary reference
	 */
	public void testFIDynamicUnaryReference() throws Exception {
		final Bundle tb = installBundle("tbf1.jar", true);
		try {
			final TestObject service = new TestObject();

			// ref not available
			BaseService bs = (BaseService) this.getService(
					BaseService.class, "(type=dynamic)");
			assertNotNull(bs.getProperties());
			assertNull(bs.getProperties().get("service"));
			ungetService(bs);

			// register ref and wait
			this.registerService(TestObject.class.getName(), service, null);
			Sleep.sleep(SLEEP * 3);

			bs = (BaseService) this.getService(BaseService.class,
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
	 * Simple field injection test.
	 *
	 * Dynamic unary reference where field is not marked volatile
	 */
	public void testFIFailingUnaryReference() throws Exception {
		final Bundle tb = installBundle("tbf1.jar", true);
		try {
			// we get the reference just to be sure that
			// component.xml is processed
			getService(BaseService.class, "(type=dynamic)");

			// we get the reference but not the service as it's not activated
			// due to a non volatile field
			final ServiceReference[] refs = getContext().getServiceReferences(
					BaseService.class.getName(), "(type=failed)");
			assertNotNull(refs);
			final Object service = getContext().getService(refs[0]);
			assertNull(service);
		} finally {
			ungetAllServices();
			uninstallBundle(tb);
		}
	}

	/**
	 * Simple field injection test.
	 *
	 * Field type test
	 */
	public void testFITypeUnaryReference() throws Exception {
		final TestObject service = new TestObject();
		final Bundle tb = installBundle("tbf1.jar", false);
		try {
			this.registerService(TestObject.class.getName(), service, null);
			final ServiceReference ref = this.getContext()
					.getServiceReference(TestObject.class.getName());

			tb.start();

			final BaseService bs = (BaseService) this.getService(
					BaseService.class, "(type=type)");
			assertNotNull(bs.getProperties());

			// service
			assertEquals(service, bs.getProperties().get("service"));

			// service reference
			assertEquals(0, ref.compareTo(bs.getProperties().get("ref")));

			// for the properties map we just check the service id
			final Map props = (Map) bs.getProperties().get("map");
			assertNotNull(props);
			assertEquals(ref.getProperty(Constants.SERVICE_ID),
					props.get(Constants.SERVICE_ID));

			// tuple
			final Map.Entry tuple = (Map.Entry) bs.getProperties().get("tuple");
			final Map serviceProps = (Map) tuple.getKey();
			assertEquals(ref.getProperty(Constants.SERVICE_ID),
					serviceProps.get(Constants.SERVICE_ID));
			assertEquals(service, tuple.getValue());

			// service objects
			final ServiceObjects objects = (ServiceObjects) bs.getProperties()
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
}

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
package org.osgi.test.cases.component.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.assertj.dictionary.DictionaryAssert;
import org.osgi.test.assertj.dictionary.DictionarySoftAssertions;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectInstalledBundle;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.context.InstalledBundleExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.test.support.sleep.Sleep;

/**
 * This is the bundle initially installed and started by the TestCase. It
 * performs the test methods of the declarative services test cases for field
 * injection.
 *
 * @author $Id$
 */
@ExtendWith(SoftAssertionsExtension.class)
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
public class FieldInjectionControl {

	private int					SLEEP	= 1000;

	@InjectBundleContext
	BundleContext				context;

	String						testName;

	@InjectSoftAssertions
	DictionarySoftAssertions	softly;

	@BeforeEach
	void setUp(TestInfo testInfo) throws Exception {
		testName = testInfo.getTestMethod().map(Method::getName).get();
		assertThat(context).isNotNull();
		String sleepTimeString = context
				.getProperty("osgi.tc.component.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(
						"Error while parsing sleep value! The default one will be used : "
								+ SLEEP);
			}
			if (sleepTime < 100) {
				System.out.println("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			} else {
				SLEEP = sleepTime;
			}
		}
	}

	/**
	 * Simple field injection test. Static unary reference
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFIStaticUnaryReference(@InjectInstalledBundle("tbf1.jar")
	Bundle tb, @InjectService(filter = "(type=static)", cardinality = 0)
	ServiceAware<BaseService> bs) throws Exception {
		final TestObject service = new TestObject();

		ServiceRegistration<TestObject> registration = context
				.registerService(TestObject.class, service, null);

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert.assertThat(bs.getService().getProperties())
				.containsEntry("service", service);

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		assertThat(bs.size()).isZero();
	}

	/**
	 * Simple field injection test. Dynamic unary reference
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFIDynamicUnaryReference(
			@InjectInstalledBundle(value = "tbf1.jar", start = true)
			Bundle tb,
			@InjectService(filter = "(type=dynamic)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		final TestObject service = new TestObject();

		// ref not available
		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert.assertThat(bs.getService().getProperties())
				.doesNotContainKey("service");

		// register ref and wait
		ServiceRegistration<TestObject> registration = context
				.registerService(TestObject.class, service, null);
		Sleep.sleep(SLEEP * 3);

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert.assertThat(bs.getService().getProperties())
				.containsEntry("service", service);

		// unregister ref again and wait
		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert.assertThat(bs.getService().getProperties())
				.doesNotContainKey("service");
	}

	/**
	 * Simple field injection test. Dynamic unary reference where field is not
	 * marked volatile TODO I think we can remove this test as
	 * {@link DeclarativeServicesControl#testDynamicNonVoltaileFieldReference130}
	 * checks the same
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFIFailingUnaryReference(
			@InjectInstalledBundle(value = "tbf1.jar", start = true)
			Bundle tb,
			@InjectService(filter = "(type=dynamic)", cardinality = 0)
			ServiceAware<BaseService> bs,
			@InjectService(filter = "(type=failed)", cardinality = 0)
			ServiceAware<BaseService> failed) throws Exception {
		// we get the reference just to be sure that
		// component.xml is processed
		assertThat(bs.waitForService(SLEEP)).isNotNull();

		// we get the reference and the service as it's not activated
		// due to a non volatile field
		assertThat(failed.waitForService(SLEEP)).isNotNull();
	}

	/**
	 * Simple field injection test. Field type test
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFITypeUnaryReference(@InjectInstalledBundle("tbf1.jar")
	Bundle tb, @InjectService(filter = "(type=type)", cardinality = 0)
	ServiceAware<BaseService> bs) throws Exception {
		final TestObject service = new TestObject();
		context.registerService(TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		// service
		softly.check(() -> {
			assertion
					.extractingByKey("service",
							InstanceOfAssertFactories.type(TestObject.class))
					.isSameAs(service);
		});
		// service reference
		softly.check(() -> {
			assertion
					.extractingByKey("ref",
							InstanceOfAssertFactories
									.type(ServiceReference.class))
					.extracting(sr -> sr.getProperty("testName"))
					.isEqualTo(testName);
		});

		// properties map
		softly.check(() -> {
			assertion
					.extractingByKey("map",
							InstanceOfAssertFactories.map(String.class,
									Object.class))
					.containsEntry("testName", testName);
		});

		// tuple
		softly.check(() -> {
			assertion
					.extractingByKey("tuple",
							InstanceOfAssertFactories.type(Map.Entry.class))
					.extracting(entry -> entry.getValue())
					.isSameAs(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tuple",
							InstanceOfAssertFactories.type(Map.Entry.class))
					.extracting(entry -> entry.getKey(),
							InstanceOfAssertFactories.map(String.class,
									Object.class))
					.containsEntry("testName", testName);
		});

		// service objects
		softly.check(() -> {
			assertion
					.extractingByKey("objects",
							InstanceOfAssertFactories
									.type(ComponentServiceObjects.class))
					.extracting(ComponentServiceObjects::getServiceReference)
					.extracting(sr -> sr.getProperty("testName"))
					.isEqualTo(testName);
		});
	}

	/**
	 * Simple field injection test. Static multiple reference
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFIStaticMultipleReference(@InjectInstalledBundle("tbf1.jar")
	Bundle tb,
			@InjectService(filter = "(type=multiple-required)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		final TestObject service = new TestObject();
		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		softly.check(() -> {
			assertion
					.extractingByKey("services",
							InstanceOfAssertFactories.list(TestObject.class))
					.containsOnly(service);
		});

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		assertThat(bs.size()).isZero();
	}

	/**
	 * Simple field injection test. Dynamic multiple reference
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFIDynamicMultipleReference(
			@InjectInstalledBundle("tbf1.jar")
			Bundle tb,
			@InjectService(filter = "(type=multiple-dynamic)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		final TestObject service1 = new TestObject();
		final TestObject service2 = new TestObject();
		final TestObject service3 = new TestObject();

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.check(() -> {
			DictionaryAssert.assertThat(bs.getService().getProperties())
					.isNotNull()
					.extractingByKey("services",
							InstanceOfAssertFactories.list(TestObject.class))
					.isEmpty();
		});

		context.registerService(TestObject.class, service1, Dictionaries
				.dictionaryOf(Constants.SERVICE_RANKING, Integer.valueOf(5)));
		context.registerService(TestObject.class, service3, Dictionaries
				.dictionaryOf(Constants.SERVICE_RANKING, Integer.valueOf(15)));
		context.registerService(TestObject.class, service2, Dictionaries
				.dictionaryOf(Constants.SERVICE_RANKING, Integer.valueOf(10)));

		// unfortunately there is no event we can wait for
		Sleep.sleep(SLEEP);

		softly.check(() -> {
			DictionaryAssert.assertThat(bs.getService().getProperties())
					.isNotNull()
					.extractingByKey("services",
							InstanceOfAssertFactories.list(TestObject.class))
					.containsExactly(service1, service2, service3);
		});
	}
}

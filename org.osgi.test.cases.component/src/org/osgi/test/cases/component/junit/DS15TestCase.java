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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.SatisfiedReferenceDTO;
import org.osgi.service.component.runtime.dto.UnsatisfiedReferenceDTO;
import org.osgi.service.condition.Condition;
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

@ExtendWith(SoftAssertionsExtension.class)
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
public class DS15TestCase {
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

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void static_unary_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=static_unary_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();

		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.containsEntry("serviceComponent", service)
				.containsEntry("serviceField", service)
				.containsEntry("serviceMethod", service)
				.containsEntry("serviceParam", service);

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		softly.assertThat(bs.size()).isZero();
	}

	@Test
	public void dynamic_unary_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=dynamic_unary_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();

		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.doesNotContainKey("serviceParam")
				.containsEntry("serviceMethod", service)
				.containsEntry("serviceField", service);

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.doesNotContainKeys("serviceParam", "serviceField",
						"serviceMethod");

		softly.assertThat(bs.size()).isOne();
	}

	@Test
	public void static_multiple_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=static_multiple_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();
		List<TestObject> list = Collections.singletonList(service);

		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.containsEntry("serviceField", list)
				.containsEntry("serviceParam", list);

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		softly.assertThat(bs.size()).isZero();
	}

	@Test
	public void dynamic_multiple_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=dynamic_multiple_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();
		List<TestObject> list = Collections.singletonList(service);

		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.doesNotContainKey("serviceParam")
				.containsEntry("serviceField", list);

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.doesNotContainKey("serviceParam")
				.containsEntry("serviceField", Collections.emptyList());

		softly.assertThat(bs.size()).isOne();
	}

	@Test
	public void no_target_required_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=no_target_required_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();

		@SuppressWarnings("unused")
		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		softly.assertThat(bs.size()).isZero();
	}

	@Test
	public void no_target_optional_reference(@InjectInstalledBundle("tb30.jar")
	Bundle tb,
			@InjectService(filter = "(type=no_target_optional_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		TestObject service = new TestObject();

		@SuppressWarnings("unused")
		ServiceRegistration<TestObject> registration = context.registerService(
				TestObject.class, service,
				Dictionaries.dictionaryOf("testName", testName));

		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		softly.assertThat(bs.getService().getProperties())
				.doesNotContainKey("serviceParam")
				.containsEntry("serviceField", Collections.emptyList());
	}

	@Test
	public void optional_optional_reference(@InjectInstalledBundle("tb31.jar")
	Bundle tb,
			@InjectService(filter = "(type=optional_optional_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {

		TestObject service = new TestObject();

		@SuppressWarnings("unused")
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
					.extractingByKey("serviceParam",
							InstanceOfAssertFactories
									.optional(TestObject.class))
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srParam",
							InstanceOfAssertFactories
									.optional(ServiceReference.class))
					.containsInstanceOf(ServiceReference.class)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soParam",
							InstanceOfAssertFactories
									.optional(ComponentServiceObjects.class))
					.containsInstanceOf(ComponentServiceObjects.class)
					.map(ComponentServiceObjects::getServiceReference)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsParam",
							InstanceOfAssertFactories.optional(Map.class))
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleParam",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getValue())
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion.extractingByKey("tupleParam",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getKey())
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});

		softly.check(() -> {
			assertion
					.extractingByKey("serviceField",
							InstanceOfAssertFactories
									.optional(TestObject.class))
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srField",
							InstanceOfAssertFactories
									.optional(ServiceReference.class))
					.containsInstanceOf(ServiceReference.class)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soField",
							InstanceOfAssertFactories
									.optional(ComponentServiceObjects.class))
					.containsInstanceOf(ComponentServiceObjects.class)
					.map(ComponentServiceObjects::getServiceReference)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsField",
							InstanceOfAssertFactories.optional(Map.class))
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getValue())
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getKey())
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});

	}

	@Test
	public void optional_absent_reference(@InjectInstalledBundle("tb31.jar")
	Bundle tb,
			@InjectService(filter = "(type=optional_optional_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {
		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		softly.check(() -> {
			assertion
					.extractingByKey("serviceParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleParam",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});

		softly.check(() -> {
			assertion
					.extractingByKey("serviceField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.OPTIONAL)
					.isEmpty();
		});
	}

	@Test
	public void optional_mandatory_reference(@InjectInstalledBundle("tb31.jar")
	Bundle tb,
			@InjectService(filter = "(type=optional_mandatory_reference)", cardinality = 0)
			ServiceAware<BaseService> bs) throws Exception {

		TestObject service = new TestObject();

		@SuppressWarnings("unused")
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
					.extractingByKey("serviceParam",
							InstanceOfAssertFactories
									.optional(TestObject.class))
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srParam",
							InstanceOfAssertFactories
									.optional(ServiceReference.class))
					.containsInstanceOf(ServiceReference.class)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soParam",
							InstanceOfAssertFactories
									.optional(ComponentServiceObjects.class))
					.containsInstanceOf(ComponentServiceObjects.class)
					.map(ComponentServiceObjects::getServiceReference)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsParam",
							InstanceOfAssertFactories.optional(Map.class))
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleParam",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getValue())
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleParam",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getKey())
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});

		softly.check(() -> {
			assertion
					.extractingByKey("serviceField",
							InstanceOfAssertFactories
									.optional(TestObject.class))
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("srField",
							InstanceOfAssertFactories
									.optional(ServiceReference.class))
					.containsInstanceOf(ServiceReference.class)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("soField",
							InstanceOfAssertFactories
									.optional(ComponentServiceObjects.class))
					.containsInstanceOf(ComponentServiceObjects.class)
					.map(ComponentServiceObjects::getServiceReference)
					.map(sr -> sr.getProperty("testName"))
					.contains(testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("propsField",
							InstanceOfAssertFactories.optional(Map.class))
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getValue())
					.containsInstanceOf(TestObject.class)
					.containsSame(service);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("tupleField",
							InstanceOfAssertFactories.optional(Map.Entry.class))
					.containsInstanceOf(Map.Entry.class)
					.map(entry -> entry.getKey())
					.containsInstanceOf(Map.class)
					.get(InstanceOfAssertFactories.map(String.class,
							Object.class))
					.containsEntry("testName", testName);
		});
	}

	@Test
	public void condition_default_reference(@InjectInstalledBundle("tb32.jar")
	Bundle tb,
			@InjectService(filter = "(type=condition_default_reference)", cardinality = 0)
			ServiceAware<BaseService> bs,
			@InjectService(filter = "(osgi.condition.id=true)")
			Condition trueCondition, @InjectService
			ServiceComponentRuntime scr) throws Exception {
		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		softly.check(() -> {
			assertion
					.extractingByKey("serviceComponent",
							InstanceOfAssertFactories.type(Condition.class))
					.isSameAs(trueCondition);
		});

		softly.check(() -> {
			ComponentDescriptionDTO componentDescriptionDTO = scr
					.getComponentDescriptionDTO(tb,
							"org.osgi.test.cases.component.tb32.condition.default");

			assertThat(componentDescriptionDTO).isNotNull();
			Collection<ComponentConfigurationDTO> componentConfigurationDTOs = scr
					.getComponentConfigurationDTOs(componentDescriptionDTO);

			ObjectArrayAssert<SatisfiedReferenceDTO> satisfied = assertThat(
					componentConfigurationDTOs).hasSize(1)
							.element(0)
							.extracting("satisfiedReferences",
									InstanceOfAssertFactories.array(
											SatisfiedReferenceDTO[].class))
							.hasSize(1);
			satisfied.extracting("name", String.class)
					.containsOnly("osgi.ds.satisfying.condition");
		});

	}

	@Test
	public void condition_true_reference(@InjectInstalledBundle("tb32.jar")
	Bundle tb,
			@InjectService(filter = "(type=condition_true_reference)", cardinality = 0)
			ServiceAware<BaseService> bs,
			@InjectService(filter = "(osgi.condition.id=true)")
			Condition trueCondition, @InjectService
			ServiceComponentRuntime scr) throws Exception {
		tb.start();

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		softly.check(() -> {
			assertion
					.extractingByKey("serviceParam",
							InstanceOfAssertFactories.type(Condition.class))
					.isSameAs(trueCondition);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("serviceField",
							InstanceOfAssertFactories.type(Condition.class))
					.isSameAs(trueCondition);
		});
		softly.check(() -> {
			assertion
					.extractingByKey("serviceComponent",
							InstanceOfAssertFactories.type(Condition.class))
					.isSameAs(trueCondition);
		});

		softly.check(() -> {
			ComponentDescriptionDTO componentDescriptionDTO = scr
					.getComponentDescriptionDTO(tb,
							"org.osgi.test.cases.component.tb32.condition.true");

			assertThat(componentDescriptionDTO).isNotNull();
			Collection<ComponentConfigurationDTO> componentConfigurationDTOs = scr
					.getComponentConfigurationDTOs(componentDescriptionDTO);

			ObjectArrayAssert<SatisfiedReferenceDTO> satisfied = assertThat(
					componentConfigurationDTOs).hasSize(1)
							.element(0)
							.extracting("satisfiedReferences",
									InstanceOfAssertFactories.array(
											SatisfiedReferenceDTO[].class))
							.hasSize(1);
			satisfied.extracting("name", String.class)
					.containsOnly("osgi.ds.satisfying.condition");
			satisfied.extracting("target", String.class)
					.containsOnly("(osgi.condition.id=true)");
		});

	}

	@Test
	public void condition_custom_reference(@InjectInstalledBundle("tb32.jar")
	Bundle tb,
			@InjectService(filter = "(type=condition_custom_reference)", cardinality = 0)
			ServiceAware<BaseService> bs, @InjectService
			ServiceComponentRuntime scr) throws Exception {
		tb.start();

		bs.waitForService(SLEEP);

		softly.assertThat(bs.size()).isZero();

		Condition service = new Condition() {
		};

		ServiceRegistration<Condition> registration = context.registerService(
				Condition.class, service,
				Dictionaries.dictionaryOf(Condition.CONDITION_ID, testName));

		bs.waitForService(SLEEP);

		assertThat(bs.getService()).isNotNull();
		DictionaryAssert<String,Object> assertion = DictionaryAssert
				.assertThat(bs.getService().getProperties())
				.isNotNull();

		softly.check(() -> {
			assertion
					.extractingByKey("serviceField",
							InstanceOfAssertFactories.type(Condition.class))
					.isSameAs(service);
		});

		softly.check(() -> {
			ComponentDescriptionDTO componentDescriptionDTO = scr
					.getComponentDescriptionDTO(tb,
							"org.osgi.test.cases.component.tb32.condition.custom");

			assertThat(componentDescriptionDTO).isNotNull();
			Collection<ComponentConfigurationDTO> componentConfigurationDTOs = scr
					.getComponentConfigurationDTOs(componentDescriptionDTO);

			ObjectArrayAssert<SatisfiedReferenceDTO> satisfied = assertThat(
					componentConfigurationDTOs).hasSize(1)
							.element(0)
							.extracting("satisfiedReferences",
									InstanceOfAssertFactories.array(
											SatisfiedReferenceDTO[].class))
							.hasSize(1);
			satisfied.extracting("name", String.class)
					.containsOnly("osgi.ds.satisfying.condition");
			satisfied.extracting("target", String.class)
					.containsOnly("(osgi.condition.id=" + testName + ")");
		});

		registration.unregister();

		Sleep.sleep(SLEEP * 3);

		softly.assertThat(bs.size()).isZero();

		softly.check(() -> {
			ComponentDescriptionDTO componentDescriptionDTO = scr
					.getComponentDescriptionDTO(tb,
							"org.osgi.test.cases.component.tb32.condition.custom");

			assertThat(componentDescriptionDTO).isNotNull();
			Collection<ComponentConfigurationDTO> componentConfigurationDTOs = scr
					.getComponentConfigurationDTOs(componentDescriptionDTO);

			ObjectArrayAssert<UnsatisfiedReferenceDTO> unsatisfied = assertThat(
					componentConfigurationDTOs).hasSize(1)
							.element(0)
							.extracting("unsatisfiedReferences",
									InstanceOfAssertFactories.array(
											UnsatisfiedReferenceDTO[].class))
							.hasSize(1);
			unsatisfied.extracting("name", String.class)
					.containsOnly("osgi.ds.satisfying.condition");
			unsatisfied.extracting("target", String.class)
					.containsOnly("(osgi.condition.id=" + testName + ")");
		});
	}
}

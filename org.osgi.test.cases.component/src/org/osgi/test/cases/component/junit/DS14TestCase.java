/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
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

package org.osgi.test.cases.component.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.cases.component.junit.DTOUtil.*;
import static org.osgi.test.support.dictionary.Dictionaries.*;

import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.Logger;
import org.osgi.test.cases.component.service.ObjectProvider1;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.map.Maps;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

public class DS14TestCase extends AbstractOSGiTestCase {
	private static int														SLEEP			= 1000;
	private static final String												TEST_CASE_ROOT	= "org.osgi.test.cases.component";
	private ServiceTracker<ServiceComponentRuntime,ServiceComponentRuntime>	scrTracker;
	private ServiceTracker<LogReaderService,LogReaderService>				lrTracker;
	private LogListener														ll;

	@Before
	public void setUp() throws Exception {
		scrTracker = new ServiceTracker<ServiceComponentRuntime,ServiceComponentRuntime>(
				getContext(), ServiceComponentRuntime.class, null);
		scrTracker.open();
		lrTracker = new ServiceTracker<LogReaderService,LogReaderService>(
				getContext(), LogReaderService.class, null);
		lrTracker.open();
		LogReaderService lr = Tracker.waitForService(lrTracker, SLEEP);
		if (lr != null) {
			ll = new LogListener() {
				@Override
				public void logged(LogEntry entry) {
					Throwable e = entry.getException();
					if (e != null) {
						System.out.printf("%s [%s] %s%n%s%n",
								entry.getLogLevel(), entry.getLoggerName(),
								entry.getMessage(), e);
					} else {
						System.out.printf("%s [%s] %s%n", entry.getLogLevel(),
								entry.getLoggerName(), entry.getMessage());
					}
				}
			};

			lr.addLogListener(ll);
		}
	}

	@After
	public void tearDown() throws Exception {
		LogReaderService lr = Tracker.waitForService(lrTracker, SLEEP);
		if (lr != null) {
			lr.removeLogListener(ll);
		}
		lrTracker.close();
		scrTracker.close();
	}

	@Test
	public void testComponentFactoryProperty() throws Exception {
		final String NAMED_CLASS = TEST_CASE_ROOT + ".tb4a.NamedService";
		Bundle tb4a = install("tb4a.jar");
		try {
			Filter filter = getContext()
					.createFilter("(&(" + ComponentConstants.COMPONENT_FACTORY
							+ '=' + NAMED_CLASS + ")(" + Constants.OBJECTCLASS
							+ '=' + ComponentFactory.class.getName()
							+ ")(factory.id=foo)(factory.properties=found))");
			ServiceTracker<ComponentFactory<Object>,ComponentFactory<Object>> factoryTracker = new ServiceTracker<>(
					getContext(), filter, null);

			try {
				factoryTracker.open();
				assertThat(factoryTracker.getTrackingCount()).isEqualTo(0);
				tb4a.start();
				ComponentFactory<Object> factory = Tracker
						.waitForService(factoryTracker, SLEEP);
				assertThat(factory).as("ComponentFactory service").isNotNull();
				assertThat(factoryTracker.getTrackingCount()).isEqualTo(1);
				assertThat(asMap(
						factoryTracker.getServiceReference().getProperties()))
								.containsEntry(
										ComponentConstants.COMPONENT_FACTORY,
										NAMED_CLASS)
								.containsEntry("factory.id", "foo")
								.containsEntry("factory.properties", "found");
				ServiceTracker<Object,Object> tracker = new ServiceTracker<>(
						getContext(), NAMED_CLASS, null);
				try {
					tracker.open();
					// create the first service
					Map<String,String> props = Maps.mapOf("name", "hello");

					assertThat(tracker.getTrackingCount()).isEqualTo(0);
					ComponentInstance<Object> instance = factory
							.newInstance(asDictionary(props));
					try {
						Tracker.waitForService(tracker, SLEEP);
						assertThat(tracker.getTrackingCount()).isEqualTo(1);
						assertThat(asMap(
								tracker.getServiceReference().getProperties()))
										.containsEntry("name", "hello")
										.doesNotContainKeys("factory.id",
												"factory.properties");
						assertThat(instance.getInstance())
								.hasToString("hello true")
								.hasFieldOrPropertyWithValue("name", "hello")
								.hasFieldOrPropertyWithValue("bundleContext",
										tb4a.getBundleContext())
								.hasFieldOrProperty("cc")
								.hasFieldOrProperty("props")
								.hasFieldOrProperty("config")
								.hasNoNullFieldsOrProperties();
					} finally {
						instance.dispose();
					}
				} finally {
					tracker.close();
				}
			} finally {
				factoryTracker.close();
			}
		} finally {
			tb4a.uninstall();
		}
	}

	@Test
	public void testServiceComponentRuntimeDTOs14() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertThat(scr).as("failed to find ServiceComponentRuntime service")
				.isNotNull();
		Bundle tb28 = install("tb28.jar");
		try {
			tb28.start();
			Collection<ComponentDescriptionDTO> descriptions = scr
					.getComponentDescriptionDTOs(tb28);
			assertThat(descriptions).hasSize(1);
			ComponentDescriptionDTO description1 = scr
					.getComponentDescriptionDTO(tb28,
							"org.osgi.test.cases.component.tb28.FailedActivation");
			ComponentDescriptionDTO expected1 = newComponentDescriptionDTO(
					"org.osgi.test.cases.component.tb28.FailedActivation",
					newBundleDTO(tb28), null, "singleton",
					"org.osgi.test.cases.component.tb28.FailedActivation", true,
					false, new String[] {
							"org.osgi.test.cases.component.service.ObjectProvider1"
					}, Maps.<String, Object> mapOf(), new ReferenceDTO[0],
					"activate", "deactivate", null, "optional", new String[] {
							"org.osgi.test.cases.component.tb28.FailedActivation"
					}, null, new String[0], 0);
			assertThat(description1)
					.isEqualToComparingFieldByFieldRecursively(expected1);
			Collection<ComponentConfigurationDTO> configurations = scr
					.getComponentConfigurationDTOs(description1);
			assertThat(configurations).hasSize(1);
			ComponentConfigurationDTO configuration1 = configurations.iterator()
					.next();
			assertThat(configuration1.state)
					.isEqualTo(ComponentConfigurationDTO.SATISFIED);
			ServiceReferenceDTO[] tb28SRs = tb28
					.adapt(ServiceReferenceDTO[].class);
			assertThat(tb28SRs).as("tb28 registered services").hasSize(1);
			assertThat(configuration1.service)
					.as("configuration DTO registered service")
					.isEqualToComparingFieldByFieldRecursively(tb28SRs[0]);

		} finally {
			tb28.uninstall();
		}
	}

	@Test
	public void testConstructorInjection() throws Exception {
		final String NAMED_CLASS = TEST_CASE_ROOT + ".tb27.NamedService";

		ServiceComponentRuntime scr = scrTracker.getService();
		assertThat(scr).as("failed to find ServiceComponentRuntime service")
				.isNotNull();
		Bundle tb27 = install("tb27.jar");
		try {
			tb27.start();
			Collection<ComponentDescriptionDTO> descriptions = scr
					.getComponentDescriptionDTOs(tb27);
			assertThat(descriptions).hasSize(1);
			ComponentDescriptionDTO description1 = scr
					.getComponentDescriptionDTO(tb27,
							"org.osgi.test.cases.component.tb27.ConstructorInjection");
			assertThat(description1).isNotNull();
			Collection<ComponentConfigurationDTO> configurations = scr
					.getComponentConfigurationDTOs(description1);
			assertThat(configurations).hasSize(1);
			ComponentConfigurationDTO configuration1 = configurations.iterator()
					.next();
			assertThat(configuration1.state)
					.isEqualTo(ComponentConfigurationDTO.SATISFIED);
			ServiceTracker<Object,Object> tracker = new ServiceTracker<>(
					getContext(), NAMED_CLASS, null);
			try {
				tracker.open();
				Object service = Tracker.waitForService(tracker, SLEEP);
				configurations = scr
						.getComponentConfigurationDTOs(description1);
				assertThat(configurations).hasSize(1);
				configuration1 = configurations.iterator().next();
				assertThat(configuration1.state)
						.isEqualTo(ComponentConfigurationDTO.ACTIVE);
				assertThat(service).hasToString("default.prop true")
						.hasFieldOrPropertyWithValue("name", "default.prop")
						.hasFieldOrPropertyWithValue("bundleContext",
								tb27.getBundleContext())
						.hasFieldOrProperty("componentContext")
						.hasFieldOrProperty("name")
						.hasNoNullFieldsOrProperties();
			} finally {
				tracker.close();
			}
		} finally {
			tb27.uninstall();
		}
	}

	@Test
	public void testFailedActivation() throws Exception {
		Filter providerFilter = getContext().createFilter("(&("
				+ Constants.OBJECTCLASS + "=" + ObjectProvider1.class.getName()
				+ ")(" + ComponentConstants.COMPONENT_NAME
				+ "=org.osgi.test.cases.component.tb28.FailedActivation))");

		ServiceComponentRuntime scr = scrTracker.getService();
		assertThat(scr).as("failed to find ServiceComponentRuntime service")
				.isNotNull();
		Bundle tb28 = install("tb28.jar");
		try {
			tb28.start();
			Collection<ComponentDescriptionDTO> descriptions = scr
					.getComponentDescriptionDTOs(tb28);
			assertThat(descriptions).hasSize(1);
			ComponentDescriptionDTO description1 = scr
					.getComponentDescriptionDTO(tb28,
							"org.osgi.test.cases.component.tb28.FailedActivation");
			assertThat(description1).isNotNull();
			Collection<ComponentConfigurationDTO> configurations = scr
					.getComponentConfigurationDTOs(description1);
			assertThat(configurations).hasSize(1);
			ComponentConfigurationDTO configuration1 = configurations.iterator()
					.next();
			assertThat(configuration1.state)
					.isEqualTo(ComponentConfigurationDTO.SATISFIED);
			ServiceTracker<ObjectProvider1<Object>,ObjectProvider1<Object>> tracker = new ServiceTracker<>(
					getContext(), providerFilter, null);
			try {
				tracker.open();
				Object service = Tracker.waitForService(tracker, SLEEP);
				assertThat(service).isNull();
				configurations = scr
						.getComponentConfigurationDTOs(description1);
				assertThat(configurations).hasSize(1);
				configuration1 = configurations.iterator().next();
				assertThat(configuration1.state)
						.as("configuration is a failed activation")
						.isEqualTo(ComponentConfigurationDTO.FAILED_ACTIVATION);
				assertThat(configuration1.failure)
						.as("configuration failure string")
						.isNotNull();
			} finally {
				tracker.close();
			}
		} finally {
			tb28.uninstall();
		}
	}

}

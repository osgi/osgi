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
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.map.Maps;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

public class DS14TestCase extends AbstractOSGiTestCase {
	private static int			SLEEP			= 1000;
	private static final String	TEST_CASE_ROOT	= "org.osgi.test.cases.component";
	private ServiceTracker<ServiceComponentRuntime,ServiceComponentRuntime>	scrTracker;

	@Before
	public void setUp() {
		scrTracker = new ServiceTracker<ServiceComponentRuntime,ServiceComponentRuntime>(
				getContext(), ServiceComponentRuntime.class, null);
		scrTracker.open();
	}

	@After
	public void tearDown() {
		scrTracker.close();
	}

	@Test
	public void testComponentFactoryProperty() throws Exception {
		final String NAMED_CLASS = TEST_CASE_ROOT + ".tb4a.NamedService";
		Bundle tb4a = install("tb4a.jar");
		try {
			Filter filter = getContext().createFilter("(&("
					+ ComponentConstants.COMPONENT_FACTORY + '=' + NAMED_CLASS
					+ ")(" + Constants.OBJECTCLASS + '='
					+ ComponentFactory.class.getName()
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
	public void testServiceComponentRuntimeDescription14() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertThat(scr).as("failed to find ServiceComponentRuntime service")
				.isNotNull();
		Bundle tb4a = install("tb4a.jar");
		try {
			tb4a.start();
			Collection<ComponentDescriptionDTO> descriptions = scr
					.getComponentDescriptionDTOs(tb4a);
			assertThat(descriptions).hasSize(1);
			ComponentDescriptionDTO description1 = scr
					.getComponentDescriptionDTO(tb4a,
							"org.osgi.test.cases.component.tb4a.NamedService");
			ComponentDescriptionDTO expected1 = newComponentDescriptionDTO(
					"org.osgi.test.cases.component.tb4a.NamedService",
					newBundleDTO(tb4a),
					"org.osgi.test.cases.component.tb4a.NamedService",
					"singleton",
					"org.osgi.test.cases.component.tb4a.impl.NamedServiceFactory",
					true, false, new String[] {
							"org.osgi.test.cases.component.tb4a.NamedService"
					}, Maps.<String, Object> mapOf(), new ReferenceDTO[] {
							newReferenceDTO("loggers",
									"org.osgi.service.log.LogService", "0..n",
									"static", "reluctant", null, null, null,
									null, null, null,
									"bundle", 4, "service")
					},
					"activate", "deactivate", null, "optional", new String[] {
							"org.osgi.test.cases.component.tb4a.NamedService"
					}, Maps.<String, Object> map​Of("factory.id", "foo",
							"factory.properties", "found"),
					new String[] {
							"context", "cc", "props", "config"
					}, 5);
			assertThat(description1)
					.isEqualToComparingFieldByFieldRecursively(expected1);
		} finally {
			tb4a.uninstall();
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
			assertThat(configurations.iterator().next())
					.hasFieldOrPropertyWithValue("state",
							ComponentConfigurationDTO.SATISFIED);
			ServiceTracker<Object,Object> tracker = new ServiceTracker<>(
					getContext(), NAMED_CLASS, null);
			try {
				tracker.open();
				Object service = Tracker.waitForService(tracker, SLEEP);
				configurations = scr
						.getComponentConfigurationDTOs(description1);
				assertThat(configurations).hasSize(1);
				assertThat(configurations.iterator().next())
						.hasFieldOrPropertyWithValue("state",
								ComponentConfigurationDTO.ACTIVE);
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

}

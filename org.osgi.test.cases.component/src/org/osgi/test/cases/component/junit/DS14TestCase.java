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
import static org.osgi.test.support.dictionary.Dictionaries.asMap;

import java.util.Map;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.test.support.dictionary.Dictionaries;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.map.Maps;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

public class DS14TestCase extends AbstractOSGiTestCase {
	private static int			SLEEP			= 1000;
	private static final String	TEST_CASE_ROOT	= "org.osgi.test.cases.component";

	@Test
	public void testComponentFactoryProperty() throws Exception {
		final String NAMED_CLASS = TEST_CASE_ROOT + ".tb4a.NamedService";
		Bundle bundle = install("tb4a.jar");
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
				bundle.start();
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
							.newInstance(Dictionaries.asDictionary(props));
					try {
						assertThat(tracker.getTrackingCount()).isEqualTo(1);
						assertThat(asMap(
								tracker.getServiceReference().getProperties()))
										.containsEntry("name", "hello")
										.doesNotContainKeys("factory.id",
												"factory.properties");
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
			bundle.uninstall();
		}
	}

}

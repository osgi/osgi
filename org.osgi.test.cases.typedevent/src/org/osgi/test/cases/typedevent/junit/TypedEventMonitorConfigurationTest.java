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

package org.osgi.test.cases.typedevent.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.monitor.MonitorEvent;
import org.osgi.service.typedevent.monitor.RangePolicy;
import org.osgi.service.typedevent.monitor.TypedEventMonitor;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.6.2 Configuring Event History")
public class TypedEventMonitorConfigurationTest {

	@InjectBundleContext
	BundleContext					context;

	protected static final String	TOPIC_A	= EventA.class.getName()
			.replace(".", "/");

	@BeforeEach
	public void reinit(@InjectService
	ServiceAware<TypedEventMonitor> monitor) throws BundleException {

		Bundle bundle = monitor.getServiceReference().getBundle();
		bundle.stop();
		bundle.start();
	}

	@DisplayName("History Storage Configuration")
	@Nested
	class HistoryStorageConfigurationTest {

		@Test
		public void configureHistoryStorageForExactTopic(@InjectService
		TypedEventMonitor monitor) {
			String topic = "test/topic";
			RangePolicy policy = RangePolicy.exact(100);

			int allocated = monitor.configureHistoryStorage(topic, policy);

			assertThat(allocated).isGreaterThanOrEqualTo(policy.getMinimum());
			assertThat(allocated).isLessThanOrEqualTo(policy.getMaximum());
		}

		@Test
		public void getConfiguredHistoryStorageReturnsAllConfigurations(
				@InjectService
				TypedEventMonitor monitor) {
			String topic1 = "test/topic1";
			String topic2 = "test/topic2";

			monitor.configureHistoryStorage(topic1, RangePolicy.exact(50));
			monitor.configureHistoryStorage(topic2, RangePolicy.exact(100));

			Map<String,RangePolicy> config = monitor
					.getConfiguredHistoryStorage();

			assertThat(config).containsKey(topic1);
			assertThat(config).containsKey(topic2);
		}

		@Test
		public void getConfiguredHistoryStorageForSpecificTopicFilter(
				@InjectService
				TypedEventMonitor monitor) {
			String topic = "test/specific";
			RangePolicy policy = RangePolicy.range(10, 50);

			monitor.configureHistoryStorage(topic, policy);

			RangePolicy retrieved = monitor.getConfiguredHistoryStorage(topic);

			assertThat(retrieved).isNotNull();
			assertThat(retrieved.getMinimum()).isEqualTo(10);
			assertThat(retrieved.getMaximum()).isEqualTo(50);
		}

		@Test
		public void getConfiguredHistoryStorageReturnsNullForUnknownFilter(
				@InjectService
				TypedEventMonitor monitor) {
			RangePolicy policy = monitor
					.getConfiguredHistoryStorage("unknown/topic");

			assertThat(policy).isNull();
		}

		@Test
		public void getEffectiveHistoryStorageReturnsMatchingPolicy(
				@InjectService
				TypedEventMonitor monitor) {
			String topicFilter = "events/*";
			monitor.configureHistoryStorage(topicFilter,
					RangePolicy.atMost(100));

			RangePolicy effective = monitor
					.getEffectiveHistoryStorage("events/test");

			assertThat(effective).isNotNull();
			assertThat(effective.getMaximum()).isEqualTo(100);
		}

		@Test
		public void getEffectiveHistoryStorageReturnsNoneForUnconfiguredTopic(
				@InjectService
				TypedEventMonitor monitor) {
			monitor.getConfiguredHistoryStorage()
					.keySet()
					.stream()
					.forEach(monitor::removeHistoryStorage);
			RangePolicy effective = monitor
					.getEffectiveHistoryStorage("unconfigured/topic");

			assertThat(effective).isNotNull();
			assertThat(effective.getMinimum()).isEqualTo(0);
			assertThat(effective.getMaximum()).isEqualTo(0);
		}

		@Test
		public void removeHistoryStorageDeletesConfiguration(@InjectService
		TypedEventMonitor monitor) {
			String topic = "test/removable";

			monitor.configureHistoryStorage(topic, RangePolicy.exact(50));
			assertThat(monitor.getConfiguredHistoryStorage(topic)).isNotNull();

			monitor.removeHistoryStorage(topic);
			assertThat(monitor.getConfiguredHistoryStorage(topic)).isNull();
		}

		@Test
		public void configureHistoryStorageOverwritesPreviousConfiguration(
				@InjectService
				TypedEventMonitor monitor) {
			String topic = "test/overwrite";

			monitor.configureHistoryStorage(topic, RangePolicy.exact(50));
			monitor.configureHistoryStorage(topic, RangePolicy.exact(100));

			RangePolicy policy = monitor.getConfiguredHistoryStorage(topic);
			assertThat(policy.getMinimum()).isEqualTo(100);
			assertThat(policy.getMaximum()).isEqualTo(100);
		}
	}

	@DisplayName("Maximum Event Storage")
	@Nested
	class MaximumEventStorageTest {

		@Test
		public void getMaximumEventStorageReturnsValidValue(@InjectService
		TypedEventMonitor monitor) {
			int maxStorage = monitor.getMaximumEventStorage();

			assertThat(maxStorage).isGreaterThanOrEqualTo(-1);
		}
	}

	@DisplayName("Wildcard Configuration Rules")
	@Nested
	class WildcardConfigurationRulesTest {

		@Test
		public void wildcardConfigurationWithZeroMinimumIsAllowed(@InjectService
		TypedEventMonitor monitor) {
			String wildcardFilter = "events/*";
			RangePolicy policy = RangePolicy.atMost(100);

			int allocated = monitor.configureHistoryStorage(wildcardFilter,
					policy);

			assertThat(allocated).isGreaterThanOrEqualTo(0);
		}

		@Test
		public void exactTopicConfigurationWithMinimumIsAllowed(@InjectService
		TypedEventMonitor monitor) {
			String exactTopic = "events/specific";
			RangePolicy policy = RangePolicy.atLeast(50);

			int allocated = monitor.configureHistoryStorage(exactTopic, policy);

			assertThat(allocated).isGreaterThanOrEqualTo(50);
		}
	}

	@DisplayName("Configuration Precedence")
	@Nested
	class ConfigurationPrecedenceTest {

		@Test
		public void exactMatchTakesPrecedenceOverWildcard(@InjectService
		TypedEventMonitor monitor) {
			String exactTopic = "events/specific";
			String wildcardFilter = "events/*";

			monitor.configureHistoryStorage(wildcardFilter,
					RangePolicy.atMost(50));
			monitor.configureHistoryStorage(exactTopic, RangePolicy.exact(100));

			RangePolicy effective = monitor
					.getEffectiveHistoryStorage(exactTopic);

			assertThat(effective.getMinimum()).isEqualTo(100);
			assertThat(effective.getMaximum()).isEqualTo(100);
		}

		@Test
		public void singleLevelWildcardTakesPrecedenceOverMultiLevel(
				@InjectService
				TypedEventMonitor monitor) {
			String singleLevel = "events/+/data";
			String multiLevel = "events/*";

			monitor.configureHistoryStorage(multiLevel, RangePolicy.atMost(50));
			monitor.configureHistoryStorage(singleLevel,
					RangePolicy.atMost(100));

			RangePolicy effective = monitor
					.getEffectiveHistoryStorage("events/test/data");

			assertThat(effective.getMaximum()).isEqualTo(100);
		}
	}

	@DisplayName("Error Handling")
	@Nested
	class ErrorHandlingTest {

		@Test
		public void nullTopicFilterThrowsNullPointerException(@InjectService
		TypedEventMonitor monitor) {
			assertThatThrownBy(() -> monitor.configureHistoryStorage(null,
					RangePolicy.exact(10)))
							.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void invalidTopicFilterSyntaxThrowsIllegalArgumentException(
				@InjectService
				TypedEventMonitor monitor) {
			assertThatThrownBy(() -> monitor.configureHistoryStorage(
					"invalid//topic", RangePolicy.exact(10)))
							.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void getConfiguredHistoryStorageWithNullThrowsNullPointerException(
				@InjectService
				TypedEventMonitor monitor) {
			assertThatThrownBy(() -> monitor.getConfiguredHistoryStorage(null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void getEffectiveHistoryStorageWithNullThrowsNullPointerException(
				@InjectService
				TypedEventMonitor monitor) {
			assertThatThrownBy(() -> monitor.getEffectiveHistoryStorage(null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void getEffectiveHistoryStorageWithWildcardThrowsIllegalArgumentException(
				@InjectService
				TypedEventMonitor monitor) {
			assertThatThrownBy(
					() -> monitor.getEffectiveHistoryStorage("topic/*"))
							.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void removeHistoryStorageWithNullThrowsNullPointerException(
				@InjectService
				TypedEventMonitor monitor) {
			assertThatThrownBy(() -> monitor.removeHistoryStorage(null))
					.isInstanceOf(NullPointerException.class);
		}
	}

	@DisplayName("History Only Streams")
	@Nested
	class HistoryOnlyStreamsTest {

		@Test
		public void monitorEventsWithHistoryOnlyByCount(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus)
				throws InterruptedException, InvocationTargetException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA event1 = new EventA();
			event1.a = "1";
			eventBus.deliver(event1);

			EventA event2 = new EventA();
			event2.a = "2";
			eventBus.deliver(event2);

			Thread.sleep(100);

			Promise<List<MonitorEvent>> eventsPromise = monitor
					.monitorEvents(5, true)
					.collect(Collectors.toList())
					.timeout(2000);

			List<MonitorEvent> events = eventsPromise.getValue();

			assertThat(events).hasSize(2);
			assertThat(events).allMatch(e -> TOPIC_A.equals(e.topic));
		}

		@Test
		public void monitorEventsWithHistoryOnlyByInstant(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus)
				throws InterruptedException, InvocationTargetException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			Instant before = Instant.now().minus(Duration.ofSeconds(1));

			EventA event = new EventA();
			event.a = "test";
			eventBus.deliver(event);

			Thread.sleep(100);

			Promise<List<MonitorEvent>> eventsPromise = monitor
					.monitorEvents(before, true)
					.collect(Collectors.toList())
					.timeout(2000);

			List<MonitorEvent> events = eventsPromise.getValue();

			assertThat(events).hasSize(1);
			assertThat(events.get(0).eventData.get("a")).isEqualTo("test");
		}

		@Test
		public void historyOnlyStreamClosesAfterDelivery(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus)
				throws InterruptedException, InvocationTargetException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA event = new EventA();
			event.a = "historical";
			eventBus.deliver(event);

			Thread.sleep(100);

			Promise<List<MonitorEvent>> eventsPromise = monitor
					.monitorEvents(10, true)
					.collect(Collectors.toList())
					.timeout(2000);

			List<MonitorEvent> events = eventsPromise.getValue();

			assertThat(events).hasSize(1);

			EventA newEvent = new EventA();
			newEvent.a = "new";
			eventBus.deliver(newEvent);

			Thread.sleep(100);

			assertThat(events).hasSize(1);
		}
	}
}

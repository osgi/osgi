/* Copyright (c) Contributors to the Eclipse Foundation
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.monitor.MonitorEvent;
import org.osgi.service.typedevent.monitor.TypedEventMonitor;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerA;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.6 Monitoring Events")
public class TypedEventMonitorIntegrationTest {

	protected static final String	TOPIC_A	= EventA.class.getName()
			.replace(".", "/");

	@InjectService
	TypedEventMonitor				monitor;
	@InjectService
	TypedEventBus					eventBus;

	@InjectBundleContext
	BundleContext					bc;

	@BeforeEach
	public void reinit() throws BundleException {
		Bundle bundle = FrameworkUtil.getBundle(TypedEventBus.class);
		bundle.stop();
		bundle.start();
	}

	@Test
	public void testTypedEventMonitor_With_TypedEventHandler()
			throws InterruptedException, InvocationTargetException {

		Promise<List<MonitorEvent>> eventsPromise = monitor.monitorEvents()
				.limit(2)
				.collect(Collectors.toList());

		TypedEventHandler<EventA> eventHandler1 = mock(
				TypedEventHandlerA.class);

		bc.registerService(TypedEventHandler.class, eventHandler1,
				Dictionaries.dictionaryOf());

		EventA event = new EventA();
		event.a = "1";

		eventBus.deliver(event);

		event = new EventA();
		event.a = "2";

		eventBus.deliver(event);

		Mockito.verify(eventHandler1, Mockito.timeout(2000))
				.notify(Mockito.eq(TOPIC_A),
						Mockito.argThat(e -> "1".equals(e.a)));
		Mockito.verify(eventHandler1, Mockito.timeout(2000))
				.notify(Mockito.eq(TOPIC_A),
						Mockito.argThat(e -> "2".equals(e.a)));

		List<MonitorEvent> events = eventsPromise.timeout(100).getValue();

		assertThat(events).hasSize(2).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("1");
		assertThat(events.get(1).eventData.get("a")).isEqualTo("2");

	}

	@Test
	public void testTypedEventMonitor_without_TypedEventHandler()
			throws InterruptedException, InvocationTargetException {

		Promise<List<MonitorEvent>> eventsPromise = monitor.monitorEvents()
				.limit(2)
				.collect(Collectors.toList());

		EventA event = new EventA();
		event.a = "1";

		eventBus.deliver(event);

		event = new EventA();
		event.a = "2";

		eventBus.deliver(event);

		List<MonitorEvent> events = eventsPromise.timeout(100).getValue();

		assertThat(events).hasSize(2).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("1");
		assertThat(events.get(1).eventData.get("a")).isEqualTo("2");
	}


	@Test
	public void testTypedEventMonitor_History_by_count()
			throws InterruptedException, InvocationTargetException {

		EventA event = new EventA();
		event.a = "1";

		eventBus.deliver(event);

		event = new EventA();
		event.a = "2";

		eventBus.deliver(event);

		Thread.sleep(500);

		Promise<List<MonitorEvent>> eventsPromise = monitor.monitorEvents()
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		List<MonitorEvent> events = eventsPromise.getValue();

		assertTrue(events.isEmpty());

		eventsPromise = monitor.monitorEvents(2)
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		events = eventsPromise.getValue();
		events.forEach(e -> System.out.println(e));
		assertThat(events).hasSize(2).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("1");
		assertThat(events.get(1).eventData.get("a")).isEqualTo("2");

		eventsPromise = monitor.monitorEvents(1)
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		events = eventsPromise.getValue();

		assertThat(events).hasSize(1).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("2");

	}


	@Test
	public void testTypedEventMonitor_History_by_Instant()
			throws InterruptedException, InvocationTargetException {

		Thread.sleep(500);
		Instant beforeFirst = Instant.now().minus(Duration.ofMillis(500));

		EventA event = new EventA();
		event.a = "1";

		eventBus.deliver(event);

		Instant afterFirst = Instant.now().plus(Duration.ofMillis(500));

		Thread.sleep(1000);

		event = new EventA();
		event.a = "2";

		eventBus.deliver(event);

		Instant afterSecond = Instant.now().plus(Duration.ofMillis(500));

		Thread.sleep(500);

		Promise<List<MonitorEvent>> eventsPromise = monitor.monitorEvents()
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		List<MonitorEvent> events = eventsPromise.getValue();

		assertTrue(events.isEmpty());

		eventsPromise = monitor.monitorEvents(beforeFirst)
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		events = eventsPromise.getValue();

		assertThat(events).hasSize(2).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("1");
		assertThat(events.get(1).eventData.get("a")).isEqualTo("2");

		eventsPromise = monitor.monitorEvents(afterFirst)
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		events = eventsPromise.getValue();

		assertThat(events).hasSize(1).allMatch(e -> TOPIC_A.equals(e.topic));

		assertThat(events.get(0).eventData.get("a")).isEqualTo("2");

		eventsPromise = monitor.monitorEvents(afterSecond)
				.limit(Duration.ofSeconds(1))
				.collect(Collectors.toList())
				.timeout(2000);

		assertThat(eventsPromise.getValue()).isEmpty();
	}

}

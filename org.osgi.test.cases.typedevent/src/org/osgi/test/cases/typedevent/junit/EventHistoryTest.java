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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.osgi.test.common.dictionary.Dictionaries.dictionaryOf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.service.typedevent.monitor.RangePolicy;
import org.osgi.service.typedevent.monitor.TypedEventMonitor;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerA;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.4.7 Event History")
public class EventHistoryTest {

	@InjectBundleContext
	BundleContext					context;

	protected static final String	TOPIC_A	= EventA.class.getName()
			.replace(".", "/");

	@BeforeEach
	public void reinit(@InjectService
	ServiceAware<TypedEventBus> eventBus, @InjectService
	ServiceAware<TypedEventMonitor> monitor) throws BundleException {
		Set<Bundle> bundlesToRestart = Stream
				.of(eventBus.getServiceReference().getBundle(),
						monitor.getServiceReference().getBundle())
				.collect(Collectors.toSet());
		for (Bundle b : bundlesToRestart) {
			b.stop();
		}
		for (Bundle b : bundlesToRestart) {
			b.start();
		}
	}

	@DisplayName("TYPED_EVENT_HISTORY Property")
	@Nested
	class TypedEventHistoryPropertyTest {

		@Test
		public void handlerReceivesHistoricalEventsBeforeNewEvents(
				@InjectService
				TypedEventMonitor monitor, @InjectService
				TypedEventBus eventBus) throws InterruptedException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA historicalEvent = new EventA();
			historicalEvent.a = "historical";
			eventBus.deliver(historicalEvent);

			Thread.sleep(100);

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_HISTORY, 1));

			EventA newEvent = new EventA();
			newEvent.a = "new";
			eventBus.deliver(newEvent);

			InOrder inOrder = inOrder(handler);
			inOrder.verify(handler, timeout(1000))
					.notify(eq(TOPIC_A),
							argThat(evA -> "historical".equals(evA.a)));
			inOrder.verify(handler, timeout(1000))
					.notify(eq(TOPIC_A), argThat(evA -> "new".equals(evA.a)));
		}

		@Test
		public void handlerReceivesRequestedNumberOfHistoricalEvents(
				@InjectService
				TypedEventMonitor monitor, @InjectService
				TypedEventBus eventBus) throws InterruptedException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.range(10, 100));

			for (int i = 1; i <= 5; i++) {
				EventA event = new EventA();
				event.a = String.valueOf(i);
				eventBus.deliver(event);
			}

			Thread.sleep(100);

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_HISTORY, 3));

			verify(handler, timeout(1000).times(3)).notify(eq(TOPIC_A),
					any(EventA.class));
		}

		@Test
		public void handlerReceivesAllAvailableHistoryWhenRequestedMoreThanAvailable(
				@InjectService
				TypedEventMonitor monitor, @InjectService
				TypedEventBus eventBus) throws InterruptedException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA event1 = new EventA();
			event1.a = "1";
			eventBus.deliver(event1);

			EventA event2 = new EventA();
			event2.a = "2";
			eventBus.deliver(event2);

			Thread.sleep(100);

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_HISTORY, 10));

			verify(handler, timeout(1000).times(2)).notify(eq(TOPIC_A),
					any(EventA.class));
		}

		@Test
		public void handlerReceivesNoHistoryWhenHistoryIsZero(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA historicalEvent = new EventA();
			historicalEvent.a = "historical";
			eventBus.deliver(historicalEvent);

			Thread.sleep(100);

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_HISTORY, 0));

			EventA newEvent = new EventA();
			newEvent.a = "new";
			eventBus.deliver(newEvent);

			verify(handler, timeout(1000).times(1)).notify(eq(TOPIC_A),
					any(EventA.class));
		}

		@Test
		public void handlerReceivesNoHistoryWhenNoEventsAvailable(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_HISTORY, 5));

			EventA newEvent = new EventA();
			newEvent.a = "new";
			eventBus.deliver(newEvent);

			verify(handler, timeout(1000).times(1)).notify(eq(TOPIC_A),
					any(EventA.class));
		}
	}

	@DisplayName("History with Topics and Filters")
	@Nested
	class HistoryWithTopicsAndFiltersTest {

		@Test
		public void historyRespectsTopicSubscription(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			String topicA = "topic/a";
			String topicB = "topic/b";

			monitor.configureHistoryStorage(topicA, RangePolicy.atLeast(10));
			monitor.configureHistoryStorage(topicB, RangePolicy.atLeast(10));

			Map<String,Object> eventA = new HashMap<>();
			eventA.put("value", "a");
			eventBus.deliverUntyped(topicA, eventA);

			Map<String,Object> eventB = new HashMap<>();
			eventB.put("value", "b");
			eventBus.deliverUntyped(topicB, eventB);

			Thread.sleep(100);

			UntypedEventHandler handler = mock(UntypedEventHandler.class);
			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, topicA,
							TypedEventConstants.TYPED_EVENT_HISTORY, 10));

			verify(handler, timeout(1000).times(1)).notifyUntyped(eq(topicA),
					any());
		}

		@Test
		public void historyRespectsEventFilter(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			monitor.configureHistoryStorage(TOPIC_A, RangePolicy.atLeast(10));

			EventA matchingEvent = new EventA();
			matchingEvent.a = "match";
			eventBus.deliver(matchingEvent);

			EventA nonMatchingEvent = new EventA();
			nonMatchingEvent.a = "nomatch";
			eventBus.deliver(nonMatchingEvent);

			Thread.sleep(100);

			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);
			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName(),
							TypedEventConstants.TYPED_EVENT_FILTER, "(a=match)",
							TypedEventConstants.TYPED_EVENT_HISTORY, 10));

			verify(handler, timeout(1000).times(1)).notify(eq(TOPIC_A),
					any(EventA.class));
		}

		@Test
		public void historyWithWildcardTopic(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			String topic1 = "events/type1";
			String topic2 = "events/type2";

			monitor.configureHistoryStorage("events/*", RangePolicy.atMost(10));

			Map<String,Object> event1 = new HashMap<>();
			event1.put("type", "1");
			eventBus.deliverUntyped(topic1, event1);

			Map<String,Object> event2 = new HashMap<>();
			event2.put("type", "2");
			eventBus.deliverUntyped(topic2, event2);

			Thread.sleep(100);

			UntypedEventHandler handler = mock(UntypedEventHandler.class);
			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"events/*", TypedEventConstants.TYPED_EVENT_HISTORY,
							10));

			verify(handler, timeout(1000).times(2)).notifyUntyped(any(), any());
		}

		@Test
		public void historyWithSingleLevelWildcard(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			String topic = "events/foo/bar";

			monitor.configureHistoryStorage(topic, RangePolicy.atLeast(10));

			Map<String,Object> event = new HashMap<>();
			event.put("key", "value");
			eventBus.deliverUntyped(topic, event);

			Thread.sleep(100);

			UntypedEventHandler handler = mock(UntypedEventHandler.class);
			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"events/+/bar",
							TypedEventConstants.TYPED_EVENT_HISTORY, 10));

			verify(handler, timeout(1000).times(1)).notifyUntyped(eq(topic),
					any());
		}
	}

	@DisplayName("Untyped Event Handler History")
	@Nested
	class UntypedEventHandlerHistoryTest {

		@Test
		public void untypedHandlerReceivesHistory(@InjectService
		TypedEventMonitor monitor, @InjectService
		TypedEventBus eventBus) throws InterruptedException {
			String topic = "untyped/topic";
			monitor.configureHistoryStorage(topic, RangePolicy.atLeast(10));

			Map<String,Object> historicalEvent = new HashMap<>();
			historicalEvent.put("key", "historical");
			eventBus.deliverUntyped(topic, historicalEvent);

			Thread.sleep(100);

			UntypedEventHandler handler = mock(UntypedEventHandler.class);
			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, topic,
							TypedEventConstants.TYPED_EVENT_HISTORY, 1));

			Map<String,Object> newEvent = new HashMap<>();
			newEvent.put("key", "new");
			eventBus.deliverUntyped(topic, newEvent);

			verify(handler, timeout(1000).times(2)).notifyUntyped(eq(topic),
					any());
		}
	}
}

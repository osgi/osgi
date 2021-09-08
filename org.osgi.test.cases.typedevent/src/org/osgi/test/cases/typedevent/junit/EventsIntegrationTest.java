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

import static org.mockito.Mockito.mock;
import static org.osgi.test.common.dictionary.Dictionaries.dictionaryOf;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UnhandledEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.cases.typedevent.common.EventAA;
import org.osgi.test.cases.typedevent.common.EventB;
import org.osgi.test.cases.typedevent.common.EventComplex;
import org.osgi.test.cases.typedevent.common.EventNonTree;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerA;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerB;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerC;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerObject;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class EventsIntegrationTest {

	@InjectBundleContext
	BundleContext					context;

	@InjectService
	TypedEventBus					eventBus;

	protected static final String	TOPIC_A			= EventA.class.getName()
			.replace(".", "/");

	protected static final String	TOPIC_AA		= EventAA.class.getName()
			.replace(".", "/");

	protected static final String	TOPIC_B			= EventB.class.getName()
			.replace(".", "/");
	protected static final String	TOPIC_COMPLEX	= EventComplex.class
			.getName()
			.replace(".", "/");

	@DisplayName("157.2.1.1 Nested Data Structures ")
	@Nested
	class NestedDataStructuresTest {
		@SuppressWarnings("unchecked")
		@Test
		public void test_deliver_nested_events() throws InterruptedException {

			EventA eventA = new EventA();
			eventA.a = "a";

			EventComplex eventC = new EventComplex();
			eventC.a = eventA;

			TypedEventHandler<Object> typedHandler = mock(
					TypedEventHandlerObject.class);

			UntypedEventHandler untypedHandler = mock(
					UntypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventComplex.class.getName()));

			context.registerService(UntypedEventHandler.class, untypedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							TOPIC_COMPLEX));

			eventBus.deliver(eventC);

			Mockito.verify(typedHandler, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_COMPLEX),
							Mockito.argThat(evA -> evA instanceof EventComplex
									&& "a".equals(((EventComplex) evA).a.a)));

			Mockito.verify(untypedHandler, Mockito.timeout(1000))
					.notifyUntyped(Mockito.eq(TOPIC_COMPLEX),
							Mockito.argThat(map -> "a"
									.equals(((Map<String,Object>) map.get("a"))
											.get("a"))));

		}

		// @Test
		public void test_deliver_nested_events_tree()
				throws InterruptedException {

			EventNonTree e1 = new EventNonTree();
			EventNonTree e2 = new EventNonTree();
			e1.eventNonTree = e2;
			e2.eventNonTree = e1;

			TypedEventHandler<Object> typedHandler = mock(
					TypedEventHandlerObject.class);

			UntypedEventHandler untypedHandler = mock(
					UntypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventNonTree.class.getName()));

			context.registerService(UntypedEventHandler.class, untypedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									"*"
							}));
			Assertions.assertThatThrownBy(() -> {
				eventBus.deliver(e1);
			});

			Mockito.verify(typedHandler, Mockito.after(1000).never())
					.notify(Mockito.any(), Mockito.any());

			Mockito.verify(untypedHandler, Mockito.after(1000).never())
					.notifyUntyped(Mockito.any(), Mockito.any());

		}

		@SuppressWarnings({
				"cast", "unchecked"
		})
		@DisplayName("157.2.2 Untyped Events")
		@Test
		public void test_deliver_untyped_events() throws InterruptedException {
			Map<String,Object> mapInner = new HashMap<>();
			mapInner.put("a", "b");
			Map<String,Object> eventMap = new HashMap<>();
			eventMap.put("a", mapInner);

			TypedEventHandler<EventComplex> typedHandler = mock(
					TypedEventHandlerC.class);

			UntypedEventHandler untypedHandler = mock(
					UntypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									TOPIC_COMPLEX
							}));

			context.registerService(UntypedEventHandler.class, untypedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									TOPIC_COMPLEX
							}));

			eventBus.deliverUntyped(TOPIC_COMPLEX, eventMap);

			Mockito.verify(untypedHandler, Mockito.timeout(1000))
					.notifyUntyped(Mockito.eq(TOPIC_COMPLEX),
							Mockito.argThat(map -> "b"
									.equals(((Map<String,Object>) map.get("a"))
											.get("a"))));

			Mockito.verify(typedHandler, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_COMPLEX),
							Mockito.argThat(evA -> evA instanceof EventComplex
									&& "b".equals(evA.a.a)));
		}

		@SuppressWarnings("unchecked")
		@Test
		@DisplayName("157.3.2 Automatically Generated Topics")
		public void test_deliver_automatic_gen_topics()
				throws InterruptedException {

			EventA eventA = new EventA();
			eventA.a = "a";

			TypedEventHandler<Object> typedHandler = mock(
					TypedEventHandlerObject.class);

			UntypedEventHandler untypedHandler = mock(
					UntypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName()));

			context.registerService(UntypedEventHandler.class, untypedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							TOPIC_A));

			eventBus.deliver(eventA);

			Mockito.verify(typedHandler, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_A), Mockito.any(EventA.class));

			Mockito.verify(untypedHandler, Mockito.timeout(1000))
					.notifyUntyped(Mockito.eq(TOPIC_A), Mockito.any(Map.class));

		}
	}

	@DisplayName("157.4.5 Filtering Events")
	@Nested
	class FilterEventsTest {
		@Test
		public void test_filterr() throws InterruptedException {

			EventA a1 = new EventA();
			a1.a = "1";

			EventA a2 = new EventA();
			a2.a = "2";

			TypedEventHandler<EventA> typedHandlerA = mock(
					TypedEventHandlerA.class);

			UnhandledEventHandler unhandledEventHandler = mock(
					UnhandledEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_FILTER,
							"(a=1)"));

			context.registerService(UnhandledEventHandler.class,
					unhandledEventHandler, dictionaryOf());

			eventBus.deliver(a1);

			Mockito.verify(typedHandlerA, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_A),
							Mockito.argThat(evA -> "1".equals(evA.a)));

			eventBus.deliver(a2);

			Mockito.verify(unhandledEventHandler, Mockito.timeout(1000))
					.notifyUnhandled(Mockito.eq(TOPIC_A),
							Mockito.argThat(map -> "2".equals(map.get("a"))));
		}

		@Test
		public void test_filter_empty() throws InterruptedException {

			EventA a1 = new EventA();
			a1.a = "1";

			TypedEventHandler<EventA> typedHandler = mock(
					TypedEventHandlerA.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_FILTER, ""));

			eventBus.deliver(a1);

			Mockito.verify(typedHandler, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_A),
							Mockito.argThat(evA -> "1".equals(evA.a)));

		}

		@Test
		public void test_filter_failing() throws InterruptedException {

			EventA a1 = new EventA();
			a1.a = "1";

			TypedEventHandler<EventA> typedHandler = mock(
					TypedEventHandlerA.class);

			context.registerService(TypedEventHandler.class, typedHandler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_FILTER,
							"(failing=filter"));

			eventBus.deliver(a1);

			Mockito.verify(typedHandler, Mockito.after(1000).never())
					.notify(Mockito.any(), Mockito.any());

		}
	}

	@DisplayName("157.4.1 Receiving Typed Events")
	@Nested
	class ReceivingTypedEventsTest {
		@Test
		public void test_deliver_to_parameterized_TypedEventHandler()
				throws InterruptedException {

			EventA a = new EventA();
			a.a = "a";

			TypedEventHandler<EventA> typedHandlerA = mock(
					TypedEventHandlerA.class);

			TypedEventHandler<EventB> typedHandlerB = mock(
					TypedEventHandlerB.class);

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf());

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf());

			eventBus.deliver(a);

			Mockito.verify(typedHandlerA, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_A),
							Mockito.argThat(evA -> "a".equals(evA.a)));

			Mockito.verify(typedHandlerB, Mockito.after(1000).never())
					.notify(Mockito.eq(TOPIC_A), Mockito.any());
		}

		@Test
		public void test_deliver_to_parameterized_TypedEventHandler_with_defined_Topic()
				throws InterruptedException {

			String fooTopic = "fooTopic";
			EventA a = new EventA();
			a.a = "a";

			TypedEventHandler<EventA> typedHandlerA1 = mock(
					TypedEventHandlerA.class);

			TypedEventHandler<EventA> typedHandlerA2 = mock(
					TypedEventHandlerA.class);

			TypedEventHandler< ? > typedHandlerA3 = mock(
					TypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandlerA1,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									fooTopic
							}));

			context.registerService(TypedEventHandler.class, typedHandlerA2,
					dictionaryOf());

			context.registerService(TypedEventHandler.class, typedHandlerA3,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName()));

			eventBus.deliver(fooTopic, a);

			Mockito.verify(typedHandlerA1, Mockito.timeout(1000))
					.notify(Mockito.eq("fooTopic"),
							Mockito.argThat(evA -> "a".equals(evA.a)));

			Mockito.verify(typedHandlerA2, Mockito.after(1000).never())
					.notify(Mockito.any(), Mockito.any());

			Mockito.verify(typedHandlerA3, Mockito.after(1000).never())
					.notify(Mockito.any(), Mockito.any());
		}

		@Test
		public void test_deliver_to_unparameterized_TypedEventHandler()
				throws InterruptedException {

			EventA a = new EventA();
			a.a = "a";

			TypedEventHandler< ? > typedHandlerA = mock(
					TypedEventHandler.class);

			TypedEventHandler< ? > typedHandlerB = mock(
					TypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName()));

			context.registerService(TypedEventHandler.class, typedHandlerB,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventB.class.getName()));

			eventBus.deliver(a);

			Mockito.verify(typedHandlerA, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_A),
							Mockito.argThat(evA -> evA instanceof EventA
									&& "a".equals(((EventA) evA).a)));

			Mockito.verify(typedHandlerB, Mockito.after(1000).never())
					.notify(Mockito.eq(TOPIC_A), Mockito.any());
		}

		@Test
		public void test_not_deliver_to_unparameterized_TypedEventHandler_WithoutTypeProp()
				throws InterruptedException {

			EventA a = new EventA();
			a.a = "a";

			TypedEventHandler< ? > typedHandlerA = mock(
					TypedEventHandler.class);

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf());

			eventBus.deliver(a);

			Mockito.verify(typedHandlerA, Mockito.after(1000).never())
					.notify(Mockito.any(), Mockito.any());
		}

		@Test
		public void test_deliver_to_parameterized_special_TypedEventHandler()
				throws InterruptedException {

			EventAA a = new EventAA();
			a.aa = "aa";
			a.a = "a";

			TypedEventHandlerA typedHandlerAA = mock(TypedEventHandlerA.class);
			TypedEventHandlerA typedHandlerA = mock(TypedEventHandlerA.class);

			context.registerService(TypedEventHandler.class, typedHandlerAA,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventAA.class.getName()));

			context.registerService(TypedEventHandler.class, typedHandlerA,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
							EventA.class.getName()));

			eventBus.deliver(a);

			Mockito.verify(typedHandlerAA, Mockito.timeout(1000))
					.notify(Mockito.eq(TOPIC_AA),
							Mockito.argThat(evA -> evA instanceof EventAA
									&& "aa".equals(((EventAA) evA).aa)
									&& "a".equals(evA.a)));

			Mockito.verify(typedHandlerA, Mockito.after(1000).never())
					.notify(Mockito.eq(TOPIC_A), Mockito.any());
		}
	}

	@DisplayName("157.4.2 Receiving Untyped Events")
	@Nested
	class ReceivingUntypedEventsTest {

		@Test
		public void test_deliver_to_untypedEventHandler()
				throws InterruptedException {
			String fooTopic = "fooTopic";
			EventA a = new EventA();
			a.a = "a";

			UntypedEventHandler untypedHandlerFoo = mock(
					UntypedEventHandler.class);

			UntypedEventHandler untypedHandler = mock(
					UntypedEventHandler.class);

			UntypedEventHandler untypedHandlerA = mock(
					UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class,
					untypedHandlerFoo,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									"fooTopic"
							}));

			context.registerService(UntypedEventHandler.class, untypedHandler,
					dictionaryOf());

			context.registerService(UntypedEventHandler.class, untypedHandlerA,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							new String[] {
									TOPIC_A
							}));

			eventBus.deliver(a);

			Mockito.verify(untypedHandlerFoo, Mockito.after(1000).never())
					.notifyUntyped(Mockito.any(), Mockito.any());

			Mockito.verify(untypedHandler, Mockito.after(1000).never())
					.notifyUntyped(Mockito.any(), Mockito.any());

			Mockito.verify(untypedHandlerA, Mockito.timeout(1000))
					.notifyUntyped(Mockito.eq(TOPIC_A),
							Mockito.argThat(map -> map.containsKey("a")
									&& map.containsValue("a")
									&& map.size() == 1));

			eventBus.deliver(fooTopic, a);

			Mockito.verify(untypedHandlerFoo, Mockito.timeout(1000))
					.notifyUntyped(Mockito.eq(fooTopic),
							Mockito.argThat(map -> map.containsKey("a")
									&& map.containsValue("a")
									&& map.size() == 1));
		}

	}
}

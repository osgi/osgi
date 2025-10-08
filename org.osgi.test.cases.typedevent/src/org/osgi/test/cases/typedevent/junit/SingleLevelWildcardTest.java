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
import static org.mockito.Mockito.mock;
import static org.osgi.test.common.dictionary.Dictionaries.dictionaryOf;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.cases.typedevent.common.TypedEventHandlerA;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.4.4 Single-Level Wildcard Topics")
public class SingleLevelWildcardTest {

	@InjectBundleContext
	BundleContext	context;

	@InjectService
	TypedEventBus	eventBus;

	@DisplayName("Single-Level Wildcard Matching")
	@Nested
	class SingleLevelWildcardMatching {

		@Test
		@DisplayName("foo/+/bar should match foo/baz/bar")
		public void testSingleLevelWildcardMatches() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/bar"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/baz/bar", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/baz/bar"), eq(event));
		}

		@Test
		@DisplayName("foo/+/bar should match foo/x/bar but not foo/bar")
		public void testSingleLevelWildcardDoesNotMatchMissingLevel() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/bar"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar", event);

			Mockito.verify(handler, Mockito.after(1000).never())
					.notifyUntyped(any(), any());
		}

		@Test
		@DisplayName("foo/+/bar should not match foo/x/y/bar")
		public void testSingleLevelWildcardDoesNotMatchMultipleLevels() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/bar"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/x/y/bar", event);

			Mockito.verify(handler, Mockito.after(1000).never())
					.notifyUntyped(any(), any());
		}

		@Test
		@DisplayName("foo/+/foobar should match both foo/bar/foobar and foo/baz/foobar")
		public void testSingleLevelWildcardMatchesMultipleTopics() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/foobar"));

			Map<String,Object> event1 = new HashMap<>();
			event1.put("message", "first");
			Map<String,Object> event2 = new HashMap<>();
			event2.put("message", "second");

			eventBus.deliverUntyped("foo/bar/foobar", event1);
			eventBus.deliverUntyped("foo/baz/foobar", event2);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/foobar"), eq(event1));
			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/baz/foobar"), eq(event2));
		}
	}

	@DisplayName("Multiple Single-Level Wildcards")
	@Nested
	class MultipleSingleLevelWildcards {

		@Test
		@DisplayName("foo/+/+ should match foo/bar/baz")
		public void testMultipleSingleLevelWildcards() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz"), eq(event));
		}

		@Test
		@DisplayName("+/+/+ should match a/b/c")
		public void testAllSingleLevelWildcards() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"+/+/+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("a/b/c", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("a/b/c"), eq(event));
		}

		@Test
		@DisplayName("+/middle/+ should match start/middle/end")
		public void testMixedSingleLevelWildcards() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"+/middle/+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("start/middle/end", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("start/middle/end"), eq(event));
		}
	}

	@DisplayName("Single-Level Wildcard at Different Positions")
	@Nested
	class WildcardPositions {

		@Test
		@DisplayName("+/bar/baz should match foo/bar/baz")
		public void testWildcardAtStart() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"+/bar/baz"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz"), eq(event));
		}

		@Test
		@DisplayName("foo/bar/+ should match foo/bar/baz")
		public void testWildcardAtEnd() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/bar/+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz"), eq(event));
		}

		@Test
		@DisplayName("foo/+/baz should match foo/bar/baz")
		public void testWildcardInMiddle() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/baz"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz"), eq(event));
		}
	}

	@DisplayName("Special Case: + Alone")
	@Nested
	class PlusAlone {

		@Test
		@DisplayName("+ should match all single-level topics")
		public void testPlusMatchesSingleLevel() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, "+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo"), eq(event));
		}

		@Test
		@DisplayName("+ should not match multi-level topics")
		public void testPlusDoesNotMatchMultiLevel() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, "+"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar", event);

			Mockito.verify(handler, Mockito.after(1000).never())
					.notifyUntyped(any(), any());
		}
	}

	@DisplayName("Typed Event Handlers with Single-Level Wildcards")
	@Nested
	class TypedHandlerWildcards {

		@Test
		@DisplayName("Typed handler should receive events matching single-level wildcard")
		public void testTypedHandlerWithSingleLevelWildcard() {
			TypedEventHandler<EventA> handler = mock(TypedEventHandlerA.class);

			context.registerService(TypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"custom/+/topic"));

			EventA event1 = new EventA();
			event1.a = "test1";
			EventA event2 = new EventA();
			event2.a = "test2";

			eventBus.deliver("custom/foo/topic", event1);
			eventBus.deliver("custom/topic", event2);

			Mockito.verify(handler, Mockito.timeout(8000).only())
					.notify(eq("custom/foo/topic"),
							argThat(e -> "test1".equals(e.a)));
		}
	}

	@DisplayName("Combining Single-Level Wildcards with Multi-Level Wildcards")
	@Nested
	class CombiningWildcards {

		@Test
		@DisplayName("foo/+/* should match foo/bar/baz/qux")
		public void testSingleLevelFollowedByMultiLevel() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/*"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz/qux", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz/qux"), eq(event));
		}

		@Test
		@DisplayName("foo/+/* should match foo/bar/baz")
		public void testSingleLevelFollowedByMultiLevelMinimal() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/*"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar/baz", event);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/bar/baz"), eq(event));
		}

		@Test
		@DisplayName("foo/+/* should not match foo/bar")
		public void testSingleLevelFollowedByMultiLevelNoMatch() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/*"));

			Map<String,Object> event = new HashMap<>();
			event.put("test", "value");

			eventBus.deliverUntyped("foo/bar", event);

			Mockito.verify(handler, Mockito.after(1000).never())
					.notifyUntyped(any(), any());
		}
	}

	@DisplayName("Event Filtering with Single-Level Wildcards")
	@Nested
	class FilteringWithWildcards {

		@Test
		@DisplayName("Single-level wildcard with filter should only match filtered events")
		public void testSingleLevelWildcardWithFilter() {
			UntypedEventHandler handler = mock(UntypedEventHandler.class);

			context.registerService(UntypedEventHandler.class, handler,
					dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
							"foo/+/bar", TypedEventConstants.TYPED_EVENT_FILTER,
							"(value=match)"));

			Map<String,Object> matchingEvent = new HashMap<>();
			matchingEvent.put("value", "match");

			Map<String,Object> nonMatchingEvent = new HashMap<>();
			nonMatchingEvent.put("value", "nomatch");

			eventBus.deliverUntyped("foo/x/bar", matchingEvent);
			eventBus.deliverUntyped("foo/y/bar", nonMatchingEvent);

			Mockito.verify(handler, Mockito.timeout(1000))
					.notifyUntyped(eq("foo/x/bar"), eq(matchingEvent));
			Mockito.verify(handler, Mockito.after(1000).never())
					.notifyUntyped(eq("foo/y/bar"), any());
		}
	}
}

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.4.4 Specification Examples for Single-Level Wildcards")
public class SpecificationExampleTest {

	@InjectBundleContext
	BundleContext	context;

	@InjectService
	TypedEventBus	eventBus;

	@Test
	@DisplayName("Example from spec: foo/+/foobar matches foo/bar/foobar and foo/baz/foobar")
	public void testSpecificationExampleMatching() throws InterruptedException {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
						"foo/+/foobar"));

		Map<String,Object> event1 = new HashMap<>();
		event1.put("message", "test1");
		Map<String,Object> event2 = new HashMap<>();
		event2.put("message", "test2");

		eventBus.deliverUntyped("foo/bar/foobar", event1);
		eventBus.deliverUntyped("foo/baz/foobar", event2);

		verify(handler, Mockito.timeout(1000))
				.notifyUntyped(eq("foo/bar/foobar"), eq(event1));
		verify(handler, Mockito.timeout(1000))
				.notifyUntyped(eq("foo/baz/foobar"), eq(event2));
	}

	@Test
	@DisplayName("Example from spec: foo/+/foobar does not match foo/foobar")
	public void testSpecificationExampleNotMatchingTooShort() {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
						"foo/+/foobar"));

		Map<String,Object> event = new HashMap<>();
		event.put("message", "test");

		eventBus.deliverUntyped("foo/foobar", event);

		verify(handler, Mockito.after(1000).never()).notifyUntyped(any(),
				any());
	}

	@Test
	@DisplayName("Example from spec: foo/+/foobar does not match foo/bar/foobar/baz")
	public void testSpecificationExampleNotMatchingTooLong() {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS,
						"foo/+/foobar"));

		Map<String,Object> event = new HashMap<>();
		event.put("message", "test");

		eventBus.deliverUntyped("foo/bar/foobar/baz", event);

		verify(handler, Mockito.after(1000).never()).notifyUntyped(any(),
				any());
	}

	@Test
	@DisplayName("Example from spec: foo/* matches foo/bar and foo/baz")
	public void testMultiLevelWildcardExample() throws InterruptedException {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, "foo/*"));

		Map<String,Object> event1 = new HashMap<>();
		event1.put("message", "test1");
		Map<String,Object> event2 = new HashMap<>();
		event2.put("message", "test2");

		eventBus.deliverUntyped("foo/bar", event1);
		eventBus.deliverUntyped("foo/baz", event2);

		verify(handler, Mockito.timeout(1000)).notifyUntyped(eq("foo/bar"),
				eq(event1));
		verify(handler, Mockito.timeout(1000)).notifyUntyped(eq("foo/baz"),
				eq(event2));
	}

	@Test
	@DisplayName("Example from spec: foo/* does not match foo")
	public void testMultiLevelWildcardDoesNotMatchParent() {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, "foo/*"));

		Map<String,Object> event = new HashMap<>();
		event.put("message", "test");

		eventBus.deliverUntyped("foo", event);

		verify(handler, Mockito.after(1000).never()).notifyUntyped(any(),
				any());
	}

	@Test
	@DisplayName("Example from spec: foo/* does not match foobar/fizzbuzz")
	public void testMultiLevelWildcardDoesNotMatchDifferentPrefix() {
		UntypedEventHandler handler = mock(UntypedEventHandler.class);

		context.registerService(UntypedEventHandler.class, handler,
				dictionaryOf(TypedEventConstants.TYPED_EVENT_TOPICS, "foo/*"));

		Map<String,Object> event = new HashMap<>();
		event.put("message", "test");

		eventBus.deliverUntyped("foobar/fizzbuzz", event);

		verify(handler, Mockito.after(1000).never()).notifyUntyped(any(),
				any());
	}
}

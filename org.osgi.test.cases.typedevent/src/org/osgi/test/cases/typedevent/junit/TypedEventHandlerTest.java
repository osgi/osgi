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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.dto.DTO;
import org.osgi.framework.BundleContext;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UnhandledEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@Nested
public class TypedEventHandlerTest {

	class A extends DTO {
		String aa;
	}

	class B extends A {
		String bb;
	}

	class C extends B {
		String cc;
	}

	@InjectService
	static TypedEventBus bus;

	@Test
	public void play(@InjectBundleContext
	BundleContext bc) {
		assertThat(bus).isNotNull();
		assertThat(bc).isNotNull();

		TypedEventHandler<TestEvent> typedHandlerAll = new TestEventHandler<>(
				"typedHandlerAll");
		TypedEventHandler<TestEvent> typedHandler12 = new TestEventHandler<>(
				"typedHandler12");
		TypedEventHandler<TestEvent> typedHandler23 = new TestEventHandler<>(
				"typedHandler23");
		TypedEventHandler<TestEvent> typedHandlerEmpty = new TestEventHandler<>(
				"typedHandlerEmpty");
		UntypedEventHandler untypedHandlerAll = new TestEventHandler<>(
				"untypedHandlerAll");
		UntypedEventHandler untypedHandler12 = new TestEventHandler<>(
				"untypedHandler12");
		UntypedEventHandler untypedHandler23 = new TestEventHandler<>(
				"untypedHandler23");
		UntypedEventHandler untypedHandlerEmpty = new TestEventHandler<>(
				"untypedHandlerEmpty");
		UnhandledEventHandler unhandledHandlerNull1 = new TestEventHandler<>(
				"unhandledHandlerNull1");
		UnhandledEventHandler unhandledHandlerNull2 = new TestEventHandler<>(
				"unhandledHandlerNull2");
		UnhandledEventHandler unhandledHandler12 = new TestEventHandler<>(
				"unhandledHandler12");
		UnhandledEventHandler unhandledHandlerAll = new TestEventHandler<>(
				"unhandledHandlerAll");

		bc.registerService(UnhandledEventHandler.class, unhandledHandlerNull1,
				null);
		bc.registerService(UnhandledEventHandler.class, unhandledHandlerNull2,
				null);
		bc.registerService(UnhandledEventHandler.class, unhandledHandler12,
				Dictionaries.dictionaryOf(TypedEventConstants.TYPED_EVENT_TYPE,
						TestEvent.class.getName(),
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"1", "2"
						}));
		bc.registerService(UnhandledEventHandler.class, unhandledHandlerAll,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"*"
						}));

		bc.registerService(TypedEventHandler.class, typedHandlerAll,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"*"
						}));
		bc.registerService(TypedEventHandler.class, typedHandler12,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"1", "2"
						}));
		bc.registerService(TypedEventHandler.class, typedHandler23,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"2", "3"
						}));
		bc.registerService(TypedEventHandler.class, typedHandlerEmpty,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
						}));

		bc.registerService(UntypedEventHandler.class, untypedHandlerAll,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"*"
						}));
		bc.registerService(UntypedEventHandler.class, untypedHandler12,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"1", "2"
						}));
		bc.registerService(UntypedEventHandler.class, untypedHandler23,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
								"2", "3"
						}));
		bc.registerService(UntypedEventHandler.class, untypedHandlerEmpty,
				Dictionaries.dictionaryOf(
						TypedEventConstants.TYPED_EVENT_TOPICS, new String[] {
						}));

		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		bus.deliver("1", new TestEvent("test1", "a", "A"));

		bus.deliver("x", new TestEvent("test1", "a", "A"));
	}

}

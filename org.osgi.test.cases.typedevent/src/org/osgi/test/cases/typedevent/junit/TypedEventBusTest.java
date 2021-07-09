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

import static org.assertj.core.api.Assertions.*;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.dto.DTO;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@Nested
public class TypedEventBusTest {

	private static final DTO	dto	= new DTO() {
										public String a = "";
									};

	@InjectService
	static TypedEventBus		typedEventBus;

	@Test
	public void test_service_exists() {
		assertThat(typedEventBus).isNotNull();
	}

	@Test
	public void test_deliver_dto() {
		typedEventBus.deliver(dto);
	}

	@Test
	public void test_deliver_null_should_throws_exception() {

		assertThatNullPointerException().isThrownBy(() -> {
			typedEventBus.deliver(null);
		});

	}

	@Test
	public void test_deliver_2_topic_null_should_throws_exception() {

		assertThatIllegalArgumentException().isThrownBy(() -> {
			typedEventBus.deliver(null, dto);
		});

	}

	@Test
	public void test_deliver_2_event_null_should_throws_exception() {

		assertThatNullPointerException().isThrownBy(() -> {
			typedEventBus.deliver("a", null);
		});

	}

	@Test
	public void test_deliveruntyped() {

		typedEventBus.deliverUntyped("a", new HashMap<>());

	}

	@Test
	public void test_deliveruntyped_topic_null_should_throws_exception() {

		assertThatIllegalArgumentException().isThrownBy(() -> {
			typedEventBus.deliverUntyped(null, new HashMap<>());
		});
	}

	@Test
	public void test_deliveruntyped_map_null_should_throws_exception() {

		assertThatNullPointerException().isThrownBy(() -> {
			typedEventBus.deliverUntyped("a", null);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"*", "%", "/", "//", "a//b", "//b", "a//", "a/", "a/*/*/b",
			"a/b/c/", "a/*/*", " /*", " ", "a/ /b", " / ", "Bär"
	})
	public void test_deliver_2_topic_invalid_should_throws_exception(
			String invalidTopic) {
		assertThatIllegalArgumentException().isThrownBy(() -> {
			typedEventBus.deliver(invalidTopic, dto);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a", "A", "a/b", "A/B", "A/b/C", "a/b/c", "foO/BaR",
			"$/a/A/b/c/d/e/f/g/h/i/j/k/l/m/n/o/p/q/r/s/t/u/v/w/x/y/z/1/2/3/4/5/6/7/8/9/0/-/_/$",
			"1", "2/3", "4/a/5", "a/_", "-/-", "_/_", "-", "_", "$", "$/$1$a$/$"
	})
	public void test_deliver_2_topic_valid_should_not_throws_exception(
			String validTopic) {
		typedEventBus.deliver(validTopic, dto);
	}

	@Nested
	static class NotSpecified {

		enum FooEnum {
			BAR
		}

		@Test
		public void test_deliver_nondto_enum_throws_exception() {

			assertThatThrownBy(() -> {
				typedEventBus.deliver(FooEnum.BAR);
			}).isInstanceOf(IllegalArgumentException.class);

		}

		@Test
		public void test_deliver_nondto_string_throws_exception() {

			assertThatThrownBy(() -> {
				typedEventBus.deliver("bar");
			}).isInstanceOf(IllegalArgumentException.class);

		}

		@Test
		public void test_deliver_nondto_map_throws_exception() {
			Entry<String,String> entry = new AbstractMap.SimpleEntry<String,String>(
					"k", "v");
			assertThatThrownBy(() -> {
				typedEventBus.deliver(entry);
			}).isInstanceOf(IllegalArgumentException.class);
		}
	}


}

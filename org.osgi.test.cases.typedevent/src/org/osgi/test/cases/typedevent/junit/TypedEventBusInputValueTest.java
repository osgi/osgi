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

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.dto.DTO;
import org.osgi.service.typedevent.TypedEventBus;
import org.osgi.test.cases.typedevent.common.EventA;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@Nested
@DisplayName("157.5.1 Error Handling")
public class TypedEventBusInputValueTest {

	private static final DTO	dto	= new EventA();

	@InjectService
	TypedEventBus				typedEventBus;

	@Test
	public void test_service_exists() {
		assertThat(typedEventBus).isNotNull();
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
			"a/b/c/", "a/*/*", " /*", " ", "a/ /b", " / ", "(/)", "[/]", "{/}",
			"foo+/bar", "foo/+bar", "foo/bar+", "+foo", "foo+", "a/+*/b",
			"a/*/+/b", "+/*/+/*"
	})
	public void test_deliver_2_topic_invalid_should_throws_exception(
			String invalidTopic) {
		assertThatIllegalArgumentException().isThrownBy(() -> {
			typedEventBus.deliver(invalidTopic, dto);
		});
	}

	@ParameterizedTest
	@ValueSource(chars = {
			'-', ']', '}', ')', '(', '}', ']'
	})
	void testIsNotJavaIdentifierPart(char c) throws Exception {
		assertThat(Character.isJavaIdentifierPart(c)).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a", "A", "a/b", "A/B", "A/b/C", "a/b/c", "foO/BaR",
			"$/a/A/b/c/d/e/f/g/h/i/j/k/l/m/n/o/p/q/r/s/t/u/v/w/x/y/z/1/2/3/4/5/6/7/8/9/0/-/_/$",
			"1", "2/3", "4/a/5", "a/_", "-/-", "_/_", "-", "_", "$",
			"$/$1$a$/$", "Bär"
	})
	public void test_deliver_2_topic_valid_should_not_throws_exception(
			String validTopic) {
		typedEventBus.deliver(validTopic, dto);
	}

}

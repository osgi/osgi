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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.typedevent.monitor.TypedEventMonitor;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.function.Predicate;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("157.11.4 TypedEventMonitor")
public class TopicFilterMatchingTest {

	@InjectService
	TypedEventMonitor monitor;

	@DisplayName("Predicate Creation")
	@Nested
	class PredicateCreationTest {

		@Test
		public void createPredicateForExactTopic() {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test");

			assertThat(predicate).isNotNull();
		}

		@Test
		public void createPredicateForWildcardFilter() {
			Predicate<String> predicate = monitor.topicFilterMatches("events/*");

			assertThat(predicate).isNotNull();
		}

		@Test
		public void createPredicateForSingleLevelWildcard() {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/data");

			assertThat(predicate).isNotNull();
		}

		@Test
		public void nullFilterThrowsNullPointerException() {
			assertThatThrownBy(() -> monitor.topicFilterMatches(null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void invalidFilterSyntaxThrowsIllegalArgumentException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches("invalid//filter"))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@DisplayName("Predicate Matching - Exact Topics")
	@Nested
	class PredicateMatchingExactTopicsTest {

		@Test
		public void exactFilterMatchesExactTopic() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test");

			assertThat(predicate.test("events/test")).isTrue();
		}

		@Test
		public void exactFilterDoesNotMatchDifferentTopic() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test");

			assertThat(predicate.test("events/other")).isFalse();
		}

		@Test
		public void exactFilterDoesNotMatchSubTopic() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test");

			assertThat(predicate.test("events/test/sub")).isFalse();
		}

		@Test
		public void exactFilterDoesNotMatchParentTopic() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test/sub");

			assertThat(predicate.test("events/test")).isFalse();
		}
	}

	@DisplayName("Predicate Matching - Multi-Level Wildcards")
	@Nested
	class PredicateMatchingMultiLevelWildcardsTest {

		@Test
		public void multiLevelWildcardMatchesSingleLevel() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("events/*");

			assertThat(predicate.test("events/test")).isTrue();
		}

		@Test
		public void multiLevelWildcardMatchesMultipleLevels() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("events/*");

			assertThat(predicate.test("events/test/sub/deep")).isTrue();
		}

		@Test
		public void multiLevelWildcardDoesNotMatchParent() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("events/*");

			assertThat(predicate.test("events")).isFalse();
		}

		@Test
		public void rootMultiLevelWildcardMatchesAll() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("*");

			assertThat(predicate.test("events")).isTrue();
			assertThat(predicate.test("events/test")).isTrue();
			assertThat(predicate.test("events/test/sub")).isTrue();
		}
	}

	@DisplayName("Predicate Matching - Single-Level Wildcards")
	@Nested
	class PredicateMatchingSingleLevelWildcardsTest {

		@Test
		public void singleLevelWildcardMatchesOneLevel() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/data");

			assertThat(predicate.test("events/test/data")).isTrue();
			assertThat(predicate.test("events/other/data")).isTrue();
		}

		@Test
		public void singleLevelWildcardDoesNotMatchZeroLevels()
				throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/data");

			assertThat(predicate.test("events/data")).isFalse();
		}

		@Test
		public void singleLevelWildcardDoesNotMatchMultipleLevels()
				throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/data");

			assertThat(predicate.test("events/a/b/data")).isFalse();
		}

		@Test
		public void multipleSingleLevelWildcards() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/+/data");

			assertThat(predicate.test("events/a/b/data")).isTrue();
			assertThat(predicate.test("events/x/y/data")).isTrue();
		}

		@Test
		public void singleLevelWildcardAtEnd() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/test/+");

			assertThat(predicate.test("events/test/a")).isTrue();
			assertThat(predicate.test("events/test/b")).isTrue();
			assertThat(predicate.test("events/test/a/b")).isFalse();
		}

		@Test
		public void singleLevelWildcardAtStart() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("+/test/data");

			assertThat(predicate.test("events/test/data")).isTrue();
			assertThat(predicate.test("other/test/data")).isTrue();
		}

		@Test
		public void singleLevelWildcardAlone() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("+");

			assertThat(predicate.test("events")).isTrue();
			assertThat(predicate.test("test")).isTrue();
			assertThat(predicate.test("events/test")).isFalse();
		}
	}

	@DisplayName("Predicate Matching - Combined Wildcards")
	@Nested
	class PredicateMatchingCombinedWildcardsTest {

		@Test
		public void singleLevelFollowedByMultiLevel() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/+/*");

			assertThat(predicate.test("events/a/b")).isTrue();
			assertThat(predicate.test("events/a/b/c/d")).isTrue();
			assertThat(predicate.test("events/a")).isFalse();
		}

		@Test
		public void multiLevelWithSingleLevelInMiddle() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("+/test/*");

			assertThat(predicate.test("events/test/data")).isTrue();
			assertThat(predicate.test("other/test/more/data")).isTrue();
		}
	}

	@DisplayName("Direct Matching Method")
	@Nested
	class DirectMatchingMethodTest {

		@Test
		public void directMatchReturnsTrue() {
			boolean matches = monitor.topicFilterMatches("events/test",
					"events/test");

			assertThat(matches).isTrue();
		}

		@Test
		public void directMatchReturnsFalse() {
			boolean matches = monitor.topicFilterMatches("events/other",
					"events/test");

			assertThat(matches).isFalse();
		}

		@Test
		public void directMatchWithWildcard() {
			boolean matches = monitor.topicFilterMatches("events/test/data",
					"events/+/data");

			assertThat(matches).isTrue();
		}

		@Test
		public void directMatchWithMultiLevelWildcard() {
			boolean matches = monitor.topicFilterMatches("events/test/sub/deep",
					"events/*");

			assertThat(matches).isTrue();
		}

		@Test
		public void nullTopicNameThrowsNullPointerException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches(null, "events/test"))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void nullTopicFilterThrowsNullPointerException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches("events/test", null))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		public void invalidTopicNameThrowsIllegalArgumentException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches("invalid//name",
							"events/test"))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void invalidTopicFilterThrowsIllegalArgumentException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches("events/test",
							"invalid//filter"))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void wildcardInTopicNameThrowsIllegalArgumentException() {
			assertThatThrownBy(
					() -> monitor.topicFilterMatches("events/*", "events/*"))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@DisplayName("Edge Cases")
	@Nested
	class EdgeCasesTest {

		@Test
		public void emptyTopicSegments() throws Exception {
			Predicate<String> predicate = monitor.topicFilterMatches("a/b/c");

			assertThat(predicate.test("a/b/c")).isTrue();
		}

		@Test
		public void longTopicPath() throws Exception {
			String longFilter = "a/b/c/d/e/f/g/h/i/j";
			Predicate<String> predicate = monitor.topicFilterMatches(longFilter);

			assertThat(predicate.test("a/b/c/d/e/f/g/h/i/j")).isTrue();
			assertThat(predicate.test("a/b/c/d/e/f/g/h/i")).isFalse();
		}

		@Test
		public void topicWithNumbers() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("events/123/+");

			assertThat(predicate.test("events/123/456")).isTrue();
		}

		@Test
		public void topicWithSpecialCharacters() throws Exception {
			Predicate<String> predicate = monitor
					.topicFilterMatches("org/osgi/test/+");

			assertThat(predicate.test("org/osgi/test/EventA")).isTrue();
		}
	}
}

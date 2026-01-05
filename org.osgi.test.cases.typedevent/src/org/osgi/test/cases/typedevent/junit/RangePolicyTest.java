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
import org.osgi.service.typedevent.monitor.RangePolicy;

@DisplayName("157.11.3 RangePolicy")
public class RangePolicyTest {

	@DisplayName("Factory Methods")
	@Nested
	class FactoryMethodsTest {

		@Test
		public void rangeCreatesValidPolicy() {
			RangePolicy policy = RangePolicy.range(5, 10);

			assertThat(policy.getMinimum()).isEqualTo(5);
			assertThat(policy.getMaximum()).isEqualTo(10);
		}

		@Test
		public void exactCreatesEqualMinMax() {
			RangePolicy policy = RangePolicy.exact(100);

			assertThat(policy.getMinimum()).isEqualTo(100);
			assertThat(policy.getMaximum()).isEqualTo(100);
		}

		@Test
		public void atLeastSetsMinWithMaxInteger() {
			RangePolicy policy = RangePolicy.atLeast(50);

			assertThat(policy.getMinimum()).isEqualTo(50);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void atMostSetsMaxWithZeroMin() {
			RangePolicy policy = RangePolicy.atMost(200);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(200);
		}

		@Test
		public void noneReturnsZeroRange() {
			RangePolicy policy = RangePolicy.none();

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(0);
		}

		@Test
		public void unlimitedReturnsMaxRange() {
			RangePolicy policy = RangePolicy.unlimited();

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void noneReturnsSameInstance() {
			RangePolicy policy1 = RangePolicy.none();
			RangePolicy policy2 = RangePolicy.none();

			assertThat(policy1).isSameAs(policy2);
		}

		@Test
		public void unlimitedReturnsSameInstance() {
			RangePolicy policy1 = RangePolicy.unlimited();
			RangePolicy policy2 = RangePolicy.unlimited();

			assertThat(policy1).isSameAs(policy2);
		}

		@Test
		public void rangeWithEqualMinMaxIsValid() {
			RangePolicy policy = RangePolicy.range(50, 50);

			assertThat(policy.getMinimum()).isEqualTo(50);
			assertThat(policy.getMaximum()).isEqualTo(50);
		}

		@Test
		public void rangeWithZeroMinAndMaxIsValid() {
			RangePolicy policy = RangePolicy.range(0, 0);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(0);
		}

		@Test
		public void exactWithZeroIsValid() {
			RangePolicy policy = RangePolicy.exact(0);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(0);
		}

		@Test
		public void atLeastWithZeroIsValid() {
			RangePolicy policy = RangePolicy.atLeast(0);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void atMostWithZeroIsValid() {
			RangePolicy policy = RangePolicy.atMost(0);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(0);
		}
	}

	@DisplayName("Invalid Arguments")
	@Nested
	class InvalidArgumentsTest {

		@Test
		public void negativeMinThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.range(-1, 10))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void negativeMaxThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.range(0, -1))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void minGreaterThanMaxThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.range(10, 5))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void exactWithNegativeThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.exact(-1))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void atLeastWithNegativeThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.atLeast(-1))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		public void atMostWithNegativeThrowsIllegalArgumentException() {
			assertThatThrownBy(() -> RangePolicy.atMost(-1))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@DisplayName("Boundary Values")
	@Nested
	class BoundaryValuesTest {

		@Test
		public void rangeWithMaxIntegerValues() {
			RangePolicy policy = RangePolicy.range(0, Integer.MAX_VALUE);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void exactWithMaxIntegerValue() {
			RangePolicy policy = RangePolicy.exact(Integer.MAX_VALUE);

			assertThat(policy.getMinimum()).isEqualTo(Integer.MAX_VALUE);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void atLeastWithMaxIntegerValue() {
			RangePolicy policy = RangePolicy.atLeast(Integer.MAX_VALUE);

			assertThat(policy.getMinimum()).isEqualTo(Integer.MAX_VALUE);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void atMostWithMaxIntegerValue() {
			RangePolicy policy = RangePolicy.atMost(Integer.MAX_VALUE);

			assertThat(policy.getMinimum()).isEqualTo(0);
			assertThat(policy.getMaximum()).isEqualTo(Integer.MAX_VALUE);
		}

		@Test
		public void rangeWithLargeValues() {
			RangePolicy policy = RangePolicy.range(1000000, 2000000);

			assertThat(policy.getMinimum()).isEqualTo(1000000);
			assertThat(policy.getMaximum()).isEqualTo(2000000);
		}
	}
}

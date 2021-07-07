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

package org.osgi.test.cases.feature.assertj;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.feature.Feature;

@ExtendWith(SoftAssertionsExtension.class)
public class FeaturesConditionsAssertTest implements ConditionAssert {

	@Nested
	class FeatureContitionsTest {

		private String	featureName	= "theFeature";
		Feature			feature		= null;

		@BeforeEach
		private void beforEach() {
			feature = mock(Feature.class, featureName);
		}

		@Test
		void testComplete() throws Exception {

			when(feature.isComplete()).thenReturn(true);

			// condition pass
			passingIs(Conditions.FeatureConditions.complete(), feature);

			// assertion pass
			Assertions.assertThat(feature)
				.isComplete();

			when(feature.isComplete()).thenReturn(false);

			// condition fail
			failingIs(Conditions.FeatureConditions.complete(), feature, "complete");

			// assertion fail
			assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> Assertions.assertThat(feature)
				.isComplete())
				.withMessage(format("%nExpecting:%n  " + featureName + "%nto be complete"));
		}

		@Test
		void testNotComplete() throws Exception {

			when(feature.isComplete()).thenReturn(false);

			// condition pass
			passingIs(Conditions.FeatureConditions.notComplete(), feature);

			// assertion pass
			Assertions.assertThat(feature)
				.isNotComplete();

			when(feature.isComplete()).thenReturn(true);

			// condition fail
			failingIs(Conditions.FeatureConditions.notComplete(), feature, "not complete");

			// assertion fail
			assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> Assertions.assertThat(feature)
				.isNotComplete())
				.withMessage(format("%nExpecting:%n  " + featureName + "%nnot to be  complete"));

		}

		@Test
		void testNameNull() throws Exception {

			when(feature.getName()).thenReturn(Optional.empty());

			// condition pass
			passingHas(Conditions.FeatureConditions.nameNull(), feature);


			when(feature.getName()).thenReturn(Optional.of("featureName"));

			// condition fail
			failingHas(Conditions.FeatureConditions.nameNull(), feature, "name <null>");

		}
	}
	// TODO: All additional tests after specification is more stable.
}

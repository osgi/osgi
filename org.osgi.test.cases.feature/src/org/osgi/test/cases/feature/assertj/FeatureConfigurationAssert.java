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

import org.osgi.service.feature.FeatureConfiguration;

/**
 * {@link FeatureConfiguration} specific assertions. Although this class is not final to allow Soft
 * assertions proxy, if you wish to extend it, extend
 * {@link AbstractFeatureConfigurationAssert} instead.
 */
public class FeatureConfigurationAssert
	extends AbstractFeatureConfigurationAssert<FeatureConfigurationAssert, FeatureConfiguration> {

	/**
	 * Creates a new <code>{@link FeatureConfigurationAssert}</code> to make
	 * assertions on actual FeatureConfiguration.
	 *
	 * @param actual the FeatureConfiguration we want to make assertions on.
	 */
	public FeatureConfigurationAssert(FeatureConfiguration actual) {
		super(actual, FeatureConfigurationAssert.class);
	}

	/**
	 * An entry point for FeatureConfigurationAssert to follow AssertJ standard
	 * <code>assertThat()</code> statements.<br>
	 * With a static import, one can write directly:
	 * <code>assertThat(myFeatureConfiguration)</code> and get specific
	 * assertion with code completion.
	 *
	 * @param actual the FeatureConfiguration we want to make assertions on.
	 * @return a new <code>{@link FeatureConfigurationAssert}</code>
	 */
	@org.assertj.core.util.CheckReturnValue
	public static FeatureConfigurationAssert assertThat(FeatureConfiguration actual) {
		return new FeatureConfigurationAssert(actual);
	}
}

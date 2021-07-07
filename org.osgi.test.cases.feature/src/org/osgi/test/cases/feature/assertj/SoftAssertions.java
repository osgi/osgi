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

/**
 * Entry point for soft assertions of different data types.
 */
public class SoftAssertions extends org.assertj.core.api.SoftAssertions {

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureAssert assertThat(
			org.osgi.service.feature.Feature actual) {
		return proxy(org.osgi.test.cases.feature.assertj.FeatureAssert.class,
				org.osgi.service.feature.Feature.class, actual);
	}

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureArtifactAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureArtifactAssert assertThat(
			org.osgi.service.feature.FeatureArtifact actual) {
		return proxy(org.osgi.test.cases.feature.assertj.FeatureArtifactAssert.class,
				org.osgi.service.feature.FeatureArtifact.class, actual);
	}

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureBundleAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureBundleAssert assertThat(
			org.osgi.service.feature.FeatureBundle actual) {
		return proxy(
				org.osgi.test.cases.feature.assertj.FeatureBundleAssert.class,
				org.osgi.service.feature.FeatureBundle.class,
			actual);
	}

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert assertThat(
			org.osgi.service.feature.FeatureConfiguration actual) {
		return proxy(org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert.class,
				org.osgi.service.feature.FeatureConfiguration.class, actual);
	}

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureExtensionAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureExtensionAssert assertThat(
			org.osgi.service.feature.FeatureExtension actual) {
		return proxy(org.osgi.test.cases.feature.assertj.FeatureExtensionAssert.class,
				org.osgi.service.feature.FeatureExtension.class, actual);
	}

	/**
	 * Creates a new "soft" instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureIDAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created "soft" assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public org.osgi.test.cases.feature.assertj.FeatureIDAssert assertThat(
			org.osgi.service.feature.ID actual) {
		return proxy(org.osgi.test.cases.feature.assertj.FeatureIDAssert.class,
				org.osgi.service.feature.ID.class, actual);
	}

}

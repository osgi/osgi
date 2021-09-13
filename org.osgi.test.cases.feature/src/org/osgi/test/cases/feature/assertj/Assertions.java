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
 * Entry point for assertions of different data types. Each method in this class
 * is a static factory for the type-specific assertion objects.
 */
public class Assertions {

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureAssert assertThat(
			org.osgi.service.feature.Feature actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureAssert(actual);
	}

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureArtifactAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureArtifactAssert assertThat(
			org.osgi.service.feature.FeatureArtifact actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureArtifactAssert(actual);
	}

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureBundleAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureBundleAssert assertThat(
			org.osgi.service.feature.FeatureBundle actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureBundleAssert(actual);
	}

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert assertThat(
			org.osgi.service.feature.FeatureConfiguration actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureConfigurationAssert(actual);
	}

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureExtensionAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureExtensionAssert assertThat(
			org.osgi.service.feature.FeatureExtension actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureExtensionAssert(actual);
	}

	/**
	 * Creates a new instance of
	 * <code>{@link org.osgi.test.cases.feature.assertj.FeatureIDAssert}</code>.
	 *
	 * @param actual the actual value.
	 * @return the created assertion object.
	 */
	@org.assertj.core.util.CheckReturnValue
	public static org.osgi.test.cases.feature.assertj.FeatureIDAssert assertThat(
			org.osgi.service.feature.ID actual) {
		return new org.osgi.test.cases.feature.assertj.FeatureIDAssert(actual);
	}

	/**
	 * Creates a new <code>{@link Assertions}</code>.
	 */
	protected Assertions() {
		// empty
	}
}

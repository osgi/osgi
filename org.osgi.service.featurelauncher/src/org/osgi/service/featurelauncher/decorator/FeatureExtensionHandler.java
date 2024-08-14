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
package org.osgi.service.featurelauncher.decorator;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureExtension;

/**
 * A {@link FeatureExtensionHandler} is used to check and pre-process a Feature
 * based on its {@link FeatureExtension}s before the feature is installed or
 * updated. This allows the caller to potentially alter feature bundles, or edit
 * configurations before the feature is installed or updated.
 * <p>
 * Note that a {@link FeatureExtensionHandler} is <em>only called for features
 * with a matching extension</em> called and may only change the feature bundles
 * or feature configurations.
 */
@ConsumerType
public interface FeatureExtensionHandler {

	/**
	 * Provides an opportunity to transform a feature before it is installed or
	 * updated
	 * 
	 * @param feature the feature to be installed or updated
	 * @param extension the feature extension which caused this handler to be
	 *            called
	 * @param decoratedFeatureBuilder a builder that can be used to produce a
	 *            decorated feature with updated values
	 * @param factory - a factory allowing users to create values for use with
	 *            <code>decoratedFeatureBuilder</code>
	 * @return The {@link Feature} to be installed. This must either be the same
	 *         instance as <code>feature</code> or a new object created by
	 *         calling <code>decoratedFeatureBuilder.build()</code>. Returning
	 *         any other object is an error that will cause the install or
	 *         update operation to fail
	 * @throws AbandonOperationException if the feature installation or update
	 *             operation must not continue
	 */
	public Feature handle(Feature feature, FeatureExtension extension,
			FeatureExtensionHandlerBuilder decoratedFeatureBuilder,
			DecoratorBuilderFactory factory) throws AbandonOperationException;

	/**
	 * A reified builder which does not permit extensions to be modified
	 */
	@ProviderType
	public interface FeatureExtensionHandlerBuilder extends
			BaseFeatureDecorationBuilder<FeatureExtensionHandlerBuilder> {
		/** No further customisation needed */
	}
}

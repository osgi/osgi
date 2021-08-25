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
package org.osgi.service.feature;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The Feature Model Feature.
 * @ThreadSafe
 */
@ProviderType
public interface Feature {
    /**
	 * Get the Feature's ID.
	 * 
	 * @return The ID of this Feature.
	 */
	ID getID();

	/**
	 * Get the name.
	 * 
	 * @return The name.
	 */
    Optional<String> getName();

    /**
	 * Get the categories.
	 *
	 * @return The categories. The returned list is unmodifiable.
	 */
	List<String> getCategories();

	/**
	 * Get the description.
	 *
	 * @return The description.
	 */
    Optional<String> getDescription();

	/**
	 * Get the documentation URL.
	 *
	 * @return The documentation URL.
	 */
    Optional<String> getDocURL();

    /**
     * Get the vendor.
     * @return The vendor.
     */
    Optional<String> getVendor();

    /**
	 * Get the license of this Feature. The syntax of the value follows the
	 * Bundle-License header syntax. See the 'Bundle Manifest Headers' section
	 * in the OSGi Core specification.
	 *
	 * @return The license.
	 */
    Optional<String> getLicense();

	/**
	 * Get the SCM information relating to the feature. The syntax of the value
	 * follows the Bundle-SCM format. See the 'Bundle Manifest Headers' section
	 * in the OSGi Core specification.
	 *
	 * @return The SCM information.
	 */
    Optional<String> getSCM();

    /**
     * Get whether the feature is complete or not.
     * @return Completeness value.
     */
    boolean isComplete();

    /**
	 * Get the bundles.
	 * 
	 * @return The bundles. The returned list is unmodifiable.
	 */
    List<FeatureBundle> getBundles();

    /**
	 * Get the configurations. The iteration order of the returned map should
	 * follow the definition order of the configurations in the feature.
	 * 
	 * @return The configurations. The returned map is unmodifiable.
	 */
    Map<String, FeatureConfiguration> getConfigurations();

    /**
	 * Get the extensions. The iteration order of the returned map should follow
	 * the definition order of the extensions in the feature.
	 * 
	 * @return The extensions. The returned map is unmodifiable.
	 */
    Map<String, FeatureExtension> getExtensions();

    /**
	 * Get the variables. The iteration order of the returned map should follow
	 * the definition order of the variables in the feature. Values are of type:
	 * String, Boolean or BigDecimal for numbers. The {@code null} JSON value is
	 * represented by a null value in the map.
	 * 
	 * @return The variables. The returned map is unmodifiable.
	 */
	Map<String,Object> getVariables();
}

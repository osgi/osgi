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

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A builder for Feature Model {@link FeatureConfiguration} objects.
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureConfigurationBuilder {

    /**
	 * Add a configuration value for this Configuration object. If a value with
	 * the same key was previously provided (regardless of case) the previous
	 * value is overwritten.
	 *
	 * @param key The configuration key.
	 * @param value The configuration value. Acceptable data types are the data
	 *            type supported by the Configuration Admin service, which are
	 *            the Primary Property Types as defined for the Filter Syntax in
	 *            the OSGi Core specification.
	 * @throws IllegalArgumentException if the value is of an invalid type.
	 * @return This builder.
	 */
    FeatureConfigurationBuilder addValue(String key, Object value);

    /**
	 * Add a map of configuration values for this Configuration object. Values
	 * will be added to any previously provided configuration values. If a value
	 * with the same key was previously provided (regardless of case) the
	 * previous value is overwritten.
	 *
	 * @param configValues The map of configuration values to add. Acceptable
	 *            value types are the data type supported by the Configuration
	 *            Admin service, which are the Primary Property Types as defined
	 *            for the Filter Syntax in the OSGi Core specification.
	 * @throws IllegalArgumentException if a value is of an invalid type or if
	 *             the same key is provided in different capitalizations
	 *             (regardless of case).
	 * @return This builder.
	 */
    FeatureConfigurationBuilder addValues(Map<String, Object> configValues);

    /**
     * Build the Configuration object. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Configuration.
     */
    FeatureConfiguration build();
}

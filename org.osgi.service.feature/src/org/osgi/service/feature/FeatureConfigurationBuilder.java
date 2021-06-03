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
     * Add a configuration value for this Configuration object. If a
     * value with the same key was previously provided the previous value is
     * overwritten.
     * @param key The configuration key.
     * @param value The configuration value. Acceptable data types are: TODO list
     * @return This builder.
     */
    FeatureConfigurationBuilder addValue(String key, Object value);

    /**
     * Add a map of configuration values for this Configuration object. All values
     * will be added to any previously provided configuration values.
     * @param cfg
     * @return This builder.
     */
    FeatureConfigurationBuilder addValues(Map<String, Object> cfg);

    /**
     * Build the Configuration object. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Configuration.
     */
    FeatureConfiguration build();
}

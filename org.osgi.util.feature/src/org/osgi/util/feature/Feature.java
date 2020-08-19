/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.util.feature;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The Feature Model Feature.
 * @ThreadSafe
 */
@ProviderType
public interface Feature extends FeatureArtifact {
    /**
     * Get the name.
     * @return The name.
     */
    String getName();

    /**
     * Get the description.
     * @return The description.
     */
    String getDescription();

    /**
     * Get the vendor.
     * @return The vendor.
     */
    String getVendor();

    /**
     * Get the license.
     * @return The license.
     */
    String getLicense();

    /**
     * Get whether the feature is complete or not.
     * @return Completeness value.
     */
    boolean isComplete();

    /**
     * Get whether the feature is final or not.
     * @return Final value.
     */
    boolean isFinal();

    /**
     * Get the bundles.
     * @return The bundles.
     */
    List<FeatureBundle> getBundles();

    /**
     * Get the configurations.
     * @return The configurations.
     */
    Map<String, FeatureConfiguration> getConfigurations();

    /**
     * Get the extensions.
     * @return The extensions.
     */
    Map<String, FeatureExtension> getExtensions();

    /**
     * Get the variables.
     * @return The variables.
     */
    Map<String, String> getVariables();

	// TODO
    // add prototype
    // add requirements
    // add capabilities
    // add framework properties

}

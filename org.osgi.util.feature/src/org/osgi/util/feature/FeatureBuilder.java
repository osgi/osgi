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

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A builder for {@link Feature} Models.
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureBuilder {

    /**
     * Set the Feature Name.
     * @param name The Name.
     * @return This builder.
     */
    FeatureBuilder setName(String name);

    /**
     * Set the Vendor.
     * @param vendor The Vendor.
     * @return This builder.
     */
    FeatureBuilder setVendor(String vendor);

    /**
     * Set the License.
     * @param license The License.
     * @return This builder.
     */
    FeatureBuilder setLicense(String license); // TODO do we need this one?

    /**
     * Set the Feature Complete flag.
     * @param complete If the feature is complete.
     * @return This builder.
     */
    FeatureBuilder setComplete(boolean complete);

    /**
     * Set the Feature is Final flag.
     * @param isFinal If the feature is final.
     * @return This builder.
     */
    FeatureBuilder setFinal(boolean isFinal);

    /**
     * Set the Feature Description.
     * @param description The description.
     * @return This builder.
     */
    FeatureBuilder setDescription(String description);

    /**
     * Add Bundles to the Feature.
     * @param bundles The Bundles to add.
     * @return This builder.
     */
    FeatureBuilder addBundles(FeatureBundle ... bundles);

    /**
     * Add Configurations to the Feature.
     * @param configs The Configurations to add.
     * @return This builder.
     */
    FeatureBuilder addConfigurations(FeatureConfiguration ... configs);

    /**
     * Add Extensions to the Feature
     * @param extensions The Extensions to add.
     * @return This builder.
     */
    FeatureBuilder addExtensions(FeatureExtension ... extensions);

    /**
     * Add a variable to the Feature. If a variable with the specified key
     * already exists it is replaced with this one.
     * @param key The key.
     * @param defaultValue The default value.
     * @return This builder.
     */
    FeatureBuilder addVariable(String key, String defaultValue);

    /**
     * Add a map of variables to the Feature
     * @param variables to be added.
     * @return This builder.
     */
    FeatureBuilder addVariables(Map<String, String> variables);

    /**
     * Build the Feature. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Feature.
     */
    Feature build();
}

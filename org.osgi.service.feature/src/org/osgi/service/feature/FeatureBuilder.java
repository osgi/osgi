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
 * A builder for {@link Feature} Models.
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureBuilder {
	/**
	 * Set the Feature Complete flag. If this method is not called the complete
	 * flag defaults to {@code false}.
	 * 
	 * @param complete If the feature is complete.
	 * @return This builder.
	 */
	FeatureBuilder setComplete(boolean complete);

	/**
	 * Set the Feature Description.
	 * 
	 * @param description The description.
	 * @return This builder.
	 */
	FeatureBuilder setDescription(String description);

	/**
	 * Set the documentation URL.
	 * 
	 * @param docURL The Documentation URL.
	 * @return This builder.
	 */
	FeatureBuilder setDocURL(String docURL);

    /**
     * Set the Feature Name.
     * @param name The Name.
     * @return This builder.
     */
    FeatureBuilder setName(String name);

    /**
     * Set the License.
     * @param license The License.
     * @return This builder.
     */
	FeatureBuilder setLicense(String license);

	/**
	 * Set the SCM information.
	 * 
	 * @param scm The SCM information.
	 * @return This builder.
	 */
	FeatureBuilder setSCM(String scm);

    /**
	 * Set the Vendor.
	 * 
	 * @param vendor The Vendor.
	 * @return This builder.
	 */
	FeatureBuilder setVendor(String vendor);

    /**
     * Add Bundles to the Feature.
     * @param bundles The Bundles to add.
     * @return This builder.
     */
    FeatureBuilder addBundles(FeatureBundle ... bundles);

    /**
	 * Adds one or more categories to the Feature.
	 *
	 * @param categories The Categories.
	 * @return This builder.
	 */
	FeatureBuilder addCategories(String... categories);

	/**
	 * Add Configurations to the Feature.
	 *
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
	 * already exists it is replaced with this one. Variable values are of type:
	 * String, Boolean or BigDecimal for numbers.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return This builder.
	 * @throws IllegalArgumentException if the value is of an invalid type.
	 */
	FeatureBuilder addVariable(String key, Object defaultValue);

    /**
	 * Add a map of variables to the Feature. Pre-existing variables with the
	 * same key in are overwritten if these keys exist in the map. Variable
	 * values are of type: String, Boolean or BigDecimal for numbers.
	 * 
	 * @param variables to be added.
	 * @return This builder.
	 * @throws IllegalArgumentException if a value is of an invalid type.
	 */
	FeatureBuilder addVariables(Map<String,Object> variables);

    /**
     * Build the Feature. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Feature.
     */
    Feature build();
}

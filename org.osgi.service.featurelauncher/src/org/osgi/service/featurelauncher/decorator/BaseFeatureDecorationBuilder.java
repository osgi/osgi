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

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.FeatureConfiguration;

/**
 * The {@link BaseFeatureDecorationBuilder} is used to allow a user to customize
 * a {@link Feature}. It is pre-populated with data from the original
 * {@link Feature}, and calling any of the setXXX methods will replace the data
 * in that section.
 * 
 * @param <T> the type of the FeatureDecorator, used to parameterize the builder
 *            return values
 */
@ProviderType
public interface BaseFeatureDecorationBuilder<T extends BaseFeatureDecorationBuilder<T>> {

	/**
	 * Replace the bundles in the Feature, discarding the current values.
	 * 
	 * @param bundles The Bundles to add.
	 * @return This builder.
	 */
	T setBundles(List<FeatureBundle> bundles);

	/**
	 * Replace the Configurations in the Feature, discarding the current values.
	 *
	 * @param configs The Configurations to add.
	 * @return This builder.
	 */
	T setConfigurations(List<FeatureConfiguration> configs);

    /**
	 * Set or replace a single variable in the Feature. If a variable with the
	 * specified key already exists it is replaced with this one. Variable
	 * values are of type: String, Boolean or BigDecimal for numbers.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return This builder.
	 * @throws IllegalArgumentException if the value is of an invalid type.
	 */
	T setVariable(String key, Object defaultValue);

    /**
	 * Replace all the variables in the Feature, discarding the current values.
	 * Variable values are of type: String, Boolean or BigDecimal for numbers.
	 * 
	 * @param variables to be added.
	 * @return This builder.
	 * @throws IllegalArgumentException if a value is of an invalid type.
	 */
	T setVariables(Map<String,Object> variables);

    /**
	 * Build the Feature. Can only be called once. After calling this method the
	 * current builder instance cannot be used any more. and all methods will
	 * throw {@link IllegalStateException}.
	 * 
	 * @return The Feature.
	 */
    Feature build();
}
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
 * A builder for (@link FeatureRequirement) objects. Instances are obtained from
 * the {@link BuilderFactory}.
 * 
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureRequirementBuilder {
	/**
	 * Add an attribute to the requirement.
	 * 
	 * @param key The key.
	 * @param val The value.
	 * @return The builder.
	 */
	FeatureRequirementBuilder addAttribute(String key, Object val);

	/**
	 * Add a map of attributes to the requirement.
	 * 
	 * @param attributes The attributes to add.
	 * @return The builder.
	 */
	FeatureRequirementBuilder addAttributes(Map<String,Object> attributes);

	/**
	 * Add a directive to the requirement.
	 * 
	 * @param key The key.
	 * @param val The value.
	 * @return The builder.
	 */
	FeatureRequirementBuilder addDirective(String key, String val);

	/**
	 * Add a map of directives to the requirement.
	 * 
	 * @param directives The directives to add.
	 * @return The builder.
	 */
	FeatureRequirementBuilder addDirectives(Map<String,String> directives);

	/**
	 * Build the FeatureRequirement object. Can only be called once on a
	 * builder. After calling this method the current builder instance cannot be
	 * used any more.
	 * 
	 * @return The requirement.
	 */
	FeatureRequirement build();
}

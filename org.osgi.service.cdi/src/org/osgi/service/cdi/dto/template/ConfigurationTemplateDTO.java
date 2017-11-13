/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.service.cdi.dto.template;

/**
 * A description of a configuration dependency of a component
 *
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 * Singleton mandatory configurations are <b>static</b> with cardinality
 * <code>1..1</code>:
 * <ul>
 * <li>{@link DependencyTemplateDTO#minimumCardinality minimumCardinality} = 1</li>
 * <li>{@link DependencyTemplateDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyTemplateDTO.MaximumCardinality#ONE ONE}</li>
 * <li>{@link DependencyTemplateDTO#dynamic dynamic} = false</li>
 * <li>{@link DependencyTemplateDTO#greedy greedy} = true</li>
 * </ul>
 * <p>
 * Singleton optional configurations are <b>static</b> with cardinality
 * <code>0..1</code>:
 * <ul>
 * <li>{@link DependencyTemplateDTO#minimumCardinality minimumCardinality} = 0</li>
 * <li>{@link DependencyTemplateDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyTemplateDTO.MaximumCardinality#ONE ONE}</li>
 * <li>{@link DependencyTemplateDTO#dynamic dynamic} = false</li>
 * <li>{@link DependencyTemplateDTO#greedy greedy} = true</li>
 * </ul>
 * <p>
 * Factory configurations are <b>dynamic</b> with cardinality <code>0..N</code>:
 * <ul>
 * <li>{@link DependencyTemplateDTO#minimumCardinality minimumCardinality} = 0</li>
 * <li>{@link DependencyTemplateDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyTemplateDTO.MaximumCardinality#MANY MANY}</li>
 * <li>{@link DependencyTemplateDTO#dynamic dynamic} = true</li>
 * <li>{@link DependencyTemplateDTO#greedy greedy} = true</li>
 * </ul>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ConfigurationTemplateDTO extends DependencyTemplateDTO {
	/**
	 * The pid of the tracked configuration objects
	 * <p>
	 * The value must not be null.
	 */
	public String pid;
}

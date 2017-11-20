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

package org.osgi.service.cdi.dto;

import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.service.cdi.dto.template.ComponentTemplateDTO;
import org.osgi.service.cdi.dto.template.ComponentTemplateDTO.Type;
import org.osgi.service.cdi.dto.template.ConfigurationTemplateDTO;
import org.osgi.service.cdi.dto.template.MaximumCardinality;

/**
 * A snapshot of the runtime state of a component lifecycle.
 * <p>
 * A component lifecycle maintains the binding of {@link ComponentDTO component
 * instances} to the {@link #configurations configuration dependencies}
 * described by one {@link ComponentTemplateDTO component model}
 * <p>
 * When the referenced {@link ComponentTemplateDTO} has type
 * {@link Type#APPLICATION} this factory can have <code>0..N</code>
 * {@link ConfigurationDTO} with {@link MaximumCardinality#ONE}.
 * <p>
 * When the referenced {@link ComponentTemplateDTO} has type
 * {@link Type#COMPONENT} this factory can have <code>0..N</code>
 * {@link ConfigurationDTO} with {@link MaximumCardinality#ONE} and
 * <code>0..1</code> {@link ConfigurationDTO} with
 * {@link MaximumCardinality#MANY}.
 * <p>
 * When all configuration dependencies managed by this factory become satisfied
 * this factory will have:
 * <ul>
 * <li><code>0..N</code> {@link ConfigurationDTO} each with <code>0..1</code>
 * {@link ConfigurationDTO#matches matching configuration} called singleton
 * configurations</li>
 * <li><code>0..1</code> {@link ConfigurationDTO} with <code>0..N</code>
 * {@link ConfigurationDTO#matches matching configurations} called the factory
 * configuration</li>
 * </ul>
 * <p>
 * The binding of configurations to component instances is maintained as
 * follows:
 * <ol>
 * <li>For every matching {@link Map} of the factory
 * {@link ConfigurationDTO}</li>
 * <li>Add the matching {@link Map Maps} of all singleton
 * {@link ConfigurationDTO}</li>
 * <li>Build one {@link ComponentDTO} that holds the
 * {@link ComponentDTO#properties merged set}</li>
 * </ol>
 * <p>
 * Therefore when all configuration dependencies are satisfied:
 * <ul>
 * <li>For component {@link Type#APPLICATION} there will always be
 * <code>1</code> {@link ComponentDTO}</li>
 * <li>For component {@link Type#COMPONENT} without a factory configuration
 * there will always be <code>1</code> {@link ComponentDTO}</li>
 * <li>For component {@link Type#COMPONENT} with a factory configuration there
 * will be <code>0..N</code> {@link ComponentDTO} where <code>N</code> is equal
 * to the {@link ConfigurationDTO#matches number of matches} of the factory
 * configuration.</li></li>
 * </ul>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class LifecycleDTO extends DTO {
	/**
	 * Name of the component template used by this lifecycle
	 * <p>
	 * Must not be {@code null}
	 */
	public String				componentName;

	/**
	 * The configuration dependencies.
	 * <p>
	 * Each entry in the array corresponds to the runtime state of one of the
	 * statically defined {@link ConfigurationTemplateDTO configurations} of
	 * {@link #componentName the component} managed by this lifecycle.
	 * <p>
	 * Must never be null.
	 * <p>
	 * May contain many {@link MaximumCardinality#ONE singleton} configurations.
	 * <p>
	 * May contain at most one {@link MaximumCardinality#MANY lifecycle}
	 * configuration.
	 */
	public ConfigurationDTO[]	configurations;

	/**
	 * All components created by this lifecycle
	 * <p>
	 * Must not be null. An empty array means on components are currently created.
	 */
	public ComponentDTO[]		components;
}

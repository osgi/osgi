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

package org.osgi.service.cdi.runtime.dto;

import java.util.List;
import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO;
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO.Type;
import org.osgi.service.cdi.runtime.dto.template.ConfigurationTemplateDTO;
import org.osgi.service.cdi.runtime.dto.template.MaximumCardinality;

/**
 * A snapshot of the runtime state of a component
 * <p>
 * A component maintains a binding of {@link ComponentInstanceDTO component
 * instances} to the {@link #configurations configuration dependencies}
 * described by an associated {@link ComponentTemplateDTO component model}
 * <p>
 * When the referenced {@link ComponentTemplateDTO} has type
 * {@link Type#APPLICATION} this component can have <code>0..N</code>
 * {@link ConfigurationDTO} with {@link MaximumCardinality#ONE}.
 * <p>
 * When the referenced {@link ComponentTemplateDTO} has type
 * {@link Type#COMPONENT} this component can have <code>0..N</code>
 * {@link ConfigurationDTO} with {@link MaximumCardinality#ONE} and
 * <code>0..1</code> {@link ConfigurationDTO} with
 * {@link MaximumCardinality#MANY}.
 * <p>
 * When all configuration dependencies managed by this component become
 * satisfied this component will have:
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
 * <li>Build one {@link ComponentInstanceDTO} that holds the
 * {@link ComponentInstanceDTO#properties merged set}</li>
 * </ol>
 * <p>
 * Therefore when all configuration dependencies are satisfied:
 * <ul>
 * <li>For component {@link Type#APPLICATION} there will always be
 * <code>1</code> {@link ComponentInstanceDTO}</li>
 * <li>For component {@link Type#COMPONENT} without a factory configuration
 * there will always be <code>1</code> {@link ComponentInstanceDTO}</li>
 * <li>For component {@link Type#COMPONENT} with a factory configuration there
 * will be <code>0..N</code> {@link ComponentInstanceDTO} where <code>N</code>
 * is equal to the {@link ConfigurationDTO#matches number of matches} of the
 * factory configuration.</li>
 * </ul>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentDTO extends DTO {
	/**
	 * Name of the {@link ComponentTemplateDTO} from which this component is derived
	 * <p>
	 * Must not be {@code null}
	 */
	public String				name;

	/**
	 * The configuration dependencies of this component.
	 * <p>
	 * Each entry in the list corresponds to the runtime state of one of the
	 * statically defined {@link ConfigurationTemplateDTO configurations} of
	 * {@link #name the component} managed by this component.
	 * <p>
	 * Must not be {@code null}
	 * <p>
	 * Contains at least one {@link MaximumCardinality#ONE singleton}
	 * configurations.
	 * <p>
	 * Contains at most one {@link MaximumCardinality#MANY component} configuration.
	 */
	public List<ConfigurationDTO>		configurations;

	/**
	 * All components created by this component
	 * <p>
	 * Must not be {@code null}
	 * <p>
	 * Is empty when not all {@link #configurations} are satisfied.
	 * <p>
	 * Contains one entry when all configurations are satisfied and has only
	 * singleton configuration dependencies.
	 * <p>
	 * Contains zero or more entries when all configurations are satisfied and has a
	 * factory configuration dependency.
	 */
	public List<ComponentInstanceDTO>	instances;
}

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
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO.Type;

/**
 * A snapshot of the runtime state of a component
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentDTO extends DTO {
	/**
	 * The name of the template of this component
	 * <p>
	 * Must never be {@code null}
	 */
	public String				name;

	/**
	 * The resolved configuration properties for the component.
	 * <p>
	 * Contains the merger of all consumed configurations merged in the order of
	 * {@link #configurations their PIDs}
	 */
	public Map<String, Object>	properties;

	/**
	 * A list of the {@code service.pid} properties of all configurations consumed
	 * by this component.
	 * <p>
	 * Each PID corresponds to an item from the {@link ConfigurationDTO#matches
	 * matched list} of one of the {@link LifecycleDTO#configurations configuration
	 * dependencies} of the parent {@link LifecycleDTO}
	 */
	public List<String>			configurations;

	/**
	 * The service dependencies of the component.
	 * <p>
	 * Must not be null. The array will be empty if the component has no reference
	 * dependencies.
	 */
	public List<ReferenceDTO>	references;

	/**
	 * The activations of the component.
	 * <p>
	 * Value must not be null. The array may be empty for the
	 * {@link Type#APPLICATION APPLICATION}. The array will always contain 1 element
	 * for a {@link Type#COMPONENT COMPONENT}.
	 */
	public List<ActivationDTO>	activations;
}

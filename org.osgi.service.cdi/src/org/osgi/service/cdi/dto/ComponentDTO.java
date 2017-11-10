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
import org.osgi.service.cdi.dto.model.ComponentModelDTO.Type;
import org.osgi.service.cdi.dto.model.ConfigurationModelDTO;

/**
 * A snapshot of the runtime state of a component
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentDTO extends DTO {
	/**
	 * The service dependencies of the component.
	 * <p>
	 * Must not be null. The array will be empty if the component has no reference
	 * rependencies.
	 */
	public ReferenceDTO[]			references;

	/**
	 * The activations of the component.
	 * <p>
	 * Value must not be null. The array may be empty for the
	 * {@link Type#APPLICATION APPLICATION}. The array will always contain 1 element
	 * for a {@link Type#COMPONENT COMPONENT}.
	 */
	public ActivationDTO[]			activations;

	/**
	 * The exact set of configuration {@link Map Maps} used by this component
	 * instance.
	 * <p>
	 * <ul>
	 * <li><code>0..N</code> maps can have only <code>service.pid</code> which
	 * corresponds to the {@link ConfigurationModelDTO#pid PID} of one of the
	 * {@link ComponentLifecycleDTO#configurations configuration dependencies} of the
	 * parent {@link ComponentLifecycleDTO}</li>
	 *
	 * <li><code>0..1</code> map can have a <code>service.factoryPid</code> which
	 * corresponds to the {@link ConfigurationModelDTO#pid PID} of one of the
	 * {@link ComponentLifecycleDTO#configurations configuration dependencies} of the
	 * parent {@link ComponentLifecycleDTO}, in which case the
	 * <code>service.pid</code> of the map corresponds to one of the
	 * {@link ConfigurationDTO#matches} of that configuration dependency.</li>
	 * </ul>
	 * <p>
	 * Must not be null. The array may be empty if this component instance does not
	 * consume configurations.
	 */
	public Map<String, Object>[]	configurations;
}

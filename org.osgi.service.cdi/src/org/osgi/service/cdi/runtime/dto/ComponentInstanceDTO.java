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

package org.osgi.service.cdi.runtime.dto;

import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;

/**
 * A snapshot of the runtime state of a component.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentInstanceDTO extends DTO {

	/**
	 * The configuration dependencies of this component.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ConfigurationDTO>	configurations;

	/**
	 * The service dependencies of the component.
	 * <p>
	 * Can be empty when the component has no reference dependencies.
	 * <p>
	 * The component instance is satisfied when the sum of
	 * {@link ReferenceDTO#minimumCardinality} equals the size of
	 * {@link ReferenceDTO#matches} for each value.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ReferenceDTO>		references;

	/**
	 * The resolved configuration properties for the component.
	 * <p>
	 * Contains the merger of all consumed configurations merged in the order of
	 * {@link #configurations}.
	 * <p>
	 * All configuration dependencies are satisfied when not {@code null}.
	 */
	public Map<String, Object>		properties;

	/**
	 * The activations of the component.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ActivationDTO>	activations;
}

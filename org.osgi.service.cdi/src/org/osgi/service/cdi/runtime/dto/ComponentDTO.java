/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import org.osgi.dto.DTO;
import org.osgi.service.cdi.ComponentType;
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO;

/**
 * A snapshot of the runtime state of a component.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentDTO extends DTO {
	/**
	 * The template of this component.
	 * <p>
	 * Must not be {@code null}
	 */
	public ComponentTemplateDTO			template;

	/**
	 * The component instances created by this component.
	 * <p>
	 * <ul>
	 * <li>When {@link #template} is of type {@link ComponentType#CONTAINER} - there will
	 * be <code>1</code> {@link ComponentInstanceDTO}</li>
	 * <li>When {@link #template} is of type {@link ComponentType#SINGLE} - there will be
	 * <code>1</code> {@link ComponentInstanceDTO}</li>
	 * <li>When {@link #template} is of type {@link ComponentType#FACTORY} - there will
	 * be one {@link ComponentInstanceDTO} for every factory configuration
	 * object associated with the factory PID of the component.</li>
	 * </ul>
	 * <p>
	 * Must not be {@code null}
	 */
	public List<ComponentInstanceDTO>	instances;

	/**
	 * Indicates if the component is enabled. The default is {@code true}.
	 * <p>
	 * A setting of {@code false} on the <em>container component</em> results in
	 * all components in the bundle being disabled.
	 */
	public boolean						enabled	= true;
}

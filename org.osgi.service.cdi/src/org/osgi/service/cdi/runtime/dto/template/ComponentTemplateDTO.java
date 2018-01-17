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

package org.osgi.service.cdi.runtime.dto.template;

import java.util.List;
import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.ComponentInstanceDTO;

/**
 * A static description of a CDI component.
 * <p>
 * At runtime it is spit between a {@link ComponentInstanceDTO} which handles
 * the resolution of the configurations, references and the creation of
 * {@link ComponentInstanceDTO} instances and one or more
 * {@link ComponentInstanceDTO} instances, which handle the resolution of
 * {@link #references} and the creation of {@link #activations}.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentTemplateDTO extends DTO {

	/**
	 * Define the possible values for {@link #type}.
	 */
	public static enum Type {
		/**
		 * The component is the <em>Container Component</em>.
		 */
		CONTAINER,
		/**
		 * The component is an <em>Single Component</em>.
		 */
		SINGLE,
		/**
		 * The component is an <em>Factory Component</em>.
		 */
		FACTORY
	}

	/**
	 * A name unique within the container.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String							name;

	/**
	 * The {@link Type type} of the component.
	 * <p>
	 * Must not be {@code null}.
	 */
	public Type								type;

	/**
	 * The configuration dependencies of this component.
	 * <p>
	 * There is always at least one default singleton configuration.
	 * <p>
	 * May contain at most one factory configuration.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ConfigurationTemplateDTO>	configurations;

	/**
	 * The service dependencies of the component.
	 * <p>
	 * The list will be empty if there are no service dependencies.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ReferenceTemplateDTO>		references;

	/**
	 * The activations associated with the component.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ActivationTemplateDTO>		activations;
}

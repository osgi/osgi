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

package org.osgi.service.cdi.runtime.dto.template;

import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.ServiceScope;

/**
 * Activations represent either immediate instances or service objects produced
 * by component instances.
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationTemplateDTO extends DTO {
	/**
	 * The {@link ServiceScope} of this activation
	 * <p>
	 * Must not be {@code null}.
	 */
	public ServiceScope				scope;

	/**
	 * Describes the set of fully qualified names of the interfaces/classes
	 * under which this activation will publish and OSGi service
	 * <p>
	 * Must not be {@code null}. An empty array indicated this activation will
	 * not publish an OSGi service
	 */
	public List<String>			serviceClasses;

	/**
	 * The default properties for activations which represent container
	 * component services. This will never be populated for single or factory
	 * components.
	 * <p>
	 * These are merged (and possibly replaced) with runtime properties.
	 * <p>
	 * Must not be {@code null}. May be empty if no default properties are
	 * provided.
	 */
	public Map<String,Object>	properties;
}

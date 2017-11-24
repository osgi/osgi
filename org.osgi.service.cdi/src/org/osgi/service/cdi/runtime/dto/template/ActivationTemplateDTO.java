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
import java.util.Map;
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO.Type;

/**
 * A description an activation of a component
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 * Can be either an OSGi service publication or a creation of a bean or both in
 * case of {@link Scope#SINGLETON}.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationTemplateDTO {
	/**
	 * Possible values for {@link #scope}.
	 */
	public enum Scope {
		/**
		 * This activation will only ever create one instance
		 * <p>
		 * The instance is created after the parent component becomes satisfied and is
		 * destroyed before the parent component becomes unsatisfied.
		 * <p>
		 * If {@link #serviceClasses} is not empty the instance will be registered as an
		 * OSGi service with <code>service.scope=singleton</code>
		 */
		SINGLETON,
		/**
		 * This activation will register an OSGi service with
		 * <code>service.scope=bundle</code>
		 * <p>
		 * The service is registered just after all {@link #SINGLETON} activations are
		 * set up and just before all {@link #SINGLETON} activations are torn down.
		 * <p>
		 * The {@link #serviceClasses} is not empty when this scope is used.</code>
		 */
		BUNDLE,
		/**
		 * This activation will register an OSGi service with
		 * <code>service.scope=prototype</code>
		 * <p>
		 * The service is registered just after all {@link #SINGLETON} activations are
		 * set up and just before all {@link #SINGLETON} activations are torn down.
		 * <p>
		 * The {@link #serviceClasses} is not empty when this scope is used.</code>
		 */
		PROTOTYPE
	}

	/**
	 * A unique within the container and persistent across reboots identified for
	 * this activation
	 * <p>
	 * When {@link Type#APPLICATION type=APPLICATION} it is equal to the bean name
	 * or if one is not available it is generated from the bean attributes.
	 * <p>
	 * When {@link Type#COMPONENT type=COMPONENT} it is equal to the name of the
	 * root component bean.
	 * 
	 */
	public String				name;

	/**
	 * The {@link Scope} of this activation
	 * <p>
	 * Must not be null.
	 */
	public Scope				scope;

	/**
	 * Describes the set of fully qualified names of the interfaces/classes under
	 * which this activation will publish and OSGi service
	 * <p>
	 * Must not be null. An empty array indicated this activation will not publish
	 * an OSGi service
	 */
	public List<String>			serviceClasses;

	/**
	 * The default properties which will be used to configure this activation.
	 * <p>
	 * These will be merged with the properties of all configurations this
	 * activation is exposed to.
	 * <p>
	 * Must not be null. May be empty if this activation does not specify default
	 * properties.
	 */
	public Map<String, Object>	properties;
}

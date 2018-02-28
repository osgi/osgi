/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

package org.osgi.service.cdi;

import org.osgi.service.cdi.runtime.dto.template.ActivationTemplateDTO;

/**
 * Possible values for {@link ActivationTemplateDTO#scope}.
 *
 * @author $Id$
 */
public enum ServiceScope {
	/**
	 * This activation will only ever create one instance
	 * <p>
	 * The instance is created after the parent component becomes satisfied and
	 * is destroyed before the parent component becomes unsatisfied.
	 * <p>
	 * If {@link ActivationTemplateDTO#serviceClasses} is not empty the instance
	 * will be registered as an OSGi service with
	 * <code>service.scope=singleton</code>
	 */
	SINGLETON,
	/**
	 * This activation will register an OSGi service with
	 * <code>service.scope=bundle</code>
	 * <p>
	 * The service is registered just after all {@link #SINGLETON} activations
	 * are set up and just before all {@link #SINGLETON} activations are torn
	 * down.
	 * <p>
	 * The {@link ActivationTemplateDTO#serviceClasses} is not empty when this
	 * scope is used.</code>
	 */
	BUNDLE,
	/**
	 * This activation will register an OSGi service with
	 * <code>service.scope=prototype</code>
	 * <p>
	 * The service is registered just after all {@link #SINGLETON} activations
	 * are set up and just before all {@link #SINGLETON} activations are torn
	 * down.
	 * <p>
	 * The {@link ActivationTemplateDTO#serviceClasses} is not empty when this
	 * scope is used.</code>
	 */
	PROTOTYPE
}
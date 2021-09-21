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
package org.osgi.service.configurator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.configurator.ConfiguratorConstants;

/**
 * This annotation can be used to require the Configurator extender. It can be
 * used directly, or as a meta-annotation.
 * <p>
 * This annotation allows users to define custom locations that should be
 * searched for configuration files using {@link RequireConfigurator#value()}
 * 
 * @author $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = ExtenderNamespace.EXTENDER_NAMESPACE, //
		name = ConfiguratorConstants.CONFIGURATOR_EXTENDER_NAME, //
		version = ConfiguratorConstants.CONFIGURATOR_SPECIFICATION_VERSION)
public @interface RequireConfigurator {

	/**
	 * This attribute can be used to define one or more locations that the
	 * configurator must search, in order, for configuration files.
	 * <p>
	 * If no locations are defined then the Configurator default of
	 * <code>/OSGI-INF/configurator</code> will be used.
	 * 
	 * @return A list of bundle locations containing configuration files
	 */
	@Attribute("configurations")
	String[] value() default {};
}

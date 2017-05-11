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

package org.osgi.service.cdi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.inject.Qualifier;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used with {@link Inject} in order to have {@link Component}
 * configuration properties injected into CDI beans. Properties are a
 * combination of the Map defined by {@link Component#property()} overlaid by
 * properties provided through Configuration Admin in association with the
 * configuration PIDs defined by {@link Component#value()}.
 */
@Qualifier
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
public @interface Configuration {

	/**
	 * The configuration policy of this Configuration.
	 *
	 * <p>
	 * Controls whether component configurations must be satisfied depending on the
	 * presence of a corresponding Configuration object in the OSGi Configuration
	 * Admin service. Corresponding configurations are Configuration objects where
	 * the PIDs equal {@link Configuration#value() value}, or the
	 * {@link Component#name() name} of the component if no values are specified.
	 *
	 * <p>
	 * If not specified, the configuration policy is
	 * {@link ConfigurationPolicy#OPTIONAL OPTIONAL}
	 */
	ConfigurationPolicy configurationPolicy() default ConfigurationPolicy.DEFAULT;

	/**
	 * The configuration PIDs for the configuration of this Component.
	 *
	 * <p>
	 * Each value specifies a configuration PID for this Component.
	 *
	 * <p>
	 * If no value is specified, the name of this Component is used as the
	 * configuration PID of this Component.
	 *
	 * <p>
	 * A special string (<code>{@value #NAME}</code>) can be used to specify the
	 * name of the component as a configuration PID. The {@code NAME} constant holds
	 * this special string. For example:
	 *
	 * <pre>
	 * &#64;Configuration({"com.acme.system", Component.NAME})
	 * </pre>
	 */
	@Nonbinding
	String[] value() default NAME;

	/**
	 * Special string representing the name of this Component.
	 *
	 * <p>
	 * This string can be used in {@link #value()} to specify the name of the
	 * component as a configuration PID. For example:
	 *
	 * <pre>
	 * &#64;Configuration({"com.acme.system", Component.NAME})
	 * </pre>
	 */
	String NAME = "$";

}

/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.context.Dependent;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Identify the annotated CDI bean class as a Service Component.
 *
 * @author $Id$
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
public @interface Component {
	/**
	 * The name of this Component.
	 *
	 * <p>
	 * If not specified, the name of this Component is the fully qualified type
	 * name of the class being annotated.
	 */
	String name() default "";

	/**
	 * The types under which to register this Component as a service.
	 *
	 * <p>
	 * The result of this value depends on the value of
	 * {@link Component#scope() scope}.
	 * <ol>
	 * <li>If {@code scope} is any value besides {@link ServiceScope#NONE
	 * NONE} and service <em>is NOT</em> specified: the component is published using
	 * all directly implemented interfaces. If there are no directly implemented
	 * interfaces, the class of the Component is used.
	 * <li>If {@code scope} is any value besides {@link ServiceScope#NONE
	 * NONE} and service <em>IS</em> specified: the component is published using the
	 * specified types.
	 * <li>If {@code scope} is {@link ServiceScope#NONE}: the value is
	 * ignored and the component is not published as a service.
	 * <ol>
	 */
	Class<?>[] service() default {};

	/**
	 * The service scope used for the Component.
	 *
	 * <p>
	 * If not specified the Component will not be published as a service.
	 * <p>
	 * In order to be published as an OSGi service the component bean must use the
	 * CDI scope {@link Dependent}. The use of any other CDI scope will result in a
	 * definition error.
	 */
	ServiceScope scope() default ServiceScope.DEFAULT;

	/**
	 * Properties for this Component.
	 * <p>
	 * Each property string is specified as {@code "name=value"}. The type of
	 * the property value can be specified in the name as
	 * {@code name:type=value}. The type must be one of the property types
	 * supported by the {@code type} attribute of the {@code property} element
	 * of a Component description.
	 * <p>
	 * To specify a property with multiple values, use multiple name, value
	 * pairs. For example, {@code {"foo=bar", "foo=baz"}}.
	 */
	String[] property() default {};

}

/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated class as a Service Component.
 * 
 * <p>
 * The annotated class is the implementation class of the Component.
 * 
 * <p>
 * This annotation is not processed at runtime by a Service Component Runtime
 * implementation. It must be processed by tools and used to add a Component
 * Description to the bundle.
 * 
 * @see "The component element of a Component Description."
 * @version $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Component {
	/**
	 * Name of this Component.
	 * 
	 * <p>
	 * If not specified, the name of this Component is the fully qualified type
	 * name of the class being annotated.
	 * 
	 * @see "The name attribute of the component element of a Component Description."
	 */
	String name() default "";

	/**
	 * Provided service types for this Component.
	 * 
	 * <p>
	 * If not specified, the provided service types for this Component is all
	 * directly implemented interfaces of the class being annotated.
	 * 
	 * TODO What if you don't want the component to register a service? We need
	 * some way to say that. We should probably define unspecified to mean no
	 * service and the special value of "*" to mean all directly implemented
	 * interfaces.
	 * 
	 * @see "The interface attribute of the provide element of a Component Description."
	 */
	Class< ? >[] provide() default {};

	/**
	 * Declare this Component to be a Factory Component.
	 * 
	 * <p>
	 * The value is the factory identifier. If not specified, the default is
	 * that this Component is not a Factory Component.
	 * 
	 * @see "The factory attribute of the component element of a Component Description."
	 */
	String factory() default "";

	/**
	 * Declare whether this Component uses the OSGi ServiceFactory concept and
	 * each bundle using this Component's service will receive a different
	 * component instance.
	 * 
	 * <p>
	 * If {@code true}, this Component uses the OSGi ServiceFactory concept. If
	 * {@code false} or not specified, this Component does not use the OSGi
	 * ServiceFactory concept.
	 * 
	 * @see "The servicefactory attribute of the service element of a Component Description."
	 */
	boolean servicefactory() default false;

	/**
	 * Declares whether this Component is enabled when the bundle containing it
	 * is started.
	 * 
	 * <p>
	 * If {@code true}, this Component is enabled. If {@code false} or not
	 * specified, this Component is disabled.
	 * 
	 * @see "The enabled attribute of the component element of a Component Description."
	 */
	boolean enabled() default true;

	/**
	 * Declares whether this Component must be immediately activated upon
	 * becoming satisfied or whether activation should be delayed.
	 * 
	 * <p>
	 * If {@code true}, this Component must be immediately activated upon
	 * becoming satisfied. If {@code false} or not specified, activation of this
	 * Component is delayed.
	 * 
	 * TODO The default value should be false if factory or service is set and
	 * true otherwise. But we can tell the difference between false being
	 * specified and nothing specified!
	 * 
	 * @see "The immediate attribute of the component element of a Component Description."
	 */
	boolean immediate() default false;

	/**
	 * Controls whether component configurations must be satisfied depending on
	 * the presence of a corresponding Configuration object in the OSGi
	 * Configuration Admin service. A corresponding configuration is a
	 * Configuration object where the PID is the name of the component.
	 * 
	 * <p>
	 * If not specified, the {@link ConfigurationPolicy#OPTIONAL OPTIONAL}
	 * configuration policy is used.
	 * 
	 * @see "The configuration-policy attribute of the component element of a Component Description."
	 */
	ConfigurationPolicy configurationPolicy() default ConfigurationPolicy.OPTIONAL;

	/**
	 * Component property.
	 * 
	 * <p>
	 * Each property string is specified as {@code "key=value"}. The type of the
	 * property value can be specified in the key as {@code key:type=value}. The
	 * type must be one of the property types supported by the type attribute of
	 * the property element of a Component Description. The value can contain
	 * multiple values, the parts must then be separated by a vertical bar (
	 * {@code '|'}) or a line feed ({@code '\n'}). For example
	 * {@code "primes:Integer=1|2|3|5|7|11"}.
	 * 
	 * TODO We need to allow escaping for | and \n since we should also allow
	 * multi valued String and Character properties. An alternate would be to
	 * allow a property to be specified multiple times.
	 * 
	 * @see "The property element of a Component Description."
	 */
	String[] property() default {};

	/**
	 * Component properties from bundle entries.
	 * 
	 * <p>
	 * Specifies the name of an entry in the bundle whose contents conform to a
	 * standard Java Properties File. The entry is read and processed to obtain
	 * the properties and their values.
	 * 
	 * @see "The properties element of a Component Description."
	 */
	String[] properties() default {};

	/**
	 * TODO How does this affect the generated component description? I think we
	 * need to leave this out unless also do the metatype annotations. There
	 * should be a separate annotation for this.
	 */
	Class< ? > designate() default Object.class;

	/**
	 * TODO How does this affect the generated component description? I think we
	 * need to leave this out unless also do the metatype annotations. There
	 * should be a separate annotation for this.
	 */
	Class< ? > designateFactory() default Object.class;
}

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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.cdi.CdiConstants.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import org.osgi.annotation.bundle.Requirement;

/**
 * Identify the annotated CDI bean class as a Service Component.
 *
 * @author $Id$
 */
@ComponentScoped
@Documented
@Named
@Requirement(namespace = EXTENDER_NAMESPACE, name = CDI_CAPABILITY_NAME, version = CDI_SPECIFICATION_VERSION)
@Retention(RUNTIME)
@Stereotype
@Target(TYPE)
public @interface Component {

	/**
	 * Support inline instantiation of the {@link Component} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Component> implements Component {

		/**
		 * Default instance.
		 */
		public static final Component	INSTANCE			= new Literal();

		private static final long		serialVersionUID	= 1L;

	}

	/**
	 * Special string representing the name of this Component.
	 *
	 * <p>
	 * This string can be used in {@link PID#value()} OR
	 * {@link FactoryPID#value()} to specify the name of the component or in
	 * the case of the non-components the CDI container id as a configuration PID.
	 * For example:
	 *
	 * <pre>
	 * &#64;SingletonConfiguration(pid = Component.NAME)
	 * </pre>
	 */
	String NAME = "$";

}

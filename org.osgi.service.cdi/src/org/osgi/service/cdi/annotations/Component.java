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
import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Identify the annotated CDI bean class as a Service Component.
 *
 * @author $Id$
 */
@ComponentScoped
@Named
@Retention(value = RetentionPolicy.RUNTIME)
@Stereotype
@Target(value = ElementType.TYPE)
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
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
	 * This string can be used in {@link SingletonConfiguration#pid()} OR
	 * {@link FactoryConfiguration#pid()} to specify the name of the component or in
	 * the case of the non-components the CDI container id as a configuration PID.
	 * For example:
	 *
	 * <pre>
	 * &#64;SingletonConfiguration(pid = Component.NAME)
	 * </pre>
	 */
	String NAME = "$";

}

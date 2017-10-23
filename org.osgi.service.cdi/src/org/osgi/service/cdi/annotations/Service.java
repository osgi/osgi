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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Singleton;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used to specify that a CDI bean should be published as a service.
 * <p>
 * There are two use cases for this annotation:
 * <ul>
 * <li>Applied to component bean - No special cases apply in this scenario.</li>
 * <li>Applied to non-component bean - In this scenario only beans which are
 * {@link ApplicationScoped} or {@link Singleton} are allowed. Use of other
 * scopes will result in a definition error. Also in this scenario the default
 * {@link ServiceScope} behaviour will be {@link ServiceScopes#SINGLETON
 * SINGLETON}. Use of other service scopes will result in a definition
 * error.</li>
 * </ul>
 *
 * @author $Id$
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
public @interface Service {

	/**
	 * Support inline instantiation of the {@link Component} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Service> implements Service {

		private static final long serialVersionUID = 1L;

		/**
		 * @param service an array of types under which to publish the service
		 * @return an instance of {@link Service}
		 */
		public static Literal of(Class<?>[] service) {
			return new Literal(service);
		}

		private Literal(Class<?>[] service) {
			_service = service;
		}

		@Override
		public Class<?>[] value() {
			return _service;
		}

		private final Class<?>[]	_service;

	}

	/**
	 * The types under which to register this Component as a service.
	 *
	 * <ul>
	 * <li>If no value, or an empty array is specified: the bean is published using
	 * all directly implemented interfaces. If there are no directly implemented
	 * interfaces, the bean class is used.
	 * <li>If any other value is specified: the component is published using the
	 * specified values.
	 * <ul>
	 */
	Class<?>[] value() default {};

}

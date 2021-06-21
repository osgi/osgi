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

package org.osgi.service.cdi.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;

/**
 * Annotation used to specify that a bean should be published as a service.
 * <p>
 * The behavior of this annotation depends on it's usage:
 * <ul>
 * <li>on the bean type - publish the service using all implemented interfaces.
 * If there are no implemented interfaces use the bean class.</li>
 * <li>on the bean's type_use(s) - publish the service using the collected
 * interface(s).</li>
 * </ul>
 * Use of {@code @Service} on both type and type_use will result in a definition
 * error.
 * <p>
 * Where this annotation is used affects how service scopes are supported:
 * <ul>
 * <li>{@link SingleComponent @SingleComponent},
 * {@link FactoryComponent @FactoryComponent} or {@link Dependent @Dependent}
 * bean - The provided service can be of any scope. The bean can either
 * implement {@link ServiceFactory ServiceFactory} or
 * {@link PrototypeServiceFactory PrototypeServiceFactory} or use
 * {@link Bundle @Bundle} or {@link PrototypeRequired @Prototype} to set it's
 * service scope. If none of those options are used the service is a singleton
 * scope service.</li>
 * <li>{@link ApplicationScoped @ApplicationScoped} bean - The provided service
 * is a singleton scope service unless the bean implements {@link ServiceFactory
 * ServiceFactory} or {@link PrototypeServiceFactory PrototypeServiceFactory}.
 * It cannot use {@link Bundle @Bundle} or {@link PrototypeRequired @Prototype}
 * to set it's service scope. Use of those annotations in this case will result
 * in a definition error.</li>
 * </ul>
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, TYPE, TYPE_USE})
public @interface Service {

	/**
	 * Support inline instantiation of the {@link Service} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Service>
			implements Service {

		private static final long serialVersionUID = 1L;

		/**
		 * @param interfaces
		 * @return instance of {@link Service}
		 */
		public static final Literal of(Class<?>[] interfaces) {
			return new Literal(interfaces);
		}

		private Literal(Class<?>[] interfaces) {
			_interfaces = interfaces;
		}

		@Override
		public Class<?>[] value() {
			return _interfaces;
		}

		private final Class<?>[] _interfaces;
	}

	/**
	 * Override the interfaces under which this service is published.
	 *
	 * @return the service types
	 */
	@Nonbinding
	Class<?>[] value() default {};

}

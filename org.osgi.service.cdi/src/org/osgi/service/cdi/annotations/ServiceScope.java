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
import javax.enterprise.util.AnnotationLiteral;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used to specify that a CDI bean to describe the scope of the
 * service. Used in conjunction with {@link Service}.
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
public @interface ServiceScope {

	/**
	 * Support inline instantiation of the {@link Component} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ServiceScope> implements ServiceScope {

		private static final long serialVersionUID = 1L;

		/**
		 * @param scope the scope of the service
		 * @return an instance of {@link Service}
		 */
		public static Literal of(ServiceScopes scope) {
			return new Literal(scope);
		}

		private Literal(ServiceScopes scope) {
			_scope = scope;
		}

		@Override
		public ServiceScopes value() {
			return _scope;
		}

		private final ServiceScopes _scope;

	}

	/**
	 * The service scope used for the service.
	 *
	 * <p>
	 * If not specified the bean will be published as a
	 * {@link ServiceScopes#SINGLETON SINGLETON} service.
	 * <p>
	 */
	ServiceScopes value() default ServiceScopes.NOT_SPECIFIED;

}

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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.service.cdi.CdiConstants.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import org.osgi.annotation.bundle.Requirement;

/**
 * Annotation used to annotate a CDI injection point informing the CDI container
 * that the injection should apply a service obtained from the OSGi registry.
 * <p>
 * This annotation must be used in conjunction with {@link javax.inject.Named}
 * which must specify a value.
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Requirement(namespace = EXTENDER_NAMESPACE, name = CDI_CAPABILITY_NAME, version = CDI_SPECIFICATION_VERSION)
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface Reference {

	/**
	 * Support inline instantiation of the {@link Reference} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Reference> implements Reference {

		private static final long serialVersionUID = 1L;

		/**
		 * @param service
		 * @return instance of {@link Reference}
		 */
		public static final Literal of(Class<?> service) {
			return new Literal(service);
		}

		private Literal(Class<?> service) {
			_service = service;
		}

		@Override
		public Class<?> service() {
			return _service;
		}

		private final Class<?>				_service;

	}

	/**
	 * Specify the type of the service for this reference.
	 * <p>
	 * If not specified, the type of the service for this reference is derived from
	 * the injection point type.
	 * <p>
	 * Finally, if {@code service} is specified it must be type compatible with
	 * (assignable to) the service type derived from the injection point type.
	 */
	Class<?> service() default Object.class;

}

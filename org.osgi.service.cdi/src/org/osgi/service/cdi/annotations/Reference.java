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
 * Annotation used on injection points informing the CDI container that the
 * injection should apply a service obtained from the OSGi registry.
 * <p>
 * The {@link javax.inject.Named} annotation may be used to specify a name which
 * is the root of properties used to configure the behavior of the reference. If
 * not specified the name of the reference will be derived from the fully
 * qualifier class name plus the injection member's name.
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
		 * @param target
		 * @return instance of {@link Reference}
		 */
		public static final Literal of(
				Class<?> service,
				String target) {

			return new Literal(service, target);
		}

		private Literal(
				Class<?> service,
				String target) {
			_service = service;
			_target = target;
		}

		@Override
		public Class<?> value() {
			return _service;
		}

		@Override
		public String target() {
			return _target;
		}

		private final Class<?>				_service;
		private final String				_target;

	}

	/**
	 * Specify the type of the service for this reference.
	 * <p>
	 * If not specified, the type of the service for this reference is derived from
	 * the injection point type.
	 * <p>
	 * If a value is specified it must be type compatible with (assignable to) the
	 * service type derived from the injection point type, otherwise a definition
	 * error will result.
	 */
	Class<?> value() default Object.class;

	/**
	 * The target property for this reference.
	 *
	 * <p>
	 * If not specified, no target property is set.
	 */
	String target() default "";

}

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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

import org.osgi.service.cdi.ServiceScope;

/**
 * Annotation used on beans, observer methods and observer fields to specify the
 * service scope for the service. Used in conjunction with {@link Service}.
 *
 * @author $Id$
 */
@Documented
@Retention(RUNTIME)
@Target({
		TYPE, FIELD, METHOD
})
public @interface ServiceInstance {

	/**
	 * Support inline instantiation of the {@link ServiceInstance} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ServiceInstance>
			implements ServiceInstance {

		private static final long serialVersionUID = 1L;

		/**
		 * @param type the type of the ServiceInstance
		 * @return an instance of {@link ServiceInstance}
		 */
		public static Literal of(ServiceScope type) {
			return new Literal(type);
		}

		private Literal(ServiceScope type) {
			this.type = type;
		}

		@Override
		public ServiceScope value() {
			return type;
		}

		private final ServiceScope type;

	}

	/**
	 * The scope of the service.
	 */
	ServiceScope value() default ServiceScope.SINGLETON;

}

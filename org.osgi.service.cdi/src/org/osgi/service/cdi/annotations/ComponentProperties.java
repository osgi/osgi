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
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Qualifier;

/**
 * Annotation used with {@link Inject} in order to have component properties
 * injected.
 * <p>
 * See "Component Properties".
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface ComponentProperties {

	/**
	 * Support inline instantiation of the {@link ComponentProperties} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ComponentProperties>
			implements ComponentProperties {

		/**
		 * Default instance.
		 */
		public static final ComponentProperties	INSTANCE			= new Literal();

		private static final long			serialVersionUID	= 1L;

	}

}

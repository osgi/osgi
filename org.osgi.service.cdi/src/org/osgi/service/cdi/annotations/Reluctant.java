/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.osgi.service.cdi.reference.BindBeanServiceObjects;
import org.osgi.service.cdi.reference.BindService;
import org.osgi.service.cdi.reference.BindServiceReference;

/**
 * Annotation used to indicate that the behavior of the reference should be
 * reluctant. Used in conjunction with {@link Reference @Reference},
 * {@link BindService}, {@link BindServiceReference} or
 * {@link BindBeanServiceObjects}.
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface Reluctant {

	/**
	 * Support inline instantiation of the {@link Reluctant} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Reluctant> implements Reluctant {

		/**
		 * Default instance
		 */
		public static final Reluctant	INSTANCE			= new Literal();

		private static final long			serialVersionUID	= 1L;

	}

}

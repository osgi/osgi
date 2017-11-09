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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * Annotation used in collaboration with {@link Reference} to specify a filter
 * used to target specific services. It can also be used with
 * {@link org.osgi.service.cdi.reference.AddingEvent}.
 *
 * @author $Id$
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface Filter {

	/**
	 * Support inline instantiation of the {@link Filter}
	 * annotation.
	 */
	public static final class Literal extends AnnotationLiteral<Filter> implements Filter {

		private static final long serialVersionUID = 1L;

		/**
		 * @param filter the service filter string
		 * @return an instance of {@link Filter}
		 */
		public static final Literal of(String filter) {
			return new Literal(filter);
		}

		private Literal(String filter) {
			_filter = filter;
		}

		@Override
		public String value() {
			return _filter;
		}

		private final String _filter;

	}

	/**
	 * The filter string used to target services.
	 */
	String value();

}

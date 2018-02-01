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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 * Identifies a single component.
 * <p>
 * Single components MUST always be {@link ComponentScoped ComponentScoped}.
 * Applying any other scope will result in a definition error.
 *
 * @author $Id$
 * @see "Single Component"
 */
@ComponentScoped
@Documented
@Named
@RequireCDIExtender
@Retention(RUNTIME)
@Stereotype
@Target(TYPE)
public @interface SingleComponent {

	/**
	 * Support inline instantiation of the {@link SingleComponent} annotation.
	 */
	public static final class Literal extends AnnotationLiteral<SingleComponent> implements SingleComponent {

		/**
		 * Default instance.
		 */
		public static final SingleComponent	INSTANCE			= new Literal();

		private static final long		serialVersionUID	= 1L;

	}

}

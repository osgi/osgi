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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * A meta annotation that can be placed on {@link Qualifier} annotations to
 * exclude them from being used as service properties.
 * <p>
 * This annotation is processed only to check the qualifiers used together with
 * {@link Service} and {@link Reference}.
 *
 * @author $Id$
 */
@Documented
@RequireCdiComponentRuntime
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ComponentPropertyIgnore {
	/**
	 * Support inline instantiation of the {@link ComponentPropertyIgnore}
	 * annotation.
	 */
	public static final class Literal extends AnnotationLiteral<ComponentPropertyIgnore> implements ComponentPropertyIgnore {
		/**
		 * Default instance
		 */
		public static final ComponentPropertyIgnore	INSTANCE			= new Literal();

		private static final long					serialVersionUID	= 1L;
	}
}

/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Identify the annotated annotation as a Bean Property Type.
 * <p>
 * Bean Property Type can be applied to beans annotated with
 * {@link SingleComponent}, {@link FactoryComponent}, to beans annotated with
 * {@link ApplicationScoped} or {@link Dependent} where the {@link Service}
 * annotation is applied, to methods and fields marked as {@link Produces} where
 * the {@link Service} annotation is applied, or to injection points where the
 * {@link Reference} annotation is applied.
 * <p>
 *
 * @see "Bean Property Types."
 * @author $Id$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface BeanPropertyType {

	/**
	 * Support inline instantiation of the {@link BeanPropertyType} annotation.
	 */
	public static final class Literal extends
			AnnotationLiteral<BeanPropertyType> implements BeanPropertyType {

		/**
		 * Default instance.
		 */
		public static final BeanPropertyType	INSTANCE			= new Literal();

		private static final long				serialVersionUID	= 1L;

	}
}

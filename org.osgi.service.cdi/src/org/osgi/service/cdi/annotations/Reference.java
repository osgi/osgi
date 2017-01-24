/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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
import javax.inject.Qualifier;

/**
 * Annotation used to annotate a CDI injection point and inform the CDI extender
 * that the injection should apply a service obtained from the OSGi registry.
 */
@Qualifier
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Reference {

	/**
	 * The reference scope for this reference.
	 *
	 * <p>
	 * If not specified, the {@link ReferenceScope#BUNDLE bundle} reference
	 * scope is used.
	 */
	ReferenceScope scope() default ReferenceScope.BUNDLE;

	/**
	 * The target property for this reference.
	 *
	 * <p>
	 * If not specified, no target property is applied.
	 */
	String target() default "";

	/**
	 * <p>
	 * If not specified, the type of the service for this reference is based
	 * upon how this annotation is used:
	 * <ul>
	 * <li>Annotated method - The type of the service is the type of the first
	 * argument of the method.</li>
	 * <li>Annotated field - The type of the service is based upon the type of
	 * the field being annotated.</li>
	 * </ul>
	 */
	Class<?> service() default Object.class;

}

/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated method as a {@code bind} method of a Service
 * Component.
 * 
 * <p>
 * The annotated method is a bind method of the Component.
 * 
 * <p>
 * This annotation is not processed at runtime by a Service Component Runtime
 * implementation. It must be processed by tools and used to add a Component
 * Description to the bundle.
 * 
 * @see "The reference element of a Component Description."
 * @version $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Reference {
	/**
	 * Name of this reference.
	 * 
	 * <p>
	 * If not specified, the name of this reference is based upon the name of
	 * the method being annotated. If the method name begins with {@code set} or
	 * {@code add}, that is removed and the first character is folded to lower
	 * case.
	 * 
	 * @see "The name attribute of the reference element of a Component Description."
	 */
	String name() default "";

	/**
	 * The type of the service to bind to this reference.
	 * 
	 * <p>
	 * If not specified, the type of the service to bind is based upon the type
	 * of the first argument of the method being annotated.
	 * 
	 * @see "The interface attribute of the reference element of a Component Description."
	 */
	Class< ? > service() default Object.class;

	/**
	 * Declare whether the reference is optional.
	 * 
	 * <p>
	 * An optional reference has a cardinality of {@code 0..1} or {@code 0..n}.
	 * A non-optional reference has a cardinality of {@code 1..1} or
	 * {@code 1..n}.
	 * 
	 * <p>
	 * If not specified, the reference is not optional.
	 * 
	 * @see "The cardinality attribute of the reference element of a Component Description."
	 */
	boolean optional() default false;

	/**
	 * Declare whether the reference can handle multiple services.
	 * 
	 * <p>
	 * A reference that supports multiple services has a cardinality of
	 * {@code 0..n} or {@code 1..n}. A reference that only supports a single
	 * service has a cardinality of {@code 0..1} or {@code 1..1}.
	 * 
	 * <p>
	 * If not specified, the reference only supports a single service.
	 * 
	 * @see "The cardinality attribute of the reference element of a Component Description."
	 */
	boolean multiple() default false;

	/**
	 * Declares the policy for the reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferencePolicy#STATIC STATIC} reference
	 * policy is used.
	 * 
	 * @see "The policy attribute of the reference element of a Component Description."
	 */
	ReferencePolicy policy() default ReferencePolicy.STATIC;

	/**
	 * Declares the target filter for the reference.
	 * 
	 * @see "The target attribute of the reference element of a Component Description."
	 */
	String target() default "";

	/**
	 * Declares the name of the unbind method which pairs with the annotated
	 * bind method.
	 * 
	 * <p>
	 * If not specified, the name of the unbind method is derived from the name
	 * of the annotated bind method. If the annotated method name begins with
	 * {@code set}, that is replaced with {@code unset} to derive the unbind
	 * method name. If the annotated method name begins with {@code add}, that
	 * is replaced with {@code remove} to derive the unbind method name.
	 * Otherwise, {@code un} is prefixed to the annotated method name to derive
	 * the unbind method name.
	 * 
	 * @see "The unbind attribute of the reference element of a Component Description."
	 */
	String unbind() default "";

	/**
	 * TODO What is this? I don't see how this relates to a component
	 * description.
	 */
	char type() default 0;
}

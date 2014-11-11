/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define a lookup strategy reference for a {@link Component}.
 * 
 * <p>
 * The referenced service can be accessed using one of the {@code locateService}
 * methods of {@code ComponentContext}.
 * 
 * <p>
 * This annotation is not processed at runtime by a Service Component Runtime
 * implementation. It must be processed by tools and used to add a Component
 * Description to the bundle.
 * 
 * <p>
 * In the generated Component Description for a component, the references must
 * be ordered in ascending lexicographical order (using {@code String.compareTo}
 * ) of the reference {@link #name() name}s.
 * 
 * @see "The reference element of a Component Description."
 * @author $Id$
 * @since 1.3
 */
@Retention(RetentionPolicy.CLASS)
@Target({})
public @interface LookupReference {
	/**
	 * The name of this reference.
	 * 
	 * @see "The name attribute of the reference element of a Component Description."
	 */
	String name();

	/**
	 * The type of the service to bind to this reference.
	 * 
	 * @see "The interface attribute of the reference element of a Component Description."
	 */
	Class<?> service();

	/**
	 * The cardinality of the reference.
	 * 
	 * <p>
	 * If not specified, the reference has a
	 * {@link ReferenceCardinality#MANDATORY 1..1} cardinality.
	 * 
	 * @see "The cardinality attribute of the reference element of a Component Description."
	 */
	ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

	/**
	 * The policy for the reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferencePolicy#STATIC STATIC} reference
	 * policy is used.
	 * 
	 * @see "The policy attribute of the reference element of a Component Description."
	 */
	ReferencePolicy policy() default ReferencePolicy.STATIC;

	/**
	 * The target filter for the reference.
	 * 
	 * @see "The target attribute of the reference element of a Component Description."
	 */
	String target() default "";

	/**
	 * The policy option for the reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferencePolicyOption#RELUCTANT RELUCTANT}
	 * reference policy option is used.
	 * 
	 * @see "The policy-option attribute of the reference element of a Component Description."
	 */
	ReferencePolicyOption policyOption() default ReferencePolicyOption.RELUCTANT;

	/**
	 * The reference scope for the reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferenceScope#BUNDLE bundle} reference
	 * scope is used.
	 * 
	 * @see "The scope attribute of the reference element of a Component Description."
	 */
	ReferenceScope scope() default ReferenceScope.BUNDLE;
}

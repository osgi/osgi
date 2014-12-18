/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
 * Identify the annotated field as a reference of a Service Component.
 * 
 * <p>
 * The annotated field is used to contain a reference of the Component.
 * 
 * <p>
 * This annotation is not processed at runtime by Service Component Runtime. It
 * must be processed by tools and used to add a Component Description to the
 * bundle.
 * 
 * <p>
 * In the generated Component Description for a component, the references must
 * be ordered in ascending lexicographical order (using {@code String.compareTo}
 * ) of the reference {@link #name() name}s.
 * 
 * @see "The field-reference element of a Component Description."
 * @since 1.3
 * @author $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface FieldReference {
	/**
	 * The name of the field reference.
	 * 
	 * <p>
	 * If not specified, the name of this reference is based upon the name of
	 * the field being annotated.
	 * 
	 * @see "The name attribute of the field-reference element of a Component Description."
	 */
	String name() default "";

	/**
	 * The type of the service to bind to the field reference.
	 * 
	 * <p>
	 * If not specified, the type of the service is based upon the type of the
	 * field being annotated and the cardinality of the reference. If the
	 * cardinality is either {@link ReferenceCardinality#MULTIPLE 0..n}, or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, the type of the field
	 * must be one of {@code java.util.Collection}, {@code java.util.List}, or a
	 * subtype of {@code java.util.Collection} so the type of the service is the
	 * generic type of the collection type of the field. Otherwise, the type of
	 * the service is the type of the field..
	 * 
	 * @see "The interface attribute of the field-reference element of a Component Description."
	 */
	Class<?> service() default Object.class;

	/**
	 * The change option for the field reference.
	 * 
	 * <p>
	 * If not specified, {@link FieldReferenceChangeOption#UPDATE} is used if
	 * the policy is {@link ReferencePolicy#DYNAMIC}, the cardinality is
	 * {@link ReferenceCardinality#MULTIPLE 0..n} or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, and the field is declared
	 * {@code final}. Otherwise, {@link FieldReferenceChangeOption#REPLACE} is
	 * used.
	 * 
	 * @see "The change-option attribute of the field-reference element of a Component Description."
	 */
	FieldReferenceChangeOption changeOption() default FieldReferenceChangeOption.REPLACE;

	/**
	 * The cardinality of the field reference.
	 * 
	 * <p>
	 * If not specified, the cardinality is based on the type of the field being
	 * annotated. If the type is either {@code java.util.Collection},
	 * {@code java.util.List}, or a subtype of {@code java.util.Collection}, the
	 * reference has a {@link ReferenceCardinality#MULTIPLE 0..n} cardinality.
	 * Otherwise the reference has a {@link ReferenceCardinality#MANDATORY 1..1}
	 * cardinality.
	 * 
	 * @see "The cardinality attribute of the field-reference element of a Component Description."
	 */
	ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

	/**
	 * The policy for the field reference.
	 * 
	 * <p>
	 * If not specified, the policy is based on the modifiers of the field being
	 * annotated. If the field is declared {@code volatile}, the
	 * {@link ReferencePolicy#DYNAMIC} reference policy is used, otherwise the
	 * {@link ReferencePolicy#STATIC STATIC} reference policy is used.
	 * 
	 * @see "The policy attribute of the field-reference element of a Component Description."
	 */
	ReferencePolicy policy() default ReferencePolicy.STATIC;

	/**
	 * The target property for the field reference.
	 * 
	 * @see "The target attribute of the field-reference element of a Component Description."
	 */
	String target() default "";

	/**
	 * The policy option for the field reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferencePolicyOption#RELUCTANT RELUCTANT}
	 * reference policy option is used.
	 * 
	 * @see "The policy-option attribute of the field-reference element of a Component Description."
	 */
	ReferencePolicyOption policyOption() default ReferencePolicyOption.RELUCTANT;

	/**
	 * The reference scope for the field reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferenceScope#BUNDLE bundle} reference
	 * scope is used.
	 * 
	 * @see "The scope attribute of the field-reference element of a Component Description."
	 */
	ReferenceScope scope() default ReferenceScope.BUNDLE;
}

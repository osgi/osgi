/*
 * Copyright (c) OSGi Alliance (2011, 2014). All Rights Reserved.
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
 * Identify the annotated member as a reference of a Service Component.
 * 
 * <p>
 * When the annotation is applied to a method, the method is the bind method of
 * the reference. When the annotation is applied to a field, the field will
 * contain the bound service(s) of the reference.
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
 * @see "The reference element of a Component Description."
 * @author $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Reference {
	/**
	 * The name of this reference.
	 * 
	 * <p>
	 * The name of this reference must be specified when using this annotation
	 * in the {@link Component#reference()} element since there is no annotated
	 * member from which the name can be determined.
	 * 
	 * If not specified, the name of this reference is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated method - If the method name begins with {@code bind},
	 * {@code set} or {@code add}, that prefix is removed to create the name of
	 * the reference. Otherwise, the name of the reference is the method name.</li>
	 * <li>Annotated field - The name of the reference is the field name.</li>
	 * </ul>
	 * 
	 * @see "The name attribute of the reference element of a Component Description."
	 */
	String name() default "";

	/**
	 * The type of the service for this reference.
	 * 
	 * <p>
	 * The type of the service for this reference must be specified when using
	 * this annotation in the {@link Component#reference()} element since there
	 * is no annotated member from which the type of the service can be
	 * determined.
	 * 
	 * <p>
	 * If not specified, the type of the service for this reference is based
	 * upon how this annotation is used:
	 * <ul>
	 * <li>Annotated method - The type of the service is the type of the first
	 * argument of the method.</li>
	 * <li>Annotated field - The type of the service is based upon the type of
	 * the field being annotated and the cardinality of the reference. If the
	 * cardinality is either {@link ReferenceCardinality#MULTIPLE 0..n}, or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, the type of the field
	 * must be one of {@code java.util.Collection}, {@code java.util.List}, or a
	 * subtype of {@code java.util.Collection} so the type of the service is the
	 * generic type of the collection. Otherwise, the type of the service is the
	 * type of the field.</li>
	 * </ul>
	 * 
	 * @see "The interface attribute of the reference element of a Component Description."
	 */
	Class<?> service() default Object.class;

	/**
	 * The cardinality of this reference.
	 * 
	 * <p>
	 * If not specified, the cardinality of this reference is based upon how
	 * this annotation is used:
	 * <ul>
	 * <li>Annotated method - The cardinality is
	 * {@link ReferenceCardinality#MANDATORY 1..1}.</li>
	 * <li>Annotated field - The cardinality is based on the type of the field.
	 * If the type is either {@code java.util.Collection},
	 * {@code java.util.List}, or a subtype of {@code java.util.Collection}, the
	 * cardinality is {@link ReferenceCardinality#MULTIPLE 0..n}. Otherwise the
	 * cardinality is {@link ReferenceCardinality#MANDATORY 1..1}.</li>
	 * <li>{@link Component#reference()} element - The cardinality is
	 * {@link ReferenceCardinality#MANDATORY 1..1}.</li>
	 * </ul>
	 * 
	 * @see "The cardinality attribute of the reference element of a Component Description."
	 */
	ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

	/**
	 * The policy for this reference.
	 * 
	 * <p>
	 * If not specified, the policy of this reference is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated method - The policy is {@link ReferencePolicy#STATIC
	 * STATIC}.</li>
	 * <li>Annotated field - The policy is based on the modifiers of the field.
	 * If the field is declared {@code volatile}, the policy is
	 * {@link ReferencePolicy#DYNAMIC}. Otherwise the policy is
	 * {@link ReferencePolicy#STATIC STATIC}.</li>
	 * <li>{@link Component#reference()} element - The policy is
	 * {@link ReferencePolicy#STATIC STATIC}.</li>
	 * </ul>
	 * 
	 * @see "The policy attribute of the reference element of a Component Description."
	 */
	ReferencePolicy policy() default ReferencePolicy.STATIC;

	/**
	 * The target property for this reference.
	 * 
	 * <p>
	 * If not specified, no target property is set.
	 * 
	 * @see "The target attribute of the reference element of a Component Description."
	 */
	String target() default "";

	/**
	 * The policy option for this reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferencePolicyOption#RELUCTANT RELUCTANT}
	 * reference policy option is used.
	 * 
	 * @see "The policy-option attribute of the reference element of a Component Description."
	 * @since 1.2
	 */
	ReferencePolicyOption policyOption() default ReferencePolicyOption.RELUCTANT;

	/**
	 * The reference scope for this reference.
	 * 
	 * <p>
	 * If not specified, the {@link ReferenceScope#BUNDLE bundle} reference
	 * scope is used.
	 * 
	 * @see "The scope attribute of the reference element of a Component Description."
	 * @since 1.3
	 */
	ReferenceScope scope() default ReferenceScope.BUNDLE;

	/* Method injection elements */

	/**
	 * The name of the bind method for this reference.
	 * 
	 * <p>
	 * If specified and this reference annotates a method, the specified name
	 * must match the name of the annotated method.
	 * 
	 * <p>
	 * If not specified, the name of the bind method is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated method - The name of the annotated method is the name of
	 * the bind method.</li>
	 * <li>Annotated field - There is no bind method name.</li>
	 * <li>{@link Component#reference()} element - There is no bind method name.
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * If there is a bind method name, the component must contain a method with
	 * that name.
	 * 
	 * @see "The bind attribute of the reference element of a Component Description."
	 * @since 1.3
	 */
	String bind() default "";

	/**
	 * The name of the updated method for this reference.
	 * 
	 * <p>
	 * If not specified, the name of the updated method is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated method - The name of the updated method is created from the
	 * name of the annotated method. If the name of the annotated method begins
	 * with {@code bind}, {@code set} or {@code add}, that prefix is replaced
	 * with {@code updated} to create the name candidate for the updated method.
	 * Otherwise, {@code updated} is prefixed to the name of the annotated
	 * method to create the name candidate for the updated method. If the
	 * component type contains a method with the candidate name, the candidate
	 * name is used as the name of the updated method. To declare no updated
	 * method when the component type contains a method with the candidate name,
	 * the value {@code "-"} must be used.</li>
	 * <li>Annotated field - There is no updated method name.</li>
	 * <li>{@link Component#reference()} element - There is no updated method
	 * name.</li>
	 * </ul>
	 * 
	 * <p>
	 * If there is an updated method name, the component must contain a method
	 * with that name.
	 * 
	 * @see "The updated attribute of the reference element of a Component Description."
	 * @since 1.2
	 */
	String updated() default "";

	/**
	 * The name of the unbind method for this reference.
	 * 
	 * <p>
	 * If not specified, the name of the unbind method is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated method - The name of the unbind method is created from the
	 * name of the annotated method. If the name of the annotated method begins
	 * with {@code bind}, {@code set} or {@code add}, that prefix is replaced
	 * with {@code unbind}, {@code unset} or {@code remove}, respectively, to
	 * create the name candidate for the unbind method. Otherwise, {@code un} is
	 * prefixed to the name of the annotated method to create the name candidate
	 * for the unbind method. If the component type contains a method with the
	 * candidate name, the candidate name is used as the name of the unbind
	 * method. To declare no unbind method when the component type contains a
	 * method with the candidate name, the value {@code "-"} must be used.</li>
	 * <li>Annotated field - There is no unbind method name.</li>
	 * <li>{@link Component#reference()} element - There is no unbind method
	 * name.</li>
	 * </ul>
	 * 
	 * <p>
	 * If there is an unbind method name, the component must contain a method
	 * with that name.
	 * 
	 * @see "The unbind attribute of the reference element of a Component Description."
	 */
	String unbind() default "";

	/* Field injection elements */

	/**
	 * The name of the field for this reference.
	 * 
	 * <p>
	 * If specified and this reference annotates a field, the specified name
	 * must match the name of the annotated field.
	 * 
	 * <p>
	 * If not specified, the name of the field is based upon how this annotation
	 * is used:
	 * <ul>
	 * <li>Annotated method - There is no field name.</li>
	 * <li>Annotated field - The name of the annotated field is the name of the
	 * field.</li>
	 * <li>{@link Component#reference()} element - There is no field name.</li>
	 * </ul>
	 * 
	 * <p>
	 * If there is a field name, the component must contain a field with that
	 * name.
	 * 
	 * @see "The field attribute of the reference element of a Component Description."
	 * @since 1.3
	 */
	String field() default "";

	/**
	 * The field option for this reference.
	 * 
	 * <p>
	 * If not specified, the field option is based upon how this annotation is
	 * used:
	 * <ul>
	 * <li>Annotated method - There is no field option.</li>
	 * <li>Annotated field - The field option is based upon the policy and
	 * cardinality of the reference and the modifiers of the field. If the
	 * policy is {@link ReferencePolicy#DYNAMIC}, the cardinality is
	 * {@link ReferenceCardinality#MULTIPLE 0..n} or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, and the field is declared
	 * {@code final}, the field option is {@link FieldOption#UPDATE}. Otherwise,
	 * the field option is {@link FieldOption#REPLACE}</li>
	 * <li>{@link Component#reference()} element - There is no field option.</li>
	 * </ul>
	 * 
	 * @see "The field-option attribute of the reference element of a Component Description."
	 * @since 1.3
	 */
	FieldOption fieldOption() default FieldOption.REPLACE;
}

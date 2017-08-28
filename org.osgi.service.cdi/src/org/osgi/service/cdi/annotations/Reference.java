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
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CdiConstants;

/**
 * Annotation used to annotate a CDI injection point informing the CDI container
 * that the injection should apply a service obtained from the OSGi registry.
 */
@Qualifier
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Requirement(
		namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
		name = CdiConstants.CDI_CAPABILITY_NAME,
		version = CdiConstants.CDI_SPECIFICATION_VERSION)
public @interface Reference {
	/**
	 * The name of this reference.
	 * <p>
	 * If not specified, the name of this reference is based upon how this
	 * annotation is used:
	 * <ul>
	 * <li>Annotated constructor parameter - The name of the reference is the
	 * simple, raw class name of the field suffixed by the positional index of
	 * the parameter.</li>
	 * <li>Annotated field - The name of the reference is the field name.</li>
	 * <li>Annotated method parameter - The name of the reference is the simple,
	 * raw class name of the field.</li>
	 * </ul>
	 */
	String name() default "";

	/**
	 * The type of the service for this reference.
	 * <p>
	 * If not specified, the type of the service for this reference is based upon
	 * how this annotation is used:
	 * <ul>
	 * <li>Annotated field - The type of the service is based upon the type of the
	 * field being annotated and the cardinality of the reference. If the
	 * cardinality is either {@link ReferenceCardinality#MULTIPLE 0..n}, or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, the type of the field must be
	 * one of {@code java.util.Collection}, {@code java.util.List}, or a subtype of
	 * {@code java.util.Collection} so the type of the service is the generic type
	 * of the collection. Otherwise, the type of the service is the type of the
	 * field.</li>
	 * <li>Annotated constructor or method parameter - The type of the service is
	 * based upon the type of the parameter being annotated and the cardinality of
	 * the reference. If the cardinality is either
	 * {@link ReferenceCardinality#MULTIPLE 0..n}, or
	 * {@link ReferenceCardinality#AT_LEAST_ONE 1..n}, the type of the parameter
	 * must be one of {@code java.util.Collection}, {@code java.util.List}, or a
	 * subtype of {@code java.util.Collection} so the type of the service is the
	 * generic type of the collection. Otherwise, the type of the service is the
	 * type of the parameter.</li>
	 * </ul>
	 */
	Class<?> service() default Object.class;

	/**
	 * The cardinality of this reference.
	 * <p>
	 * If not specified, the cardinality of this reference is based upon how
	 * this annotation is used:
	 * <ul>
	 * <li>Annotated field - The cardinality is based on the type of the field.
	 * If the type is either {@code java.util.Collection},
	 * {@code java.util.List}, or a subtype of {@code java.util.Collection}, the
	 * cardinality is {@link ReferenceCardinality#MULTIPLE 0..n}. Otherwise the
	 * cardinality is {@link ReferenceCardinality#MANDATORY 1..1}.</li>
	 * <li>Annotated constructor or method parameter - The cardinality is based
	 * on the type of the parameter. If the type is either
	 * {@code java.util.Collection}, {@code java.util.List}, or a subtype of
	 * {@code java.util.Collection}, the cardinality is
	 * {@link ReferenceCardinality#MULTIPLE 0..n}. Otherwise the cardinality is
	 * {@link ReferenceCardinality#MANDATORY 1..1}.</li>
	 */
	ReferenceCardinality cardinality() default ReferenceCardinality.DEFAULT;

	/**
	 * The policy for this reference.
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
	 * <li>Annotated constructor parameter - The policy is
	 * {@link ReferencePolicy#STATIC STATIC}. Constructor parameters must always
	 * assume {@link ReferencePolicy#STATIC STATIC} policy.</li>
	 * </ul>
	 *
	 * @see "The policy attribute of the reference element of a Component Description."
	 */
	ReferencePolicy policy() default ReferencePolicy.DEFAULT;

	/**
	 * The target property for this reference.
	 *
	 * <p>
	 * If not specified, no target property is set.
	 */
	String target() default "";

	/**
	 * The policy option for this reference.
	 *
	 * <p>
	 * If not specified, the {@link ReferencePolicyOption#RELUCTANT RELUCTANT}
	 * reference policy option is used.
	 */
	ReferencePolicyOption policyOption() default ReferencePolicyOption.DEFAULT;

	/**
	 * The reference scope for this reference.
	 *
	 * <p>
	 * If not specified, the {@link ReferenceScope#BUNDLE} reference scope is
	 * used.
	 */
	ReferenceScope scope() default ReferenceScope.DEFAULT;
}

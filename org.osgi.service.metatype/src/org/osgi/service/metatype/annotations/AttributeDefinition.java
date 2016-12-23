/*
 * Copyright (c) OSGi Alliance (2013, 2016). All Rights Reserved.
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

package org.osgi.service.metatype.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code AttributeDefinition} information for the annotated method.
 * 
 * <p>
 * Each method of a type annotated by {@link ObjectClassDefinition} has an
 * implied AttributeDefinition annotation. This annotation is only used to
 * specify non-default AttributeDefinition information.
 * 
 * <p>
 * The {@code id} of this AttributeDefinition is generated from the name of the
 * annotated method. The annotated method name is processed from left to right
 * changing each character as follows:
 * <ul>
 * <li>A dollar sign ({@code '$'} &#92;u0024) is removed unless it is followed
 * by another dollar sign in which case the two consecutive dollar signs (
 * {@code '$$'}) are changed to a single dollar sign.</li>
 * <li>A low line ({@code '_'} &#92;u005F) is changed to a full stop (
 * {@code '.'} &#92;u002E) unless is it followed by another low line in which
 * case the two consecutive low lines ({@code '__'}) are changed to a single low
 * line.</li>
 * <li>All other characters are unchanged.</li>
 * </ul>
 * This id is the value of the id attribute of the generate AD element and is
 * used as the name of the corresponding configuration property.
 * 
 * <p>
 * This annotation is not processed at runtime. It must be processed by tools
 * and used to contribute to a Meta Type Resource document for the bundle.
 * 
 * @see "The AD element of a Meta Type Resource."
 * @author $Id$
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AttributeDefinition {
	/**
	 * The human readable name of this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, the name of this AttributeDefinition is derived from
	 * the name of the annotated method. For example, low line ({@code '_'}
	 * &#92;u005F) and dollar sign ({@code '$'} &#92;u0024) are replaced with
	 * space ({@code ' '} &#92;u0020) and space is inserted between camel case
	 * words.
	 * 
	 * <p>
	 * If the name begins with the percent sign ({@code '%'} &#92;u0025), the
	 * name can be {@link ObjectClassDefinition#localization() localized}.
	 * 
	 * @see "The name attribute of the AD element of a Meta Type Resource."
	 */
	String name() default "";

	/**
	 * The human readable description of this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, the description of this AttributeDefinition is the
	 * empty string.
	 * 
	 * <p>
	 * If the description begins with the percent sign ({@code '%'} &#92;u0025),
	 * the description can be {@link ObjectClassDefinition#localization()
	 * localized}.
	 * 
	 * @see "The description attribute of the AD element of a Meta Type Resource."
	 */
	String description() default "";

	/**
	 * The type of this AttributeDefinition.
	 * 
	 * <p>
	 * This must be one of the defined {@link AttributeType attributes types}.
	 * 
	 * <p>
	 * If not specified, the type is derived from the return type of the
	 * annotated method. Return types of {@code Class} and {@code Enum} are
	 * mapped to {@link AttributeType#STRING STRING}. If the return type is
	 * {@code List}, {@code Set}, {@code Collection}, {@code Iterable} or some
	 * type which can be determined at annotation processing time to
	 * <ol>
	 * <li>be a subtype of {@code Collection} and</li>
	 * <li>have a public no argument constructor,</li>
	 * </ol>
	 * then the type is derived from the generic type. For example, a return
	 * type of {@code List<String>} will be mapped to
	 * {@link AttributeType#STRING STRING}. A return type of a single
	 * dimensional array is supported and the type is the component type of the
	 * array. Multi dimensional arrays are not supported. Annotation return
	 * types are not supported. Any unrecognized type is mapped to
	 * {@link AttributeType#STRING STRING}. A tool processing the annotation
	 * should declare an error for unsupported return types.
	 * 
	 * @see "The type attribute of the AD element of a Meta Type Resource."
	 */
	AttributeType type() default AttributeType.STRING;

	/**
	 * The cardinality of this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, the cardinality is derived from the return type of the
	 * annotated method. For an array return type, the cardinality is a large
	 * positive value. If the return type is {@code List}, {@code Set},
	 * {@code Collection}, {@code Iterable} or some type which can be determined
	 * at annotation processing time to
	 * <ol>
	 * <li>be a subtype of {@code Collection} and</li>
	 * <li>have a public no argument constructor,</li>
	 * </ol>
	 * the cardinality is a large negative value. Otherwise, the cardinality is
	 * 0.
	 * 
	 * @see "The cardinality attribute of the AD element of a Meta Type Resource."
	 */
	int cardinality() default 0;

	/**
	 * The minimum value for this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, there is no minimum value.
	 * 
	 * @see "The min attribute of the AD element of a Meta Type Resource."
	 */
	String min() default "";

	/**
	 * The maximum value for this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, there is no maximum value.
	 * 
	 * @see "The max attribute of the AD element of a Meta Type Resource."
	 */
	String max() default "";

	/**
	 * The default value for this AttributeDefinition.
	 * 
	 * <p>
	 * The specified values are concatenated into a comma delimited list to
	 * become the value of the {@code default} attribute of the generated
	 * {@code AD} element.
	 * 
	 * <p>
	 * If not specified and the annotated method is an annotation element that
	 * has a {@code default} value, then the value of this element is the
	 * {@code default} value of the annotated element. Otherwise, there is no
	 * default value.
	 * 
	 * @see "The default attribute of the AD element of a Meta Type Resource."
	 */
	String[] defaultValue() default {};

	/**
	 * The required value for this AttributeDefinition.
	 * 
	 * <p>
	 * If not specified, the value is {@code true}.
	 * 
	 * @see "The required attribute of the AD element of a Meta Type Resource."
	 */
	boolean required() default true;

	/**
	 * The option information for this AttributeDefinition.
	 * 
	 * <p>
	 * For each specified {@link Option}, an {@code Option} element is generated
	 * for this AttributeDefinition.
	 *
	 * <p>
	 * If not specified, the option information is derived from the return type
	 * of the annotated method. If the return type is an {@code enum}, a single
	 * dimensional array of an {@code enum}, or a {@code List}, {@code Set},
	 * {@code Collection}, {@code Iterable} or some type which can be determined
	 * at annotation processing time to
	 * <ol>
	 * <li>be a subtype of {@code Collection} and</li>
	 * <li>have a public no argument constructor,</li>
	 * </ol>
	 * with a generic type of an {@code enum}, then the value of this element
	 * has an {@link Option} for each value of the {@code enum}. The label and
	 * value of each {@link Option} are set to the name of the corresponding
	 * {@code enum} value. Otherwise, no {@code Option} elements will be
	 * generated.
	 * 
	 * @see "The Option element of a Meta Type Resource."
	 */
	Option[] options() default {};
}

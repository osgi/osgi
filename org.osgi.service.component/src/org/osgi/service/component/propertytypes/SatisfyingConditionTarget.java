/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.component.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.condition.Condition;

/**
 * Component Property Type for the {@code osgi.ds.satisfying.condition.target}
 * reference property.
 * <p>
 * This annotation can be used on a {@link Component} to declare the value of
 * the target property for the component's satisfying condition reference if a
 * value other than the default value is desired.
 * 
 * @see "Component Property Types"
 * @author $Id$
 * @since 1.5
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SatisfyingConditionTarget {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.ds.";

	/**
	 * Filter expression to select the component's satisfying condition.
	 *
	 * @return The filter expression to select the component's satisfying
	 *         condition.
	 */
	String value() default "(" + Condition.CONDITION_ID + "="
			+ Condition.CONDITION_ID_TRUE + ")";
}

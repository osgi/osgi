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

package org.osgi.service.cdi.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.framework.Constants;
import org.osgi.service.cdi.annotations.BeanPropertyType;

/**
 * Bean Property Type for the {@code service.description} service property.
 * <p>
 * This annotation can be used as defined by {@link BeanPropertyType} to declare
 * the value the {@link Constants#SERVICE_DESCRIPTION} service property.
 *
 * @see "Bean Property Types"
 * @author $Id$
 */
@BeanPropertyType
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.TYPE
})
public @interface ServiceDescription {
	/**
	 * Service property identifying a service's description.
	 *
	 * @return The service description.
	 * @see Constants#SERVICE_DESCRIPTION
	 */
	String value();
}

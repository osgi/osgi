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

package org.osgi.service.component.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.util.converter.annotations.KeyName;

/**
 * Component Property Type for the {@code service.description} service property.
 * <p>
 * This annotation can be used on a {@link Component} to declare the value of
 * the {@link Constants#SERVICE_DESCRIPTION} service property.
 * 
 * @see "Component Property Types"
 * @author $Id$
 * @since 1.4
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ServiceDescription {
	/**
	 * Service property identifying a service's description.
	 * 
	 * @return The service description.
	 * @see Constants#SERVICE_DESCRIPTION
	 */
	@KeyName(Constants.SERVICE_DESCRIPTION)
	String value();
}

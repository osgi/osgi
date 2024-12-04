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
package org.osgi.service.webservice.whiteboard.propertytypes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.service.webservice.whiteboard.annotations.RequireWebserviceWhiteboard;

/**
 * Component property type to mark a Handler to be considered by the Jakarta XML
 * Webservices Whiteboard Runtime
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE
})
@RequireWebserviceWhiteboard
@ComponentPropertyType
public @interface WhiteboardHandler {
	
	/**
	 * prefix used for component properties
	 */
	String PREFIX_ = WebserviceWhiteboardConstants.WEBSERVICE_HANDLER_PREFIX;
	

	/**
	 * Marks this component as to be considered by the whiteboard extender
	 * 
	 * @return true
	 */
	boolean extension() default true;
	
	/**
	 * @return the filter to select an endpoint must be a valid OSGi filter, the
	 *         empty string is treated as if the property is not defined and
	 *         should match any endpoint
	 */
	String filter() default "";

}

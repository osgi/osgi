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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;
import org.osgi.service.webservice.whiteboard.annotations.RequireWebserviceWhiteboard;

/**
 * Annotation that can be used to mark a service component as an object that
 * should be considered by the SOPA Whiteboard Extender specifying a default
 * address
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireWebserviceWhiteboard
public @interface WhiteboardEndpoint {

	/**
	 * prefix used for component properties
	 */
	String PREFIX_ = WebserviceWhiteboardConstants.WEBSERVICE_ENDPOINT_PREFIX;
    /**
     * @return <code>true</code> if this is an implementor for the soap whiteboard, <code>false</code> otherwise, can be used to switch an implementation on/off
     */
    boolean implementor() default true;
    
	/**
	 * A URI specifying the address and transport/protocol to use, this will be
	 * passed to the {@link jakarta.xml.ws.Endpoint#publish(String)}, if the
	 * address is empty, it is assumed that there is some other way of specify
	 * the address for example using {@link HttpWhiteboardEndpoint} annotation
	 * 
	 * @return the address under which this endpoint should be registered
	 */
	String address() default "";

}

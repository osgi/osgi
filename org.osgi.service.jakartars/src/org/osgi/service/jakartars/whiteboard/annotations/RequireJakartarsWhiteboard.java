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

package org.osgi.service.jakartars.whiteboard.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;

/**
 * This annotation can be used to require the Jakarta RESTful Web Services
 * Whiteboard implementation. It can be used directly, or as a meta-annotation.
 * <p>
 * This annotation is applied to several of the Jakarta RESTful Web Services
 * Whiteboard component property annotations meaning that it does not normally
 * need to be applied to Declarative Services components which use the Jakarta
 * RESTful Web Services Whiteboard.
 * 
 * @author $Id$
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, //
		name = JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_IMPLEMENTATION, //
		version = JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_SPECIFICATION_VERSION)
public @interface RequireJakartarsWhiteboard {
	// This is a marker annotation.
}

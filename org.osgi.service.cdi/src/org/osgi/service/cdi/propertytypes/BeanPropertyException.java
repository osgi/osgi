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

/**
 * This Runtime Exception is thrown when a Bean Property Type method attempts an
 * invalid component property coercion. For example when the bean property type
 * method {@code Long test();} is applied to a component property {@code "test"}
 * of type String.
 *
 * @author $Id$
 */
public class BeanPropertyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a Bean Property Exception with a message.
	 *
	 * @param message The message for this exception.
	 */
	public BeanPropertyException(String message) {
		super(message);
	}

	/**
	 * Create a Bean Property Exception with a message and a nested cause.
	 *
	 * @param message The message for this exception.
	 * @param cause   The causing exception.
	 */
	public BeanPropertyException(String message, Throwable cause) {
		super(message, cause);
	}
}

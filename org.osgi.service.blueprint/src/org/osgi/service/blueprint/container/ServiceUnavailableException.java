/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.container;

import org.osgi.framework.ServiceException;

/**
 * Thrown when an invocation is made on an OSGi service reference component, and
 * a backing service is not available.
 */
public class ServiceUnavailableException extends ServiceException {

	private final String filter;


    /**
     * Creates a <code>ServiceUnavaiableException</code> with the specified message.
     *
     * @param message The associated message.
     * @param filterExpression
     *                The filter expression used for the service lookup.
     */
	public ServiceUnavailableException(
           String message,
           String filterExpression) {
		super(message, UNREGISTERED);
		this.filter = filterExpression;
	}


	/**
	 * Creates a <code>ServiceUnavaiableException</code> with the specified message and
	 * exception cause.
	 *
	 * @param message The associated message.
     * @param filterExpression
     *                The filter expression used for the service lookup.
	 * @param cause The cause of this exception.
	 */
	public ServiceUnavailableException(
           String message,
           String filterExpression,
           Throwable cause) {
		super(message, UNREGISTERED, cause);
		this.filter = filterExpression;
	}

	/**
	 * The filter expression that a service would have needed to satisfy in order
	 * for the invocation to proceed.
	 */
	public String getFilter() {
		return this.filter;
	}
}


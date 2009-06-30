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

/**
 * Thrown when an attempt is made to lookup a component by id and no such
 * component exists in the Blueprint Container.
 */
public class NoSuchComponentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * The id of the component request that generated the exception.
	 */
	private final String managerId;

	/**
	 * Create an exception for a single manager id request.
	 * 
	 * @param id
	 *            The id of the non-existent manager.
	 */
	public NoSuchComponentException(String id) {
		this.managerId = id;
	}

	/**
	 * Returns the manager id that generated the Exception.
	 * 
	 * @return The id of the component associated with an unresolved
	 *         request.
	 */
	public String getComponentId() {
		return this.managerId;
	}

	/**
	 * Returns a human readable message associated with the exception.
	 * 
	 * @return The descriptive message for the exception.
	 */
	public String getMessage() {
		return "No manager with id '"
				+ (this.managerId == null ? "<null>" : this.managerId)
				+ "' could be found";
	}
}

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
 * Thrown when an attempt is made to lookup a component by name and no such named
 * component exists in the blueprint container.
 */
public class NoSuchComponentException extends RuntimeException {

	/**
	 * The name of the component request that generated the
	 * exception.
	 */
	private final String componentName;

	/**
	 * Create an exception for a single component name request.
	 *
	 * @param componentName
	 *               The name of the non-existent component.
	 */
	public NoSuchComponentException(String componentName) {
		this.componentName = componentName;
	}

	/**
	 * Returns the component name that generated the exception.
	 *
	 * @return The String name of the component associated with an unresolved
	 *         request.
	 */
	public String getComponentName() {
		return this.componentName;
	}

	/**
	 * Returns a human readable message associated with the exception.
	 *
	 * @return The descriptive message for the exception.
	 */
	public String getMessage() {
		return "No component named '" +
		       (this.componentName == null ? "<null>" : this.componentName) +
		       "' could be found";
	}
}

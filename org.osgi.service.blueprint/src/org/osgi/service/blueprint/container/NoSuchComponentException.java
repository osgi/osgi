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
package org.osgi.service.blueprint.context;

/**
 * Thrown when an attempt is made to lookup a component by name and no such named
 * component exists in the module context.
 */
public class NoSuchComponentException extends RuntimeException {

	private final String componentName;
	
	public NoSuchComponentException(String componentName) {
		this.componentName = componentName;
	}
	
	public String getComponentName() {
		return this.componentName;
	}

	public String getMessage() {
		return "No component named '" + 
		       (this.componentName == null ? "<null>" : this.componentName) + 
		       "' could be found";
	}
}

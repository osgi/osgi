/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.service.framework;

/**
 * Describes the content which is shared over a framework link.
 */
//TODO javadoc needs much work!!
public class LinkDescription {
	private final String[] imports;
	private final String[] serviceFilters;

	/**
	 * Create a link description which imports the specified content
	 * from the a source framework and exports the content to a target
	 * framework 
	 * @param imports a list of import package specifications for
	 *        a list of packages to import from a source framework
	 * @param serviceFilters a list of service filters specifications
	 *        for a list of services to import from a source framework
	 */
	public LinkDescription(String[] imports, String[] serviceFilters) {
		this.imports = imports;
		this.serviceFilters = serviceFilters;
	}

	/**
	 * Returns the import package specifications
	 * @return the imports
	 */
	public String[] getImports() {
		return copyOf(imports);
	}

	/**
	 * Returns the service filter specifications
	 * @return the service filters
	 */
	public String[] getServiceFilters() {
		return copyOf(serviceFilters);
	}

	private String[] copyOf(String[] array) {
		String[] results = new String[array.length];
		System.arraycopy(array, 0, results, 0, array.length);
		return results;
	}
}

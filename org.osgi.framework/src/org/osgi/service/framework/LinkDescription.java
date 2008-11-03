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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes the packages and services which are shared from a source framework
 * to a target framework through a <code>FrameworkLink</code>.
 * 
 * @see FrameworkLink
 * 
 * @ThreadSafe
 * @version $Revision$
 */
// TODO javadoc needs review
// TODO do we need to specify the start-level for the source and target bundles? (Bug 813)
public final class LinkDescription {
	private final List imports;
	private final List serviceFilters;

	/**
	 * Creates a link description that specifies the packages and services to
	 * share from a source framework to a target framework
	 * 
	 * @param imports
	 *            a list of import package specifications that specify the
	 *            packages to import from a source framework.
	 * @param serviceFilters
	 *            a list of service filters specifications that specify the
	 *            services to import from a source framework.
	 */
	public LinkDescription(List imports, List serviceFilters) {
		this.imports = (imports == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList(imports)));
		this.serviceFilters = (serviceFilters == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList(serviceFilters)));
	}

	/**
	 * Returns the import package specifications
	 * 
	 * @return the imports
	 */
	public List getImports() {
		return imports;
	}

	/**
	 * Returns the service filter specifications
	 * 
	 * @return the service filters
	 */
	public List getServiceFilters() {
		return serviceFilters;
	}
}

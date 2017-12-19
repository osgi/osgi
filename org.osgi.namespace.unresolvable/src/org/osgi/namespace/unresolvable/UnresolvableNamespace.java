/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.namespace.unresolvable;

import org.osgi.resource.Namespace;

/**
 * Unresolvable Capability and Requirement Namespace.
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace.
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * @Immutable
 * @author $Id$
 */
public final class UnresolvableNamespace extends Namespace {

	/**
	 * Namespace name for "unresolvable" capabilities and requirements.
	 * <p>
	 * This is typically used as follows to prevent a bundle from being
	 * resolvable.
	 * 
	 * <pre>
	 * Require-Capability: osgi.unresolvable;
	 *   filter:="(&amp;(must.not.resolve=*)(!(must.not.resolve=*)))"
	 * </pre>
	 */
	public static final String UNRESOLVABLE_NAMESPACE = "osgi.unresolvable";

	private UnresolvableNamespace() {
		// empty
	}
}

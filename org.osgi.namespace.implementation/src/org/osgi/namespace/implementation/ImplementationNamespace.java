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

package org.osgi.namespace.implementation;

import org.osgi.resource.Namespace;

/**
 * Implementation Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace.
 * 
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
public final class ImplementationNamespace extends Namespace {

	/**
	 * Namespace name for "implementation" capabilities and requirements.
	 * 
	 * This is also the capability attribute used to specify the name of the
	 * specification or contract being implemented.
	 * 
	 * <p>
	 * A {@code ImplementationNamespace} capability should express a
	 * {@link Namespace#CAPABILITY_USES_DIRECTIVE uses constraint} for the
	 * appropriate packages defined by the specification/contract the packages
	 * mentioned in the value of this attribute.
	 */
	public static final String	IMPLEMENTATION_NAMESPACE		= "osgi.implementation";

	/**
	 * The capability attribute contains the {@code Version} of the
	 * specification or contract being implemented. The value of this attribute
	 * must be of type {@code Version}.
	 */
	public static final String	CAPABILITY_VERSION_ATTRIBUTE	= "version";

	private ImplementationNamespace() {
		// empty
	}
}

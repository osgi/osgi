/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.framework.namespace;

import org.osgi.resource.Namespace;

/**
 * Contract Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * @Immutable
 * @version $Id$
 */
public final class ContractNamespace extends Namespace {

	/**
	 * Namespace name for contract capabilities and requirements.
	 * 
	 * <p>
	 * Also, the capability attribute used to specify the name of the contract.
	 */
	public static final String	CONTRACT_NAMESPACE				= "osgi.contract";

	/**
	 * The capability attribute contains the {@code Version} of the
	 * specification of the contract. The value of this attribute must be of
	 * type {@code Version}.
	 */
	public final static String	CAPABILITY_VERSION_ATTRIBUTE	= "version";

	private ContractNamespace() {
		// empty
	}
}

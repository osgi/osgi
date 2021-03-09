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

package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.A;
import org.osgi.dmt.ddf.MAP;
import org.osgi.dmt.ddf.Scope;

/**
 * A Wire is a link between two bundles where the semantics of this link is
 * defined by the used name space. This is closely modeled after the Wiring API
 * in the Core Framework.
 */
public interface Wire {
	/**
	 * The name space of this wire. Can be:
	 * <ul>
	 * <li>osgi.wiring.bundle - Defined in the OSGi Core</li>
	 * <li>osgi.wiring.package - Defined in the OSGi Core</li>
	 * <li>osgi.wiring.host - Defined in the OSGi Core</li>
	 * <li>osgi.wiring.rmt.service - Defined in this specification</li>
	 * <li>* - Generic name spaces</li>
	 * </ul>
	 * <p>
	 * The osgi.wiring.rmt.service name space is not defined by the OSGi Core as
	 * it is not part of the module layer. The name space has the following
	 * layout:
	 * <ul>
	 * <li>Requirement - A filter on the service.id service property.</li>
	 * <li>Capability - All service properties as attributes. No defined
	 * directives.</li>
	 * <li>Requirer - The bundle that has gotten the service</li>
	 * <li>Provider - The bundle that has registered the service</li>
	 * </ul>
	 * <p>
	 * There is a wire for each registration-get pair. That is, if a service is
	 * registered by A and gotten by B and C then there are two wires:
	 * {@code B->A} and {@code C->A}.
	 * 
	 * @return The name space for this wire.
	 */
	@Scope(A)
	String Namespace();

	/**
	 * The Requirement that caused this wire.
	 * 
	 * @return The requirement that caused this wire.
	 */
	@Scope(A)
	Requirement Requirement();

	/**
	 * The Capability that satisfied the requirement of this wire.
	 * 
	 * @return The capability that satisfied the requirement for this wire.
	 */
	@Scope(A)
	Capability Capability();

	/**
	 * The location of the Bundle that contains the requirement for this wire.
	 * 
	 * @return The requirer's location
	 */
	@Scope(A)
	String Requirer();

	/**
	 * The location of the Bundle that provides the capability for this wire.
	 * 
	 * @return The provider's location
	 */
	@Scope(A)
	String Provider();

	/**
	 * Instance Id to allow addressing by Instance Id.
	 * 
	 * @return The InstanceId
	 */

	@Scope(A)
	int InstanceId();

	/**
	 * Describes a Requirement.
	 */
	public interface Requirement {
		/**
		 * The Filter string for this requirement.
		 * 
		 * @return The Filter string for this requirement
		 */
		@Scope(A)
		String Filter();

		/**
		 * The Directives for this requirement. These directives must contain
		 * the filter: directive as described by the Core.
		 * 
		 * @return The Directives for this requirement.
		 */
		@Scope(A)
		MAP<String, String> Directive();

		/**
		 * The Attributes for this requirement.
		 * 
		 * @return The Attributes for this requirement.
		 */
		@Scope(A)
		MAP<String, String> Attribute();
	}

	/**
	 * Describes a Capability.
	 */
	public interface Capability {
		/**
		 * The Directives for this capability.
		 * 
		 * @return The Directives for this capability.
		 */
		@Scope(A)
		MAP<String, String> Directive();

		/**
		 * The Attributes for this capability.
		 * 
		 * @return The Attributes for this requirement.
		 */
		@Scope(A)
		MAP<String, String> Attribute();

	}
}

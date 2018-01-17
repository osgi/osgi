/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.service.cdi.runtime.dto.template;

import org.osgi.dto.DTO;

/**
 * A description of a reference dependency of a component
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ReferenceTemplateDTO extends DTO {
	/**
	 * Defines the possible values of the policy of a reference towards propagating
	 * service changes to the CDI runtime
	 */
	public enum Policy {
		/**
		 * Reboot the CDI component that depends on this reference
		 */
		STATIC,
		/**
		 * Update the CDI reference
		 */
		DYNAMIC
	}

	/**
	 * Defines the possible values of the policy of a satisfied reference towards
	 * new matching services appearing.
	 */
	public enum PolicyOption {
		/**
		 * Consume the matching service applying it's {@link Policy}
		 */
		GREEDY,
		/**
		 * Do not consume the matching service
		 */
		RELUCTANT
	}

	/**
	 * A unique within the container and persistent across reboots identified for
	 * this activation
	 * <p>
	 * The value must not be <code>null</code>. The value must be equal to to the
	 * reference name.
	 */
	public String				name;

	/**
	 * Indicates the type of service matched by the reference.
	 * <p>
	 * The value must not be null.
	 */
	public String				serviceType;

	/**
	 * Indicates a target filter used in addition to the {@link #serviceType} to
	 * match services.
	 * <p>
	 * Contains the target filter resolved from the CDI bundle metadata. The filter
	 * can be replaced by configuration at runtime.
	 */
	public String				targetFilter;

	/**
	 * The minimum cardinality of the reference.
	 * <p>
	 * Contains the minimum cardinality statically resolved from the CDI bundle
	 * metadata. The minimum cardinality can be replaced by configuration at
	 * runtime.
	 * <p>
	 * <ul>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#ONE ONE} the
	 * value must be either 0 or 1.</li>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#MANY MANY}
	 * the value must be from 0 to {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int					minimumCardinality;

	/**
	 * The maximum cardinality of the reference.
	 */
	public MaximumCardinality	maximumCardinality;

	/**
	 * Indicates if the reference is dynamic or static in nature.
	 */
	public Policy				policy;

	/**
	 * Indicates if the reference is greedy or reluctant in nature.
	 */
	public PolicyOption			policyOption;
}

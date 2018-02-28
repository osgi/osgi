/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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
import org.osgi.service.cdi.MaximumCardinality;
import org.osgi.service.cdi.ReferencePolicy;
import org.osgi.service.cdi.ReferencePolicyOption;

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
	public ReferencePolicy				policy;

	/**
	 * Indicates if the reference is greedy or reluctant in nature.
	 */
	public ReferencePolicyOption			policyOption;
}

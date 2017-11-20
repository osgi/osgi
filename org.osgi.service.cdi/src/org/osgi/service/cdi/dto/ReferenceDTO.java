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

package org.osgi.service.cdi.dto;

import java.util.List;
import org.osgi.dto.DTO;
import org.osgi.service.cdi.dto.template.MaximumCardinality;
import org.osgi.service.cdi.dto.template.ReferenceTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component} reference
 * dependency
 *
 * @NotThreadSafe
 * @author $Id: e8462ff43db112f04370ec4eb50253bbecb57de8 $
 */
public class ReferenceDTO extends DTO {
	/**
	 * The name of this reference. Matches the name of the template of this
	 * reference
	 * <p>
	 * Must not be {@code null}
	 */
	public String		name;

	/**
	 * The runtime minimum cardinality of the dependency.
	 * <p>
	 * <ul>
	 * <li>If {@link ReferenceTemplateDTO#maximumCardinality
	 * template.maximumCardinality} is {@link MaximumCardinality#ONE ONE} the value
	 * must be either 0 or 1.</li>
	 * <li>If {@link ReferenceTemplateDTO#maximumCardinality
	 * template.maximumCardinality} is {@link MaximumCardinality#MANY MANY} the
	 * value must be from 0 to {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int			minimumCardinality;

	/**
	 * Indicates the runtime target filter used in addition to the
	 * {@link ReferenceTemplateDTO#targetFilter model.serviceType} to match
	 * services.
	 */
	public String		targetFilter;

	/**
	 * The list of IDs of the services that match this reference.
	 * <p>
	 * The value must not be null. An empty array indicates no matching services.
	 * <p>
	 * This dependency is satisfied when.
	 * <p>
	 * <pre>
	 * {@link #minimumCardinality minimumCardinality} <= matches.length <= {@link MaximumCardinality#toInt()}
	 * </pre> where the maximum cardinality can be obtained from the associated
	 * {@link ReferenceTemplateDTO}.
	 */
	public List<Long>	matches;
}

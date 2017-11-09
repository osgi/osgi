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

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.model.DependencyModelDTO.MaximumCardinality;
import org.osgi.service.cdi.dto.model.ReferenceModelDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component} reference
 * dependency
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ReferenceDTO extends DependencyDTO {
	/**
	 * The static model of this reference dependency as resolved at initialization
	 * time.
	 */
	public ReferenceModelDTO		model;

	/**
	 * Indicates the runtime target filter used in addition to the
	 * {@link ReferenceModelDTO#targetFilter model.serviceType} to match services.
	 */
	public String					targetFilter;

	/**
	 * The set of services that match this reference.
	 * <p>
	 * The value must not be null. An empty array indicates no matching services.
	 * <p>
	 * This dependency is satisfied when.
	 * <p>
	 * <pre>
	 * {@link DependencyDTO#minimumCardinality minimumCardinality} <= matches.length <= {@link MaximumCardinality#toInt() model.maximumCardinality.toInt()}
	 * </pre>
	 */
	public ServiceReferenceDTO[]	matches;
}

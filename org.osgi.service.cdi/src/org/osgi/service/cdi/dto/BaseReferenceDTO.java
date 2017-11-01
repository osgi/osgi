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

package org.osgi.service.cdi.dto;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.annotations.Reference;

/**
 * Description of a CDI service dependency (an injection point marked with
 * {@link Reference}).
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class BaseReferenceDTO extends DTO {

	/**
	 * Defines the possible values for {@link #maximumCardinality}
	 */
	public enum MaximumCardinality {
		/**
		 * Defines a unary reference.
		 */
		ONE,
		/**
		 * Defines a plural reference.
		 */
		MANY
	}

	/**
	 * The name of the reference.
	 */
	public String name;

	/**
	 * The maximum cardinality of the reference.
	 */
	public MaximumCardinality	maximumCardinality;

	/**
	 * The minimum cardinality of the reference.
	 * <p>
	 * <ul>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#ONE ONE} the
	 * value must be either 0 or 1.</li>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#MANY MANY}
	 * the value must be from 0 to {@link Integer#MAX_VALUE}.
	 */
	public int						minimumCardinality;

	/**
	 * Indicates if the reference is greedy or reluctant in nature.
	 */
	public boolean	greedy;

	/**
	 * Indicates if the reference is dynamic or static in nature.
	 */
	public boolean	dynamic;

	/**
	 * Indicates a target filter used in addition to the {@link #serviceType} to
	 * match services.
	 */
	public String	target;

	/**
	 * Indicates the type of service matched by the reference.
	 */
	public String	serviceType;

}

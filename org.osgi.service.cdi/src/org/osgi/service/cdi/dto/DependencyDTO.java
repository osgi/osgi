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

import org.osgi.dto.DTO;
import org.osgi.service.cdi.dto.template.DependencyTemplateDTO;
import org.osgi.service.cdi.dto.template.DependencyTemplateDTO.MaximumCardinality;

/**
 * Base abstraction for the runtime state of a dependency
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class DependencyDTO extends DTO {
	/**
	 * The runtime minimum cardinality of the dependency.
	 * <p>
	 * <ul>
	 * <li>If {@link DependencyTemplateDTO#maximumCardinality} is
	 * {@link MaximumCardinality#ONE ONE} the value must be either 0 or 1.</li>
	 * <li>If {@link DependencyTemplateDTO#maximumCardinality} is
	 * {@link MaximumCardinality#MANY MANY} the value must be from 0 to
	 * {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int	minimumCardinality;
}

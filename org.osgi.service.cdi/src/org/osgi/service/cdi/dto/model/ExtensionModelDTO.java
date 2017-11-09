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

package org.osgi.service.cdi.dto.model;

import org.osgi.service.cdi.dto.ContainerDTO;

/**
 * Models an extension dependency of the {@link ContainerDTO}
 *
 * <ul>
 * <li>{@link DependencyModelDTO#maximumCardinality maximumCardinality} =
 * {@link DependencyModelDTO.MaximumCardinality#ONE ONE}</li>
 * <li>{@link DependencyModelDTO#minimumCardinality minimumCardinality} = 1</li>
 * <li>{@link DependencyModelDTO#dynamic dynamic} = false</li>
 * <li>{@link DependencyModelDTO#greedy greedy} = true</li>
 * </ul>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ExtensionModelDTO extends DependencyModelDTO {
	/**
	 * Target filter for the extension service.
	 * <p>
	 * The value must not be null.
	 */
	public String target;
}

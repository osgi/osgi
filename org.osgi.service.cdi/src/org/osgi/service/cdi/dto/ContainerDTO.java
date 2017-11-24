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
import org.osgi.service.cdi.dto.template.ComponentTemplateDTO.Type;
import org.osgi.service.cdi.dto.template.ContainerTemplateDTO;

/**
 * A snapshot of the runtime state of a CDI container
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ContainerDTO extends DTO {
	/**
	 * The {@link ContainerTemplateDTO#id} of the template associated with this
	 * {@link ContainerDTO}
	 * <p>
	 * Must not be {@code null}.
	 */
	public String				id;

	/**
	 * The change count of the container at the time this DTO was created
	 * <p>
	 * Must not be 0.
	 */
	public long					changeCount;

	/**
	 * The bundle declaring the CDI container.
	 * <p>
	 * Must not be 0.
	 */
	public long					bundle;

	/**
	 * The extension dependencies of this CDI container.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<ExtensionDTO>	extensions;

	/**
	 * The component lifecycles defined by this CDI container.
	 * <p>
	 * Must not be {@code null}. The list always contains at least one element
	 * representing the {@link Type#APPLICATION APPLICATION}.
	 */
	public List<LifecycleDTO>	componentLifecycles;
}

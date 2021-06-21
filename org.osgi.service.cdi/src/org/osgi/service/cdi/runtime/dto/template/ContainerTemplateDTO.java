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

package org.osgi.service.cdi.runtime.dto.template;

import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.ComponentType;

/**
 * Description of a CDI container.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ContainerTemplateDTO extends DTO {
	/**
	 * The id of the CDI container.
	 */
	public String id;

	/**
	 * The extension dependencies of this CDI container.
	 * <p>
	 * Must not be {@code null}
	 * <p>
	 * May be empty if the CDI container does not require CDI extensions.
	 */
	public List<ExtensionTemplateDTO>	extensions;

	/**
	 * The components defined in this CDI container.
	 * <p>
	 * Must not be {@code null}
	 * <p>
	 * Has at lest one element for the {@link ComponentType#CONTAINER container component}.
	 * See <em>Container Component</em>.
	 */
	public List<ComponentTemplateDTO>	components;
}

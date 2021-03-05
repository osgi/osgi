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

import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;

/**
 * Models an extension dependency of the {@link ContainerDTO}
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ExtensionTemplateDTO extends DTO {
	/**
	 * The service filter used for finding the extension service.
	 * <p>
	 * The value must be associated to the {@code osgi.cdi} extender requirement
	 * whose '{@code extension}' attribute contains a value equal to
	 * {@link #serviceFilter}.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String	serviceFilter;
}

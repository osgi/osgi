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

package org.osgi.service.cdi.runtime.dto;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.template.ActivationTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentInstanceDTO component}
 * activation
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationDTO extends DTO {
	/**
	 * The {@link ActivationTemplateDTO#name} describing this activation
	 * <p>
	 * Must not be {@code null}
	 */
	public String	name;

	/**
	 * The ID of the service this activation may have registered.
	 * <p>
	 * Must be greater than 0 if {@link ActivationTemplateDTO#serviceClasses} of the
	 * associated template is not empty.
	 */
	public long		service;
}

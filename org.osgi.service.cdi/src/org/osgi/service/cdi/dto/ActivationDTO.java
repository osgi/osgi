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
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.template.ActivationTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component}
 * activation
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationDTO extends DTO {
	/**
	 * The template of this activation
	 */
	public ActivationTemplateDTO	template;

	/**
	 * The service this activation may have registered.
	 * <p>
	 * Must not be null if {@link ActivationTemplateDTO#serviceClasses
	 * model.serviceClasses} is not empty.
	 */
	public ServiceReferenceDTO	service;

	/**
	 * The number of objects this activation has created.
	 * <p>
	 * Each instance is dependency injected.
	 * <p>
	 * Depends on {@link ActivationTemplateDTO#scope model.scope}
	 */
	public int					instances;
}

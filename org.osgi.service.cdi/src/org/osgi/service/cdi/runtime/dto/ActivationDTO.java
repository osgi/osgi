/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.runtime.dto.template.ActivationTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentInstanceDTO component}
 * activation.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationDTO extends DTO {
	/**
	 * The template describing this activation.
	 * <p>
	 * Must not be {@code null}
	 */
	public ActivationTemplateDTO	template;

	/**
	 * The service this activation may have registered.
	 * <p>
	 * Must not be {@code null} if {@link #template
	 * template}.{@link ActivationTemplateDTO#serviceClasses serviceClasses} is
	 * not empty.
	 */
	public ServiceReferenceDTO		service;

	/**
	 * The list of errors which occurred during initialization. An empty list
	 * means there were no errors.
	 * <p>
	 * Must not be {@code null}.
	 */
	public List<String>	errors;
}

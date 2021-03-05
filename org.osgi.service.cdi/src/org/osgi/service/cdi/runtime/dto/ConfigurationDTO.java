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

package org.osgi.service.cdi.runtime.dto;

import java.util.Map;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.template.ConfigurationTemplateDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component
 * factory} configuration dependency
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ConfigurationDTO extends DTO {
	/**
	 * The template of this configuration dependency
	 * <p>
	 * Must never be {@code null}
	 */
	public ConfigurationTemplateDTO	template;

	/**
	 * The properties of this configuration.
	 * <p>
	 * The configuration dependency is satisfied when not {@code null}.
	 */
	public Map<String,Object>		properties;
}

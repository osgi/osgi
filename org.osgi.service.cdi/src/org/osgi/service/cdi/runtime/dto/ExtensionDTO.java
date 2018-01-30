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

import org.osgi.dto.DTO;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.cdi.runtime.dto.template.ExtensionTemplateDTO;

/**
 * A snapshot of the runtime state of an
 * {@code javax.enterprise.inject.spi.Extension} dependency required by this CDI
 * container.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ExtensionDTO extends DTO {
	/**
	 * The template of this extension dependency.
	 * <p>
	 * Must not be {@code null}
	 */
	public ExtensionTemplateDTO	template;

	/**
	 * The bundle providing the extension.
	 * <p>
	 * This extension is satisfied when not {@code null}.
	 */
	public BundleDTO			bundle;
}

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

package org.osgi.service.cdi.runtime.dto.template;

import org.osgi.dto.DTO;
import org.osgi.framework.Version;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;

/**
 * Models an extension dependency of the {@link ContainerDTO}
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ExtensionTemplateDTO extends DTO {
	/**
	 * The name of the extension. The name must be unique within the container.
	 * <p>
	 * The value must be associated to a requirement in the
	 * {@code osgi.cdi.extension} namespace who's attribute
	 * {@code osgi.cdi.extension} value is equal to {@link #name}.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String	name;

	/**
	 * The implementation class name of the extension.
	 * <p>
	 * The value must be associated to a requirement in the
	 * {@code osgi.cdi.extension} namespace who's attribute {@code implementation}
	 * value is equal to {@link #implementation}.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String	implementation;

	/**
	 * The version of the extension.
	 * <p>
	 * The value must be associated to a requirement in the
	 * {@code osgi.cdi.extension} namespace who's attribute {@code version} value is
	 * equal to {@link #version}.
	 * <p>
	 * The value may be {@code null} if no version was specified in the requirement.
	 */
	public Version	version;
}

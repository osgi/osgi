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

package org.osgi.service.cdi.dto.template;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.dto.ContainerDTO;

/**
 * Models an extension dependency of the {@link ContainerDTO}
 *
 * @NotThreadSafe
 * @author $Id: 2a6b16c6e027fe7f1b545282296fe821720b28d2 $
 */
public class ExtensionTemplateDTO extends DTO {
	/**
	 * A name for this extension dependency that is unique within the container and
	 * persistent across container restarts.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String	name;

	/**
	 * Target LDAP filter used to select extension services.
	 * <p>
	 * Must not be {@code null}.
	 */
	public String	target;
}

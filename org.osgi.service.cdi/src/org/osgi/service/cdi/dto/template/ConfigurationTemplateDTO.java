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

/**
 * A description of a configuration dependency of a component
 *
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ConfigurationTemplateDTO extends DTO {
	/**
	 * A name for this extension dependency that is unique within the container and
	 * persistent across container restarts.
	 * <p>
	 * Equal to {@link #targetPid}.
	 */
	public String				name;

	/**
	 * The PID of the tracked configuration objects
	 * <p>
	 * Must not be {@code null}.
	 */
	public String				targetPid;

	/**
	 * The maximum cardinality of the configuration dependency.
	 * <p>
	 * <ul>
	 * <li>When {@link MaximumCardinality#ONE} this is a singleton
	 * configuration.</li>
	 * <li>When {@link MaximumCardinality#MANY} this is a factory
	 * configuration.</li>
	 * </ul>
	 * <p>
	 * Must not be {@code null}.
	 */
	public MaximumCardinality	maximumCardinality;
}

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

import java.util.List;
import org.osgi.dto.DTO;
import org.osgi.service.cdi.runtime.dto.template.MaximumCardinality;
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
	 * The Id of the template of this configuration dependency
	 * <p>
	 * Must never be {@code null}
	 */
	public String	name;

	/**
	 * The list of {@code service.pid} of the configurations that match this
	 * configuration dependency.
	 * <p>
	 * Must never be {@code null}
	 * <p>
	 * Can be empty when three are no matching configurations.
	 * <p>
	 * This dependency is satisfied when: <pre>
	 * 1 <= matches.length <= {@link MaximumCardinality#toInt()}
	 * </pre> where the maximum cardinality can be obtained from the respective
	 * {@link ConfigurationTemplateDTO}
	 * <p>
	 */
	public List<String>	matches;
}

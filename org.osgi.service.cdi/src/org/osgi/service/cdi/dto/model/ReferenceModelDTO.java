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

package org.osgi.service.cdi.dto.model;

/**
 * A description of a reference dependency of a component
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ReferenceModelDTO extends DependencyModelDTO {
	/**
	 * The name of the reference.
	 * <p>
	 * The value must not be null.
	 */
	public String	name;

	/**
	 * Indicates the type of service matched by the reference.
	 * <p>
	 * The value must not be null.
	 */
	public String	targetType;

	/**
	 * Indicates a target filter used in addition to the {@link #targetType} to
	 * match services.
	 * <p>
	 * Contains the target filter resolved from the CDI bundle metadata. The filter
	 * can be replaced by configuration at runtime.
	 */
	public String	targetFilter;
}

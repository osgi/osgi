/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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

package org.osgi.service.dal;

import java.util.Map;

/**
 * Contains metadata about function operation.
 * 
 * @see Function
 * @see PropertyMetadata
 */
public interface OperationMetadata {

	/**
	 * Metadata key, which value represents the operation description. The
	 * property value type is {@code java.lang.String}.
	 */
	public static final String	DESCRIPTION	= "description";

	/**
	 * Returns metadata about the function operation. The keys of the
	 * {@code java.util.Map} result must be of {@code java.lang.String} type.
	 * Possible keys:
	 * <ul>
	 * <li>{@link #DESCRIPTION}</li>
	 * <li>custom key</li>
	 * </ul>
	 * 
	 * @return The operation metadata or {@code null} if no such metadata is
	 *         available.
	 */
	public Map<String, ? > getMetadata();

	/**
	 * Returns metadata about the operation return value or {@code null} if no
	 * such metadata is available.
	 * 
	 * @return Operation return value metadata.
	 */
	public PropertyMetadata getReturnValueMetadata();

	/**
	 * Returns metadata about the operation parameters or {@code null} if no
	 * such metadata is available.
	 * 
	 * @return Operation parameters metadata.
	 */
	public PropertyMetadata[] getParametersMetadata();
}

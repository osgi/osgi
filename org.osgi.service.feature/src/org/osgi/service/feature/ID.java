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
package org.osgi.service.feature;

import java.util.Optional;

import org.osgi.annotation.versioning.ProviderType;

/**
 * ID used to denote an artifact. This could be a feature model, a bundle which
 * is part of the feature model or some other artifact.
 * <p>
 * Artifact IDs follow the Maven convention of having:
 * <ul>
 * <li>A group ID
 * <li>An artifact ID
 * <li>A version
 * <li>A type identifier (optional)
 * <li>A classifier (optional)
 * </ul>
 * 
 * @ThreadSafe
 */
@ProviderType
public interface ID {
	/**
	 * ID type for use with Features.
	 */
	static final String FEATURE_ID_TYPE = "osgifeature";

	/**
	 * Get the group ID.
	 * 
	 * @return The group ID.
	 */
	String getGroupId();

	/**
	 * Get the artifact ID.
	 * 
	 * @return The artifact ID.
	 */
	String getArtifactId();

	/**
	 * Get the version.
	 * 
	 * @return The version.
	 */
	String getVersion();

	/**
	 * Get the type identifier.
	 * 
	 * @return The type identifier.
	 */
	Optional<String> getType();

	/**
	 * Get the classifier.
	 * 
	 * @return The classifier.
	 */
	Optional<String> getClassifier();

	/**
	 * This method returns the ID using the following syntax:
	 * <p>
	 * {@code groupId ':' artifactId ( ':' type ( ':' classifier )? )? ':' version }
	 *
	 * @return The string representation.
	 */
	@Override
	String toString();
}

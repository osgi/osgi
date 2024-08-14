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
package org.osgi.service.featurelauncher.repository;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.featurelauncher.FeatureLauncherConstants;

/**
 * A {@link ArtifactRepositoryFactory} is used to create implementations of
 * {@link ArtifactRepository} for one of the built in repository types:
 * <ul>
 * <li>Local File System</li>
 * <li>HTTP repository</li>
 * </ul>
 */
@ProviderType
public interface ArtifactRepositoryFactory {

	/**
	 * Create an {@link ArtifactRepository} using the local file system
	 * 
	 * @param path a path to the root of a Maven Repository Layout containing
	 *            installable artifacts
	 * @return an {@link ArtifactRepository} using the local file system
	 * @throws IllegalArgumentException if the path does not exist, or exists
	 *             and is not a directory
	 * @throws NullPointerException if the path is <code>null</code>
	 */
	ArtifactRepository createRepository(Path path);

	/**
	 * Create an {@link ArtifactRepository} using a remote Maven repository.
	 * 
	 * @param uri the URI for the repository. The <code>http</code>,
	 *            <code>https</code> and <code>file</code> schemes must be
	 *            supported by all implementations.
	 * @param props the configuration properties for the remote repository. See
	 *            {@link FeatureLauncherConstants} for standard property names
	 * @return an {@link ArtifactRepository} using the local file system
	 * @throws IllegalArgumentException if the uri scheme is not supported by
	 *             this implementation
	 * @throws NullPointerException if the path is <code>null</code>
	 */
	ArtifactRepository createRepository(URI uri, Map<String,Object> props);

}

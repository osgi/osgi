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
import java.util.Map;

/**
 * Defines standard constants for creating {@link ArtifactRepository} instances
 * using the {@link ArtifactRepositoryFactory}
 * 
 * @author $Id$
 */
public final class ArtifactRepositoryConstants {
	private ArtifactRepositoryConstants() {
		// non-instantiable
	}

	/**
	 * The configuration property key used to set the repository name when
	 * creating an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_NAME					= "name";

	/**
	 * The configuration property key used to set the repository user when
	 * creating an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_USER					= "user";

	/**
	 * The configuration property key used to set the repository password when
	 * creating an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_PASSWORD				= "password";

	/**
	 * The configuration property key used to set the bearer token when creating
	 * an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_BEARER_TOKEN			= "token";

	/**
	 * The configuration property key used to set that SNAPSHOT release versions
	 * are enabled for an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_SNAPSHOTS_ENABLED		= "snapshot";

	/**
	 * The configuration property key used to set that release versions are
	 * enabled for an {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_RELEASES_ENABLED		= "release";

	/**
	 * The configuration property key used to set the trust store to be used
	 * when accessing a remote {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_TRUST_STORE				= "truststore";

	/**
	 * The configuration property key used to set the trust store format to be
	 * used when accessing a remote {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_TRUST_STORE_FORMAT		= "truststoreFormat";

	/**
	 * The configuration property key used to set the trust store password to be
	 * used when accessing a remote {@link ArtifactRepository} using
	 * {@link ArtifactRepositoryFactory#createRepository(URI, Map)}
	 */
	public static final String	ARTIFACT_REPOSITORY_TRUST_STORE_PASSWORD	= "truststorePassword";
}

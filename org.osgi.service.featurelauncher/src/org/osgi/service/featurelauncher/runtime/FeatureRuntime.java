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
package org.osgi.service.featurelauncher.runtime;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Constants;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.ID;
import org.osgi.service.featurelauncher.ArtifactRepository;
import org.osgi.service.featurelauncher.ArtifactRepositoryFactory;
import org.osgi.service.featurelauncher.LaunchException;

/**
 * The Feature runtime service allows features to be installed and removed
 * dynamically at runtime. This is a {@link Constants#SCOPE_PROTOTYPE} scope
 * service and each instance maintains a separate collection of
 * {@link ArtifactRepository} instances, allowing for additional
 * {@link ArtifactRepository} instances to be added in order to install a single
 * feature.
 * <p>
 * Instances should not be shared between threads.
 */
@ProviderType
public interface FeatureRuntime extends ArtifactRepositoryFactory {

	/**
	 * Add an {@link ArtifactRepository} for use by this {@link FeatureRuntime}
	 * instance. If an {@link ArtifactRepository} is already set for the given
	 * name then it will be replaced.
	 * 
	 * @param name the name to use for this repository
	 * @param repository the repository
	 * @return <code>this</code>
	 */
	FeatureRuntime addRepository(String name, ArtifactRepository repository);

	/**
	 * Remove an {@link ArtifactRepository} from this {@link FeatureRuntime}.
	 * 
	 * @param name the name of the repository to remove
	 * @return <code>this</code>
	 */
	FeatureRuntime removeRepository(String name);

	/**
	 * Get the default repositories for the {@link FeatureRuntime} service.
	 * These are the repositories which would be used unless they were removed
	 * using {@link #removeRepository(String)} or replaced using
	 * {@link #addRepository(String, ArtifactRepository)}.
	 * <p>
	 * This method can be used to reset the repositories for a
	 * {@link FeatureRuntime} without having to obtain a new instance from the
	 * service registry
	 * 
	 * @return the default repositories
	 */
	Map<String,ArtifactRepository> getDefaultRepositories();

	/**
	 * Get the currently used repositories for this {@link FeatureRuntime}
	 * service.
	 * 
	 * @return a {@link Map} of repositories where the key is the name given in
	 *         {@link #addRepository(String, ArtifactRepository)}.
	 */
	Map<String,ArtifactRepository> getRepositories();

	/**
	 * Install a feature into the runtime
	 * 
	 * @param feature the feature to launch
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 * @throws LaunchException if installation fails
	 */
	InstalledFeature install(Feature feature);

	/**
	 * Install a feature into the runtime based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 * @throws LaunchException if installation fails
	 */
	InstalledFeature install(Reader jsonReader);

	/**
	 * Install a feature into the runtime based on the supplied feature and
	 * variables
	 * 
	 * @param feature the feature to launch
	 * @param variables key/value pairs to set variables in the feature
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 * @throws LaunchException if installation fails
	 */
	InstalledFeature install(Feature feature, Map<String,Object> variables);

	/**
	 * Install a feature into the runtime based on the supplied feature JSON and
	 * variables
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @param variables key/value pairs to set variables in the feature
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 * @throws LaunchException if installation fails
	 */
	InstalledFeature install(Reader jsonReader, Map<String,Object> variables);

	/**
	 * Get the features that have been installed by the {@link FeatureRuntime}
	 * service
	 * 
	 * @return a list of installed features
	 */
	List<InstalledFeature> getInstalledFeatures();

	/**
	 * Remove an installed feature
	 * 
	 * @param featureId the feature id
	 */
	void remove(ID featureId);

	/**
	 * Update a feature in the runtime
	 * 
	 * @param featureId the id of the feature to update
	 * @param feature the feature to launch
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	InstalledFeature update(ID featureId, Feature feature);

	/**
	 * Update a feature in the runtime based on the supplied feature JSON
	 * 
	 * @param featureId the id of the feature to update
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	InstalledFeature update(ID featureId, Reader jsonReader);

	/**
	 * Update a feature in the runtime based on the supplied feature and
	 * variables
	 * 
	 * @param featureId the id of the feature to update
	 * @param feature the feature to launch
	 * @param variables key/value pairs to set variables in the feature
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	InstalledFeature update(ID featureId, Feature feature,
			Map<String,Object> variables);

	/**
	 * Update a feature in the runtime based on the supplied feature JSON and
	 * variables
	 * 
	 * @param featureId the id of the feature to update
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @param variables key/value pairs to set variables in the feature
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	InstalledFeature update(ID featureId, Reader jsonReader,
			Map<String,Object> variables);

}

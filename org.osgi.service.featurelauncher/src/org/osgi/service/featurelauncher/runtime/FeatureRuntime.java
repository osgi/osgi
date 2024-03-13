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
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.ID;

/**
 * The Feature runtime service allows features to be installed and removed
 * dynamically at runtime.
 * 
 * @ThreadSafe
 */
@ProviderType
public interface FeatureRuntime {
    /**
	 * Install a feature into the runtime
	 * 
	 * @param feature the feature to launch
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 */
	InstalledFeature install(Feature feature);

	/**
	 * Install a feature into the runtime based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return An installedFeature representing the results of installing this
	 *         feature
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
	 * Remove an installed feature
	 * 
	 * @param featureId the feature id
	 */
	void remove(String featureId);

}

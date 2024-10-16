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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.Feature;
import org.osgi.service.featurelauncher.FeatureLauncher;

/**
 * An {@link InstalledFeature} represents the current state of a feature
 * installed by the {@link FeatureRuntime}.
 * <p>
 * This type is a snapshot and represents the state of the runtime when it was
 * created. It may become out of date if additional features are installed or
 * removed.
 */
@ProviderType
public interface InstalledFeature {

	/**
	 * @return The {@link Feature} that was installed. This will be identical to
	 *         {@link #getOriginalFeature()} unless the feature was decorated
	 *         during installation. If decoration did occur then
	 *         {@link #isDecorated()} will return <code>true</code> and this
	 *         method will return the decorated feature.
	 */
	public Feature getFeature();

	/**
	 * @return The undecorated {@link Feature} that was originally used in the
	 *         operation. If no decoration occurred then {@link #isDecorated()}
	 *         will return <code>false</code> and this method will return the
	 *         same value as {@link #getFeature()};
	 */
	public Feature getOriginalFeature();

	/**
	 * @return <code>true</code> if the original feature was decorated by one or
	 *         more decorators. If <code>true</code> then the undecorated
	 *         feature will be available from {@link #getOriginalFeature()} and
	 *         the actual feature used will be available from
	 *         {@link #getFeature()}.
	 */
	public boolean isDecorated();

	/**
	 * Is this a feature installed by {@link FeatureLauncher}
	 * 
	 * @return <code>true</code> If this feature was installed as part of a
	 *         {@link FeatureLauncher} launch operation. <code>false</code> if
	 *         it was installed by the {@link FeatureRuntime}
	 */
	public boolean isInitialLaunch();

    /**
	 * Get the bundles installed by this feature
	 * 
	 * @return A {@link List} of the bundles installed by this feature, in the
	 *         order they were declared by the feature
	 */
	public List<InstalledBundle> getInstalledBundles();

	/**
	 * Get the configurations installed by this feature
	 * 
	 * @return A {@link List} of the configurations installed by this feature,
	 *         in the order they were declared by the feature
	 */
	public List<InstalledConfiguration> getInstalledConfigurations();
}

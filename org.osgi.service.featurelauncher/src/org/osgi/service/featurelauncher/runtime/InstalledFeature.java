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
import org.osgi.service.feature.ID;
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
public final class InstalledFeature {

	/**
	 * The {@link ID} of the installed feature
	 */
	public ID							featureId;

	/**
	 * <code>true</code> If this feature was installed as part of a
	 * {@link FeatureLauncher} launch operation. <code>false</code> if it was
	 * installed by the {@link FeatureRuntime}
	 */
	public boolean						initialLaunch;

    /**
	 * A {@link List} of the bundles installed by this feature
	 */
	public List<InstalledBundle>		installedBundles;


	/**
	 * A list of the configurations that were installed by this feature
	 */
	public List<InstalledConfiguration>	installedConfigurations;
}
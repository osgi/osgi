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

package org.osgi.service.featurelauncher;

import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.feature.FeatureExtension.Kind;
import org.osgi.service.feature.FeatureExtension.Type;
import org.osgi.service.feature.ID;

/**
 * Defines standard constants for the Feature Launcher specification.
 * 
 * @author $Id$
 */
public final class FeatureLauncherConstants {
	private FeatureLauncherConstants() {
		// non-instantiable
	}

	/**
	 * The name of the implementation capability for the Feature specification.
	 */
	public static final String	FEATURE_LAUNCHER_IMPLEMENTATION			= "osgi.featurelauncher";

	/**
	 * The version of the implementation capability for the Feature
	 * specification.
	 */
	public static final String	FEATURE_LAUNCHER_SPECIFICATION_VERSION	= "1.0";

	/**
	 * The name for the {@link FeatureExtension} which defines the framework
	 * that should be used to launch the feature. The extension must be of
	 * {@link Type#ARTIFACTS} and contain one or more {@link ID} entries
	 * corresponding to OSGi framework implementations. This extension must be
	 * processed even if it is {@link Kind#OPTIONAL} or {@link Kind#TRANSIENT}.
	 * <p>
	 * If more than one framework entry is provided then the list will be used
	 * as a priority order when determining the framework implementation to use.
	 * If none of the frameworks are present then an error is raised and
	 * launching will be aborted.
	 */
	public static final String	LAUNCH_FRAMEWORK						= "launch-framework";
	
	/**
	 * The name for the {@link FeatureExtension} which defines the framework
	 * properties that should be used when launching the feature.
	 */
	public static final String	FRAMEWORK_LAUNCHING_PROPERTIES			= "framework-launching-properties";
}

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
package org.osgi.service.featurelauncher.decorator;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.BuilderFactory;
import org.osgi.service.feature.FeatureArtifactBuilder;
import org.osgi.service.feature.FeatureBuilder;
import org.osgi.service.feature.FeatureBundleBuilder;
import org.osgi.service.feature.FeatureConfigurationBuilder;
import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.feature.FeatureExtensionBuilder;
import org.osgi.service.feature.ID;

/**
 * The Builder Factory can be used to obtain builders for the various entities.
 * <p>
 * This is similar to {@link BuilderFactory} but does not permit the creation of
 * {@link FeatureBuilder} instances.
 */
@ProviderType
public interface DecoratorBuilderFactory {
	/**
	 * Obtain a new builder for Artifact objects.
	 * 
	 * @param id The artifact ID for the artifact object being built.
	 * @return The builder.
	 */
	FeatureArtifactBuilder newArtifactBuilder(ID id);

    /**
	 * Obtain a new builder for Bundle objects.
	 * 
	 * @param id The ID for the bundle object being built. If the ID has no
	 *            {@code type} specified, a default type of @{code jar} is
	 *            assumed.
	 * @return The builder.
	 */
    FeatureBundleBuilder newBundleBuilder(ID id);

    /**
     * Obtain a new builder for Configuration objects.
     * @param pid The persistent ID for the Configuration being built.
     * @return The builder.
     */
    FeatureConfigurationBuilder newConfigurationBuilder(String pid);

    /**
     * Obtain a new builder for Factory Configuration objects.
     * @param factoryPid The factory persistent ID for the Configuration being built.
     * @param name The name of the configuration being built. The PID for the configuration
     * will be the factoryPid + '~' + name
     * @return The builder.
     */
    FeatureConfigurationBuilder newConfigurationBuilder(String factoryPid, String name);

    /**
     * Obtain a new builder for Feature objects.
     * @param name The extension name.
     * @param type The type of extension: JSON, Text or Artifacts.
     * @param kind The kind of extension: Mandatory, Optional or Transient.
     * @return The builder.
     */
    FeatureExtensionBuilder newExtensionBuilder(String name, FeatureExtension.Type type, FeatureExtension.Kind kind);
}

/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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
package org.osgi.util.feature;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Context provided by the caller for the merge operation.
 */
@ProviderType
public interface MergeContext {
    /**
     * If two merged features both contain the same bundle, same group ID and
     * artifact ID but different version, this method is called to resolve what to
     * do.
     * @param f1 The first feature.
     * @param b1 The first bundle.
     * @param f2 The second feature.
     * @param b2 The second bundle.
     * @return Return a list of bundles that should be used in this case. This could
     * be one or both of the provided bundles, or a different bundle altogether.
     */
    List<FeatureBundle> handleBundleConflict(Feature f1, FeatureBundle b1, Feature f2, FeatureBundle b2);

    /**
     * If two merged features both contain the same configuration PID, this method
     * is called to perform the merge operation.
     * @param f1 The first feature.
     * @param c1 The first configuration.
     * @param f2 The second feature.
     * @param c2 The second configuration.
     * @return The merged configuration to use.
     */
    FeatureConfiguration handleConfigurationConflict(Feature f1, FeatureConfiguration c1, Feature f2, FeatureConfiguration c2);

    /**
     * If two merged features both contain an extension with the same IF, this method
     * is called to perform the merge operation.
     * @param f1 The first feature.
     * @param e1 The first extension.
     * @param f2 The second feature.
     * @param e2 The second extension.
     * @return The merged extension.
     */
    FeatureExtension handleExtensionConflict(Feature f1, FeatureExtension e1, Feature f2, FeatureExtension e2);
}

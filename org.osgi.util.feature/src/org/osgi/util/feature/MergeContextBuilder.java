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
package org.osgi.util.feature;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A builder for {@link MergeContext} objects.
 * @NotThreadSafe
 */
@ProviderType
public interface MergeContextBuilder {
    /**
     * Set the Bundle Conflict Resolver.
     * @param bh The Conflict Resolver.
     * @return This builder.
     */
    MergeContextBuilder bundleConflictHandler(ConflictResolver<FeatureBundle, List<FeatureBundle>> bh);

    /**
     * Set the Configuration Conflict Resolver.
     * @param ch The Conflict Resolver.
     * @return This builder.
     */
    MergeContextBuilder configConflictHandler(ConflictResolver<FeatureConfiguration, FeatureConfiguration> ch);

    /**
     * Set the Extension Conflict Resolver.
     * @param eh The Conflict Resolver.
     * @return This builder.
     */
    MergeContextBuilder extensionConflictHandler(ConflictResolver<FeatureExtension, FeatureExtension> eh);

    /**
     * Build the Merge Context. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Merge Context.
     */
    MergeContext build();

}

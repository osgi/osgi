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
package org.osgi.service.feature;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A builder for {@link FeatureArtifact} objects.
 * 
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureArtifactBuilder {
	/**
	 * Add metadata for this Artifact.
	 * 
	 * @param key Metadata key.
	 * @param value Metadata value.
	 * @return This builder.
	 */
	FeatureArtifactBuilder addMetadata(String key, Object value);

	/**
	 * Add metadata for this Artifact by providing a map. All metadata in the
	 * map is added to any previously provided metadata.
	 * 
	 * @param md The map with metadata.
	 * @return This builder.
	 */
	FeatureArtifactBuilder addMetadata(Map<String,Object> md);

	/**
	 * Build the Artifact object. Can only be called once on a builder. After
	 * calling this method the current builder instance cannot be used any more.
	 * 
	 * @return The Feature Artifact.
	 */
	FeatureArtifact build();

}

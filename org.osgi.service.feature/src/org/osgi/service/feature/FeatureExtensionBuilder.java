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

import org.osgi.annotation.versioning.ProviderType;

/**
 * A builder for Feature Model {@link FeatureExtension} objects.
 * @NotThreadSafe
 */
@ProviderType
public interface FeatureExtensionBuilder {

    /**
	 * Add a line of text to the extension. Can only be called for extensions of
	 * type {@link FeatureExtension.Type#TEXT}.
	 * 
	 * @param text The text to be added.
	 * @return This builder.
	 */
    FeatureExtensionBuilder addText(String text);

    /**
	 * Add JSON in String form to the extension. Can only be called for
	 * extensions of type {@link FeatureExtension.Type#JSON}.
	 * 
	 * @param json The JSON to be added.
	 * @return This builder.
	 */
    FeatureExtensionBuilder setJSON(String json);

    /**
	 * Add an Artifact to the extension. Can only be called for extensions of
	 * type {@link FeatureExtension.Type#ARTIFACTS}.
	 * 
	 * @param aid The ArtifactID of the artifact to add.
	 * @return This builder.
	 */
    FeatureExtensionBuilder addArtifact(ID aid);

    /**
	 * Add an Artifact to the extension. Can only be called for extensions of
	 * type {@link FeatureExtension.Type#ARTIFACTS}.
	 * 
	 * @param groupId The Group ID of the artifact to add.
	 * @param artifactId The Artifact ID of the artifact to add.
	 * @param version The Version of the artifact to add.
	 * @return This builder.
	 */
    FeatureExtensionBuilder addArtifact(String groupId, String artifactId, String version);

    /**
	 * Add an Artifact to the extension. Can only be called for extensions of
	 * type {@link FeatureExtension.Type#ARTIFACTS}.
	 * 
	 * @param groupId The Group ID of the artifact to add.
	 * @param artifactId The Artifact ID of the artifact to add.
	 * @param version The Version of the artifact to add.
	 * @param at The type indicator of the artifact to add.
	 * @param classifier The classifier of the artifact to add.
	 * @return This builder.
	 */
    FeatureExtensionBuilder addArtifact(String groupId, String artifactId, String version, String at, String classifier);

    /**
     * Build the Extension. Can only be called once on a builder. After
     * calling this method the current builder instance cannot be used any more.
     * @return The Extension.
     */
    FeatureExtension build();

}

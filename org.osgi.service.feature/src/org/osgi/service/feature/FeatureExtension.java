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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A Feature Model Extension. Extensions can contain either Text, JSON or
 * a list of Artifacts. <p>
 * Extensions are of one of the following kinds:
 * <ul>
 * <li> Mandatory: this extension must be processed by the runtime
 * <li> Optional: this extension does not have to be processed by the runtime
 * <li> Transient: this extension contains transient information such as caching
 * data that is for optimization purposes. It may be changed or removed and is
 * not part of the feature's identity.
 * </ul>
 * @ThreadSafe
 */
@ProviderType
public interface FeatureExtension {
	/**
	 * The kind of extension: optional, mandatory or transient.
	 */
	enum Kind {
		/**
		 * A mandatory extension must be processed.
		 */
		MANDATORY,

		/**
		 * An optional extension can be ignored if no processor is found.
		 */
		OPTIONAL,

		/**
		 * A transient extension contains computed information which can be used
		 * as a cache to speed up operation.
		 */
		TRANSIENT
	}

	/**
	 * The type of extension
	 */
	enum Type {
		/**
		 * A JSON extension.
		 */
		JSON,

		/**
		 * A plain text extension.
		 */
		TEXT,

		/**
		 * An extension that is a list of artifact identifiers.
		 */
		ARTIFACTS
	}

    /**
     * Get the extension name.
     * @return The name.
     */
    String getName();

    /**
     * Get the extension type.
     * @return The type.
     */
    Type getType();

    /**
     * Get the extension kind.
     * @return The kind.
     */
    Kind getKind();

    /**
     * Get the JSON from this extension.
     * @return The JSON, or {@code null} if this is not a JSON extension.
     */
    String getJSON();

    /**
	 * Get the Text from this extension.
	 * 
	 * @return The Text line by line, or {@code null} if this is not a Text
	 *         extension.
	 */
	List<String> getText();

    /**
     * Get the Artifacts from this extension.
     * @return The Artifacts, or {@code null} if this is not an Artifacts extension.
     */
    List<ID> getArtifacts();
}

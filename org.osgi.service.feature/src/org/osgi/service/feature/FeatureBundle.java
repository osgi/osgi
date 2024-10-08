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
 * A Bundle which is part of a feature.
 * @ThreadSafe
 */
@ProviderType
public interface FeatureBundle {
	/**
	 * Get the bundle's ID.
	 * 
	 * @return The ID of this bundle.
	 */
	ID getID();

	/**
	 * Get the metadata for this bundle.
	 * 
	 * @return The metadata. The returned map is unmodifiable.
	 */
	Map<String,Object> getMetadata();
}

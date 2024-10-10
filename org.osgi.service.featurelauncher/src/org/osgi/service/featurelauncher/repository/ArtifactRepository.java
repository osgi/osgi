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
package org.osgi.service.featurelauncher.repository;

import java.io.InputStream;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.feature.ID;

/**
 * An {@link ArtifactRepository} is used to get hold of the bytes used to
 * install an artifact. Users of this specification may provide their own
 * implementations for use when installing feature artifacts. Instances must be
 * Thread Safe.
 * 
 * @ThreadSafe
 */
@ConsumerType
public interface ArtifactRepository {

	/**
	 * Get a stream to the bytes of an artifact
	 * 
	 * @param id the id of the artifact
	 * @return an {@link InputStream} containing the bytes of the artifact or
	 *         <code>null</code> if this repository does not have access to the
	 *         bytes
	 */
	public InputStream getArtifact(ID id);

}

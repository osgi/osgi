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

package org.osgi.service.repository;

import java.io.InputStream;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Resource;

/**
 * An accessor for the content of a resource.
 * 
 * All {@link Resource} objects which represent resources in a
 * {@link Repository} must implement this interface. A user of the resource can
 * then cast the {@link Resource} object to this type and then obtain an
 * {@code InputStream} to the content of the resource.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface RepositoryContent {

	/**
	 * Returns a new input stream to the content of this resource. The content
	 * is represented on the resource through the {@code osgi.content}
	 * capability. If more than one such capability is associated with the
	 * resource, the first such capability is returned.
	 * 
	 * @return A new input stream for associated content.
	 */
	InputStream getContent();
}

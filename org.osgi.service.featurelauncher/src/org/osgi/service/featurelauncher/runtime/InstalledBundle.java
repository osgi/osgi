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
package org.osgi.service.featurelauncher.runtime;

import java.util.Collection;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.service.feature.ID;

/**
 * An {@link InstalledBundle} represents a configuration that has been
 * installed as a result of one or more feature installations.
 * <p>
 * This type is a snapshot and represents the state of the runtime when it was
 * created. It may become out of date if additional features are installed or
 * removed.
 */
@ProviderType
public interface InstalledBundle {

	/**
	 * Get the {@link ID} of the bundle that has been installed
	 * 
	 * @return the id of the bundle that was installed
	 */
	public ID getBundleId();

	/**
	 * Get any known IDs which correspond to the same bundle
	 * 
	 * @return an immutable collection of aliases for this bundle. Always
	 *         includes the id returned by {@link #getBundleId()}
	 */
	public Collection<ID> getAliases();

	/**
	 * The actual bundle installed in the framework
	 * 
	 * @return the {@link Bundle} installed for this {@link #getBundleId()}
	 */
	public Bundle getBundle();

	/**
	 * The start level for this bundle
	 * 
	 * @return the start level
	 */
	public int getStartLevel();

	/**
	 * The features responsible for this bundle being installed, in installation
	 * order
	 * 
	 * @return A list of Feature {@link ID}s
	 */
	public List<ID> getOwningFeatures();
}

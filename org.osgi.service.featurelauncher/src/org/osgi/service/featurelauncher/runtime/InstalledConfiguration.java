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

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.ID;

/**
 * An {@link InstalledConfiguration} represents a configuration that has been
 * installed as a result of one or more feature installations.
 * <p>
 * This type is a snapshot and represents the state of the runtime when it was
 * created. It may become out of date if additional features are installed or
 * removed.
 */
@ProviderType
public final class InstalledConfiguration {

	/**
	 * The {@link ID} of the installed feature
	 */
	public ID					featureId;

	/**
	 * The PID of the configuration
	 */
	public String				pid;

	/**
	 * The factory PID of the configuration, or <code>null</code> if this is not
	 * a factory configuration
	 */
	public String				factoryPid;

	/**
	 * The merged configuration properties for this configuration
	 */
	public Map<String,Object>	properties;

	/**
	 * The features responsible for creating this configuration
	 */
	public List<ID>				owningFeatures;
}

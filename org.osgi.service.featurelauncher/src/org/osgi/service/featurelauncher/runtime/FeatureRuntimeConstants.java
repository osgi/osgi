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

/**
 * Defines standard constants for the Feature Runtime.
 * 
 * @author $Id$
 */
public final class FeatureRuntimeConstants {
	private FeatureRuntimeConstants() {
		// non-instantiable
	}

	/**
	 * The ID of the virtual <em>external</em> feature representing ownership of
	 * a bundle or configuration that was deployed by another management agent.
	 */
	public static final String EXTERNAL_FEATURE_ID = "org.osgi.service.featurelauncher:external:1.0.0";


}

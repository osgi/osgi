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

/**
 * Defines standard constants for the Feature services.
 * 
 * @author $Id$
 */
public final class FeatureConstants {
	private FeatureConstants() {
		// non-instantiable
	}

	/**
	 * The name of the implementation capability for the Feature specification
	 */
	public static final String	FEATURE_IMPLEMENTATION			= "osgi.feature";

	/**
	 * The version of the implementation capability for the Feature
	 * specification
	 */
	public static final String FEATURE_SPECIFICATION_VERSION = "1.0.0";
}

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
 * A FeatureRuntimeException is thrown by the {@link FeatureRuntime} if it is
 * unable to:
 * <ul>
 * <li>Locate the installable bytes of any bundle in a Feature</li>
 * <li>Install a bundle in the Feature</li>
 * <li>Determine a value for a Feature variable that has no default value
 * defined</li>
 * <li>Successfully merge a feature with the existing environment</li>
 * </ul>
 */
public class FeatureRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1687639103344634839L;

	/**
	 * Create a LaunchException with the supplied error message
	 * 
	 * @param message
	 */
	public FeatureRuntimeException(String message) {
		super(message);
	}

	/**
	 * Create a LaunchException with the supplied error message and cause
	 * 
	 * @param message
	 * @param cause
	 */
	public FeatureRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}

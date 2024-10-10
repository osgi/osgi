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
package org.osgi.service.featurelauncher.decorator;

/**
 * An AbandonOperationException is thrown by a {@link FeatureDecorator} or
 * {@link FeatureExtensionHandler} if it needs to prevent the operation from
 * continuing. This may be because of a problem detected in the feature, or
 * because an extension has determined that the feature cannot be used in the
 * current environment.
 */
public final class AbandonOperationException extends Exception {

	private static final long serialVersionUID = 1687639103344634839L;

	/**
	 * Create an AbandonOperationException with the supplied error message
	 * 
	 * @param message
	 */
	public AbandonOperationException(String message) {
		super(message);
	}

	/**
	 * Create an AbandonOperationException with the supplied error message and cause
	 * 
	 * @param message
	 * @param cause
	 */
	public AbandonOperationException(String message, Throwable cause) {
		super(message, cause);
	}

}

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

package org.osgi.service.onem2m;

import java.io.IOException;

/**
 * General Exception for oneM2M.
 */
public class OneM2MException extends IOException {
	private static final long	serialVersionUID	= 7025371906099079000L;
	private final int			errorCode;

	/**
	 * Construct a OneM2MException with a message and an error code.
	 * 
	 * @param message The exception message.
	 * @param errorCode The exception error code.
	 */
	public OneM2MException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Return the error code for the exception.
	 * 
	 * @return The error code for the exception.
	 */
	public int getErrorCode() {
		return errorCode;
	}
}

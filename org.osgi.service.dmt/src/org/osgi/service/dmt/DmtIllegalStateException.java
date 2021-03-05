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

package org.osgi.service.dmt;

/**
 * Unchecked illegal state exception. This class is used in DMT because
 * java.lang.IllegalStateException does not exist in CLDC.
 * 
 * @author $Id$
 */
public class DmtIllegalStateException extends RuntimeException {
	private static final long	serialVersionUID	= 2015244852018469700L;

	/**
	 * Create an instance of the exception with no message.
	 */
	public DmtIllegalStateException() {
		super();
	}

	/**
	 * Create an instance of the exception with the specified message.
	 * 
	 * @param message the reason for the exception
	 */
	public DmtIllegalStateException(String message) {
		super(message);
	}

	/**
	 * Create an instance of the exception with the specified cause exception
	 * and no message.
	 * 
	 * @param cause the cause of the exception
	 */
	public DmtIllegalStateException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create an instance of the exception with the specified message and cause
	 * exception.
	 * 
	 * @param message the reason for the exception
	 * @param cause the cause of the exception
	 */
	public DmtIllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}
}

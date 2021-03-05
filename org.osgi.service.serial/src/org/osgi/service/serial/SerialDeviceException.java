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

package org.osgi.service.serial;

/**
 * A exception used to indicate that a serial device communication problem
 * occurred.
 * 
 * @author $Id$
 */
public class SerialDeviceException extends Exception {

	private static final long	serialVersionUID		= -3575343159595283441L;

	/**
	 * The reason is unknown.
	 */
	public static final int		UNKNOWN					= 0;

	/**
	 * The port in use.
	 */
	public static final int		PORT_IN_USE				= 1;

	/**
	 * The operation is unsupported.
	 */
	public static final int		UNSUPPORTED_OPERATION	= 2;

	private final int			type;

	/**
	 * Creates a SerialDeviceException with the specified type and message.
	 * 
	 * @param type The type for this exception.
	 * @param message The message.
	 */
	public SerialDeviceException(int type, String message) {
		super(message);
		this.type = type;
	}

	/**
	 * Returns the type for this exception.
	 * 
	 * @return The type of this exception.
	 */
	public int getType() {
		return type;
	}
}

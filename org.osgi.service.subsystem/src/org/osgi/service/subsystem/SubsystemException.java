/*
 * Copyright (c) OSGi Alliance (2011, 2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.subsystem;

/**
 * A Subsystem exception used to indicate a problem.
 * 
 * @author $Id$
 */
public class SubsystemException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Construct a Subsystem exception with no message.
	 */
	public SubsystemException() {
		super();
	}

	/**
	 * Construct a Subsystem exception specifying a message.
	 * 
	 * @param message The message to include in the exception.
	 */
	public SubsystemException(String message) {
		super(message);
	}

	/**
	 * Construct a Subsystem exception specifying a cause.
	 * 
	 * @param cause The cause of the exception.
	 */
	public SubsystemException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a Subsystem exception specifying a message and a cause.
	 * 
	 * @param message The message to include in the exception.
	 * @param cause The cause of the exception.
	 */
	public SubsystemException(String message, Throwable cause) {
		super(message, cause);
	}
}

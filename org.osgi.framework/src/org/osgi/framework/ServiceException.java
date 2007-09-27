/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
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

package org.osgi.framework;

/**
 * A service exception used to indicate that a service problem occurred.
 * 
 * <p>
 * A <code>ServiceException</code> object is created by the Framework or
 * service implementation to denote an exception condition in the service. A
 * type code is used to identify the exception type for future extendability.
 * 
 * <p>
 * OSGi Alliance reserves the right to extend the set of types. The type values
 * 0x0000 through 0x7FFF are reserved for use by the OSGi Alliance. Service
 * implementations wishing to define custom service exception types must not use
 * values in this reserved range.
 * 
 * <p>
 * This exception conforms to the general purpose exception chaining mechanism.
 * 
 * @version $Revision$
 * @since 1.5
 */

public class ServiceException extends RuntimeException {
	static final long		serialVersionUID	= 3038963223712959631L;
	/**
	 * Nested exception.
	 */
	private final Throwable	cause;

	/**
	 * Type of service exception.
	 */
	private final int		type;

	/**
	 * No exception type is unspecified.
	 */
	public static final int	UNSPECIFIED			= 0;
	/**
	 * The service has been unregistered.
	 */
	public static final int	UNREGISTERED		= 1;
	/**
	 * The service factory produced an invalid service object.
	 */
	public static final int	FACTORY_ERROR		= 2;
	/**
	 * The service factory threw an exception.
	 */
	public static final int	FACTORY_EXCEPTION	= 3;

	/**
	 * Creates a <code>ServiceException</code> with the specified message and
	 * exception cause.
	 * 
	 * @param msg The associated message.
	 * @param cause The cause of this exception.
	 */
	public ServiceException(String msg, Throwable cause) {
		this(msg, UNSPECIFIED, cause);
	}

	/**
	 * Creates a <code>ServiceException</code> with the specified message.
	 * 
	 * @param msg The message.
	 */
	public ServiceException(String msg) {
		this(msg, UNSPECIFIED, null);
	}

	/**
	 * Creates a <code>ServiceException</code> with the specified message,
	 * type and exception cause.
	 * 
	 * @param msg The associated message.
	 * @param type The type for this exception.
	 * @param cause The cause of this exception.
	 */
	public ServiceException(String msg, int type, Throwable cause) {
		super(msg);
		this.type = type;
		this.cause = cause;
	}

	/**
	 * Creates a <code>ServiceException</code> with the specified message and
	 * type.
	 * 
	 * @param msg The message.
	 * @param type The type for this exception.
	 */
	public ServiceException(String msg, int type) {
		this(msg, type, null);
	}

	/**
	 * Returns the cause of this exception or <code>null</code> if no cause
	 * was specified when this exception was created.
	 * 
	 * @return The cause of this exception or <code>null</code> if no cause
	 *         was specified.
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * The cause of this exception can only be set when constructed.
	 * 
	 * @param cause Cause of the exception.
	 * @return This object.
	 * @throws java.lang.IllegalStateException This method will always throw an
	 *         <code>IllegalStateException</code> since the cause of this
	 *         exception can only be set when constructed.
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}

	/**
	 * Returns the type for this exception or <code>UNSPECIFIED</code> if the
	 * type was unspecified or unknown.
	 * 
	 * @return The type of this exception.
	 */
	public int getType() {
		return type;
	}
}

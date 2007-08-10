/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2000, 2006). All Rights Reserved.
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
 * A Framework exception used to indicate that a bundle lifecycle problem
 * occurred.
 * 
 * <p>
 * <code>BundleException</code> object is created by the Framework to denote
 * an exception condition in the lifecycle of a bundle.
 * <code>BundleException</code>s should not be created by bundle developers.
 * A type code is used to identify the exception type for future extendability.
 * 
 * <p>
 * OSGi Alliance reserves the right to extend the set of types.
 * 
 * <p>
 * This exception is updated to conform to the general purpose exception
 * chaining mechanism.
 * 
 * @version $Revision$
 */

public class BundleException extends Exception {
	static final long	serialVersionUID	= 3571095144220455665L;
	/**
	 * Nested exception.
	 */
	private final Throwable	cause;
	
	/**
	 * Type of bundle exception.
	 * @since 1.5
	 */
	private final int type;
	
	/**
	 * No exception type is unspecified.
	 * @since 1.5
	 */
	public static final int UNSPECIFIED = 0;
	/**
	 * The operation was unsupported.
	 * @since 1.5
	 */
	public static final int UNSUPPORTED_OPERATION = 1;
	/**
	 * The operation was invalid.
	 * @since 1.5
	 */
	public static final int INVALID_OPERATION = 2;
	/**
	 * The bundle manifest was in error.
	 * @since 1.5
	 */
	public static final int MANIFEST_ERROR = 3;
	/**
	 * The bundle was not resolved.
	 * @since 1.5
	 */
	public static final int RESOLVE_ERROR = 4;
	/**
	 * The bundle activator was in error.
	 * @since 1.5
	 */
	public static final int ACTIVATOR_ERROR = 5;
	/**
	 * The operation failed due to insufficient permissions.
	 * @since 1.5
	 */
	public static final int SECURITY_ERROR = 6;
	/**
	 * The operation failed to complete the requested lifecycle state change.
	 * @since 1.5
	 */
	public static final int STATECHANGE_ERROR = 7;

	/**
	 * Creates a <code>BundleException</code> 
	 * with the specified message and exception cause.
	 * 
	 * @param msg The associated message.
	 * @param cause The cause of this exception.
	 */
	public BundleException(String msg, Throwable cause) {
		this(msg, UNSPECIFIED, cause);
	}

	/**
	 * Creates a <code>BundleException</code> with the specified
	 * message.
	 * 
	 * @param msg The message.
	 */
	public BundleException(String msg) {
		this(msg, UNSPECIFIED, null);
	}

	/**
	 * Creates a <code>BundleException</code> 
	 * with the specified message, type and exception cause.
	 * 
	 * @param msg The associated message.
	 * @param type The type for this exception.
	 * @param cause The cause of this exception.
	 * @since 1.5
	 */
	public BundleException(String msg, int type, Throwable cause) {
		super(msg);
		this.type = type;
		this.cause = cause;
	}
	
	/**
	 * Creates a <code>BundleException</code> with the specified
	 * message and type.
	 * 
	 * @param msg The message.
	 * @param type The type for this exception.
	 * @since 1.5
	 */
	public BundleException(String msg, int type) {
		this(msg, type, null);
	}
	
	/**
	 * Returns any nested exceptions included in this exception.
	 * 
	 * <p>
	 * This method predates the general purpose exception chaining mechanism.
	 * The {@link #getCause()} method is now the preferred means of obtaining
	 * this information.
	 * 
	 * @return The nested exception; <code>null</code> if there is no nested
	 *         exception.
	 */
	public Throwable getNestedException() {
		return cause;
	}

	/**
	 * Returns the cause of this exception or <code>null</code> if no cause
	 * was specified when this exception was created.
	 * 
	 * @return The cause of this exception or <code>null</code> if no cause
	 *         was specified.
	 * @since 1.3
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
	 * @since 1.3
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}

	/**
	 * Returns the type for this exception or <code>UNSPECIFIED</code>
	 * if the type was unspecified or unknown.
	 * 
	 * @return The type of this exception.
	 * @since 1.5
	 */
	public int getType() {
		return type;
	}
}

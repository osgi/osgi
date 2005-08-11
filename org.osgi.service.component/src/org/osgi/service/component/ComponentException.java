/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.component;

/**
 * Unchecked exception which may be thrown by the Service Component Runtime.
 * 
 * @version $Revision$
 */
public class ComponentException extends RuntimeException {
	static final long	serialVersionUID	= -7438212656298726924L;
	/**
	 * Nested exception.
	 */
	private Throwable	cause;

	/**
	 * Construct a new ComponentException with the specified message and cause.
	 * 
	 * @param message The message for the exception.
	 * @param cause The cause of the exception. May be <code>null</code>.
	 */
	public ComponentException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}

	/**
	 * Construct a new ComponentException with the specified message.
	 * 
	 * @param message The message for the exception.
	 */
	public ComponentException(String message) {
		super(message);
		this.cause = null;
	}

	/**
	 * Construct a new ComponentException with the specified cause.
	 * 
	 * @param cause The cause of the exception. May be <code>null</code>.
	 */
	public ComponentException(Throwable cause) {
		super();
		this.cause = cause;
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
}
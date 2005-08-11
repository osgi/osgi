/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.http;

/**
 * A NamespaceException is thrown to indicate an error with the caller's request
 * to register a servlet or resources into the URI namespace of the Http
 * Service. This exception indicates that the requested alias already is in use.
 * 
 * @version $Revision$
 */
public class NamespaceException extends Exception {
    static final long serialVersionUID = 7235606031147877747L;
	/**
	 * Nested exception.
	 */
	private Throwable	cause;

	/**
	 * Construct a <code>NamespaceException</code> object with a detail message.
	 * 
	 * @param message the detail message
	 */
	public NamespaceException(String message) {
		super(message);
		cause = null;
	}

	/**
	 * Construct a <code>NamespaceException</code> object with a detail message
	 * and a nested exception.
	 * 
	 * @param message The detail message.
	 * @param cause The nested exception.
	 */
	public NamespaceException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}

	/**
	 * Returns the nested exception.
	 *
     * <p>This method predates the general purpose exception chaining mechanism.
     * The {@link #getCause()} method is now the preferred means of
     * obtaining this information.
	 * 
	 * @return the nested exception or <code>null</code> if there is no nested
	 *         exception.
	 */
	public Throwable getException() {
		return cause;
	}

	/**
	 * Returns the cause of this exception or <code>null</code> if no
	 * cause was specified when this exception was created.
	 *
	 * @return  The cause of this exception or <code>null</code> if no
	 * cause was specified.
	 * @since 1.2 
	 */
	public Throwable getCause() {
	    return cause;
	}

	/**
	 * The cause of this exception can only be set when constructed.
	 *
	 * @param cause Cause of the exception.
	 * @return This object.
	 * @throws java.lang.IllegalStateException
	 * This method will always throw an <code>IllegalStateException</code>
	 * since the cause of this exception can only be set when constructed.
	 * @since 1.2 
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}
}

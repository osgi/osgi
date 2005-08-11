/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.prefs;

/**
 * Thrown to indicate that a preferences operation could not complete because of
 * a failure in the backing store, or a failure to contact the backing store.
 * 
 * @version $Revision$
 */
public class BackingStoreException extends Exception {
    static final long serialVersionUID = -1415637364122829574L;
	/**
	 * Nested exception.
	 */
	private Throwable	cause;

	/**
	 * Constructs a <code>BackingStoreException</code> with the specified detail
	 * message.
	 * 
	 * @param s The detail message.
	 */
	public BackingStoreException(String s) {
		super(s);
	}
	
	/**
	 * Constructs a <code>BackingStoreException</code> with the specified detail
	 * message.
	 * 
	 * @param s The detail message.
	 * @param cause The cause of the exception. May be <code>null</code>.
	 * @since 1.1 
	 */
	public BackingStoreException(String s, Throwable cause) {
		super(s);
		this.cause = cause;
	}
	
	/**
	 * Returns the cause of this exception or <code>null</code> if no cause was
	 * specified when this exception was created.
	 * 
	 * @return The cause of this exception or <code>null</code> if no cause was
	 *         specified.
	 * @since 1.1 
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
	 * @since 1.1 
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}

}
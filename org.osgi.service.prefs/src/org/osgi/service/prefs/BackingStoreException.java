/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2001). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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
	 * Constructs a <tt>BackingStoreException</tt> with the specified detail
	 * message.
	 * 
	 * @param s The detail message.
	 */
	public BackingStoreException(String s) {
		super(s);
	}
	
	/**
	 * Constructs a <tt>BackingStoreException</tt> with the specified detail
	 * message.
	 * 
	 * @param s The detail message.
	 * @param cause The cause of the exception. May be <tt>null</tt>.
	 * @since 1.1 
	 */
	public BackingStoreException(String s, Throwable cause) {
		super(s);
		this.cause = cause;
	}
	
	/**
	 * Returns the cause of this exception or <tt>null</tt> if no cause was
	 * specified when this exception was created.
	 * 
	 * @return The cause of this exception or <tt>null</tt> if no cause was
	 *         specified.
	 * @since 1.1 
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * The cause of this exception can only be set when constructed.
	 * 
	 * @throws java.lang.IllegalStateException This method will always throw an
	 *         <tt>IllegalStateException</tt> since the cause of this
	 *         exception can only be set when constructed.
	 * @since 1.1 
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}

}
/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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

package org.osgi.framework;

/**
 * A Framework exception.
 * 
 * <p>
 * An <code>InvalidSyntaxException</code> object indicates that a filter string
 * parameter has an invalid syntax and cannot be parsed.
 * 
 * <p>
 * See {@link Filter}for a description of the filter string syntax.
 * 
 * @version $Revision$
 */

public class InvalidSyntaxException extends Exception {
	static final long	serialVersionUID	= -4295194420816491875L;
	/**
	 * The invalid filter string.
	 */
	private String		filter;
	/**
	 * Nested exception.
	 */
	private Throwable	cause;

	/**
	 * Creates an exception of type <code>InvalidSyntaxException</code>.
	 * 
	 * <p>
	 * This method creates an <code>InvalidSyntaxException</code> object with the
	 * specified message and the filter string which generated the exception.
	 * 
	 * @param msg The message.
	 * @param filter The invalid filter string.
	 */
	public InvalidSyntaxException(String msg, String filter) {
		super(msg);
		this.filter = filter;
		this.cause = null;
	}

	/**
	 * Creates an exception of type <code>InvalidSyntaxException</code>.
	 * 
	 * <p>
	 * This method creates an <code>InvalidSyntaxException</code> object with the
	 * specified message and the filter string which generated the exception.
	 * 
	 * @param msg The message.
	 * @param filter The invalid filter string.
	 * @param cause The cause of this exception.
	 * @since 1.3
	 */
	public InvalidSyntaxException(String msg, String filter, Throwable cause) {
		super(msg);
		this.filter = filter;
		this.cause = cause;
	}

	/**
	 * Returns the filter string that generated the
	 * <code>InvalidSyntaxException</code> object.
	 * 
	 * @return The invalid filter string.
	 * @see BundleContext#getServiceReferences
	 * @see BundleContext#addServiceListener(ServiceListener,String)
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Returns the cause of this exception or <code>null</code> if no cause was
	 * specified when this exception was created.
	 * 
	 * @return The cause of this exception or <code>null</code> if no cause was
	 *         specified.
	 * @since 1.3
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * The cause of this exception can only be set when constructed.
	 * 
	 * @throws java.lang.IllegalStateException This method will always throw an
	 *         <code>IllegalStateException</code> since the cause of this
	 *         exception can only be set when constructed.
	 * @since 1.3
	 */
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException();
	}
}


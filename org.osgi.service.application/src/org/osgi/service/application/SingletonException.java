/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application;

/**
 * An exception that is raised when an application that can only be 
 * launched once is attempted to be launched twice.
 */
public class SingletonException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Parameterless constructor.
	 */
	public SingletonException() {
		super();
	}

	/**
	 * Constructor to create the exception with the specified message.
	 * 
	 * @param message
	 *            the message for the exception
	 */
	public SingletonException(String message) {
		super(message);
	}
}
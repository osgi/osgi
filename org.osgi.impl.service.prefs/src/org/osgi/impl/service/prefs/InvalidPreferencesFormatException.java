/*
 * @(#)InvalidPreferencesFormatException.java	1.3 01/07/18
 * $Id$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.impl.service.prefs;

/**
 * Thrown to indicate that an operation could not complete because of a failure
 * in the backing store, or a failure to contact the backing store.
 * 
 * @version $Id$
 */
public class InvalidPreferencesFormatException extends Exception {
	/**
	 * The line number at which a format error was detected, or -1 if unknown or
	 * inapplicable.
	 */
	final int	lineNumber;

	/**
	 * Constructs an InvalidPreferencesFormatException with the specified detail
	 * message and a line number of -1.
	 * 
	 * @parameter s the detail message.
	 */
	public InvalidPreferencesFormatException(String s) {
		super(s);
		lineNumber = -1;
	}

	/**
	 * Constructs an InvalidPreferencesFormatException with the specified detail
	 * message and line number.
	 */
	public InvalidPreferencesFormatException(String s, int lineNumber) {
		super();
		this.lineNumber = lineNumber;
	}

	/**
	 * Returns the line number at which a format error was detected, or -1 if
	 * unknown or inapplicable.
	 */
	public int lineNumber() {
		return lineNumber;
	}
}

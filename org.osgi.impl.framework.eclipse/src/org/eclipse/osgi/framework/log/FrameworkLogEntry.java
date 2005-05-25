/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.log;

public class FrameworkLogEntry {
	//TODO "entry" has another meaning here - title, summary, tag are better names 
	private String entry;
	private String message;
	//TODO get rid of this
	private int stackCode;
	//TODO: use "reason" or "cause" instead
	private Throwable throwable;
	private FrameworkLogEntry[] children;

	/**
	 * Constructs a new FrameworkLogEntry
	 * @param entry the entry
	 * @param message the message
	 * @param stackCode the stack code
	 * @param throwable the throwable
	 * @param children the children
	 */
	public FrameworkLogEntry(String entry, String message, int stackCode, Throwable throwable, FrameworkLogEntry[] children) {
		this.entry = entry;
		this.message = message;
		this.stackCode = stackCode;
		this.throwable = throwable;
		this.children = children;
	}

	/**
	 * 
	 * @return Returns the children.
	 */
	public FrameworkLogEntry[] getChildren() {
		return children;
	}

	/**
	 * @return Returns the entry.
	 */
	public String getEntry() {
		return entry;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the stackCode.
	 */
	public int getStackCode() {
		return stackCode;
	}

	/**
	 * @return Returns the throwable.
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}

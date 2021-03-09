/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.prefs;

/**
 * Thrown to indicate that an operation could not complete because of a failure
 * in the backing store, or a failure to contact the backing store.
 * 
 * @author $Id$
 */
public class InvalidPreferencesFormatException extends Exception {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
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

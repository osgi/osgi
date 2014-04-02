/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.coordinator;

/**
 * Package private singleton exception class.
 * 
 * @author $Id$
 */
class SingletonException extends Exception {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Create a singleton exception with the specified message and no cause.
	 * 
	 * @param message The message for the singleton exception.
	 */
	public SingletonException(String message) {
		super(message, null);
	}

	/**
	 * The stack trace cannot be filled in.
	 * 
	 * @return this
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

	/**
	 * The stack trace cannot be set.
	 * 
	 * @param stackTrace Must not be {@code null} or contain any {@code null}
	 *        elements; otherwise ignored.
	 */
	@Override
	public void setStackTrace(StackTraceElement[] stackTrace) {
		for (StackTraceElement e : stackTrace) {
			if (e == null) {
				throw new NullPointerException();
			}
		}
	}
}

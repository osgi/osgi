/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.log;

/**
 * Log Levels.
 * 
 * @since 1.4
 * @author $Id$
 */
public enum LogLevel {
	/*
	 * The ordering of the elements is deliberate and must be kept. See {@link
	 * #implies(LogLevel)}.
	 */
	/**
	 * Audit – Information that must always be logged.
	 */
	AUDIT,
	/**
	 * Error – Information about an error situation.
	 */
	ERROR,
	/**
	 * Warning – Information about a failure or unwanted situation that is not
	 * blocking.
	 */
	WARN,
	/**
	 * Info – Information about normal operation.
	 */
	INFO,
	/**
	 * Debug – Detailed output for debugging operations.
	 */
	DEBUG,
	/**
	 * Trace level – Large volume of output for tracing operations.
	 */
	TRACE;

	/**
	 * Returns whether this log level implies the specified log level.
	 * 
	 * @param other The other log level.
	 * @return {@code true} If this log level implies the specified log level;
	 *         {@code false} otherwise.
	 */
	public boolean implies(LogLevel other) {
		return ordinal() >= other.ordinal();
	}
}

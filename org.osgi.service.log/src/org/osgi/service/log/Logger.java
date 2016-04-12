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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides methods for bundles to write messages to the log using SLF4J-style
 * format strings.
 * <p>
 * Messages can be formatted by the Logger once the Logger determines the log
 * level is enabled. Use <code>"{}"</code> as a place holder for an argument. If
 * you need to use the literal <code>"{}"</code> in the formatted message,
 * precede the place holder with a backslash: <code>"\\{}"</code>. If you need
 * to place a backslash before the place holder, precede the backslash with a
 * backslash: <code>"\\\\{}"</code>.
 * <p>
 * You can also add a {@code Throwable} and/or {@code ServiceReference} to the
 * generated {@link LogEntry} by passing them to the logging methods as
 * additional arguments. If the last argument is a {@code Throwable} or
 * {@code ServiceReference}, it is added to the generated {@link LogEntry} and
 * then if the next to last argument is a {@code ServiceReference} or
 * {@code Throwable} and not the same type as the last argument, it is added to
 * the generated {@link LogEntry}. These arguments will not be used for place
 * holders. For example:
 * 
 * <pre>
 * logger.info("Found service {}.", serviceReference, serviceReference);
 * logger.warn("Something named {} happened.", name, serviceReference,
 * 		throwable);
 * logger.error("Failed.", exception);
 * </pre>
 * 
 * @ThreadSafe
 * @author $Id$
 * @since 1.4
 */
@ProviderType
public interface Logger {
	/**
	 * Root Logger Name.
	 */
	static String ROOT_LOGGER_NAME = "ROOT";

	/**
	 * Return the name of this Logger.
	 * 
	 * @return The name of this Logger.
	 */
	String getName();

	/**
	 * Is logging enabled for the {@link LogLevel#TRACE} level?
	 * 
	 * @return {@code true} if logging is enabled for the {@link LogLevel#TRACE}
	 *         level.
	 */
	boolean isTraceEnabled();

	/**
	 * Log a message at the {@link LogLevel#TRACE} level.
	 * 
	 * @param message The message to log.
	 */
	void trace(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#TRACE} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void trace(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#TRACE} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void trace(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#TRACE} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void trace(String format, Object... arguments);

	/**
	 * Is logging enabled for the {@link LogLevel#DEBUG} level?
	 * 
	 * @return {@code true} if logging is enabled for the {@link LogLevel#DEBUG
	 *         trace} level.
	 */
	boolean isDebugEnabled();

	/**
	 * Log a message at the {@link LogLevel#DEBUG} level.
	 * 
	 * @param message The message to log.
	 */
	void debug(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#DEBUG} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void debug(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#DEBUG} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void debug(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#DEBUG} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void debug(String format, Object... arguments);

	/**
	 * Is logging enabled for the {@link LogLevel#INFO} level?
	 * 
	 * @return {@code true} if logging is enabled for the {@link LogLevel#INFO
	 *         trace} level.
	 */
	boolean isInfoEnabled();

	/**
	 * Log a message at the {@link LogLevel#INFO} level.
	 * 
	 * @param message The message to log.
	 */
	void info(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#INFO} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void info(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#INFO} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void info(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#INFO} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void info(String format, Object... arguments);

	/**
	 * Is logging enabled for the {@link LogLevel#WARN} level?
	 * 
	 * @return {@code true} if logging is enabled for the {@link LogLevel#WARN
	 *         trace} level.
	 */
	boolean isWarnEnabled();

	/**
	 * Log a message at the {@link LogLevel#WARN} level.
	 * 
	 * @param message The message to log.
	 */
	void warn(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#WARN} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void warn(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#WARN} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void warn(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#WARN} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void warn(String format, Object... arguments);

	/**
	 * Is logging enabled for the {@link LogLevel#ERROR} level?
	 * 
	 * @return {@code true} if logging is enabled for the {@link LogLevel#ERROR
	 *         trace} level.
	 */
	boolean isErrorEnabled();

	/**
	 * Log a message at the {@link LogLevel#ERROR} level.
	 * 
	 * @param message The message to log.
	 */
	void error(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#ERROR} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void error(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#ERROR} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void error(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#ERROR} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void error(String format, Object... arguments);

	/**
	 * Log a message at the {@link LogLevel#AUDIT} level.
	 * 
	 * @param message The message to log.
	 */
	void audit(String message);

	/**
	 * Log a formatted message at the {@link LogLevel#AUDIT} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg The argument to format into the message.
	 */
	void audit(String format, Object arg);

	/**
	 * Log a formatted message at the {@link LogLevel#AUDIT} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arg1 The first argument to format into the message.
	 * @param arg2 The second argument to format into the message.
	 */
	void audit(String format, Object arg1, Object arg2);

	/**
	 * Log a formatted message at the {@link LogLevel#AUDIT} level.
	 * 
	 * @param format The format of the message to log.
	 * @param arguments The arguments to format into the message.
	 */
	void audit(String format, Object... arguments);
}

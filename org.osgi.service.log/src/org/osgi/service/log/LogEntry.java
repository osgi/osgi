/*
 * Copyright (c) OSGi Alliance (2000, 2017). All Rights Reserved.
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
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * Provides methods to access the information contained in an individual Log
 * Service log entry.
 * 
 * <p>
 * A {@code LogEntry} object may be acquired from the
 * {@code LogReaderService.getLog} method or by registering a
 * {@code LogListener} object.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LogEntry {
	/**
	 * Returns the bundle that created this {@code LogEntry} object.
	 * 
	 * @return The bundle that created this {@code LogEntry} object;
	 *         {@code null} if no bundle is associated with this
	 *         {@code LogEntry} object.
	 */
	Bundle getBundle();

	/**
	 * Returns the {@code ServiceReference} object for the service associated
	 * with this {@code LogEntry} object.
	 * 
	 * @return {@code ServiceReference} object for the service associated with
	 *         this {@code LogEntry} object; {@code null} if no
	 *         {@code ServiceReference} object was provided.
	 */
	ServiceReference< ? > getServiceReference();

	/**
	 * Returns the integer level of this {@code LogEntry} object.
	 * <p>
	 * If one of the {@code log} methods of {@link LogService} was used, this is
	 * the specified integer level. Otherwise, this is the
	 * {@link LogLevel#ordinal() ordinal} value of the {@link #getLogLevel() log
	 * level}.
	 * 
	 * @return Integer level of this {@code LogEntry} object.
	 * @deprecated Since 1.4. Replaced by {@link #getLogLevel()}.
	 */
	@Deprecated
	int getLevel();

	/**
	 * Returns the formatted message associated with this {@code LogEntry}
	 * object.
	 * 
	 * @return {@code String} containing the formatted message associated with
	 *         this {@code LogEntry} object.
	 */
	String getMessage();

	/**
	 * Returns the exception object associated with this {@code LogEntry}
	 * object.
	 * 
	 * <p>
	 * In some implementations, the returned exception may not be the original
	 * exception. To avoid references to a bundle defined exception class, thus
	 * preventing an uninstalled bundle from being garbage collected, the Log
	 * Service may return an exception object of an implementation defined
	 * Throwable subclass. The returned object will attempt to provide as much
	 * information as possible from the original exception object such as the
	 * message and stack trace.
	 * 
	 * @return {@code Throwable} object of the exception associated with this
	 *         {@code LogEntry};{@code null} if no exception is associated with
	 *         this {@code LogEntry} object.
	 */
	Throwable getException();

	/**
	 * Returns the value of {@code currentTimeMillis()} at the time this
	 * {@code LogEntry} object was created.
	 * 
	 * @return The system time in milliseconds when this {@code LogEntry} object
	 *         was created.
	 * @see "System.currentTimeMillis()"
	 */
	long getTime();

	/**
	 * Returns the level of this {@code LogEntry} object.
	 * 
	 * @return The level of this {@code LogEntry} object.
	 * @since 1.4
	 */
	LogLevel getLogLevel();

	/**
	 * Returns the name of the {@link Logger} object used to create this
	 * {@code LogEntry} object.
	 * 
	 * @return The name of the {@link Logger} object used to create this
	 *         {@code LogEntry} object.
	 * @since 1.4
	 */
	String getLoggerName();

	/**
	 * Returns the sequence number for this {@code LogEntry} object.
	 * <p>
	 * A unique, non-negative value that is larger than all previously assigned
	 * values since the log implementation was started. These values are
	 * transient and are reused upon restart of the log implementation.
	 * 
	 * @return The sequence number for this {@code LogEntry} object.
	 * @since 1.4
	 */
	long getSequence();

	/**
	 * Returns a string representing the thread which created this
	 * {@code LogEntry} object.
	 * <p>
	 * This string must contain the name of the thread and may contain other
	 * information about the thread.
	 * 
	 * @return A string representing the thread which created this
	 *         {@code LogEntry} object.
	 * @since 1.4
	 */
	String getThreadInfo();

	/**
	 * Returns the location information of the creation of this {@code LogEntry}
	 * object.
	 * 
	 * @return The location information of the creation of this {@code LogEntry}
	 *         object.
	 * @since 1.4
	 */
	StackTraceElement getLocation();
}

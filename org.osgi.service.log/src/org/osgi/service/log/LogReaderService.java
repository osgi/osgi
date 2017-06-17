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

import java.util.Enumeration;

import org.osgi.annotation.versioning.ProviderType;

/**
 * LogReaderService for obtaining logging information.
 * <p>
 * Since 1.4, {@link org.osgi.service.log.stream.LogStreamProvider} is the
 * preferred way to obtain {@link LogEntry} objects.
 * <p>
 * The LogReaderService provides two ways to obtain {@link LogEntry} objects:
 * <ul>
 * <li>The primary way to retrieve {@link LogEntry} objects is to register a
 * {@link LogListener} object whose {@link LogListener#logged(LogEntry)} method
 * will be called for each entry added to the log.</li>
 * <li>To obtain past {@link LogEntry} objects, the {@link #getLog()} method can
 * be called which will return an {@code Enumeration} of the {@link LogEntry}
 * objects in the log.</li>
 * </ul>
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LogReaderService {
	/**
	 * Subscribes to {@link LogEntry} objects.
	 * <p>
	 * This method registers a {@link LogListener} object with the Log Reader
	 * Service. The {@link LogListener#logged(LogEntry)} method will be called
	 * for each {@link LogEntry} object placed into the log.
	 * <p>
	 * When a bundle which registers a {@link LogListener} object is stopped or
	 * otherwise releases the Log Reader Service, the Log Reader Service must
	 * remove all of the bundle's listeners.
	 * <p>
	 * If this Log Reader Service's list of listeners already contains a
	 * listener {@code l} such that {@code (l==listener)}, this method does
	 * nothing.
	 * <p>
	 * Since 1.4, {@link org.osgi.service.log.stream.LogStreamProvider} is the
	 * preferred way to obtain {@link LogEntry} objects.
	 * 
	 * @param listener A {@link LogListener} object to register; the
	 *            {@link LogListener} object is used to receive {@link LogEntry}
	 *            objects.
	 */
	void addLogListener(LogListener listener);

	/**
	 * Unsubscribes to {@link LogEntry} objects.
	 * <p>
	 * This method unregisters a {@link LogListener} object from the Log Reader
	 * Service.
	 * <p>
	 * If {@code listener} is not contained in this Log Reader Service's list of
	 * listeners, this method does nothing.
	 * <p>
	 * Since 1.4, {@link org.osgi.service.log.stream.LogStreamProvider} is the
	 * preferred way to obtain {@link LogEntry} objects.
	 * 
	 * @param listener A {@link LogListener} object to unregister.
	 */
	void removeLogListener(LogListener listener);

	/**
	 * Returns an {@code Enumeration} of the {@link LogEntry} objects in the
	 * log.
	 * <p>
	 * Each element of the enumeration is a {@link LogEntry} object, ordered
	 * with the most recent entry first. Whether the enumeration is of all
	 * {@link LogEntry} objects since the Log Service was started or some recent
	 * past is implementation-specific.
	 * 
	 * @return An {@code Enumeration} of the {@link LogEntry} objects in the
	 *         log.
	 */
	Enumeration<LogEntry> getLog();
}

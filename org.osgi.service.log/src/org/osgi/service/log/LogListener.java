/*
 * Copyright (c) OSGi Alliance (2000, 2016). All Rights Reserved.
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

import java.util.EventListener;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Subscribes to {@code LogEntry} objects from the {@code LogReaderService}.
 * <p>
 * A {@code LogListener} object may be registered with the Log Reader Service
 * using the {@code LogReaderService.addLogListener} method. After the listener
 * is registered, the {@code logged} method will be called for each
 * {@code LogEntry} object created. The {@code LogListener} object may be
 * unregistered by calling the {@code LogReaderService.removeLogListener}
 * method.
 * <p>
 * Since 1.4, {@link org.osgi.service.log.stream.LogStreamProvider} is the
 * preferred way to obtain {@link LogEntry} objects.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface LogListener extends EventListener {
	/**
	 * Listener method called for each LogEntry object created.
	 * 
	 * @param entry A {@link LogEntry} object containing log information.
	 */
	void logged(LogEntry entry);
}

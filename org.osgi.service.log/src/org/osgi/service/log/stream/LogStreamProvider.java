/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.log.stream;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.log.LogEntry;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.QueuePolicyOption;

/**
 * LogStreamProvider service for creating a {@link PushStream} of
 * {@link LogEntry} objects.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LogStreamProvider {
	/**
	 * Creation options for the {@link PushStream} of {@link LogEntry} objects.
	 */
	enum Options {
		/**
		 * Include history.
		 * <p>
		 * Prime the created PushStream with the available historical
		 * {@link LogEntry} objects. The number of available {@link LogEntry}
		 * objects is implementation specific.
		 * <p>
		 * The created PushStream will supply the available historical
		 * {@link LogEntry} objects followed by newly created {@link LogEntry}
		 * objects.
		 */
		HISTORY;
	}

	/**
	 * Create a {@link PushStream} of {@link LogEntry} objects.
	 * <p>
	 * The returned PushStream must:
	 * <ul>
	 * <li>Be buffered with a buffer large enough to contain the history, if
	 * included.</li>
	 * <li>Have the {@link QueuePolicyOption#DISCARD_OLDEST} queue policy
	 * option.</li>
	 * <li>Use a shared executor.</li>
	 * <li>Have a parallelism of one.</li>
	 * </ul>
	 * <p>
	 * When this LogStreamProvider service is released by the obtaining bundle,
	 * this LogStreamProvider service must call {@link PushStream#close()} on
	 * the returned PushStream object if it has not already been closed.
	 * 
	 * @param options The options to use when creating the PushStream.
	 * @return A {@link PushStream} of {@link LogEntry} objects.
	 */
	PushStream<LogEntry> createStream(Options... options);
}

/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.util.pushstream;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.promise.Promise;

/**
 * A {@link SimplePushEventSource} is a helper that makes it simpler to write a
 * {@link PushEventSource}. Users do not need to manage multiple registrations
 * to the stream, nor do they have to be concerned with back pressure.
 *
 * @param <T> The type of the events produced by this source
 */
@ProviderType
public interface SimplePushEventSource<T>
		extends PushEventSource<T>, AutoCloseable {
	/**
	 * Close this source. Calling this method indicates that there will never be
	 * any more events published by it. Calling this method sends a close event
	 * to all connected consumers. After calling this method any
	 * {@link PushEventConsumer} that tries to {@link #open(PushEventConsumer)}
	 * this source will immediately receive a close event.
	 */
	@Override
	void close();

	/**
	 * Asynchronously publish an event to this stream and all connected
	 * {@link PushEventConsumer} instances. When this method returns there is no
	 * guarantee that all consumers have been notified. Events published by a
	 * single thread will maintain their relative ordering, however they may be
	 * interleaved with events from other threads.
	 * 
	 * @param t
	 * @throws IllegalStateException if the source is closed
	 */
	void publish(T t);

	/**
	 * Close this source for now, but potentially reopen it later. Calling this
	 * method asynchronously sends a close event to all connected consumers.
	 * After calling this method any {@link PushEventConsumer} that wishes may
	 * {@link #open(PushEventConsumer)} this source, and will receive subsequent
	 * events.
	 */
	void endOfStream();

	/**
	 * Close this source for now, but potentially reopen it later. Calling this
	 * method asynchronously sends an error event to all connected consumers.
	 * After calling this method any {@link PushEventConsumer} that wishes may
	 * {@link #open(PushEventConsumer)} this source, and will receive subsequent
	 * events.
	 *
	 * @param e the error
	 */
	void error(Exception e);

	/**
	 * Determine whether there are any {@link PushEventConsumer}s for this
	 * {@link PushEventSource}. This can be used to skip expensive event
	 * creation logic when there are no listeners.
	 * 
	 * @return true if any consumers are currently connected
	 */
	boolean isConnected();

	/**
	 * This method can be used to delay event generation until an event source
	 * has connected. The returned promise will resolve as soon as one or more
	 * {@link PushEventConsumer} instances have opened the
	 * SimplePushEventSource.
	 * <p>
	 * The returned promise may already be resolved if this
	 * {@link SimplePushEventSource} already has connected consumers. If the
	 * {@link SimplePushEventSource} is closed before the returned Promise
	 * resolves then it will be failed with an {@link IllegalStateException}.
	 * <p>
	 * Note that the connected consumers are able to asynchronously close their
	 * connections to this {@link SimplePushEventSource}, and therefore it is
	 * possible that once the promise resolves this
	 * {@link SimplePushEventSource} may no longer be connected to any
	 * consumers.
	 * 
	 * @return A promise representing the connection state of this EventSource
	 */
	Promise<Void> connectPromise();

}

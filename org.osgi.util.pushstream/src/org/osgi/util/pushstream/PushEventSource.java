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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * An event source. An event source can open a channel between a source and a
 * consumer. Once the channel is opened (even before it returns) the source can
 * send events to the consumer.
 *
 * A source should stop sending and automatically close the channel when sending
 * an event returns a negative value, see {@link PushEventConsumer#ABORT}.
 * Values that are larger than 0 should be treated as a request to delay the
 * next events with those number of milliseconds.
 * 
 * @param <T>
 *            The payload type
 */
@ConsumerType
@FunctionalInterface
public interface PushEventSource<T> {

	/**
	 * Open the asynchronous channel between the source and the consumer. The
	 * call returns an {@link AutoCloseable}. This can be closed, and should
	 * close the channel, including sending a Close event if the channel was not
	 * already closed. The returned object must be able to be closed multiple
	 * times without sending more than one Close events.
	 * 
	 * @param aec the consumer (not null)
	 * @return a {@link AutoCloseable} that can be used to close the stream
	 * @throws Exception
	 */
	AutoCloseable open(PushEventConsumer< ? super T> aec) throws Exception;
}

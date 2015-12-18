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

package org.osgi.service.distributedeventing;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;

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
 * Distribution Providers can recognize this type and allow the establishment of
 * a channel over a network.
 * 
 * @param <T> The payload type
 */
@ConsumerType
@FunctionalInterface
public interface DistributableEventSource<T> extends PushEventSource<DistributableEvent<T>> {

	/**
	 * The service property name defining the topics which this
	 * PushEventConsumer consumes from
	 */
	public static final String CONSUMED_EVENT_TOPICS = org.osgi.service.event.EventConstants.EVENT_TOPIC;
	
	/**
	 * The service property name defining the type of the event which this
	 * PushEventConsumer expects to receive
	 */
	public static final String	EVENT_TYPE				= "event.type";
	
}

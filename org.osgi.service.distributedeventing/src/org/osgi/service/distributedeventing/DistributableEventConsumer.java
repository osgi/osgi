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

/**
 * An event consumer.
 * 
 * An event consumer will be called with events coming from the relevant topics
 *
 * If this consumer returns a negative value then no further events will be sent
 * to it, see {@link PushEventConsumer#ABORT}. Values that are larger than 0
 * should be treated as a request to delay the next events with those number of
 * milliseconds.
 * 
 * If the consumer is unable to keep pace with the rate of event arrival then
 * the DistributedEventAdmin service will buffer the events until they can be
 * processed. If the Distributed Event Admin buffer grows too large then the
 * consumer will be closed, blacklisted and not called again.
 * 
 * @param <T> The payload type
 */
@ConsumerType
@FunctionalInterface
public interface DistributableEventConsumer<T> extends PushEventConsumer<DistributableEvent<T>> {

	/**
	 * The service property name defining the topics which this PushEventSource
	 * publishes to
	 */
	public static final String CONSUMED_EVENT_TOPICS = org.osgi.service.event.EventConstants.EVENT_TOPIC;
	
	/**
	 * The service property name defining the type of the event which this
	 * PushEventSource produces
	 */
	public static final String	EVENT_TYPE				= "event.type";
	
}

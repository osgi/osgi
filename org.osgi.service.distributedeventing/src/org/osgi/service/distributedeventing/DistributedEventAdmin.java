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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.pushstream.PushStream;

/**
 * 
 */
@ProviderType
public interface DistributedEventAdmin {

	/**
	 * Asynchronously send a single event to a given topic. The payload will
	 * automatically associated with {@link DistributableEvent} data.
	 * 
	 * @param topic
	 * @param payload
	 */
	void publishEvent(String topic, Object payload);

	/**
	 * Asynchronously send a single event to a given topic. The payload will
	 * automatically associated with {@link DistributableEvent} data.
	 * 
	 * This method sets a correlation id in the event so that consumers of the
	 * event may reply to it
	 * 
	 * @param topic
	 * @param correlationId
	 * @param replyTo
	 * @param payload
	 */
	void publishEvent(String topic, String correlationId, String replyTo, Object payload);

	/**
	 * Asynchronously send a single event
	 * 
	 * @param e
	 */
	void publish(DistributableEvent<?> e);
	
	/**
	 * Create a subscription to the named topic. Typically used to pre-register
	 * a listener for a private topic before the sender begins sending events.
	 * 
	 * @param s
	 * @param type
	 * @return A stream pipeline for processing events
	 */
	<T> PushStream<T> createSubscription(String s, Class<T> type);
	
	
	/**
	 * A private topic is a topic with an automatically generated name that
	 * start with a '.' character. Aside from the first character, the format of
	 * a private topic name is unspecified.
	 * 
	 * <p>
	 * A private topic name may be shared remotely and so must not be reused. In
	 * particular two calls to {@link #createPrivateTopic()} must not return the
	 * same value, even after the VM has been restarted
	 * 
	 * @return The name of a private topic that can be used by the sender
	 */
	String createPrivateTopic();

}

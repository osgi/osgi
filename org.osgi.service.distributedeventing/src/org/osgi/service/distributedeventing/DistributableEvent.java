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

import java.io.Serializable;
import org.osgi.util.pushstream.PushEvent;

/**
 * This class encapsulates event data for publishing via EventAdmin in an
 * {@link PushEvent} 
 *
 * @param <T>
 */
public final class DistributableEvent<T> implements Serializable {

	private static final long serialVersionUID = 1343043991917920304L;

	private final String topic;
	
	private final long originatingBundle;
	
	private final String		originatingFrameworkId;
	
	private final String replyTo;
	
	private final String correlationId;
	
	private final T data;

	/**
	 * Create a new DistributableEvent
	 * 
	 * @param topic
	 * @param originatingBundle
	 * @param originatingFrameworkId
	 * @param replyTo
	 * @param correlationId
	 * @param data
	 */
	public DistributableEvent(String topic, long originatingBundle, String originatingFrameworkId,
			String replyTo, String correlationId, T data) {
		this.topic = topic;
		this.originatingBundle = originatingBundle;
		this.originatingFrameworkId = originatingFrameworkId;
		this.replyTo = replyTo;
		this.correlationId = correlationId;
		this.data = data;
	}

	/**
	 * The topic for this Event
	 * 
	 * @return the topic name
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * The id of the bundle which sent this event
	 * 
	 * @return the bundle id of the source
	 */
	public long getOriginatingBundle() {
		return originatingBundle;
	}

	/**
	 * The framework from which this event originated
	 * 
	 * @return the id of the originating framework
	 */
	public String getOriginatingFramework() {
		return originatingFrameworkId;
	}

	/**
	 * The replyTo location for this event
	 * <p>
	 * May be null if the event sender did not register to receive replies
	 * 
	 * @return The replyTo address
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * The correlation id for this message
	 * 
	 * @return the correlation id for this message. May be null if no
	 *         correlation id was set
	 */
	public String getCorrelationId() {
		return correlationId;
	}
	
	/**
	 * The raw event data
	 * 
	 * @return the raw event data
	 */
	public T getData() {
		return data;
	}
	
}

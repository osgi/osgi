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

package org.osgi.service.cdi;

import org.osgi.framework.Bundle;

/**
 * CdiEvents are sent by the CDI extender and received by registered CdiListener
 * services.
 */
public class CdiEvent {

	/**
	 * An enum defining the states of a CDI container.
	 *
	 */
	public static enum State {

		/**
		 * The CDI container has started being created.
		 */
		CREATING,

		/**
		 * The CDI container is created and should be fully usable.
		 */
		CREATED,

		/**
		 * The CDI container has started being destroyed.
		 */
		DESTROYING,

		/**
		 * The CDI container is completely destroyed.
		 */
		DESTROYED,

		/**
		 * The CDI container is waiting for dependent extensions.
		 */
		WAITING_FOR_EXTENSIONS,

		/**
		 * The CDI container is waiting for dependent services.
		 */
		WAITING_FOR_SERVICES,

		/**
		 * The CDI container is satisfied and resuming construction.
		 */
		SATISFIED,

		/**
		 * The CDI container has suffered a failure and will be destroyed.
		 */
		FAILURE
	}

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 */
	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle) {
		this(type, bundle, extenderBundle, null, null);
	}

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 * @param payload
	 */
	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle, String payload) {
		this(type, bundle, extenderBundle, payload, null);
	}

	/**
	 * @param type
	 * @param bundle
	 * @param extenderBundle
	 * @param payload
	 * @param cause
	 */
	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle, String payload, Throwable cause) {
		this.type = type;
		this.bundle = bundle;
		this.extenderBundle = extenderBundle;
		this.payload = payload;
		this.cause = cause;
		this.timestamp = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();

		sb.append("{type:'");
		sb.append(this.type);
		sb.append("',timestamp:");
		sb.append(this.timestamp);
		sb.append(",bundle:'");
		sb.append(this.bundle);
		sb.append("',extenderBundle:'");
		sb.append(this.extenderBundle);
		if (this.payload != null) {
			sb.append("',payload:'");
			sb.append(this.payload);
		}
		if (this.cause != null) {
			sb.append("',cause:'");
			sb.append(this.cause.getMessage());
		}
		sb.append("'}");

		string = sb.toString();
	}

	/**
	 * @return the bundle who's CDI container triggered this event
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @return the cause of the event if there was one
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * @return the bundle of the CDI extender
	 */
	public Bundle getExtenderBundle() {
		return extenderBundle;
	}

	/**
	 * @return the payload associated with this event
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * @return the timestamp of the event
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the state of this event
	 */
	public State getState() {
		return type;
	}

	@Override
	public String toString() {
		return string;
	}

	private final Bundle bundle;
	private final Throwable cause;
	private final Bundle extenderBundle;
	private final String payload;
	private final long timestamp;
	private final State type;
	private final String string;

}

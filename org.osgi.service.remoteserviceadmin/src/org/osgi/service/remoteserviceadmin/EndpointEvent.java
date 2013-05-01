/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.remoteserviceadmin;

/**
 * An Endpoint Event.
 * <p/>
 * 
 * {@code EndpointEvent} objects are delivered to all registered
 * {@link EndpointEventListener} services where the {@link EndpointDescription}
 * properties match one of the filters specified in the
 * {@link EndpointEventListener#ENDPOINT_LISTENER_SCOPE} registration properties
 * of the Endpoint Event Listener.
 * <p/>
 * 
 * A type code is used to identify the type of event. The following event types
 * are defined:
 * <ul>
 * <li>{@link #ADDED}</li>
 * <li>{@link #REMOVED}</li>
 * <li>{@link #MODIFIED}</li>
 * <li>{@link #MODIFIED_ENDMATCH}</li>
 * </ul>
 * Additional event types may be defined in the future.
 * <p/>
 * 
 * @see EndpointEventListener
 * @Immutable
 * @since 1.1
 */
public class EndpointEvent {
	/**
	 * An endpoint has been added.
	 * <p/>
	 * 
	 * This {@code EndpointEvent} type indicates that a new endpoint has been
	 * added. The endpoint is represented by the associated
	 * {@link EndpointDescription} object.
	 */
	public static final int				ADDED				= 0x00000001;

	/**
	 * An endpoint has been removed.
	 * <p/>
	 * 
	 * This {@code EndpointEvent} type indicates that an endpoint has been
	 * removed. The endpoint is represented by the associated
	 * {@link EndpointDescription} object.
	 */
	public static final int				REMOVED				= 0x00000002;

	/**
	 * The properties of an endpoint have been modified.
	 * <p/>
	 * 
	 * This {@code EndpointEvent} type indicates that the properties of an
	 * existing endpoint have been modified. The endpoint is represented by the
	 * associated {@link EndpointDescription} object and its properties can be
	 * obtained via {@link EndpointDescription#getProperties()}. The endpoint
	 * properties still match the filters as specified in the
	 * {@link EndpointEventListener#ENDPOINT_LISTENER_SCOPE} filter.
	 */
	public static final int				MODIFIED			= 0x00000004;

	/**
	 * The properties of an endpoint have been modified and the new properties
	 * no longer match the listener's filter.
	 * <p/>
	 * 
	 * This {@code EndpointEvent} type indicates that the properties of an
	 * existing endpoint have been modified and no longer match the filter. The
	 * endpoint is represented by the associated {@link EndpointDescription}
	 * object and its properties can be obtained via
	 * {@link EndpointDescription#getProperties()}. As a consequence of the
	 * modification the filters as specified in the
	 * {@link EndpointEventListener#ENDPOINT_LISTENER_SCOPE} do not match any
	 * more.
	 */
	public static final int				MODIFIED_ENDMATCH	= 0x00000008;

	/**
	 * Reference to the associated endpoint description.
	 */
	private final EndpointDescription	endpoint;

	/**
	 * Type of the event.
	 */
	private final int					type;

	/**
	 * Constructs a {@code EndpointEvent} object from the given arguments.
	 * 
	 * @param type The event type. See {@link #getType()}.
	 * @param endpoint The endpoint associated with the event.
	 */
	public EndpointEvent(int type, EndpointDescription endpoint) {
		this.endpoint = endpoint;
		this.type = type;
	}

	/**
	 * Return the endpoint associated with this event.
	 * 
	 * @return The endpoint associated with the event.
	 */
	public EndpointDescription getEndpoint() {
		return endpoint;
	}

	/**
	 * Return the type of this event.
	 * <p/>
	 * The type values are:
	 * <ul>
	 * <li>{@link #ADDED}</li>
	 * <li>{@link #REMOVED}</li>
	 * <li>{@link #MODIFIED}</li>
	 * <li>{@link #MODIFIED_ENDMATCH}</li>
	 * </ul>
	 * 
	 * @return The type of this event.
	 */
	public int getType() {
		return type;
	}
}

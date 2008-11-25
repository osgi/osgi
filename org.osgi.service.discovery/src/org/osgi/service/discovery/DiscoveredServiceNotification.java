/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.discovery;

/**
 * Interface for notification on discovered services.
 */
public interface DiscoveredServiceNotification {

	/**
	 * Notification indicating that a service matching the listening criteria has been
	 * discovered.
	 * <p>
	 * The value of <code>AVAILABLE</code> is 0x00000001.
	 */
	public final static int AVAILABLE = 0x00000001;

	/**
	 * Notification indicating that the properties of a previously discovered service
	 * have changed.
	 * <p>
	 * The value of <code>MODIFIED</code> is 0x00000002.
	 */
	public final static int MODIFIED = 0x00000002;

	/**
	 * Notification indicating that a previously discovered service is no longer known
	 * to discovery.
	 * <p>
	 * The value of <code>UNAVAILABLE</code> is 0x00000004.
	 */
	public final static int UNAVAILABLE = 0x00000004;

	/**
	 * Notification indicating that the properties of a previously discovered service
	 * have changed and the new properties no longer match the listener's
	 * filter.
	 * <p>
	 * The value of <code>MODIFIED_ENDMATCH</code> is 0x00000008.
	 */
	public final static int MODIFIED_ENDMATCH = 0x00000008;

	/**
	 * Returns information currently known to Discovery regarding the service
	 * endpoint.
	 * <p>
	 * 
	 * @return metadata of the service this Discovery notifies about.
	 */
	ServiceEndpointDescription getServiceEndpointDescription();

	/**
	 * Returns the type of notification. The type values are:
	 * <ul>
	 * <li>{@link #AVAILABLE} </li> <li>{@link #MODIFIED} </li> <li>
	 * {@link #MODIFIED_ENDMATCH} </li> <li>{@link #UNAVAILABLE} </li>
	 * </ul>
	 * 
	 * @return Type of notification regarding known service metadata.
	 */
	int getType();
}

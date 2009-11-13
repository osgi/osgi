/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
 * A {@link RemoteServiceAdminEvent} listener is notified asynchronously of any
 * export or import registrations and unregistrations.
 * 
 * <p>
 * If the Java Runtime Environment supports permissions, then filtering is done.
 * <code>RemoteServiceAdminEvent</code> objects are only delivered to the
 * listener if the bundle which defines the listener object's class has the
 * appropriate <code>EndpointPermission[endpoint,READ]</code> for the endpoint
 * referenced by the event.
 * 
 * 
 * @see RemoteServiceAdminEvent
 * @ThreadSafe
 * @version $Revision$
 */

public interface RemoteServiceAdminListener {
	/**
	 * Receive notification of any export or import registrations and
	 * unregistrations.
	 * 
	 * @param event The {@link RemoteServiceAdminEvent} object.
	 */
	void remoteAdminEvent(RemoteServiceAdminEvent event);
}

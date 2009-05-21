/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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

import java.util.Collection;

/**
 * Interface for notification on discovered remote services.
 * <p>
 * <code>RemoteServiceNotification</code> objects are immutable.
 * 
 * @Immutable
 * @version $Revision$
 */
public interface RemoteServiceNotification {

	/**
	 * Returns information currently known to <code>Discovery</code> regarding
	 * the service endpoint.
	 * 
	 * @return metadata of the service <code>Discovery</code> notifies about. Is
	 *         never <code>null</code>.
	 */
	ServiceEndpointDescription getServiceEndpointDescription();

	/**
	 * Returns interface name criteria of the {@link ImportServiceHandler}
	 * object matching with the interfaces of the
	 * <code>ServiceEndpointDescription</code> and thus caused the notification.
	 * 
	 * @return <code>Collection (&lt;String&gt;)</code> of matching interface
	 *         name criteria of the <code>ImportServiceHandler</code> object
	 *         being notified, or an empty collection if notification hasn't
	 *         been caused by a matching interface name criteria.
	 */
	Collection/* <String> */getMatchedInterfaces();

	/**
	 * Returns filters of the <code>ImportServiceHandler</code> object matching
	 * with the properties of the <code>ServiceEndpointDescription</code> and
	 * thus caused the notification.
	 * 
	 * @return <code>Collection (&lt;String&gt;)</code> of matching filters of
	 *         the <code>ImportServiceHandler</code> object being notified, or
	 *         an empty collection if notification hasn't been caused by a
	 *         matching filter criteria.
	 */
	Collection/* <String> */getMatchedFilters();
}
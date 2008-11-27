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
 * Interface of trackers for discovered remote services. <br>
 * When such a service is registered with the framework, then {@link Discovery}
 * will notify it about remote services matching one of the provided criteria
 * and will keep notifying it on changes of information known to Discovery
 * regarding this services.
 * 
 * <code>Discovery</code> may deliver notifications on discovered services to a
 * <code>DiscoveredServiceTracker</code> out of order and may concurrently call
 * and/or reenter a <code>DiscoveredServiceTracker</code>.
 * 
 * @version $Revision$
 */
public interface DiscoveredServiceTracker {

	/**
	 * Property describing service interfaces this tracker is interested in.
	 * Value of this property is of type String[]. Property is optional, may be
	 * null.
	 */
	public static final String PROP_KEY_MATCH_CRITERIA_INTERFACES = "osgi.discovery.interest.interfaces";

	/**
	 * Property describing filters for services this tracker is interested in.
	 * Value of this property is of type String[]. Property is optional, may be
	 * null.
	 */
	public static final String PROP_KEY_MATCH_CRITERIA_FILTERS = "osgi.discovery.interest.filters";

	/**
	 * Receives notification that information known to Discovery regarding a
	 * remote service has changed. <br>
	 * The tracker is only notified about remote services which fulfill the
	 * matching criteria, either one of the interfaces or one of the filters,
	 * provided as properties of this service.
	 * 
	 * @param notification
	 *            the <code>DiscoveredServiceNotification</code> object
	 *            describing the change.
	 */
	void serviceChanged(DiscoveredServiceNotification notification);
}

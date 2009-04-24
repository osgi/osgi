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

package org.osgi.service.discovery;

/**
 * Interface of trackers for discovered remote services.
 * <p>
 * When a service implementing this interface is registered with the framework,
 * then <code>Discovery</code> will notify it about remote services matching one
 * of the provided criteria and will keep notifying it on changes of information
 * known to Discovery regarding this services.
 * 
 * <code>Discovery</code> may deliver notifications on discovered services to a
 * <code>DiscoveredServiceTracker</code> out of order and may concurrently call
 * and/or reenter a <code>DiscoveredServiceTracker</code>.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface DiscoveredServiceTracker {

	/**
	 * Optional ServiceRegistration property which contains service interfaces
	 * this tracker is interested in.
	 * <p>
	 * Value of this property is of type
	 * <code>Collection (&lt;? extends String&gt;)</code>. May be
	 * <code>null</code> or empty.
	 */
	public static final String	INTERFACE_MATCH_CRITERIA	= "osgi.remote.discovery.interest.interfaces";

	/**
	 * Optional ServiceRegistration property which contains filters for services
	 * this tracker is interested in.
	 * <p>
	 * Note that these filters need to take into account service publication
	 * properties which are not necessarily the same as properties under which a
	 * service is registered. See {@link ServicePublication} for some standard
	 * properties used to publish service metadata.
	 * <p>
	 * The following sample filter will make <code>Discovery</code> notify the
	 * <code>DiscoveredServiceTracker</code> about services providing interface
	 * 'my.company.foo' of version '1.0.1.3':
	 * <code>"(&amp;(service.interface=my.company.foo)(service.interface.version=my.company.foo|1.0.1.3))"</code>.
	 * <p>
	 * Value of this property is of type
	 * <code>Collection (&lt;? extends String&gt;)</code>. May be
	 * <code>null</code>. or empty
	 */
	public static final String	FILTER_MATCH_CRITERIA		= "osgi.remote.discovery.interest.filters";

	/**
	 * Receives notification that information known to <code>Discovery</code>
	 * regarding a remote service has changed.
	 * <p>
	 * The tracker is only notified about remote services which fulfill the
	 * matching criteria, either one of the interfaces or one of the filters,
	 * provided as properties of this service.
	 * <p>
	 * If multiple criteria match, then the tracker is notified about each of
	 * them. This can be done either by a single notification callback or by
	 * multiple subsequent ones.
	 * 
	 * @param notification the <code>DiscoveredServiceNotification</code> object
	 *        describing the change. Is never <code>null</code>.
	 */
	void serviceChanged(DiscoveredServiceNotification notification);
}

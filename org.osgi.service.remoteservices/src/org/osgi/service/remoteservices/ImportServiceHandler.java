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
package org.osgi.service.remoteservices;

/**
 * Interface of handlers for remote service metadata.
 * <p>
 * When a service implementing this interface is registered with the framework,
 * it should be called back about remote services matching one of the provided
 * criteria. Updates regarding the remote service should also be provided
 * through the appropriate callbacks.
 * <p>
 * Notifications on discovered services may be delivered to a
 * <code>ImportServiceHandler</code> out of order and may be concurrent.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ImportServiceHandler {

	/**
	 * Optional service property which contains service interfaces this
	 * ImportServiceHandler is interested in.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	public static final String MATCH_INTERFACES = "match.interfaces";

	/**
	 * Optional service properties which contains the filters of services the
	 * <code>ImportServiceHandler<code> is interested in.  	 
	 * <p>
	 * Note that these filters need to take into account service publication
	 * properties which are not necessarily the same as properties under which a
	 * service is registered. See {@link ExportedEndpointDescription} for some
	 * standard properties used to publish service metadata.
	 * <p>
	 * ### davidb: how do we do this now that we don't put this as a property on the exported 
	 * service any more? Should we maybe add ExportedEndpointDescription.PROVIDED_INTERFACES_VERSION? ###
	 * The following sample filter will make <code>Discovery</code> notify the
	 * <code>ImportServiceHandler</code> about services providing interface
	 * 'my.company.foo' of version '1.0.1.3':
	 * <code>"(&amp;(service.interface=my.company.foo)(service.interface.version=my.company.foo|1.0.1.3))"</code>
	 * .
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	public static final String MATCH_FILTERS = "match.filters";

	/**
	 * Receives notification that information known to <code>Discovery</code>
	 * indicating that a service matching the listening criteria has been
	 * discovered.
	 * <p>
	 * The ImportServiceHandler is only notified about remote services which
	 * fulfill the matching criteria, either one of the interfaces or one of the
	 * filters, provided as properties of this service.
	 * <p>
	 * If multiple criteria match, then the ImportServiceHandler is notified
	 * about each of them. This can be done either by a single notification
	 * callback or by multiple subsequent ones.
	 * 
	 * @param notification
	 *            the <code>ImportServiceHandler</code> object. Is never
	 *            <code>null</code>.
	 * @return <code>true</code> if the handler processes the notification or
	 *         <code>false</code> if the handler chooses not to process the
	 *         notification.
	 */
	boolean importService(RemoteServiceNotification notification);

	/**
	 * Receives notification that information known to <code>Discovery</code>
	 * indicating that the properties of a previously discovered service have
	 * changed.
	 * <p>
	 * The ImportServiceHandler is only notified about remote services which
	 * fulfill the matching criteria, either one of the interfaces or one of the
	 * filters, provided as properties of this service.
	 * <p>
	 * If multiple criteria match, then the ImportServiceHandler is notified
	 * about each of them. This can be done either by a single notification
	 * callback or by multiple subsequent ones.
	 * 
	 * @param notification
	 *            the <code>ImportServiceHandler</code> object. Is never
	 *            <code>null</code>.
	 * @return <code>true</code> if the handler processes the notification or
	 *         <code>false</code> if the handler chooses not to process the
	 *         notification.
	 */
	boolean modifyService(RemoteServiceNotification notification);

	/**
	 * Receives notification that information known to <code>Discovery</code>
	 * indicating that a previously discovered service no longer matches the
	 * listening criteria.
	 * <p>
	 * The ImportServiceHandler is only notified about remote services which
	 * fulfill the matching criteria, either one of the interfaces or one of the
	 * filters, provided as properties of this service.
	 * <p>
	 * If multiple criteria match, then the ImportServiceHandler is notified
	 * about each of them. This can be done either by a single notification
	 * callback or by multiple subsequent ones.
	 * 
	 * @param notification
	 *            the <code>ImportServiceHandler</code> object. Is never
	 *            <code>null</code>.
	 * @return <code>true</code> if the handler processes the notification or
	 *         <code>false</code> if the handler chooses not to process the
	 *         notification.
	 */
	boolean unimportService(RemoteServiceNotification notification);
}

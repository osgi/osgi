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
 * A white board service that represents a listener for endpoints.
 * 
 * An Endpoint Listener represents a participant in the distributed model that
 * is interested in Endpoint Descriptions.
 * 
 * This white board service can be used in many different scenarios. However,
 * the primary use case is to allow a remote manager to be informed of End Point
 * Descriptions available in the network and inform the network about available
 * End Point Descriptions.
 * 
 * Both the network bundle and the manager bundle register an Endpoint Listener
 * service. The manager informs the network bundle about End Points that it
 * creates. The network bundles then uses a protocol like SLP to announce these
 * local end-points to the network.
 * 
 * If the network bundle discovers a new Endpoint through its discovery
 * protocol, then it sends an End Point Description to all the End Point
 * Listener services that are registered (except its own) that have specified an
 * interest in that endpoint.
 * 
 * Endpoint Listener services can express their <i>scope</i> with the service
 * property {@link #ENDPOINT_LISTENER_SCOPE}. This service property is a list of
 * filters. An Endpoint Description should only be given to a Endpoint Listener
 * when there is at least one filter that matches the Endpoint Description
 * properties. given to it.
 * 
 * This filter model is quite flexible. For example, a discovery bundle is only
 * interested in locally originating Endpoint Descriptions. The following filter
 * ensure that it only sees local endpoints.
 * 
 * <pre>
 *   (org.osgi.framework.uuid=72dc5fd9-5f8f-4f8f-9821-9ebb433a5b72)
 * </pre>
 * 
 * In the same vein, a manager that is only interested in remote Endpoint
 * Descriptions can use a filter like:
 * 
 * <pre>
 *   (!(org.osgi.framework.uuid=72dc5fd9-5f8f-4f8f-9821-9ebb433a5b72))
 * </pre>
 * 
 * Where in both cases, the given UUID is the UUID of the local framework that
 * can be found in the Framework properties.
 * 
 * The Endpoint Listener's scope maps very well to the service hooks. A manager
 * can just register all filters found from the Listener Hook as its scope. This
 * will automatically provide it with all known endpoints that match the given
 * scope, without having to inspect the filter string.
 * 
 * In general, when an Endpoint Description is discovered, it should be
 * dispatched to all registered Endpoint Listener services. If a new Endpoint
 * Listener is registered, it should be informed about all currently known
 * Endpoints that match its scope. If a getter of the Endpoint Listener service
 * is unregistered, then all its registered Endpoint Description objects must be
 * removed.
 * 
 * The Endpoint Listener models a <i>best effort</i> approach. Participating
 * bundles should do their utmost to keep the listeners up to date, but
 * implementers should realize that many endpoints come through unreliable
 * discovery processes.
 * 
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface EndpointListener {
	/**
	 * Specifies the interest of this listener with filters. This listener is
	 * only interested in Endpoint Descriptions where its properties match the
	 * given filter. The type of this property must be <code>String+</code>.
	 */
	String	ENDPOINT_LISTENER_SCOPE	= "endpoint.listener.scope";

	/**
	 * Register an endpoint with this listener.
	 * 
	 * If the endpoint matches one of the filters registered with the
	 * {@link #ENDPOINT_LISTENER_SCOPE} service property then this filter should
	 * be given as the <code>matchedFilter</code> parameter.
	 * 
	 * When this service is first registered or it is modified, it should
	 * receive all known endpoints matching the filter.
	 * 
	 * @param endpoint The Endpoint Description to be published
	 * @param matchedFilter The filter from the {@link #ENDPOINT_LISTENER_SCOPE}
	 *        that matched the endpoint, must not be <code>null</code>.
	 */
	void endpointAdded(EndpointDescription endpoint, String matchedFilter);

	/**
	 * Remove the registration of an endpoint.
	 * 
	 * If an endpoint that was registered with the
	 * {@link #endpointAdded(EndpointDescription, String)} method is no longer
	 * available then this method should be called. This will remove the
	 * endpoint from the listener.
	 * 
	 * It is not necessary to remove endpoints when the service is unregistered
	 * or modified in such a way that not all endpoints match the interest
	 * filter anymore.
	 * 
	 * @param endpoint The Endpoint Description that is no longer valid.
	 * @param matchedFilter The filter from the {@link #ENDPOINT_LISTENER_SCOPE}
	 *        that matched the endpoint, must not be <code>null</code>.
	 */
	void endpointRemoved(EndpointDescription endpoint, String matchedFilter);
}

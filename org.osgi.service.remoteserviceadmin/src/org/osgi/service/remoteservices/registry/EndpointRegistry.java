package org.osgi.service.remoteservices.registry;

import java.util.*;

import org.osgi.service.remoteservices.*;

/**
 * A whiteboard service that implements a local or distributed registry of End
 * Point Descriptions.
 * 
 * This service represents a registry of End Point Descriptions. This registry
 * can be implemented locally or it can use some discovery protocol to create a
 * distributed registry.
 * 
 * This whiteboard service can be used in many different scenarios. However, the
 * primary use case is to allow a remote controller to be informed of End Point
 * Descriptions available in the network and inform the network about available
 * End Point Descriptions.
 * 
 * Both the network bundle and the controller bundle register a Endpoint
 * Registry service. The controller bundle informs the network bundle about End
 * Points that it creates. The network bundles then uses a protocol like for
 * example SLP to announce these local end-points to the network.
 * 
 * If the network bundle discovers a new Endpoint through its discovery
 * protocol, then it sends an End Point Description to all the End Point
 * Registry services registered except its own.
 * 
 * Endpoint Registry services can express their <i>interest</i> with a service
 * property. This service property is a list of filters that must match any
 * Endpoint Description given to it. This feature can be used by the network
 * bundle to limit its interest to only Endpoint Descriptions that originate
 * locally (### how?). The controller can limit the Endpoint Descriptions that
 * map to a service needed by some bundle in the framework. It can find out
 * about this need using the service hooks.
 * 
 * In general, when an Endpoint Description is discovered, it should be
 * dispatched to all registered Endpoint Registry services. If a new Endpoint
 * Registry is registered, it should be informed about all currently known
 * Endpoints.
 * 
 * @ThreadSafe
 */
public interface EndpointRegistry {
	/**
	 * Specifies the interest of this registry with filters. This registry is
	 * only interested in Endpoint Descriptions where its properties match the
	 * given filter. The type of this property must be <code>String+</code>.
	 */
	String REMOTE_REGISTRY_INTEREST = "remote.registry.interest";

	/**
	 * Answer a list of EndPoint Description maintained by this registry that
	 * match the given filter, or all if the filter is null.
	 * 
	 * An implementation my limit the number of returned objects.
	 * 
	 * @param filter
	 *            The filter to use to limit the return set of
	 *            EndpointDescription objects, or null if all are requested.
	 * @return A list of EndpointDescription objects
	 */
	List/* <EndpointDescription> */list(String filter);

	/**
	 * Register an endpoint with this registry.
	 * 
	 * If the endpoint matches one of the filters registered with the
	 * <code>REMOTE_REGISTRY_INTEREST</code> service property then this filter
	 * should be given as the <code>matchedFilter</code> parameter.
	 * 
	 * When this service is first registered, it should receive all known
	 * endpoints matching the filter. If the service is modified, any newly
	 * matched endpoints should be registered here.
	 * 
	 * @param endpoint
	 *            The description of the endpoint that is published in this
	 *            registry
	 * @param matchedFilter
	 *            The filter from the <code>REMOTE_REGISTRY_INTEREST</code> that matched the
	 *            endpoint or <code>null</code>.
	 */
	void registerEndpoint(EndpointDescription endpoint, String matchedFilter);

	/**
	 * Remove the registration of an endpoint.
	 * 
	 * If an endpoint that was registered with the registerEndpoint method is no
	 * longer available then this method should be called. This will remove the
	 * endpoint from the registry.
	 * 
	 * It is not necessary to remove endpoints when the service is unregistered
	 * or modified in such a way that not all endpoints match the interest
	 * filter anymore.
	 * 
	 * @param endpoint
	 *            The endpoint that is no longer valid.
	 */
	void unregisterEndpoint(EndpointDescription endpoint);
}

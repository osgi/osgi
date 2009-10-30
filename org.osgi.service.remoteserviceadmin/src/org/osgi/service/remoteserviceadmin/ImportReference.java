package org.osgi.service.remoteserviceadmin;

import org.osgi.framework.ServiceReference;

/**
 * An Import Reference associates an active proxy service to a remote endpoint.
 * 
 * The Import Reference can be used to reference an imported service. When the
 * service is no longer imported, all methods must return <code>null</code>;
 * 
 * @ThreadSafe
 */
public interface ImportReference {
	/**
	 * Answer the associated Service Reference for the proxy to the endpoint.
	 * 
	 * @return A Service Reference to the proxy for the endpoint.
	 */
	ServiceReference getImportedService();

	/**
	 * Answer the associated remote Endpoint Description.
	 * 
	 * @return A Endpoint Description for the remote endpoint.
	 */
	EndpointDescription getImportedEndpointDescription();
}

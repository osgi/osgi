package org.osgi.service.remoteserviceadmin;

import org.osgi.framework.ServiceReference;

/**
 * An Export Reference associates a service with a local endpoint.
 * 
 * The Export Reference can be used to reference an exported service. When the 
 * service is no longer exported, all methods must return <code>null</code>;
 * 
 * @ThreadSafe
 */
public interface ExportReference {
	/**
	 * Return the service being exported.
	 * 
	 * @return The service being exported, must be <code>null</code> when this
	 *         registration is unregistered.
	 */
	ServiceReference getExportedService();

	/**
	 * Return the Endpoint Description that is created for this registration.
	 * 
	 * @return the local Endpoint Description
	 */
	EndpointDescription getEndpointDescription();
}

package org.osgi.service.remoteservices.admin;

import java.util.*;

import org.osgi.framework.*;

/**
 * An Export Registration associates a service to a local
 * endpoint.
 * 
 * The Export Registration can be used to delete the endpoint associated with an
 * this registration. It is created with the{@link RemoteServiceAdmin#exportService}
 * method.
 * 
 * When this Export Registration has been unregistered, the methods must all return <code>null</code>.
 * 
 * @ThreadSafe
 */
public interface ExportRegistration {
	/**
	 * Return the service being exported.
	 * @return The service being exported, must be <code>null</code> when this registration is unregistered.
	 */
	ServiceReference getExportedService();
	
	/**
	 * Return the Endpoint Description that is created for this registration.
	 * 
	 * @return the local Endpoint Description
	 */
	List/*<EndpointDescription>*/ getEndpointDescription();
	
	/**
	 * Delete the local endpoint and disconnect any remote distribution providers. After this method
	 * returns, all the methods must return <code>null</code>.
	 * 
	 * This method has no effect when the endpoint is already destroyed or being destroyed.
	 */
	void unregister();
}

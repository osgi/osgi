package org.osgi.service.remoteserviceadmin;

import org.osgi.framework.ServiceReference;

/**
 * An Import Registration associates an active proxy service to a remote
 * endpoint.
 * 
 * The Import Registration can be used to delete the proxy associated with an
 * endpoint. It is created with the{@link RemoteServiceAdmin#importService}
 * method.
 * 
 * @ThreadSafe
 */
public interface ImportRegistration {
	/**
	 * Answer the associated Service Reference for the proxy to the endpoint.
	 * 
	 * @return A Service Reference to the proxy for the endpoint.
	 * @throws IllegalStateException Thrown when this object was not properly initialized, see {@link #getException()}
	 */
	ServiceReference getImportedService();

	/**
	 * Answer the associated remote Endpoint Description.
	 * 
	 * @return A Endpoint Description for the remote endpoint.
	 * @throws IllegalStateException Thrown when this object was not properly initialized, see {@link #getException()}
	 */
	EndpointDescription getImportedEndpointDescription();

	/**
	 * Unregister this Import Registration. This must close the connection 
	 * to the end endpoint unregister the proxy. After this method returns,
	 * all other methods must return null.
	 * 
	 * This method has no effect when the service is already unregistered or in the process off.
	 */
	void close();
	
	
	/**
	 * Exception for any error during the import process.
	 * 
	 * If the Remote Admin for some reasons is unable to create a registration,
	 * then it must return a <code>Throwable</code> from this method. In this
	 * case, all other methods must return on this interface must thrown an
	 * Illegal State Exception. If no error occurred, this method must return
	 * <code>null</code>.
	 * 
	 * The error must be set before this Import Registration is returned.
	 * Asynchronously occurring errors must be reported to the log.
	 * 
	 * @return The exception that occurred during the creation of the
	 *         registration or <code>null</code> if no exception occurred.
	 */
	Throwable getException();

}

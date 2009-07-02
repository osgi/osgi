package org.osgi.service.remoteservices.admin;

import org.osgi.framework.*;
import org.osgi.service.remoteservices.*;

/**
 * An Export Registration associates a service to a local endpoint.
 * 
 * The Export Registration can be used to delete the endpoint associated with an
 * this registration. It is created with the{@link RemoteServiceAdmin#exportService(ServiceReference)}
 * method.
 * 
 * When this Export Registration has been unregistered, the methods must all
 * return <code>null</code>.
 * 
 * @ThreadSafe
 */
public interface ExportRegistration {
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

	/**
	 * Delete the local endpoint and disconnect any remote distribution
	 * providers. After this method returns, all the methods must return
	 * <code>null</code>.
	 * 
	 * This method has no effect when the endpoint is already destroyed or being
	 * destroyed.
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

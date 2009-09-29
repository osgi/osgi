package org.osgi.service.remoteserviceadmin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * A Remote Service Admin manages the import and export of services.
 * 
 * A Distribution Provider can expose a control interface. This interface allows
 * the a remote manager to control the export and import of services.
 * 
 * The API allows a remote manager to export a service, to import a service, and
 * find out about the current imports and exports.
 * 
 * @ThreadSafe
 */
public interface RemoteServiceAdmin {

	/**
	 * Export a service to a given endpoint. The Remote Service Admin must
	 * create an endpoint from the given description that can be used by other
	 * Distrbution Providers to connect to this Remote Service Admin and use the
	 * exported service. This method can return null if the service could not be
	 * exported because the endpoint could not be implemented by this Remote
	 * Service Admin.
	 * 
	 * The properties on a Service Reference are case insensitive while the
	 * properties on a <code>properties</code> are case sensitive. A value in
	 * the <code>properties</code> must therefore override any case variant in
	 * the properties of the Service Reference.
	 * 
	 * @param ref The Service Reference to export
	 * @param properties The properties to create a local endpoint that can be
	 *        implemented by this Remote Service Admin. If this is null, the
	 *        endpoint will be determined by the properties on the service. The
	 *        properties are the same as given for an exported service. They are
	 *        overlaid over any properties the service defines (case
	 *        insensitive). This parameter can be <code>null</code>, this
	 *        should be treated as an empty map.
	 * @return An Export Registration that combines the Endpoint Description and
	 *         the Service Reference or <code>null</code> if the service could
	 *         not be exported
	 * @throws IllegalArgumentException
	 * @throws UnsupportedOperationException
	 * 
	 * TODO discuss case difference in properties
	 * 
	 * TODO More exceptions?
	 */
	List/* <ExportRegistration> */exportService(ServiceReference ref,
			Map/* <String,Object> */properties)
			throws IllegalArgumentException, UnsupportedOperationException;

	/**
	 * Import a service from an endpoint. The Remote Service Admin must use the
	 * given endpoint to create a proxy. This method can return null if the
	 * service could not be imported. ### do we need exceptions?
	 * 
	 * @param endpoint The Endpoint Description to be used for import
	 * @return An Import Registration that combines the Endpoint Description and
	 *         the Service Reference or <code>null</code> if the endpoint
	 *         could not be imported
	 */
	ImportRegistration importService(EndpointDescription endpoint);

	/**
	 * Answer the currently active Export Registrations.
	 * 
	 * @return A collection of Export Registrations that are currently active.
	 */
	Collection/* <ExportRegistration> */getExportedServices();

	/**
	 * Answer the currently active Import Registrations.
	 * 
	 * @return A collection of Import Registrations that are currently active.
	 */
	Collection/* <ImportRegistration> */getImportedEndpoints();

}

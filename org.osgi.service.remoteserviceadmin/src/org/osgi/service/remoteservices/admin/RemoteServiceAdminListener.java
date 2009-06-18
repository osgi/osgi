package org.osgi.service.remoteservices.admin;

import java.util.*;

import org.osgi.framework.*;

/**
 * A Remote Service Admin Listener is notified asynchronously of any export or
 * import registrations and unregistrations.
 * 
* @ThreadSafe
 */

public interface RemoteServiceAdminListener {
	/**
	 * Add an import registration. The Remote Services Admin will call this
	 * method when it imports a service. When this service is registered, the
	 * Remote Service Admin must notify the listener of all existing Import
	 * Registrations.
	 * 
	 * @param reg
	 *            The associated import registration
	 */
	void addImport(ImportRegistration reg);

	/**
	 * Add an export registration. The Remote Services Admin will call this
	 * method when it exports a service. When this service is registered, the
	 * Remote Service Admin must notify the listener of all existing Export
	 * Registrations.
	 * 
	 * @param reg
	 *            The associated export registration
	 */
	void addExport(ExportRegistration reg);

	/**
	 * Remove an export registration. The Remote Services Admin will call this
	 * method when it removes the export of a service.
	 * 
	 * @param reg
	 *            The associated export registration
	 */
	void removeExport(ExportRegistration reg);

	/**
	 * Remove an export registration. The Remote Services Admin will call this
	 * method when it removes the import of a service.
	 * 
	 * @param reg
	 *            The associated import registration
	 */
	void removeImport(ImportRegistration reg);
	
	
}

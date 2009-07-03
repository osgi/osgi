package org.osgi.service.remoteadmin;

import org.osgi.framework.*;

/**
 * 
 * Provides the event information for a Remote Admin event.
 * 
 * @Immutable
 */
public class RemoteAdminEvent {
	/**
	 * The Type of the event.
	 * 
	 */
	public enum Type {
		/**
		 * Add an import registration. The Remote Services Admin will call this
		 * method when it imports a service. When this service is registered,
		 * the Remote Service Admin must notify the listener of all existing
		 * Import Registrations.
		 * 
		 */
		IMPORT_REGISTRATION,

		/**
		 * Add an export registration. The Remote Services Admin will call this
		 * method when it exports a service. When this service is registered,
		 * the Remote Service Admin must notify the listener of all existing
		 * Export Registrations.
		 */
		EXPORT_REGISTRATION,

		/**
		 * Remove an export registration. The Remote Services Admin will call
		 * this method when it removes the export of a service.
		 * 
		 */
		EXPORT_UNREGISTRATION,

		/**
		 * Remove an import registration. The Remote Services Admin will call
		 * this method when it removes the import of a service.
		 * 
		 */
		IMPORT_UNREGISTRATION,

		/**
		 * A fatal importing error occurred. The Import Registration has been closed.
		 */
		IMPORT_ERROR, 
		/**
		 * A fatal exporting error occurred. The Export Registration has been closed.
		 */
		EXPORT_ERROR, 
		
		/**
		 * A problematic situation occurred, the export is still active.
		 */
		EXPORT_WARNING,
		/**
		 * A problematic situation occurred, the import is still active.
		 */
		IMPORT_WARNING

	};

	private ImportRegistration importRegistration;
	private ExportRegistration exportRegistration;
	private Throwable exception;
	private Type type;
	private Bundle source;

	RemoteAdminEvent(Type type, Bundle source,
			ImportRegistration importRegistration,
			ExportRegistration exportRegistration, Throwable exception) {
		this.type = type;
		this.source = source;
		this.importRegistration = importRegistration;
		this.exportRegistration = exportRegistration;
		this.exception = exception;
	}

	/**
	 * @return the importRegistration
	 */
	public ImportRegistration getImportRegistration() {
		return importRegistration;
	}

	/**
	 * @return the exportRegistration
	 */
	public ExportRegistration getExportRegistration() {
		return exportRegistration;
	}

	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the source
	 */
	public Bundle getSource() {
		return source;
	}
}

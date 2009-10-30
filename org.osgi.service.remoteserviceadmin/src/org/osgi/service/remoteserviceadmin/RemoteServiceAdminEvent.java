package org.osgi.service.remoteserviceadmin;

import org.osgi.framework.Bundle;

/**
 * 
 * Provides the event information for a Remote Admin event.
 * 
 * @Immutable
 */
public class RemoteServiceAdminEvent {
	/**
	 * Add an import registration. The Remote Services Admin will call this
	 * method when it imports a service. When this service is registered, the
	 * Remote Service Admin must notify the listener of all existing Import
	 * Registrations.
	 * 
	 */
	public static final int			IMPORT_REGISTRATION		= 1;

	/**
	 * Add an export registration. The Remote Services Admin will call this
	 * method when it exports a service. When this service is registered, the
	 * Remote Service Admin must notify the listener of all existing Export
	 * Registrations.
	 */
	public static final int			EXPORT_REGISTRATION		= 2;

	/**
	 * Remove an export registration. The Remote Services Admin will call this
	 * method when it removes the export of a service.
	 * 
	 */
	public static final int			EXPORT_UNREGISTRATION	= 3;

	/**
	 * Remove an import registration. The Remote Services Admin will call this
	 * method when it removes the import of a service.
	 * 
	 */
	public static final int			IMPORT_UNREGISTRATION	= 4;

	/**
	 * A fatal importing error occurred. The Import Registration has been
	 * closed.
	 */
	public static final int			IMPORT_ERROR			= 5;

	/**
	 * A fatal exporting error occurred. The Export Registration has been
	 * closed.
	 */
	public static final int			EXPORT_ERROR			= 6;

	/**
	 * A problematic situation occurred, the export is still active.
	 */
	public static final int			EXPORT_WARNING			= 7;
	/**
	 * A problematic situation occurred, the import is still active.
	 */
	public static final int			IMPORT_WARNING			= 8;

	private final ImportReference	importReference;
	private final ExportReference	exportReference;
	private final Throwable			exception;
	private final int				type;
	private final Bundle			source;

	/**
	 * Private constructor.
	 * 
	 * @param type The event type
	 * @param source The source bundle, must not be <code>null</code>.
	 * @param importReference The importReference, can be <code>null</code>.
	 * @param exportReference The exportReference, can be <code>null</code>.
	 * @param exception Any exceptions encountered, can be <code>null</code>
	 */
	RemoteServiceAdminEvent(int type, Bundle source,
			ImportReference importReference, ExportReference exportReference,
			Throwable exception) {
		this.type = type;
		this.source = source;
		this.importReference = importReference;
		this.exportReference = exportReference;
		this.exception = exception;
	}

	/**
	 * Create a Remote Service Admin Event for an export issue.
	 * 
	 * @param type The event type
	 * @param source The source bundle, must not be <code>null</code>.
	 * @param exportRegistration The exportRegistration, can not be
	 *        <code>null</code>.
	 * @param exception Any exceptions encountered, can be <code>null</code>
	 */
	public RemoteServiceAdminEvent(int type, Bundle source,
			ExportReference exportRegistration, Throwable exception) {
		this(type, source, null, exportRegistration, exception);
	}

	/**
	 * Create a Remote Service Admin Event for an import issue.
	 * 
	 * @param type The event type
	 * @param source The source bundle, must not be <code>null</code>.
	 * @param importRegistration The importRegistration, can not be
	 *        <code>null</code>.
	 * @param exception Any exceptions encountered, can be <code>null</code>
	 */
	public RemoteServiceAdminEvent(int type, Bundle source,
			ImportReference importRegistration, Throwable exception) {
		this(type, source, importRegistration, null, exception);
	}

	/**
	 * @return the importRegistration or <code>null</code>
	 */
	public ImportReference getImportReference() {
		return importReference;
	}

	/**
	 * @return the exportRegistration or <code>null</code>
	 */
	public ExportReference getExportReference() {
		return exportReference;
	}

	/**
	 * @return the exception or <code>null</code>
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the source
	 */
	public Bundle getSource() {
		return source;
	}
}

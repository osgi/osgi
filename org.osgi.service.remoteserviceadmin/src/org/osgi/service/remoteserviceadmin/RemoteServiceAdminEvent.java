/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.remoteserviceadmin;

import org.osgi.framework.Bundle;

/**
 * Provides the event information for a Remote Admin event.
 * 
 * @Immutable
 * @version $Revision$
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
	private RemoteServiceAdminEvent(int type, Bundle source,
			ImportReference importReference, ExportReference exportReference,
			Throwable exception) {
		if (source == null) {
			throw new NullPointerException("source must not be null");
		}
		this.type = type;
		this.source = source;
		this.importReference = importReference;
		this.exportReference = exportReference;
		this.exception = exception;
	}

	/**
	 * Create a Remote Service Admin Event for an export notification.
	 * 
	 * @param type The event type.
	 * @param source The source bundle, must not be <code>null</code>.
	 * @param exportReference The exportReference, can not be <code>null</code>.
	 * @param exception Any exceptions encountered, can be <code>null</code>.
	 */
	public RemoteServiceAdminEvent(int type, Bundle source,
			ExportReference exportReference, Throwable exception) {
		this(type, source, null, exportReference, exception);
	}

	/**
	 * Create a Remote Service Admin Event for an import notification.
	 * 
	 * @param type The event type.
	 * @param source The source bundle, must not be <code>null</code>.
	 * @param importReference The importReference, can not be <code>null</code>.
	 * @param exception Any exceptions encountered, can be <code>null</code>.
	 */
	public RemoteServiceAdminEvent(int type, Bundle source,
			ImportReference importReference, Throwable exception) {
		this(type, source, importReference, null, exception);
	}

	/**
	 * Return the Import Reference for this event.
	 * 
	 * @return The Import Reference or <code>null</code>.
	 */
	public ImportReference getImportReference() {
		return importReference;
	}

	/**
	 * Return the Export Reference for this event.
	 * 
	 * @return The Export Reference or <code>null</code>.
	 */
	public ExportReference getExportReference() {
		return exportReference;
	}

	/**
	 * Return the exception for this event.
	 * 
	 * @return The exception or <code>null</code>.
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Return the type of this event.
	 * 
	 * @return The type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Return the bundle source of this event.
	 * 
	 * @return The bundle source of this event.
	 */
	public Bundle getSource() {
		return source;
	}
}

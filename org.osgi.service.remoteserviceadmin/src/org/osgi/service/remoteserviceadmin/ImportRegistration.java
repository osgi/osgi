/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

/**
 * An Import Registration associates an active proxy service to a remote
 * endpoint.
 * 
 * The Import Registration can be used to delete the proxy associated with an
 * endpoint. It is created with the
 * {@link RemoteServiceAdmin#importService(EndpointDescription)} method.
 * 
 * When this Import Registration has been closed, all methods must return
 * <code>null</code>.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ImportRegistration {
	/**
	 * Return the Import Reference for the imported service.
	 * 
	 * @return The Import Reference for this registration.
	 * @throws IllegalStateException When this registration was not properly
	 *         initialized. See {@link #getException()}.
	 */
	ImportReference getImportReference();

	/**
	 * Close this Import Registration. This must close the connection to the
	 * endpoint and unregister the proxy. After this method returns, all other
	 * methods must return <code>null</code>.
	 * 
	 * This method has no effect when this registration has already been closed
	 * or is being closed.
	 */
	void close();

	/**
	 * Return the exception for any error during the import process.
	 * 
	 * If the Remote Service Admin for some reasons is unable to properly
	 * initialize this registration, then it must return an exception from this
	 * method. If no error occurred, this method must return <code>null</code>.
	 * 
	 * The error must be set before this Import Registration is returned.
	 * Asynchronously occurring errors must be reported to the log.
	 * 
	 * @return The exception that occurred during the initialization of this
	 *         registration or <code>null</code> if no exception occurred.
	 */
	Throwable getException();
}

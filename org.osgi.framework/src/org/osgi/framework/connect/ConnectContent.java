/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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
package org.osgi.framework.connect;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleRevisions;

/**
 * A connect content provides a {@link Framework framework} access to the
 * content of a connect {@link ConnectModule module}. A framework may
 * {@link ConnectModule#open() open} and {@link #close() close} the content for
 * a connect module multiple times while the connect module is in use by the
 * framework instance. The framework must close the connect content once the
 * connect content is no longer used as the content of a current bundle revision
 * or an in use bundle revision.
 * 
 * @see BundleRevisions
 * @ThreadSafe
 * @author $Id$
 */
public interface ConnectContent {

	/**
	 * Returns an iterable with all the entry names available in this
	 * ConnectContent
	 * 
	 * @return the entry names
	 * @throws IOException if an error occurs reading the ConnectContent
	 * @throws IllegalStateException if the connect content has been closed
	 */
	Iterable<String> getEntries() throws IOException;

	/**
	 * Returns the connect entry for the specified name, or {@code null} if not
	 * found.
	 * 
	 * @param name the name of the entry
	 * @return the connect entry, or {@code null} if not found.
	 * @throws IllegalStateException if the connect content has been closed
	 */
	Optional<ConnectEntry> getEntry(String name);

	/**
	 * @param name
	 * @return null if framework should handle
	 * @throws IllegalArgumentException if no such entry
	 * @throws IOException
	 */
	// TODO remove this?
	ConnectContent getEntryAsContent(String name)
			throws IllegalArgumentException, IOException;

	/**
	 * @param name
	 * @return null if framework should handle
	 * @throws IllegalArgumentException if no such entry
	 * @throws IOException
	 */
	// TODO remove this?
	String getEntryAsNativeLibrary(String name)
			throws IllegalArgumentException, IOException;

	/**
	 * Closes this connect content.
	 * 
	 * @throws IOException if an error occurred closing the connect content
	 */
	void close() throws IOException;

	/**
	 * Represents the entry of a connect module
	 */
	public interface ConnectEntry {
		/**
		 * Returns the name of the entry
		 * 
		 * @return the name of the entry
		 */
		String getName();

		/**
		 * Returns the size of the entry
		 * 
		 * @return the size of the entry
		 */
		public int getContentLength();

		/**
		 * Returns the last modification time of the entry
		 * 
		 * @return the last modification time of the entry
		 */
		public long getLastModified();

		/**
		 * Returns the content of the entry as a byte array.
		 * 
		 * @return the content bytes
		 * @throws IOException if an error occurs reading the content
		 */
		byte[] getBytes() throws IOException;

		/**
		 * Returns the content of the entry as an input stream.
		 * 
		 * @return the content input stream
		 * @throws IOException if an error occurs reading the content
		 */
		InputStream getIntputStream() throws IOException;

		/**
		 * Returns the URL to the entry.
		 * 
		 * @return the URL to the entry
		 */
		// TODO remove this?
		URL getURL();
	}
}
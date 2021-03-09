/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.prefs;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.service.prefs.BackingStoreException;

/**
 * Root node for a tree of SimplePreferences nodes. This implementation passes
 * all flush() commands to the root node which saves out the whole tree.
 * 
 * @author $Id$
 */
class SimpleRootPref extends SimplePreferences {
	final File		preferencesFile;
	final File		tmpFile;
	private boolean	modified	= false;

	public SimpleRootPref(final File preferencesFile, File tmpFile) {
		super(null, "");
		this.preferencesFile = preferencesFile;
		this.tmpFile = tmpFile;
		//j2security
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				//endblock
				TextFileSupport.read(preferencesFile, SimpleRootPref.this);
				//j2security
				return null;
			}
		});
		//endblock
	}

	// Attempt to avoid possible data loss on e.g. a power
	// failure while writing the file: Write to a temp file
	// and then rename it.
	@Override
	public void flush() throws BackingStoreException {
		synchronized (lock) {
			if (isRemoved()) {	// RFC 60
				throw new IllegalStateException("Node has been removed.");
			}
			if (modified) {
				modified = false;
				//j2security
				try {
					AccessController
							.doPrivileged(
									new PrivilegedExceptionAction<Void>() {
								@Override
										public Void run()
										throws BackingStoreException {
									//endblock
									tmpFile.delete();
									TextFileSupport.write(tmpFile,
											SimpleRootPref.this);
									if (!tmpFile.renameTo(preferencesFile)) {
										// can't rename to existing file on
										// Windows
										preferencesFile.delete();
										if (!tmpFile
												.renameTo(preferencesFile)) {
											throw new BackingStoreException(
													"rename " + "failed");
										}
									}
									//j2security
									return null;
								}
							});
				}
				catch (PrivilegedActionException pae) {
					throw (BackingStoreException) pae.getException();
				}
				//endblock
			}
		}
	}

	/*
	 * Not synchronized on lock, to avoid deadlock, and so that modifications
	 * can be done concurrently with flush. When a modification occurs during a
	 * flush, the change may or may not be included in the flushed file, but the
	 * modified flag will be left set, so the next flush will be sure to write
	 * the file again, whether or not it is really necessary.
	 * 
	 * (The other way to avoid deadlock would be to have a single lock object
	 * for the whole tree. This would avoid the occasional unnecessary writing
	 * out of the file, but at the cost of holding up all accesses during a
	 * flush.)
	 */
	@Override
	void setModified() {
		modified = true;
	}

	/*
	 * RFC 60 Override SimplePreferences since we have no parent. 
	 * Delete the backing store.
	 */
	@Override
	protected void removeSpi() throws BackingStoreException{
		//j2security
		try {
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
						@Override
						public Void run()
								throws BackingStoreException {
							//endblock
							tmpFile.delete();
							preferencesFile.delete();
							//j2security
							return null;
						}
					});
		}
		catch (PrivilegedActionException pae) {
			throw (BackingStoreException) pae.getException();
		}
		//endblock
		setModified();
	}
}

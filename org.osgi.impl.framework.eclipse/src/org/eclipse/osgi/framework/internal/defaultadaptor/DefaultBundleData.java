/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.defaultadaptor;

import java.io.File;
import java.io.IOException;
import org.eclipse.osgi.framework.adaptor.core.AbstractBundleData;
import org.eclipse.osgi.framework.debug.Debug;
import org.osgi.framework.BundleException;

/**
 * The <code>BundleData</code> represents a single bundle that is persistently 
 * stored by a <code>FrameworkAdaptor</code>.  A <code>BundleData</code> creates
 * the ClassLoader for a bundle, finds native libraries installed in the
 * FrameworkAdaptor for the bundle, creates data files for the bundle,
 * used to access bundle entries, manifest information, and getting and saving
 * metadata.
 * 
 */
public class DefaultBundleData extends AbstractBundleData {
	/**
	 * Read data from an existing directory.
	 * This constructor is used by getInstalledBundles.
	 *
	 * @throws NumberFormatException if the directory is not a
	 * number, the directory contains a ".delete" file or
	 * the directory does not contain a ".bundle" file.
	 * @throws IOException If an error occurs initializing the bundle data.
	 */
	public void initializeExistingBundle() throws BundleException, IOException {
		File delete = new File(getBundleStoreDir(), ".delete"); //$NON-NLS-1$

		/* and the directory is not marked for delete */
		if (delete.exists())
			throw new IOException();

		createBaseBundleFile();

		loadFromManifest();
	}

	/**
	 * Constructs a DefaultBundleData for the DefaultAdaptor.
	 *
	 * @param adaptor the DefaultAdaptor for this DefaultBundleData
	 * @param id the Bundle ID for this DefaultBundleData
	 */
	public DefaultBundleData(DefaultAdaptor adaptor, long id) {
		super(adaptor, id);
	}

	public String toString() {
		return getLocation();
	}

	/**
	 * Save the bundle data in the data file.
	 *
	 * @throws IOException if a write error occurs.
	 */
	public synchronized void save() throws IOException {
		if (adaptor.canWrite())
			((DefaultAdaptor) adaptor).saveMetaDataFor(this);
	}

	/**
	 * Return the top level bundle directory.
	 *
	 * @return Top level bundle directory.
	 */
	protected File createBundleStoreDir() {
		if (!getBundleStoreDir().exists() && !getBundleStoreDir().mkdirs()) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle store directory: " + getBundleStoreDir().getPath()); //$NON-NLS-1$
			}
		}
		return getBundleStoreDir();
	}

}

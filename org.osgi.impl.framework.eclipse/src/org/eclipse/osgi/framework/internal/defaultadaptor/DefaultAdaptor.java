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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.core.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

/**
 * The DefaultAdaptor for the Framework.  This adaptor uses
 * root bundle store directory on the local filesystem to 
 * store bundle files and bundle data.  
 * <p>
 * Each bundle installed in the Framework will have a unique 
 * directory using the bundle ID as the name.  Each bundles
 * unique directory has a generational directory a data directory 
 * and a metadata file.  
 * <p>
 * The generational directory is used to store the different 
 * versions of the bundle that have been installed.  Each time
 * the bundle is updated a new generational directory will be
 * created.
 * <p>
 * The data directory is used to create data file objects requested
 * by the bundle.  This directory will not change when updating
 * a bundle
 * <p>
 * The metadata file contains persistent data about the bundle
 * (e.g. startlevel, persistent start state, etc)
 */
public class DefaultAdaptor extends AbstractFrameworkAdaptor {
	public static final String METADATA_ADAPTOR_NEXTID = "METADATA_ADAPTOR_NEXTID"; //$NON-NLS-1$
	public static final String METADATA_ADAPTOR_IBSL = "METADATA_ADAPTOR_IBSL"; //$NON-NLS-1$

	public static final String METADATA_BUNDLE_GEN = "METADATA_BUNDLE_GEN"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_LOC = "METADATA_BUNDLE_LOC"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_REF = "METADATA_BUNDLE_REF"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_NAME = "METADATA_BUNDLE_NAME"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_NCP = "METADATA_BUNDLE_NCP"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_ABSL = "METADATA_BUNDLE_ABSL"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_STATUS = "METADATA_BUNDLE_STATUS"; //$NON-NLS-1$
	public static final String METADATA_BUNDLE_METADATA = "METADATA_BUNDLE_METADATA"; //$NON-NLS-1$
	public static final String METADATA_LAST_MODIFIED = "METADATA_LAST_MODIFIED"; //$NON-NLS-1$

	/**
	 * The MetaData for the default adaptor 
	 */
	protected MetaData fwMetadata;

	public DefaultAdaptor(String[] args) {
		super(args);
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getInstalledBundles()
	 */
	public BundleData[] getInstalledBundles() {
		String list[] = getBundleStoreRootDir().list();

		if (list == null) {
			return null;
		}
		ArrayList bundleDatas = new ArrayList(list.length);

		/* create bundle objects for all installed bundles. */
		for (int i = 0; i < list.length; i++) {
			try {
				DefaultBundleData data;

				long id = -1;
				try {
					id = Long.parseLong(list[i]);
				} catch (NumberFormatException nfe) {
					continue;
				}
				data = (DefaultBundleData) getElementFactory().createBundleData(this, id);
				loadMetaDataFor(data);
				data.initializeExistingBundle();

				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("BundleData created: " + data); //$NON-NLS-1$
				}
				processExtension(data, EXTENSION_INITIALIZE);
				bundleDatas.add(data);
			} catch (BundleException e) {
				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("Unable to open Bundle[" + list[i] + "]: " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					Debug.printStackTrace(e);
				}
			} catch (IOException e) {
				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("Unable to open Bundle[" + list[i] + "]: " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					Debug.printStackTrace(e);
				}
			}
		}

		return (BundleData[]) bundleDatas.toArray(new BundleData[bundleDatas.size()]);
	}

	public void setInitialBundleStartLevel(int value) {
		super.setInitialBundleStartLevel(value);
		try {
			persistInitialBundleStartLevel(value);
		} catch (IOException e) {
			eventPublisher.publishFrameworkEvent(FrameworkEvent.ERROR, context.getBundle(), e);
		}
	}

	protected void persistInitialBundleStartLevel(int value) throws IOException {
		fwMetadata.setInt(METADATA_ADAPTOR_IBSL, value);
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					fwMetadata.save();
					return null;
				}
			});
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof IOException) {
				throw (IOException)e.getException();
			}
			throw (RuntimeException)e.getException();
		}
	}

	public AdaptorElementFactory getElementFactory() {
		if (elementFactory == null)
			elementFactory = new DefaultElementFactory();
		return elementFactory;
	}

	protected void loadMetaDataFor(DefaultBundleData data) throws IOException {
		MetaData bundleMetaData = (new MetaData(new File(data.getBundleStoreDir(), ".bundle"), "Bundle metadata")); //$NON-NLS-1$ //$NON-NLS-2$
		bundleMetaData.load();

		data.setLocation(bundleMetaData.get(METADATA_BUNDLE_LOC, null));
		data.setFileName(bundleMetaData.get(METADATA_BUNDLE_NAME, null));
		data.setGeneration(bundleMetaData.getInt(METADATA_BUNDLE_GEN, -1));
		data.setNativePaths(bundleMetaData.get(METADATA_BUNDLE_NCP, null));
		data.setStartLevel(bundleMetaData.getInt(METADATA_BUNDLE_ABSL, 1));
		data.setStatus(bundleMetaData.getInt(METADATA_BUNDLE_STATUS, 0));
		data.setReference(bundleMetaData.getBoolean(METADATA_BUNDLE_REF, false));
		data.setLastModified(bundleMetaData.getLong(METADATA_LAST_MODIFIED, 0));

		if (data.getGeneration() == -1 || data.getFileName() == null || data.getLocation() == null) {
			throw new IOException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION); 
		}
	}

	public void saveMetaDataFor(AbstractBundleData data) throws IOException {
		MetaData bundleMetadata = (new MetaData(new File(((DefaultBundleData) data).createBundleStoreDir(), ".bundle"), "Bundle metadata")); //$NON-NLS-1$ //$NON-NLS-2$
		bundleMetadata.load();

		bundleMetadata.set(METADATA_BUNDLE_LOC, data.getLocation());
		bundleMetadata.set(METADATA_BUNDLE_NAME, data.getFileName());
		bundleMetadata.setInt(METADATA_BUNDLE_GEN, data.getGeneration());
		String nativePaths = data.getNativePathsString();
		if (nativePaths != null) {
			bundleMetadata.set(METADATA_BUNDLE_NCP, nativePaths);
		}
		bundleMetadata.setInt(METADATA_BUNDLE_ABSL, data.getStartLevel());
		bundleMetadata.setInt(METADATA_BUNDLE_STATUS, data.getStatus());
		bundleMetadata.setBoolean(METADATA_BUNDLE_REF, data.isReference());
		bundleMetadata.setLong(METADATA_LAST_MODIFIED, data.getLastModified());

		bundleMetadata.save();
	}

	protected void persistNextBundleID(long id) throws IOException {
		fwMetadata.setLong(METADATA_ADAPTOR_NEXTID, nextId);
		fwMetadata.save();
	}

	protected void initializeMetadata() throws IOException {
		fwMetadata = new MetaData(getMetaDataFile(), "Framework metadata"); //$NON-NLS-1$
		fwMetadata.load();
		nextId = fwMetadata.getLong(METADATA_ADAPTOR_NEXTID, 1);
		initialBundleStartLevel = fwMetadata.getInt(METADATA_ADAPTOR_IBSL, 1);
	}

	protected FrameworkLog createFrameworkLog() {
		return new DefaultLog();
	}
}

/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

import org.osgi.framework.BundleException;

/**
 * Bundle Storage interface for managing a persistent storage life
 * cycle operation upon a bundle.
 *
 * <p>This class is used to provide methods to manage a life cycle
 * operation on a bundle in persistent storage. BundleOperation objects
 * are returned by the FrameworkAdaptor object and are called by OSGi
 * to complete the persistent storage life cycle operation.
 *
 * <p>For example
 * <pre>
 *      Bundle bundle;
 *      BundleOperation storage = adaptor.installBundle(location, source);
 *      try {
 *          bundle = storage.begin();
 *
 *          // Perform some implementation specific work
 *          // which may fail.
 *
 *          storage.commit(false);
 *          // bundle has been successfully installed
 *      } catch (BundleException e) {
 *          storage.undo();
 *          throw e; // rethrow the error
 *      }
 *      return bundle;
 * </pre>
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public abstract interface BundleOperation {

	/**
	 * Begin the operation on the bundle (install, update, uninstall).
	 *
	 * @return BundleData object for the target bundle.
	 * @throws BundleException If a failure occured modifiying peristent storage.
	 */
	public abstract BundleData begin() throws BundleException;

	/**
	 * Commit the operation performed.
	 *
	 * @param postpone If true, the bundle's persistent
	 * storage cannot be immediately reclaimed. This may occur if the
	 * bundle is still exporting a package.
	 * @throws BundleException If a failure occured modifiying peristent storage.
	 */
	public abstract void commit(boolean postpone) throws BundleException;

	/**
	 * Undo the change to persistent storage.
	 * <p>This method can be called before calling commit or if commit
	 * throws an exception to undo any changes in progress.
	 *
	 * @throws BundleException If a failure occured modifiying peristent storage.
	 */
	public abstract void undo() throws BundleException;

}

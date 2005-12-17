/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.adaptor.core;

import java.io.IOException;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

/**
 * This interface allows extending adaptors to control how BundleData and BundleClassLoader
 * objects are created.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface AdaptorElementFactory {
	/**
	 * Creates a bundle data object for a bundle
	 * @param adaptor the adaptor creating the bundle data for
	 * @param id the bundle ID to use for the constructed bundle data object
	 * @return a bundle data object for a bundle
	 * @throws IOException
	 */
	public AbstractBundleData createBundleData(AbstractFrameworkAdaptor adaptor, long id) throws IOException;

	/**
	 * Creates a bundle classloader for a bundle
	 * @param delegate the classloader delegate
	 * @param domain the classloader domain
	 * @param bundleclasspath the classloader classapth
	 * @param data the classloader bundle data
	 * @return a bundle classloader for a bundle
	 */
	public BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, BundleProtectionDomain domain, String[] bundleclasspath, AbstractBundleData data);

}

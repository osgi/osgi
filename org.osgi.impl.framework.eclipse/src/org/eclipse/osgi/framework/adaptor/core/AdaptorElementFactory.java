/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.adaptor.core;

import java.io.IOException;
import java.security.ProtectionDomain;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;

public interface AdaptorElementFactory {

	public AbstractBundleData createBundleData(AbstractFrameworkAdaptor adaptor, long id) throws IOException;

	public BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, ProtectionDomain domain, String[] bundleclasspath, AbstractBundleData data);

}
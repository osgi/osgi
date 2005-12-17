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

import java.io.IOException;

import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.framework.adaptor.core.*;

public class DefaultElementFactory implements AdaptorElementFactory {

	public AbstractBundleData createBundleData(AbstractFrameworkAdaptor adaptor, long id) throws IOException {
		return new DefaultBundleData((DefaultAdaptor) adaptor, id);
	}

	public org.eclipse.osgi.framework.adaptor.BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, BundleProtectionDomain domain, String[] bundleclasspath, AbstractBundleData data) {
		return new DefaultClassLoader(delegate, domain, bundleclasspath, data.getAdaptor().getBundleClassLoaderParent(), data);
	}

}

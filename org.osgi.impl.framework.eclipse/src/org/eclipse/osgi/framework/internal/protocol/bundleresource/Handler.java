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

package org.eclipse.osgi.framework.internal.protocol.bundleresource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import org.eclipse.osgi.framework.adaptor.core.BundleEntry;
import org.eclipse.osgi.framework.adaptor.core.BundleResourceHandler;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.framework.internal.core.BundleLoader;

/**
 * URLStreamHandler the bundleresource protocol.
 */

public class Handler extends BundleResourceHandler {

	/**
	 * Constructor for a bundle protocol resource URLStreamHandler.
	 */
	public Handler() {
		super();
	}

	public Handler(BundleEntry bundleEntry) {
		super(bundleEntry);
	}

	protected BundleEntry findBundleEntry(URL url, AbstractBundle bundle) throws IOException {
		BundleLoader bundleLoader = bundle.getBundleLoader();
		if (bundleLoader == null)
			throw new FileNotFoundException(url.getPath());
		BundleEntry entry = (BundleEntry) bundleLoader.findObject(url.getPath());
		if (entry == null)
			throw new FileNotFoundException(url.getPath());
		return entry;

	}

}


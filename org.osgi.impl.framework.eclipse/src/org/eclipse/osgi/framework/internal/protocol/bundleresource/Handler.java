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
import java.util.Enumeration;
import org.eclipse.osgi.framework.adaptor.core.*;
import org.eclipse.osgi.framework.adaptor.core.BundleEntry;
import org.eclipse.osgi.framework.internal.core.*;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;

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
		AbstractClassLoader cl = (AbstractClassLoader) getBundleClassLoader(bundle);
		if (cl== null)
			throw new FileNotFoundException(url.getPath());
		int index = url.getPort();
		BundleEntry entry = null;
		if (index == 0) {
			entry = (BundleEntry) cl.findLocalObject(url.getPath());
		}
		else {
			Enumeration entries = cl.findLocalObjects(url.getPath());
			if (entries != null)
				for (int i = 0; entries.hasMoreElements() && i <= index; i++)
					entry = (BundleEntry) entries.nextElement();
		}
		if (entry == null)
			throw new FileNotFoundException(url.getPath());
		return entry;
	}

}


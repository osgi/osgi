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

package org.eclipse.osgi.framework.internal.protocol.reference;

import java.io.*;
import java.net.*;
import org.eclipse.osgi.framework.adaptor.core.ReferenceInputStream;

/**
 * URLConnection for the reference protocol.
 */

public class ReferenceURLConnection extends URLConnection {
	protected URL reference;

	protected ReferenceURLConnection(URL url) {
		super(url);
	}

	public synchronized void connect() throws IOException {
		if (!connected) {
			// TODO assumes that reference URLs are always based on file: URLs.
			// There are not solid usecases to the contrary. Yet.
			// Construct the ref URL carefully so as to preserve UNC paths etc.
			File file = new File(url.getPath().substring(5));
			URL ref;
			if (!file.isAbsolute()) {
				File installPath = getInstallPath();
				if (installPath != null)
					file = makeAbsolute(installPath, file);
			}
			ref = file.toURL();
			if (!file.exists())
				throw new FileNotFoundException(file.toString());
			reference = ref;
		}
	}

	public boolean getDoInput() {
		return true;
	}

	public boolean getDoOutput() {
		return false;
	}

	public InputStream getInputStream() throws IOException {
		if (!connected) {
			connect();
		}

		return new ReferenceInputStream(reference);
	}

	private File getInstallPath() throws MalformedURLException {
		String installURL = System.getProperty("osgi.install.area"); //$NON-NLS-1$
		if (installURL == null)
			return null;
		if (!installURL.startsWith("file:")) //$NON-NLS-1$
			return null;
		return new File(new URL(installURL).getPath());
	}

	private static File makeAbsolute(File base, File relative) {
		if (relative.isAbsolute())
			return relative;
		File absolute = new File(base, relative.getPath());
		return absolute;
	}
}

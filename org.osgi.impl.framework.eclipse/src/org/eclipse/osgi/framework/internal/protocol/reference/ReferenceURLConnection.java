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
			URL ref = file.toURL();
			if (!file.exists())
				throw new FileNotFoundException();
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

}

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

import java.io.IOException;
import java.net.*;

/**
 * URLStreamHandler for reference protocol.  A reference URL is used to hold a 
 * reference to a local file URL.  A reference URL allows bundles to be installed
 * by reference.  This means the content of the bundle will not be copied.  Instead
 * the content of the bundle will be loaded from the reference location specified
 * by the reference URL.  The Framework only supports reference URLs that refer
 * to a local file URL.  For example: <p>
 * <pre>
 *     reference:file:/eclipse/plugins/org.eclipse.myplugin_1.0.0/
 *     reference:file:/eclispe/plugins/org.eclipse.mybundle_1.0.0.jar
 * </pre>
 */
public class Handler extends URLStreamHandler {
	public Handler() {
	}

	protected URLConnection openConnection(URL url) throws IOException {
		return new ReferenceURLConnection(url);
	}

	protected void parseURL(URL url, String str, int start, int end) {
		if (end < start) {
			return;
		}
		String reference = (start < end) ? str.substring(start, end) : url.getPath();

		setURL(url, url.getProtocol(), null, -1, null, null, reference, null, null);
	}

}

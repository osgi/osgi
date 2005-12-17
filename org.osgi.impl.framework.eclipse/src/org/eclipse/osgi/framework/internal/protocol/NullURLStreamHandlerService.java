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

package org.eclipse.osgi.framework.internal.protocol;

import java.io.IOException;
import java.net.*;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerSetter;

/**
 * The NullURLStreamService is created when a registered URLStreamHandler service
 * with an associated URLStreamHandlerProxy becomes unregistered.  The associated
 * URLStreamHandlerProxy must still handle all future requests for the now unregistered
 * scheme (the JVM caches URLStreamHandlers making up impossible to "unregister" them). 
 * When requests come in for an unregistered URLStreamHandlerService, the
 * NullURLStreamHandlerService is used in it's place.
 */

public class NullURLStreamHandlerService implements URLStreamHandlerService {

	public URLConnection openConnection(URL u) throws IOException {
		throw new MalformedURLException();
	}

	public boolean equals(URL url1, URL url2) {
		throw new IllegalStateException();
	}

	public int getDefaultPort() {
		throw new IllegalStateException();
	}

	public InetAddress getHostAddress(URL url) {
		throw new IllegalStateException();
	}

	public int hashCode(URL url) {
		throw new IllegalStateException();
	}

	public boolean hostsEqual(URL url1, URL url2) {
		throw new IllegalStateException();
	}

	public boolean sameFile(URL url1, URL url2) {
		throw new IllegalStateException();
	}

	public void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) {
		throw new IllegalStateException();
	}

	public void setURL(URL u, String protocol, String host, int port, String file, String ref) {
		throw new IllegalStateException();
	}

	public String toExternalForm(URL url) {
		throw new IllegalStateException();
	}

	public void parseURL(URLStreamHandlerSetter realHandler, URL u, String spec, int start, int limit) {
		throw new IllegalStateException();
	}

}

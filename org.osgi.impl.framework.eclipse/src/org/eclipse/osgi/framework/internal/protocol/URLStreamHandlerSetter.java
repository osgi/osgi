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

import java.net.URL;

public class URLStreamHandlerSetter implements org.osgi.service.url.URLStreamHandlerSetter {

	protected URLStreamHandlerProxy handlerProxy;

	public URLStreamHandlerSetter(URLStreamHandlerProxy handler) {
		this.handlerProxy = handler;
	}

	/**
	 * @see org.osgi.service.url.URLStreamHandlerSetter#setURL(URL, String, String, int, String, String)
	 * @deprecated
	 */
	public void setURL(URL url, String protocol, String host, int port, String file, String ref) {
		handlerProxy.setURL(url, protocol, host, port, file, ref);
	}

	/**
	 * @see org.osgi.service.url.URLStreamHandlerSetter#setURL(URL, String, String, int, String, String, String, String, String)
	 */
	public void setURL(URL url, String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
		handlerProxy.setURL(url, protocol, host, port, authority, userInfo, path, query, ref);
	}

}

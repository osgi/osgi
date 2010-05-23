/*
 * $Id$
 *

 *
 * (c) Copyright 2002 Atinav Inc.
 *
 * This source code is owned by Atinav Inc. and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.composite.tb5;

import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.url.AbstractURLStreamHandlerService;

public class CustomUrlHandler2 extends AbstractURLStreamHandlerService {
	URLConnection	ucon;

	public URLConnection openConnection(URL u) throws java.io.IOException {
		ucon = new CustomUrlConnection2(u);
		return ucon;
	}

	public URLConnection getConnectionObject() {
		return ucon;
	}
}

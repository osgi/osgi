/*
 * $Id$
 *

 *
 * (c) Copyright 2002 Atinav Inc.
 *
 * This source code is owned by Atinav Inc. and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.url.tbc;

import java.io.*;
import java.net.*;

public class CustomUrlConnection1 extends URLConnection {
	public CustomUrlConnection1(URL url) {
		super(url);
	}

	public String getContentType() {
		return "osgi/test";
	}

	public void connect() throws IOException {
	}

	public InputStream getInputStream() throws IOException {
		return null;
	}
}

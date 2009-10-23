/*
 * $Id: CustomUrlConnection2.java 6749 2009-04-16 12:58:32Z hargrave@us.ibm.com $
 *

 *
 * (c) Copyright 2002 Atinav Inc.
 *
 * This source code is owned by Atinav Inc. and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.composite.tb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class CustomUrlConnection2 extends URLConnection {
	private InputStream	in;

	public CustomUrlConnection2(URL url) {
		super(url);
	}

	public String getContentType() {
		return "osgi/test";
	}

	public InputStream getInputStream() throws IOException {
		connect();
		return in;
	}

	public void connect() throws IOException {
		if (in == null) {
			in = new ByteArrayInputStream("CustomUrlConnection2".getBytes());
		}
	}
}

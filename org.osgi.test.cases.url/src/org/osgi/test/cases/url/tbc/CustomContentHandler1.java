/*
 * $Header$
 *

 *
 * (c) Copyright 2002 Atinav Inc.
 *
 * This source code is owned by Atinav Inc. and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.url.tbc;

import java.io.IOException;
import java.net.*;

public class CustomContentHandler1 extends ContentHandler {
	public Object getContent(URLConnection urlc) throws IOException {
		return "CustomContentHandler1";
	}
}

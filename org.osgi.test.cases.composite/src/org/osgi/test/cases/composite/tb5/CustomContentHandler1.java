/*
 * $Id: CustomContentHandler1.java 6744 2009-04-15 15:03:56Z hargrave@us.ibm.com $
 *

 *
 * (c) Copyright 2002 Atinav Inc.
 *
 * This source code is owned by Atinav Inc. and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.composite.tb5;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;

public class CustomContentHandler1 extends ContentHandler {
	public Object getContent(URLConnection urlc) throws IOException {
		return "CustomContentHandler1";
	}
}

/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract class JarURLConnection extends java.net.URLConnection {
	protected JarURLConnection(java.net.URL var0) throws java.net.MalformedURLException { super((java.net.URL) null); }
	public java.lang.String getEntryName() { return null; }
	public java.net.URL getJarFileURL() { return null; }
	protected java.net.URLConnection jarFileURLConnection;
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package java.net;
public abstract class JarURLConnection extends java.net.URLConnection {
	protected JarURLConnection(java.net.URL var0) throws java.net.MalformedURLException { super((java.net.URL) null); }
	public java.util.jar.Attributes getAttributes() throws java.io.IOException { return null; }
	public java.security.cert.Certificate[] getCertificates() throws java.io.IOException { return null; }
	public java.lang.String getEntryName() { return null; }
	public java.util.jar.JarEntry getJarEntry() throws java.io.IOException { return null; }
	public java.util.jar.Manifest getManifest() throws java.io.IOException { return null; }
	public abstract java.util.jar.JarFile getJarFile() throws java.io.IOException;
	public java.net.URL getJarFileURL() { return null; }
	public java.util.jar.Attributes getMainAttributes() throws java.io.IOException { return null; }
	protected java.net.URLConnection jarFileURLConnection;
}


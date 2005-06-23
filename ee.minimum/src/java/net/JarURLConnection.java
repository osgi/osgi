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
	public java.lang.String getEntryName() { return null; }
	public java.net.URL getJarFileURL() { return null; }
	protected java.net.URLConnection jarFileURLConnection;
}


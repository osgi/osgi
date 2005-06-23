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
public abstract class URLStreamHandler {
	public URLStreamHandler() { }
	protected abstract java.net.URLConnection openConnection(java.net.URL var0) throws java.io.IOException;
	protected void parseURL(java.net.URL var0, java.lang.String var1, int var2, int var3) { }
	protected void setURL(java.net.URL var0, java.lang.String var1, java.lang.String var2, int var3, java.lang.String var4, java.lang.String var5, java.lang.String var6, java.lang.String var7, java.lang.String var8) { }
	protected java.lang.String toExternalForm(java.net.URL var0) { return null; }
	protected boolean equals(java.net.URL var0, java.net.URL var1) { return false; }
	protected int getDefaultPort() { return 0; }
	protected java.net.InetAddress getHostAddress(java.net.URL var0) { return null; }
	protected int hashCode(java.net.URL var0) { return 0; }
	protected boolean hostsEqual(java.net.URL var0, java.net.URL var1) { return false; }
	protected boolean sameFile(java.net.URL var0, java.net.URL var1) { return false; }
}


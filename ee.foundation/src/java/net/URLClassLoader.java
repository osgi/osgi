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
public class URLClassLoader extends java.security.SecureClassLoader {
	public URLClassLoader(java.net.URL[] var0) { }
	public URLClassLoader(java.net.URL[] var0, java.lang.ClassLoader var1) { }
	protected void addURL(java.net.URL var0) { }
	public java.util.Enumeration findResources(java.lang.String var0) throws java.io.IOException { return null; }
	protected java.security.PermissionCollection getPermissions(java.security.CodeSource var0) { return null; }
	public java.net.URL[] getURLs() { return null; }
	public static java.net.URLClassLoader newInstance(java.net.URL[] var0) { return null; }
	public static java.net.URLClassLoader newInstance(java.net.URL[] var0, java.lang.ClassLoader var1) { return null; }
	public URLClassLoader(java.net.URL[] var0, java.lang.ClassLoader var1, java.net.URLStreamHandlerFactory var2) { }
	protected java.lang.Class findClass(java.lang.String var0) throws java.lang.ClassNotFoundException { return null; }
	public java.net.URL findResource(java.lang.String var0) { return null; }
	protected java.lang.Package definePackage(java.lang.String var0, java.util.jar.Manifest var1, java.net.URL var2) throws java.lang.IllegalArgumentException { return null; }
}


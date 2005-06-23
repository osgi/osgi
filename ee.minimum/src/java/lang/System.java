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

package java.lang;
public final class System {
	public static void setIn(java.io.InputStream var0) { }
	public static void setOut(java.io.PrintStream var0) { }
	public static void setErr(java.io.PrintStream var0) { }
	public static void arraycopy(java.lang.Object var0, int var1, java.lang.Object var2, int var3, int var4) { }
	public static long currentTimeMillis() { return 0l; }
	public static void exit(int var0) { }
	public static void gc() { }
	public static java.util.Properties getProperties() { return null; }
	public static java.lang.String getProperty(java.lang.String var0) { return null; }
	public static java.lang.String getProperty(java.lang.String var0, java.lang.String var1) { return null; }
	public static java.lang.SecurityManager getSecurityManager() { return null; }
	public static int identityHashCode(java.lang.Object var0) { return 0; }
	public static void loadLibrary(java.lang.String var0) { }
	public static void runFinalization() { }
	public static void setProperties(java.util.Properties var0) { }
	public static void setSecurityManager(java.lang.SecurityManager var0) { }
	public static java.lang.String mapLibraryName(java.lang.String var0) { return null; }
	public final static java.io.InputStream in; static { in = null; }
	public final static java.io.PrintStream out; static { out = null; }
	public final static java.io.PrintStream err; static { err = null; }
	private System() { } /* generated constructor to prevent compiler adding default public constructor */
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util;
public class Properties extends java.util.Hashtable {
	public Properties() { }
	public Properties(java.util.Properties var0) { }
	public java.lang.String getProperty(java.lang.String var0) { return null; }
	public java.lang.String getProperty(java.lang.String var0, java.lang.String var1) { return null; }
	public void list(java.io.PrintStream var0) { }
	public void list(java.io.PrintWriter var0) { }
	public void load(java.io.InputStream var0) throws java.io.IOException { }
	public java.util.Enumeration propertyNames() { return null; }
	public void save(java.io.OutputStream var0, java.lang.String var1) { }
	public java.lang.Object setProperty(java.lang.String var0, java.lang.String var1) { return null; }
	public void store(java.io.OutputStream var0, java.lang.String var1) throws java.io.IOException { }
	protected java.util.Properties defaults;
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.lang;
public class Runtime {
	public java.lang.Process exec(java.lang.String[] var0) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String[] var0, java.lang.String[] var1) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String[] var0, java.lang.String[] var1, java.io.File var2) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String var0) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String var0, java.lang.String[] var1) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String var0, java.lang.String[] var1, java.io.File var2) throws java.io.IOException { return null; }
	public void exit(int var0) { }
	public long freeMemory() { return 0l; }
	public void gc() { }
	public static java.lang.Runtime getRuntime() { return null; }
	public void load(java.lang.String var0) { }
	public void loadLibrary(java.lang.String var0) { }
	public void runFinalization() { }
	public long totalMemory() { return 0l; }
	public void traceInstructions(boolean var0) { }
	public void traceMethodCalls(boolean var0) { }
	public void addShutdownHook(java.lang.Thread var0) { }
	public boolean removeShutdownHook(java.lang.Thread var0) { return false; }
	public void halt(int var0) { }
	private Runtime() { } /* generated constructor to prevent compiler adding default public constructor */
}


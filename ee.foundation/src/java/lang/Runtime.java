/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.lang;
public class Runtime {
    private Runtime() { }
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
}


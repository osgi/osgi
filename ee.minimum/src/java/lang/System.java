/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.lang;
public final class System {
    public static void setIn(java.io.InputStream var0) { }
    public static void setOut(java.io.PrintStream var0) { }
    public static void setErr(java.io.PrintStream var0) { }
    private System() { }
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
}


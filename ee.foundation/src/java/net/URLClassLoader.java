/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

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


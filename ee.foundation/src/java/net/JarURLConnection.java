/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.net;
public abstract class JarURLConnection extends java.net.URLConnection {
    protected JarURLConnection(java.net.URL var0) throws java.net.MalformedURLException { super(null); }
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


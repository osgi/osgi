/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.net;
public abstract class JarURLConnection extends java.net.URLConnection {
    protected JarURLConnection(java.net.URL var0) throws java.net.MalformedURLException { super(null); }
    public java.lang.String getEntryName() { return null; }
    public java.net.URL getJarFileURL() { return null; }
    protected java.net.URLConnection jarFileURLConnection;
}


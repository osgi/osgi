/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.net;
public final class URL implements java.io.Serializable {
    public static void setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory var0) { }
    public URL(java.lang.String var0) throws java.net.MalformedURLException { }
    public URL(java.net.URL var0, java.lang.String var1) throws java.net.MalformedURLException { }
    public URL(java.net.URL var0, java.lang.String var1, java.net.URLStreamHandler var2) throws java.net.MalformedURLException { }
    public URL(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws java.net.MalformedURLException { }
    public URL(java.lang.String var0, java.lang.String var1, int var2, java.lang.String var3) throws java.net.MalformedURLException { }
    public URL(java.lang.String var0, java.lang.String var1, int var2, java.lang.String var3, java.net.URLStreamHandler var4) throws java.net.MalformedURLException { }
    protected void set(java.lang.String var0, java.lang.String var1, int var2, java.lang.String var3, java.lang.String var4) { }
    public boolean equals(java.lang.Object var0) { return false; }
    public boolean sameFile(java.net.URL var0) { return false; }
    public int hashCode() { return 0; }
    public final java.lang.Object getContent() throws java.io.IOException { return null; }
    public final java.lang.Object getContent(java.lang.Class[] var0) throws java.io.IOException { return null; }
    public final java.io.InputStream openStream() throws java.io.IOException { return null; }
    public java.net.URLConnection openConnection() throws java.io.IOException { return null; }
    public java.lang.String toString() { return null; }
    public java.lang.String toExternalForm() { return null; }
    public java.lang.String getFile() { return null; }
    public java.lang.String getHost() { return null; }
    public int getPort() { return 0; }
    public java.lang.String getProtocol() { return null; }
    public java.lang.String getRef() { return null; }
    public java.lang.String getQuery() { return null; }
    public java.lang.String getPath() { return null; }
    public java.lang.String getUserInfo() { return null; }
    public java.lang.String getAuthority() { return null; }
    protected void set(java.lang.String var0, java.lang.String var1, int var2, java.lang.String var3, java.lang.String var4, java.lang.String var5, java.lang.String var6, java.lang.String var7) { }
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.jar;
public class JarEntry extends java.util.zip.ZipEntry {
    public JarEntry(java.lang.String var0) { super(var0); }
    public JarEntry(java.util.zip.ZipEntry var0) { super(var0); }
    public java.util.jar.Attributes getAttributes() throws java.io.IOException { return null; }
    public java.security.cert.Certificate[] getCertificates() { return null; }
    public JarEntry(java.util.jar.JarEntry var0) { super(var0); }
}


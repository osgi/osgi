/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.jar;
public class JarFile extends java.util.zip.ZipFile {
    public JarFile(java.io.File var0) throws java.io.IOException { super(var0); }
    public JarFile(java.io.File var0, boolean var1) throws java.io.IOException { super(var0); }
    public JarFile(java.io.File var0, boolean var1, int var2) throws java.io.IOException { super(var0); }
    public JarFile(java.lang.String var0) throws java.io.IOException { super(var0); }
    public JarFile(java.lang.String var0, boolean var1) throws java.io.IOException { super(var0); }
    public java.util.Enumeration entries() { return null; }
    public java.util.jar.JarEntry getJarEntry(java.lang.String var0) { return null; }
    public java.util.jar.Manifest getManifest() throws java.io.IOException { return null; }
    public java.io.InputStream getInputStream(java.util.zip.ZipEntry var0) throws java.io.IOException { return null; }
    public java.util.zip.ZipEntry getEntry(java.lang.String var0) { return null; }
    public final static java.lang.String MANIFEST_NAME = "META-INF/MANIFEST.MF";
}


/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.net.URL;
import java.util.jar.*;

/**
 * JAR file handling.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Archive
{
    /**
     * Base for filename used to store copy of archive.
     */
    final static String ARCHIVE = "jar";
    
    /**
     * Directory base name to use for sub-archives.
     */
    final static String SUBDIR = "sub";
    
    /**
     * File handle for file that contains current archive.
     */
    File file;
    
    /**
     * URL used for CodeSource, set to bundle location if it is
     * a valid URL otherwise set to URL of archive file.
     */
    URL codeUrl;
    
    /**
     * JAR file handle for file that contains current archive.
     */
    JarFile jar;
    

    /**
    * Manifest supporting UTF-8 (read-only)
    */
    UTF8Manifest manifest = null;

    /**
     * Create an Archive based on contents of an InputStream,
     * the archive is saved as local copy in the specified
     * directory.
     *
     * @param dir Directory to save data in.
     * @param is Jar file data in an InputStream.
     * @param url URL to use to CodeSource.
     */
    Archive(File dir, InputStream is, URL url) throws IOException {
      file = new File(dir, ARCHIVE + "0");
      loadFile(file, is);
      jar = new JarFile(file);
      codeUrl = url != null ? url : file.toURL();
    }
    
    
    /**
     * Create an Archive based on contents of a saved
     * archive in the specified directory.
     * Take lowest versioned archive and remove rest.
     *
     * @param dir Directory with saved Archive.
     * @param url URL to use to CodeSource.
     */
    Archive(File dir, URL url) throws IOException
    {
	String [] f = dir.list();
	int low = Integer.MAX_VALUE;
	file = null;
	for (int i = 0; i < f.length; i++) {
	    if (f[i].startsWith(ARCHIVE)) {
		try {
		    int c = Integer.parseInt(f[i].substring(ARCHIVE.length()));
		    if (c < low) {
			low = c;
			file = new File(dir, f[i]);
		    }
		} catch (NumberFormatException ignore) { }
	    }
	}
	for (int i = 0; i < f.length; i++) {
	    if (f[i].startsWith(ARCHIVE)) {
		try {
		    int c = Integer.parseInt(f[i].substring(ARCHIVE.length()));
		    if (c != low) {
			(new File(dir, f[i])).delete();
		    }
		} catch (NumberFormatException ignore) { }
	    }
	    if (f[i].startsWith(SUBDIR)) {
		try {
		    int c = Integer.parseInt(f[i].substring(SUBDIR.length()));
		    if (c != low) {
			(new FileTree(dir, f[i])).delete();
		    }
		} catch (NumberFormatException ignore) { }
	    }
	}
	if (file == null) {
	    throw new IOException("No saved jar file found in: " + dir.getAbsolutePath());
	}
	    jar = new JarFile(file);
		  codeUrl = url != null ? url : file.toURL();
    }
    
    
    /**
     * Create a Sub-Archive based on a path to in an already
     * existing Archive. The new archive is saved in a subdirectory
     * below local copy of the existing Archive.
     *
     * @param a Parent Archive.
     * @param path Path of new Archive inside old Archive.
     * @exception FileNotFoundException if no such Jar file in archive.
     * @exception IOException if failed to read Jar file.
     */
    Archive(Archive a, String path) throws IOException
    {
	file = getSubFile(a, path);
	if (!file.exists()) {
	    file.getParentFile().mkdirs();
	    JarEntry je = a.jar.getJarEntry(path);
	    if (je != null) {
		loadFile(file, a.jar.getInputStream(je));
	    } else {
		throw new FileNotFoundException("No such sub-archive: " + path);
	    }
	}
	jar = new JarFile(file);
	codeUrl = a.codeUrl;
    }
    
    
    /**
     * Create a new Archive that will later replace the archive
     * sent as a parameter. This is used for updating a bundle.
     * The bundle data is saved in a temporary file until we now
     * that the contents is okay.
     *
     * @param a Original archive to be replaced.
     * @param is New Bundle Jar file data in an InputStream.
     */
    Archive(Archive a, InputStream is) throws IOException
    {
	try {
	    int c = Integer.parseInt(a.file.getName().substring(ARCHIVE.length()));
	    c++;
	    file = new File(a.file.getParentFile(), ARCHIVE + c);
	} catch (NumberFormatException ignore) { }
	loadFile(file, is);
	jar = new JarFile(file);
	codeUrl = a.codeUrl;
    }
    
    
    
    /**
     * Get an attribute from the manifest of the archive.
     *
     * @param key Name of attribute to get.
     * @return A string with result or null if the entry doesn't exists.
     */
    String getAttribute(String key) {
      return getManifest().get(key);

    }
    
    
    /**
     * Get the main attributes from the archive in a HeaderDictionary
     *
     * @return All attributes.
     */
    HeaderDictionary getHeaderDictionary() {
      return getManifest().getHeaderDictionary();
    }

  /**
   * Get the Manifest, parse and cache result if neccesary
   *
   * @return The manifest
   */
    UTF8Manifest getManifest() {
      if(manifest == null) {
        try {
          manifest = new UTF8Manifest(jar);
        } catch(IOException ie) {
          throw new RuntimeException("Failed UTF-8 parsing of manifest");
        }
      }
      return manifest;
    }
    
    
    /**
     * Get a byte array containg the contents of named file from
     * the archive.
     *
     * @param component File to get.
     * @return Byte array with contents of file or null if file doesn't exist.
     * @exception IOException if failed to read jar entry.
     */
    byte[] getBytes(String component) throws IOException
    {
	JarEntry je = jar.getJarEntry(component);
	if (je == null) {
	    return null;
	}
	int len = (int) je.getSize();
	if (len < 0) {
	    throw new IOException("Size unknown for " + je);
	}
	InputStream is = jar.getInputStream(je);
	byte[] bytes = new byte[len];
	DataInputStream dis = new DataInputStream(is);
	dis.readFully(bytes);
	is.close();
	return bytes;
    }
    
    
    /**
     * Get an InputStream to named entry inside an Archive.
     *
     * @param component Entry to get reference to.
     * @return InputStream to entry or null if it doesn't exist.
     */
    InputStream getInputStream(String component) throws IOException
    {
	if (component.startsWith("/")) {
	    component = component.substring(1);
	}
	JarEntry je = jar.getJarEntry(component);
	if (je != null) {
	    return jar.getInputStream(je);
	}
	return null;
    }
    
    
    /**
     * Get an Archive handle to a named Jar file within this archive.
     *
     * @param path Name of Jar file to get.
     * @return An Archive object representing new archive.
     * @exception FileNotFoundException if no such Jar file in archive.
     * @exception IOException if failed to read Jar file.
     */
    Archive getSubArchive(String path) throws IOException
    {
	Archive a = new Archive(this, path);
	return a;
    }
    
    
    /**
     * Extract native library from JAR.
     *
     * @param key Name of Jar file to get.
     * @return A string with path to native library.
     */
    String getNativeLibrary(String path) throws IOException
    {
	File lib = getSubFile(this, path);
	if (!lib.exists()) {
	    lib.getParentFile().mkdirs();
	    JarEntry je = jar.getJarEntry(path);
	    if (je != null) {
		loadFile(lib, jar.getInputStream(je));
	    } else {
		throw new FileNotFoundException("No such sub-archive: " + path);
	    }
	}
	return lib.getAbsolutePath();
    }
    
    
    /**
     * Remove archive and any unpacked sub-archives.
     */
    void purge()
    {
	close();
	// Remove archive
	file.delete();
	// Remove any cached sub files
	getSubFileTree(this).delete();
    }
    
    
    /**
     * Close archive and all open sub-archives.
     * If close fails it is silently ignored.
     */
    void close()
    {
	if (jar != null) {
	    try {
		jar.close();
	    } catch (IOException ignore) {}
	}
    }
    
    //
    // Private methods
    //
    
    /**
     * Get dir for unpacked components.
     *
     * @param archive Archive which contains the components.
     * @return FileTree for archives component cache directory.
     */
    private FileTree getSubFileTree(Archive archive)
    {
	return new FileTree(archive.file.getParentFile(),
			    SUBDIR + archive.file.getName().substring(ARCHIVE.length()));
    }
    
    
    /**
     * Get file for an unpacked component.
     *
     * @param archive Archive which contains the component.
     * @param path Name of the component to get.
     * @return File for componets cache file.
     */
    private File getSubFile(Archive archive, String path)
    {
	return new File(getSubFileTree(archive), path.replace('/', '-'));
    }
    
    
    /**
     * Loads a file from an InputStream and stores it in a file.
     *
     * @param output File to save data in.
     * @param is InputStream to read from.
     */
    private void loadFile(File output, InputStream is) throws IOException
    {
	OutputStream os = null;
	InputStream bis = new BufferedInputStream(is);
	try {
	    os = new BufferedOutputStream(new FileOutputStream(output));
	    byte[] buf = new byte[4096];
	    for(;;) {
		int n = bis.read(buf);
		if (n < 0) {
		    break;
		}
		os.write(buf, 0, n);
	    }
	} catch (IOException e) {
	    output.delete();
	    throw e;
	} finally {
	    bis.close();
	    if (os != null) {
		os.close();
	    }
	}
    }
    
}

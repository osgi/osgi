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


/**
 * FileTree is extension to java.io.File that handles copying
 * and deletion of complete file structures.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class FileTree extends File
{
    /**
     * Creates a new FileTree instance based on given pathname string.
     */
    public FileTree(String name)
    {
	super(name);
    }
    
    
    /**
     * Creates a new Filetree instance by a pathname string to an existing
     * File or FileTree.
     */
    public FileTree(File file, String name)
    {
	super(file, name);
    }
    
    
    /**
     * Creates a new FileTree instance from a parent pathname string and
     * a child pathname string.
     */
    public FileTree(String n1, String n2)
    {
	super(n1, n2);
    }
    
    
    /**
     * Copy this file tree to specified destination.
     *
     * @param copyFile File object representing the destination.
     * @exception IOException if copy failed. Will leave destination
     *            in an unspecified state.
     */
    public void copyTo(File copyFile) throws IOException
    {
	if (isDirectory()) {
	    copyFile.mkdirs();
	    String [] dirs = list();
	    for (int i = dirs.length - 1; i >= 0; i--) {
		(new FileTree(this, dirs[i])).copyTo(new File(copyFile, dirs[i]));
	    }
	} else {
	    InputStream is = null; 
	    OutputStream os = null;
	    try {
		is = new BufferedInputStream(new FileInputStream(this));
		os = new BufferedOutputStream(new FileOutputStream(copyFile));
		byte[] buf=new byte[4096];
		for (;;) {
		    int n=is.read(buf);
		    if (n<0) {
			break;
		    }
		    os.write(buf, 0, n);
		}
	    } finally {
		try {
		    if (is != null) {
			is.close();
		    }
		} finally {
		    if (os != null) {
			os.close();
		    }
		}
	    }
	}
    }
    
    
    /**
     * Delete this file tree from disk.
     *
     * @return True if operation completed okay.
     */
    public boolean delete()
    {
	if (isDirectory()) {
	    String [] dirs = list();
	    for (int i = dirs.length - 1; i>= 0; i--) {
		(new FileTree(this, dirs[i])).delete();
	    }
	}
	return super.delete();
    }
}

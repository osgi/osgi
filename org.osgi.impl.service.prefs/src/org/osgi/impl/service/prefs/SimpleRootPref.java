/*
 * @(#)SimpleRootPref.java	1.6 01/07/18
 * $Header$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */

package org.osgi.impl.service.prefs;

import org.osgi.service.prefs.*;
import java.io.File;
import java.security.*;

/**
 * Root node for a tree of SimplePreferences nodes.  This implementation
 * passes all flush() commands to the root node which saves out the whole
 * tree.
 * @version $Revision$
 */
class SimpleRootPref extends SimplePreferences {

    private File preferencesFile;
    private File tmpFile;
    private boolean modified = false;

    public SimpleRootPref(final File preferencesFile, File tmpFile) {
        super(null, "");
	this.preferencesFile = preferencesFile;
	this.tmpFile = tmpFile;
//j2security
	AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
//endblock
		TextFileSupport.read(preferencesFile, SimpleRootPref.this);
//j2security
		return null;
	    }
	});
//endblock
    }

    // Attempt to avoid possible data loss on e.g. a power
    // failure while writing the file:  Write to a temp file
    // and then rename it.

    public void flush() throws BackingStoreException {
	synchronized (lock) {
	    if (modified) {
		modified = false;
		final File finalTmpFile = tmpFile;
//j2security
		try {
		    AccessController.doPrivileged(
		                            new PrivilegedExceptionAction() {
			public Object run() throws BackingStoreException {
//endblock
			    finalTmpFile.delete();
			    TextFileSupport.write(finalTmpFile,
						  SimpleRootPref.this);
			    if (!finalTmpFile.renameTo(preferencesFile)) {
				// can't rename to existing file on Windows
				preferencesFile.delete();
				if (!finalTmpFile.renameTo(preferencesFile)) {
				    throw new BackingStoreException("rename " +
								    "failed");
				}
			    }
//j2security
			    return null;
			}
		    });
		} catch (PrivilegedActionException pae) {
		    throw (BackingStoreException) pae.getException();
		}
//endblock
	    }
	}
    }

    /*
     * Not synchronized on lock, to avoid deadlock, and so that modifications 
     * can be done concurrently with flush.  When a modification occurs during
     * a flush, the change may or may not be included in the flushed file,
     * but the modified flag will be left set, so the next flush will be sure
     * to write the file again, whether or not it is really necessary.
     *
     * (The other way to avoid deadlock would be to have a single lock object
     * for the whole tree.  This would avoid the occasional unnecessary writing
     * out of the file, but at the cost of holding up all accesses during a 
     * flush.)
     */
    void setModified() {
	modified = true;
    }

}

/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public final class FileDescriptor {
    public FileDescriptor() { }
    public void sync() throws java.io.SyncFailedException { }
    public boolean valid() { return false; }
    public final static java.io.FileDescriptor in; static { in = null; }
    public final static java.io.FileDescriptor out; static { out = null; }
    public final static java.io.FileDescriptor err; static { err = null; }
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.lang;
public abstract class Process {
    public Process() { }
    public abstract void destroy();
    public abstract int exitValue();
    public abstract java.io.InputStream getErrorStream();
    public abstract java.io.InputStream getInputStream();
    public abstract java.io.OutputStream getOutputStream();
    public abstract int waitFor() throws java.lang.InterruptedException;
}


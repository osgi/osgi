/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package javax.microedition.io;
public abstract interface ContentConnection extends javax.microedition.io.StreamConnection {
    public abstract java.lang.String getEncoding();
    public abstract long getLength();
    public abstract java.lang.String getType();
}


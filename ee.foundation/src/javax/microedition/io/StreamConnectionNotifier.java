/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package javax.microedition.io;
public abstract interface StreamConnectionNotifier extends javax.microedition.io.Connection {
    public abstract javax.microedition.io.StreamConnection acceptAndOpen() throws java.io.IOException;
}


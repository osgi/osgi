/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package javax.microedition.io;
public abstract interface OutputConnection extends javax.microedition.io.Connection {
    public abstract java.io.DataOutputStream openDataOutputStream() throws java.io.IOException;
    public abstract java.io.OutputStream openOutputStream() throws java.io.IOException;
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package javax.microedition.io;
public abstract interface OutputConnection extends javax.microedition.io.Connection {
    public abstract java.io.DataOutputStream openDataOutputStream() throws java.io.IOException;
    public abstract java.io.OutputStream openOutputStream() throws java.io.IOException;
}


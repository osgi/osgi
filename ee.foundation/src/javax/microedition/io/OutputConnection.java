/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package javax.microedition.io;
public abstract interface OutputConnection extends javax.microedition.io.Connection {
	public abstract java.io.DataOutputStream openDataOutputStream() throws java.io.IOException;
	public abstract java.io.OutputStream openOutputStream() throws java.io.IOException;
}


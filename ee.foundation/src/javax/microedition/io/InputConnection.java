/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package javax.microedition.io;
public abstract interface InputConnection extends javax.microedition.io.Connection {
	public abstract java.io.DataInputStream openDataInputStream() throws java.io.IOException;
	public abstract java.io.InputStream openInputStream() throws java.io.IOException;
}


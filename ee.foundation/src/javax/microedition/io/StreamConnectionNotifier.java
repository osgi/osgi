/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package javax.microedition.io;
public abstract interface StreamConnectionNotifier extends javax.microedition.io.Connection {
	public abstract javax.microedition.io.StreamConnection acceptAndOpen() throws java.io.IOException;
}


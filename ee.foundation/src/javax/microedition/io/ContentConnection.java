/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package javax.microedition.io;
public abstract interface ContentConnection extends javax.microedition.io.StreamConnection {
	public abstract java.lang.String getEncoding();
	public abstract long getLength();
	public abstract java.lang.String getType();
}


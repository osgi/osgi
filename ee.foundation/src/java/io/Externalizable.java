/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract interface Externalizable extends java.io.Serializable {
	public abstract void readExternal(java.io.ObjectInput var0) throws java.io.IOException, java.lang.ClassNotFoundException;
	public abstract void writeExternal(java.io.ObjectOutput var0) throws java.io.IOException;
}


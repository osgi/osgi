/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class PermissionCollection implements java.io.Serializable {
	public PermissionCollection() { }
	public abstract void add(java.security.Permission var0);
	public abstract java.util.Enumeration elements();
	public abstract boolean implies(java.security.Permission var0);
	public boolean isReadOnly() { return false; }
	public void setReadOnly() { }
	public java.lang.String toString() { return null; }
}


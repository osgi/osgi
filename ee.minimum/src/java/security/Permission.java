/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class Permission implements java.io.Serializable {
	public Permission(java.lang.String var0) { }
	public abstract boolean equals(java.lang.Object var0);
	public abstract int hashCode();
	public abstract java.lang.String getActions();
	public final java.lang.String getName() { return null; }
	public abstract boolean implies(java.security.Permission var0);
	public java.security.PermissionCollection newPermissionCollection() { return null; }
	public java.lang.String toString() { return null; }
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class BasicPermission extends java.security.Permission implements java.io.Serializable {
	public BasicPermission(java.lang.String var0) { super((java.lang.String) null); }
	public BasicPermission(java.lang.String var0, java.lang.String var1) { super((java.lang.String) null); }
	public boolean equals(java.lang.Object var0) { return false; }
	public java.lang.String getActions() { return null; }
	public int hashCode() { return 0; }
	public boolean implies(java.security.Permission var0) { return false; }
	public java.security.PermissionCollection newPermissionCollection() { return null; }
}


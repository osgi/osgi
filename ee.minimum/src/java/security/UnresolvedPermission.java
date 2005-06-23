/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package java.security;
public final class UnresolvedPermission extends java.security.Permission implements java.io.Serializable {
	public UnresolvedPermission(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.security.cert.Certificate[] var3) { super((java.lang.String) null); }
	public boolean equals(java.lang.Object var0) { return false; }
	public boolean implies(java.security.Permission var0) { return false; }
	public java.security.PermissionCollection newPermissionCollection() { return null; }
	public java.lang.String getActions() { return null; }
	public int hashCode() { return 0; }
	public java.lang.String toString() { return null; }
}


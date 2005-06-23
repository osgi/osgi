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
public abstract class Permission implements java.security.Guard, java.io.Serializable {
	public Permission(java.lang.String var0) { }
	public abstract boolean equals(java.lang.Object var0);
	public abstract int hashCode();
	public void checkGuard(java.lang.Object var0) throws java.lang.SecurityException { }
	public abstract java.lang.String getActions();
	public final java.lang.String getName() { return null; }
	public abstract boolean implies(java.security.Permission var0);
	public java.security.PermissionCollection newPermissionCollection() { return null; }
	public java.lang.String toString() { return null; }
}


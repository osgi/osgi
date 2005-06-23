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

package java.security.acl;
public abstract interface AclEntry extends java.lang.Cloneable {
	public abstract boolean addPermission(java.security.acl.Permission var0);
	public abstract boolean checkPermission(java.security.acl.Permission var0);
	public abstract java.lang.Object clone();
	public abstract java.security.Principal getPrincipal();
	public abstract boolean isNegative();
	public abstract java.util.Enumeration permissions();
	public abstract boolean removePermission(java.security.acl.Permission var0);
	public abstract void setNegativePermissions();
	public abstract boolean setPrincipal(java.security.Principal var0);
	public abstract java.lang.String toString();
}


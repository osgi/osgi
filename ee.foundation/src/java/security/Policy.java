/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class Policy {
	public Policy() { }
	public static java.security.Policy getPolicy() { return null; }
	public static void setPolicy(java.security.Policy var0) { }
	public abstract java.security.PermissionCollection getPermissions(java.security.CodeSource var0);
	public abstract void refresh();
}


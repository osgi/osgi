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
public abstract class Policy {
	public Policy() { }
	public static java.security.Policy getPolicy() { return null; }
	public static void setPolicy(java.security.Policy var0) { }
	public abstract java.security.PermissionCollection getPermissions(java.security.CodeSource var0);
	public abstract void refresh();
}


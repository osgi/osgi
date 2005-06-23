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
public final class AccessController {
	public static void checkPermission(java.security.Permission var0) throws java.security.AccessControlException { }
	public static java.security.AccessControlContext getContext() { return null; }
	public static java.lang.Object doPrivileged(java.security.PrivilegedAction var0) { return null; }
	public static java.lang.Object doPrivileged(java.security.PrivilegedAction var0, java.security.AccessControlContext var1) { return null; }
	public static java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction var0) throws java.security.PrivilegedActionException { return null; }
	public static java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction var0, java.security.AccessControlContext var1) throws java.security.PrivilegedActionException { return null; }
	private AccessController() { } /* generated constructor to prevent compiler adding default public constructor */
}


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
public abstract interface Group extends java.security.Principal {
	public abstract boolean addMember(java.security.Principal var0);
	public abstract boolean isMember(java.security.Principal var0);
	public abstract java.util.Enumeration members();
	public abstract boolean removeMember(java.security.Principal var0);
}


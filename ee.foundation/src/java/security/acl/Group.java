/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.acl;
public abstract interface Group extends java.security.Principal {
	public abstract boolean addMember(java.security.Principal var0);
	public abstract boolean isMember(java.security.Principal var0);
	public abstract java.util.Enumeration members();
	public abstract boolean removeMember(java.security.Principal var0);
}


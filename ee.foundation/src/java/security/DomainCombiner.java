/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract interface DomainCombiner {
	public abstract java.security.ProtectionDomain[] combine(java.security.ProtectionDomain[] var0, java.security.ProtectionDomain[] var1);
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.cert;
public abstract class CRL {
	protected CRL(java.lang.String var0) { }
	public final java.lang.String getType() { return null; }
	public abstract boolean isRevoked(java.security.cert.Certificate var0);
	public abstract java.lang.String toString();
}


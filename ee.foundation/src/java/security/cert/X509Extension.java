/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.cert;
public abstract interface X509Extension {
	public abstract boolean hasUnsupportedCriticalExtension();
	public abstract java.util.Set getCriticalExtensionOIDs();
	public abstract java.util.Set getNonCriticalExtensionOIDs();
	public abstract byte[] getExtensionValue(java.lang.String var0);
}


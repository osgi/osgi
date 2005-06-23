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

package java.security.cert;
public abstract class X509CRLEntry implements java.security.cert.X509Extension {
	public X509CRLEntry() { }
	public boolean equals(java.lang.Object var0) { return false; }
	public abstract byte[] getEncoded() throws java.security.cert.CRLException;
	public abstract java.math.BigInteger getSerialNumber();
	public abstract java.util.Date getRevocationDate();
	public abstract boolean hasExtensions();
	public int hashCode() { return 0; }
	public abstract java.lang.String toString();
	public abstract boolean hasUnsupportedCriticalExtension();
	public abstract java.util.Set getCriticalExtensionOIDs();
	public abstract java.util.Set getNonCriticalExtensionOIDs();
	public abstract byte[] getExtensionValue(java.lang.String var0);
}


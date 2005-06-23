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
public abstract class Identity implements java.security.Principal, java.io.Serializable {
	protected Identity() { }
	public Identity(java.lang.String var0) { }
	public Identity(java.lang.String var0, java.security.IdentityScope var1) throws java.security.KeyManagementException { }
	public final java.security.IdentityScope getScope() { return null; }
	public java.security.PublicKey getPublicKey() { return null; }
	public void setPublicKey(java.security.PublicKey var0) throws java.security.KeyManagementException { }
	public final java.lang.String getName() { return null; }
	public java.lang.String getInfo() { return null; }
	public void setInfo(java.lang.String var0) { }
	public java.security.Certificate[] certificates() { return null; }
	public void addCertificate(java.security.Certificate var0) throws java.security.KeyManagementException { }
	public void removeCertificate(java.security.Certificate var0) throws java.security.KeyManagementException { }
	public final boolean equals(java.lang.Object var0) { return false; }
	protected boolean identityEquals(java.security.Identity var0) { return false; }
	public java.lang.String toString() { return null; }
	public java.lang.String toString(boolean var0) { return null; }
	public int hashCode() { return 0; }
}


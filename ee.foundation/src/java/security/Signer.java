/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class Signer extends java.security.Identity {
    protected Signer() { }
    public Signer(java.lang.String var0) { }
    public Signer(java.lang.String var0, java.security.IdentityScope var1) throws java.security.KeyManagementException { }
    public java.security.PrivateKey getPrivateKey() { return null; }
    public final void setKeyPair(java.security.KeyPair var0) throws java.security.InvalidParameterException, java.security.KeyException { }
    public java.lang.String toString() { return null; }
}


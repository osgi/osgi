/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public final class SignedObject implements java.io.Serializable {
    public SignedObject(java.io.Serializable var0, java.security.PrivateKey var1, java.security.Signature var2) throws java.io.IOException, java.security.InvalidKeyException, java.security.SignatureException { }
    public java.lang.String getAlgorithm() { return null; }
    public byte[] getSignature() { return null; }
    public boolean verify(java.security.PublicKey var0, java.security.Signature var1) throws java.security.InvalidKeyException, java.security.SignatureException { return false; }
    public java.lang.Object getObject() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
}


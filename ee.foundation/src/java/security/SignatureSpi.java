/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract class SignatureSpi {
    public SignatureSpi() { }
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
    protected abstract void engineInitSign(java.security.PrivateKey var0) throws java.security.InvalidKeyException;
    protected void engineInitSign(java.security.PrivateKey var0, java.security.SecureRandom var1) throws java.security.InvalidKeyException { }
    protected abstract void engineInitVerify(java.security.PublicKey var0) throws java.security.InvalidKeyException;
    protected void engineSetParameter(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
    protected abstract byte[] engineSign() throws java.security.SignatureException;
    protected int engineSign(byte[] var0, int var1, int var2) throws java.security.SignatureException { return 0; }
    protected abstract void engineUpdate(byte[] var0, int var1, int var2) throws java.security.SignatureException;
    protected abstract void engineUpdate(byte var0) throws java.security.SignatureException;
    protected abstract boolean engineVerify(byte[] var0) throws java.security.SignatureException;
    protected java.security.SecureRandom appRandom;
}


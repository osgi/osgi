/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class Signature extends java.security.SignatureSpi {
    protected Signature(java.lang.String var0) { }
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
    public final java.lang.String getAlgorithm() { return null; }
    public static java.security.Signature getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.Signature getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public final void initSign(java.security.PrivateKey var0) throws java.security.InvalidKeyException { }
    public final void initSign(java.security.PrivateKey var0, java.security.SecureRandom var1) throws java.security.InvalidKeyException { }
    public final void initVerify(java.security.PublicKey var0) throws java.security.InvalidKeyException { }
    public final void initVerify(java.security.cert.Certificate var0) throws java.security.InvalidKeyException { }
    public final void setParameter(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
    public final byte[] sign() throws java.security.SignatureException { return null; }
    public final int sign(byte[] var0, int var1, int var2) throws java.security.SignatureException { return 0; }
    public java.lang.String toString() { return null; }
    public final void update(byte[] var0) throws java.security.SignatureException { }
    public final void update(byte[] var0, int var1, int var2) throws java.security.SignatureException { }
    public final void update(byte var0) throws java.security.SignatureException { }
    public final boolean verify(byte[] var0) throws java.security.SignatureException { return false; }
    protected final static int UNINITIALIZED = 0;
    protected final static int SIGN = 2;
    protected final static int VERIFY = 3;
    protected int state;
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class KeyPairGeneratorSpi {
    public KeyPairGeneratorSpi() { }
    public abstract java.security.KeyPair generateKeyPair();
    public abstract void initialize(int var0, java.security.SecureRandom var1);
    public void initialize(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
}


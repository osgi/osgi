/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public class AlgorithmParameters {
    protected AlgorithmParameters(java.security.AlgorithmParametersSpi var0, java.security.Provider var1, java.lang.String var2) { }
    public final java.lang.String getAlgorithm() { return null; }
    public final byte[] getEncoded() throws java.io.IOException { return null; }
    public final byte[] getEncoded(java.lang.String var0) throws java.io.IOException { return null; }
    public static java.security.AlgorithmParameters getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.AlgorithmParameters getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.spec.AlgorithmParameterSpec getParameterSpec(java.lang.Class var0) throws java.security.spec.InvalidParameterSpecException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public final void init(byte[] var0) throws java.io.IOException { }
    public final void init(byte[] var0, java.lang.String var1) throws java.io.IOException { }
    public final void init(java.security.spec.AlgorithmParameterSpec var0) throws java.security.spec.InvalidParameterSpecException { }
    public final java.lang.String toString() { return null; }
}


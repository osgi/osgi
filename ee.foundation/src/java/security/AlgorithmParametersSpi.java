/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract class AlgorithmParametersSpi {
    public AlgorithmParametersSpi() { }
    protected abstract byte[] engineGetEncoded() throws java.io.IOException;
    protected abstract byte[] engineGetEncoded(java.lang.String var0) throws java.io.IOException;
    protected abstract java.security.spec.AlgorithmParameterSpec engineGetParameterSpec(java.lang.Class var0) throws java.security.spec.InvalidParameterSpecException;
    protected abstract void engineInit(byte[] var0) throws java.io.IOException;
    protected abstract void engineInit(byte[] var0, java.lang.String var1) throws java.io.IOException;
    protected abstract void engineInit(java.security.spec.AlgorithmParameterSpec var0) throws java.security.spec.InvalidParameterSpecException;
    protected abstract java.lang.String engineToString();
}


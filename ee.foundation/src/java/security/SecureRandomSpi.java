/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class SecureRandomSpi implements java.io.Serializable {
    public SecureRandomSpi() { }
    protected abstract byte[] engineGenerateSeed(int var0);
    protected abstract void engineNextBytes(byte[] var0);
    protected abstract void engineSetSeed(byte[] var0);
}


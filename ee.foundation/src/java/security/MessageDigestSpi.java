/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class MessageDigestSpi {
    public MessageDigestSpi() { }
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
    protected abstract byte[] engineDigest();
    protected int engineDigest(byte[] var0, int var1, int var2) throws java.security.DigestException { return 0; }
    protected int engineGetDigestLength() { return 0; }
    protected abstract void engineReset();
    protected abstract void engineUpdate(byte[] var0, int var1, int var2);
    protected abstract void engineUpdate(byte var0);
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.spec;
public abstract class EncodedKeySpec implements java.security.spec.KeySpec {
    public EncodedKeySpec(byte[] var0) { }
    public byte[] getEncoded() { return null; }
    public abstract java.lang.String getFormat();
}


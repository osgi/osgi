/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract interface Certificate {
    public abstract void decode(java.io.InputStream var0) throws java.security.KeyException, java.io.IOException;
    public abstract void encode(java.io.OutputStream var0) throws java.security.KeyException, java.io.IOException;
    public abstract java.lang.String getFormat();
    public abstract java.security.Principal getGuarantor();
    public abstract java.security.Principal getPrincipal();
    public abstract java.security.PublicKey getPublicKey();
    public abstract java.lang.String toString(boolean var0);
}


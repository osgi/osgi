/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.cert;
public abstract class CRL {
    protected CRL(java.lang.String var0) { }
    public final java.lang.String getType() { return null; }
    public abstract boolean isRevoked(java.security.cert.Certificate var0);
    public abstract java.lang.String toString();
}


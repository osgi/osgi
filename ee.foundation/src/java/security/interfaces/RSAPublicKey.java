/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface RSAPublicKey extends java.security.PublicKey, java.security.interfaces.RSAKey {
    public abstract java.math.BigInteger getPublicExponent();
}


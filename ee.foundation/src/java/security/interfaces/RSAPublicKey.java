/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface RSAPublicKey extends java.security.PublicKey, java.security.interfaces.RSAKey {
    public abstract java.math.BigInteger getPublicExponent();
}


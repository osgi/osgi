/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface RSAPrivateKey extends java.security.PrivateKey, java.security.interfaces.RSAKey {
    public abstract java.math.BigInteger getPrivateExponent();
}


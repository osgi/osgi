/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface DSAPublicKey extends java.security.interfaces.DSAKey, java.security.PublicKey {
    public abstract java.math.BigInteger getY();
    public final static long serialVersionUID = 1234526332779022332l;
}


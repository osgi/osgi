/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface DSAPublicKey extends java.security.interfaces.DSAKey, java.security.PublicKey {
    public abstract java.math.BigInteger getY();
    public final static long serialVersionUID = 1234526332779022332l;
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface DSAPrivateKey extends java.security.interfaces.DSAKey, java.security.PrivateKey {
    public abstract java.math.BigInteger getX();
    public final static long serialVersionUID = 7776497482533790279l;
}


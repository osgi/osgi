/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface DSAParams {
    public abstract java.math.BigInteger getP();
    public abstract java.math.BigInteger getQ();
    public abstract java.math.BigInteger getG();
}


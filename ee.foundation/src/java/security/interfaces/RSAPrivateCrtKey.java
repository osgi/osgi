/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.interfaces;
public abstract interface RSAPrivateCrtKey extends java.security.interfaces.RSAPrivateKey {
    public abstract java.math.BigInteger getPublicExponent();
    public abstract java.math.BigInteger getPrimeP();
    public abstract java.math.BigInteger getPrimeQ();
    public abstract java.math.BigInteger getPrimeExponentP();
    public abstract java.math.BigInteger getPrimeExponentQ();
    public abstract java.math.BigInteger getCrtCoefficient();
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract interface DomainCombiner {
    public abstract java.security.ProtectionDomain[] combine(java.security.ProtectionDomain[] var0, java.security.ProtectionDomain[] var1);
}


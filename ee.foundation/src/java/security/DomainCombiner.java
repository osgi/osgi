/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract interface DomainCombiner {
    public abstract java.security.ProtectionDomain[] combine(java.security.ProtectionDomain[] var0, java.security.ProtectionDomain[] var1);
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public final class AccessControlContext {
    public AccessControlContext(java.security.ProtectionDomain[] var0) { }
    public AccessControlContext(java.security.AccessControlContext var0, java.security.DomainCombiner var1) { }
    public void checkPermission(java.security.Permission var0) throws java.security.AccessControlException { }
    public boolean equals(java.lang.Object var0) { return false; }
    public int hashCode() { return 0; }
    public java.security.DomainCombiner getDomainCombiner() { return null; }
}


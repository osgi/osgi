/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract class IdentityScope extends java.security.Identity {
    protected IdentityScope() { }
    public IdentityScope(java.lang.String var0) { }
    public IdentityScope(java.lang.String var0, java.security.IdentityScope var1) throws java.security.KeyManagementException { }
    public abstract void addIdentity(java.security.Identity var0) throws java.security.KeyManagementException;
    public abstract void removeIdentity(java.security.Identity var0) throws java.security.KeyManagementException;
    public abstract java.util.Enumeration identities();
    public java.security.Identity getIdentity(java.security.Principal var0) { return null; }
    public abstract java.security.Identity getIdentity(java.security.PublicKey var0);
    public abstract java.security.Identity getIdentity(java.lang.String var0);
    protected static void setSystemScope(java.security.IdentityScope var0) { }
    public abstract int size();
    public java.lang.String toString() { return null; }
    public static java.security.IdentityScope getSystemScope() { return null; }
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public final class UnresolvedPermission extends java.security.Permission implements java.io.Serializable {
    public UnresolvedPermission(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.security.cert.Certificate[] var3) { super(null); }
    public boolean equals(java.lang.Object var0) { return false; }
    public boolean implies(java.security.Permission var0) { return false; }
    public java.security.PermissionCollection newPermissionCollection() { return null; }
    public java.lang.String getActions() { return null; }
    public int hashCode() { return 0; }
    public java.lang.String toString() { return null; }
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class Permission implements java.security.Guard, java.io.Serializable {
    public Permission(java.lang.String var0) { }
    public abstract boolean equals(java.lang.Object var0);
    public abstract int hashCode();
    public void checkGuard(java.lang.Object var0) throws java.lang.SecurityException { }
    public abstract java.lang.String getActions();
    public final java.lang.String getName() { return null; }
    public abstract boolean implies(java.security.Permission var0);
    public java.security.PermissionCollection newPermissionCollection() { return null; }
    public java.lang.String toString() { return null; }
}


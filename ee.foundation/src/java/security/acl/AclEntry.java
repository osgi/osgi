/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security.acl;
public abstract interface AclEntry extends java.lang.Cloneable {
    public abstract boolean addPermission(java.security.acl.Permission var0);
    public abstract boolean checkPermission(java.security.acl.Permission var0);
    public abstract java.lang.Object clone();
    public abstract java.security.Principal getPrincipal();
    public abstract boolean isNegative();
    public abstract java.util.Enumeration permissions();
    public abstract boolean removePermission(java.security.acl.Permission var0);
    public abstract void setNegativePermissions();
    public abstract boolean setPrincipal(java.security.Principal var0);
    public abstract java.lang.String toString();
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security.acl;
public abstract interface Acl extends java.security.acl.Owner {
    public abstract boolean addEntry(java.security.Principal var0, java.security.acl.AclEntry var1) throws java.security.acl.NotOwnerException;
    public abstract boolean checkPermission(java.security.Principal var0, java.security.acl.Permission var1);
    public abstract java.util.Enumeration entries();
    public abstract java.lang.String getName();
    public abstract java.util.Enumeration getPermissions(java.security.Principal var0);
    public abstract boolean removeEntry(java.security.Principal var0, java.security.acl.AclEntry var1) throws java.security.acl.NotOwnerException;
    public abstract void setName(java.security.Principal var0, java.lang.String var1) throws java.security.acl.NotOwnerException;
    public abstract java.lang.String toString();
}


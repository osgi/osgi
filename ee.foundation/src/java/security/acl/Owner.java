/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security.acl;
public abstract interface Owner {
    public abstract boolean addOwner(java.security.Principal var0, java.security.Principal var1) throws java.security.acl.NotOwnerException;
    public abstract boolean deleteOwner(java.security.Principal var0, java.security.Principal var1) throws java.security.acl.NotOwnerException, java.security.acl.LastOwnerException;
    public abstract boolean isOwner(java.security.Principal var0);
}


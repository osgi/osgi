/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.acl;
public abstract interface Group extends java.security.Principal {
    public abstract boolean addMember(java.security.Principal var0);
    public abstract boolean isMember(java.security.Principal var0);
    public abstract java.util.Enumeration members();
    public abstract boolean removeMember(java.security.Principal var0);
}


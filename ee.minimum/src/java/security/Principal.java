/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract interface Principal {
    public abstract boolean equals(java.lang.Object var0);
    public abstract java.lang.String getName();
    public abstract int hashCode();
    public abstract java.lang.String toString();
}


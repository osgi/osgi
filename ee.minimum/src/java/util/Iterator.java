/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util;
public abstract interface Iterator {
    public abstract boolean hasNext();
    public abstract java.lang.Object next();
    public abstract void remove();
}


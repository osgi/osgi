/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util;
public abstract interface Set extends java.util.Collection {
    public abstract boolean add(java.lang.Object var0);
    public abstract boolean addAll(java.util.Collection var0);
    public abstract void clear();
    public abstract boolean contains(java.lang.Object var0);
    public abstract boolean containsAll(java.util.Collection var0);
    public abstract boolean equals(java.lang.Object var0);
    public abstract int hashCode();
    public abstract boolean isEmpty();
    public abstract java.util.Iterator iterator();
    public abstract boolean remove(java.lang.Object var0);
    public abstract boolean removeAll(java.util.Collection var0);
    public abstract boolean retainAll(java.util.Collection var0);
    public abstract int size();
    public abstract java.lang.Object[] toArray();
    public abstract java.lang.Object[] toArray(java.lang.Object[] var0);
}


/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util;
public abstract interface SortedSet extends java.util.Set {
    public abstract java.util.Comparator comparator();
    public abstract java.lang.Object first();
    public abstract java.util.SortedSet headSet(java.lang.Object var0);
    public abstract java.lang.Object last();
    public abstract java.util.SortedSet subSet(java.lang.Object var0, java.lang.Object var1);
    public abstract java.util.SortedSet tailSet(java.lang.Object var0);
}


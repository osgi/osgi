/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.util;
public abstract class AbstractList extends java.util.AbstractCollection implements java.util.List {
    protected AbstractList() { }
    public void add(int var0, java.lang.Object var1) { }
    public boolean add(java.lang.Object var0) { return false; }
    public boolean addAll(int var0, java.util.Collection var1) { return false; }
    public void clear() { }
    public boolean equals(java.lang.Object var0) { return false; }
    public abstract java.lang.Object get(int var0);
    public int hashCode() { return 0; }
    public int indexOf(java.lang.Object var0) { return 0; }
    public java.util.Iterator iterator() { return null; }
    public int lastIndexOf(java.lang.Object var0) { return 0; }
    public java.util.ListIterator listIterator() { return null; }
    public java.util.ListIterator listIterator(int var0) { return null; }
    public java.lang.Object remove(int var0) { return null; }
    protected void removeRange(int var0, int var1) { }
    public java.lang.Object set(int var0, java.lang.Object var1) { return null; }
    public java.util.List subList(int var0, int var1) { return null; }
    protected int modCount;
}


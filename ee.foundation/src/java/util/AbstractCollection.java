/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util;
public abstract class AbstractCollection implements java.util.Collection {
	protected AbstractCollection() { }
	public boolean add(java.lang.Object var0) { return false; }
	public boolean addAll(java.util.Collection var0) { return false; }
	public void clear() { }
	public boolean contains(java.lang.Object var0) { return false; }
	public boolean containsAll(java.util.Collection var0) { return false; }
	public boolean isEmpty() { return false; }
	public abstract java.util.Iterator iterator();
	public boolean remove(java.lang.Object var0) { return false; }
	public boolean removeAll(java.util.Collection var0) { return false; }
	public boolean retainAll(java.util.Collection var0) { return false; }
	public abstract int size();
	public java.lang.Object[] toArray() { return null; }
	public java.lang.Object[] toArray(java.lang.Object[] var0) { return null; }
	public java.lang.String toString() { return null; }
}


/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util;
public abstract class AbstractMap implements java.util.Map {
	protected AbstractMap() { }
	public void clear() { }
	public boolean containsKey(java.lang.Object var0) { return false; }
	public boolean containsValue(java.lang.Object var0) { return false; }
	public abstract java.util.Set entrySet();
	public boolean equals(java.lang.Object var0) { return false; }
	public java.lang.Object get(java.lang.Object var0) { return null; }
	public int hashCode() { return 0; }
	public boolean isEmpty() { return false; }
	public java.util.Set keySet() { return null; }
	public java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public void putAll(java.util.Map var0) { }
	public java.lang.Object remove(java.lang.Object var0) { return null; }
	public int size() { return 0; }
	public java.lang.String toString() { return null; }
	public java.util.Collection values() { return null; }
}


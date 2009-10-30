/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.util;
public class Vector<E> extends java.util.AbstractList<E> implements java.io.Serializable, java.lang.Cloneable, java.util.List<E>, java.util.RandomAccess {
	protected int capacityIncrement;
	protected int elementCount;
	protected java.lang.Object[] elementData;
	public Vector() { } 
	public Vector(int var0) { } 
	public Vector(int var0, int var1) { } 
	public Vector(java.util.Collection<? extends E> var0) { } 
	public boolean add(E var0) { return false; }
	public boolean addAll(int var0, java.util.Collection<? extends E> var1) { return false; }
	public boolean addAll(java.util.Collection<? extends E> var0) { return false; }
	public void addElement(E var0) { }
	public int capacity() { return 0; }
	public java.lang.Object clone() { return null; }
	public boolean containsAll(java.util.Collection<?> var0) { return false; }
	public void copyInto(java.lang.Object[] var0) { }
	public E elementAt(int var0) { return null; }
	public java.util.Enumeration<E> elements() { return null; }
	public void ensureCapacity(int var0) { }
	public boolean equals(java.lang.Object var0) { return false; }
	public E firstElement() { return null; }
	public E get(int var0) { return null; }
	public int hashCode() { return 0; }
	public int indexOf(java.lang.Object var0, int var1) { return 0; }
	public void insertElementAt(E var0, int var1) { }
	public boolean isEmpty() { return false; }
	public E lastElement() { return null; }
	public int lastIndexOf(java.lang.Object var0) { return 0; }
	public int lastIndexOf(java.lang.Object var0, int var1) { return 0; }
	public E remove(int var0) { return null; }
	public boolean removeAll(java.util.Collection<?> var0) { return false; }
	public void removeAllElements() { }
	public boolean removeElement(java.lang.Object var0) { return false; }
	public void removeElementAt(int var0) { }
	protected void removeRange(int var0, int var1) { }
	public boolean retainAll(java.util.Collection<?> var0) { return false; }
	public E set(int var0, E var1) { return null; }
	public void setElementAt(E var0, int var1) { }
	public void setSize(int var0) { }
	public int size() { return 0; }
	public java.util.List<E> subList(int var0, int var1) { return null; }
	public java.lang.Object[] toArray() { return null; }
	public <T> T[] toArray(T[] var0) { return null; }
	public java.lang.String toString() { return null; }
	public void trimToSize() { }
}


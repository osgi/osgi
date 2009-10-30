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

package java.util.concurrent;
public class CopyOnWriteArrayList<E> implements java.io.Serializable, java.lang.Cloneable, java.util.List<E>, java.util.RandomAccess {
	public CopyOnWriteArrayList() { } 
	public CopyOnWriteArrayList(java.util.Collection<? extends E> var0) { } 
	public CopyOnWriteArrayList(E[] var0) { } 
	public void add(int var0, E var1) { }
	public boolean add(E var0) { return false; }
	public boolean addAll(int var0, java.util.Collection<? extends E> var1) { return false; }
	public boolean addAll(java.util.Collection<? extends E> var0) { return false; }
	public int addAllAbsent(java.util.Collection<? extends E> var0) { return 0; }
	public boolean addIfAbsent(E var0) { return false; }
	public void clear() { }
	public java.lang.Object clone() { return null; }
	public boolean contains(java.lang.Object var0) { return false; }
	public boolean containsAll(java.util.Collection<?> var0) { return false; }
	public E get(int var0) { return null; }
	public int hashCode() { return 0; }
	public int indexOf(java.lang.Object var0) { return 0; }
	public int indexOf(E var0, int var1) { return 0; }
	public boolean isEmpty() { return false; }
	public java.util.Iterator<E> iterator() { return null; }
	public int lastIndexOf(java.lang.Object var0) { return 0; }
	public int lastIndexOf(E var0, int var1) { return 0; }
	public java.util.ListIterator<E> listIterator() { return null; }
	public java.util.ListIterator<E> listIterator(int var0) { return null; }
	public E remove(int var0) { return null; }
	public boolean remove(java.lang.Object var0) { return false; }
	public boolean removeAll(java.util.Collection<?> var0) { return false; }
	public boolean retainAll(java.util.Collection<?> var0) { return false; }
	public E set(int var0, E var1) { return null; }
	public int size() { return 0; }
	public java.util.List<E> subList(int var0, int var1) { return null; }
	public java.lang.Object[] toArray() { return null; }
	public <T> T[] toArray(T[] var0) { return null; }
}


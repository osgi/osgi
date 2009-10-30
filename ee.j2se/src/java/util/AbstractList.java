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
public abstract class AbstractList<E> extends java.util.AbstractCollection<E> implements java.util.List<E> {
	protected int modCount;
	protected AbstractList() { } 
	public void add(int var0, E var1) { }
	public boolean addAll(int var0, java.util.Collection<? extends E> var1) { return false; }
	public int hashCode() { return 0; }
	public int indexOf(java.lang.Object var0) { return 0; }
	public java.util.Iterator<E> iterator() { return null; }
	public int lastIndexOf(java.lang.Object var0) { return 0; }
	public java.util.ListIterator<E> listIterator() { return null; }
	public java.util.ListIterator<E> listIterator(int var0) { return null; }
	public E remove(int var0) { return null; }
	protected void removeRange(int var0, int var1) { }
	public E set(int var0, E var1) { return null; }
	public java.util.List<E> subList(int var0, int var1) { return null; }
}


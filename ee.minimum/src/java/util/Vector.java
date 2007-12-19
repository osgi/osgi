/*
 * $Date$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2007). All Rights Reserved.
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
public class Vector extends java.util.AbstractList implements java.io.Serializable, java.lang.Cloneable, java.util.List {
	public Vector() { }
	public Vector(int var0) { }
	public Vector(int var0, int var1) { }
	public Vector(java.util.Collection var0) { }
	public void addElement(java.lang.Object var0) { }
	public int capacity() { return 0; }
	public java.lang.Object clone() { return null; }
	public void copyInto(java.lang.Object[] var0) { }
	public java.lang.Object elementAt(int var0) { return null; }
	public java.util.Enumeration elements() { return null; }
	public void ensureCapacity(int var0) { }
	public java.lang.Object firstElement() { return null; }
	public java.lang.Object get(int var0) { return null; }
	public int indexOf(java.lang.Object var0, int var1) { return 0; }
	public void insertElementAt(java.lang.Object var0, int var1) { }
	public java.lang.Object lastElement() { return null; }
	public int lastIndexOf(java.lang.Object var0, int var1) { return 0; }
	public void removeAllElements() { }
	public boolean removeElement(java.lang.Object var0) { return false; }
	public void removeElementAt(int var0) { }
	public void setElementAt(java.lang.Object var0, int var1) { }
	public void setSize(int var0) { }
	public int size() { return 0; }
	public void trimToSize() { }
	protected int capacityIncrement;
	protected int elementCount;
	protected java.lang.Object[] elementData;
}


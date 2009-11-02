/*
 * (C) Copyright 2001 Sun Microsystems, Inc.
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
public interface List<E> extends java.util.Collection<E> {
	void add(int var0, E var1);
	boolean addAll(int var0, java.util.Collection<? extends E> var1);
	boolean equals(java.lang.Object var0);
	E get(int var0);
	int hashCode();
	int indexOf(java.lang.Object var0);
	int lastIndexOf(java.lang.Object var0);
	java.util.ListIterator<E> listIterator();
	java.util.ListIterator<E> listIterator(int var0);
	E remove(int var0);
	E set(int var0, E var1);
	java.util.List<E> subList(int var0, int var1);
}


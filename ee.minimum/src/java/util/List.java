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
public interface List extends java.util.Collection {
	void add(int var0, java.lang.Object var1);
	boolean addAll(int var0, java.util.Collection var1);
	boolean equals(java.lang.Object var0);
	java.lang.Object get(int var0);
	int hashCode();
	int indexOf(java.lang.Object var0);
	int lastIndexOf(java.lang.Object var0);
	java.util.ListIterator listIterator();
	java.util.ListIterator listIterator(int var0);
	java.lang.Object remove(int var0);
	java.lang.Object set(int var0, java.lang.Object var1);
	java.util.List subList(int var0, int var1);
}


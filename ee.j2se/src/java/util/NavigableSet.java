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
public interface NavigableSet<E> extends java.util.SortedSet<E> {
	E ceiling(E var0);
	java.util.Iterator<E> descendingIterator();
	java.util.NavigableSet<E> descendingSet();
	E floor(E var0);
	java.util.NavigableSet<E> headSet(E var0, boolean var1);
	E higher(E var0);
	java.util.Iterator<E> iterator();
	E lower(E var0);
	E pollFirst();
	E pollLast();
	java.util.NavigableSet<E> subSet(E var0, boolean var1, E var2, boolean var3);
	java.util.NavigableSet<E> tailSet(E var0, boolean var1);
}


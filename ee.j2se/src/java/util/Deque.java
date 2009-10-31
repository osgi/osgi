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
public interface Deque<E> extends java.util.Queue<E> {
	void addFirst(E var0);
	void addLast(E var0);
	boolean contains(java.lang.Object var0);
	java.util.Iterator<E> descendingIterator();
	E getFirst();
	E getLast();
	java.util.Iterator<E> iterator();
	boolean offerFirst(E var0);
	boolean offerLast(E var0);
	E peekFirst();
	E peekLast();
	E pollFirst();
	E pollLast();
	E pop();
	void push(E var0);
	boolean remove(java.lang.Object var0);
	E removeFirst();
	boolean removeFirstOccurrence(java.lang.Object var0);
	E removeLast();
	boolean removeLastOccurrence(java.lang.Object var0);
	int size();
}


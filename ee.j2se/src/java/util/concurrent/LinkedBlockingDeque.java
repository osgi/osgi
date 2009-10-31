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
public class LinkedBlockingDeque<E> extends java.util.AbstractQueue<E> implements java.io.Serializable, java.util.concurrent.BlockingDeque<E> {
	public LinkedBlockingDeque() { } 
	public LinkedBlockingDeque(int var0) { } 
	public LinkedBlockingDeque(java.util.Collection<? extends E> var0) { } 
	public void addFirst(E var0) { }
	public void addLast(E var0) { }
	public java.util.Iterator<E> descendingIterator() { return null; }
	public int drainTo(java.util.Collection<? super E> var0) { return 0; }
	public int drainTo(java.util.Collection<? super E> var0, int var1) { return 0; }
	public E getFirst() { return null; }
	public E getLast() { return null; }
	public java.util.Iterator<E> iterator() { return null; }
	public boolean offer(E var0) { return false; }
	public boolean offer(E var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException { return false; }
	public boolean offerFirst(E var0) { return false; }
	public boolean offerFirst(E var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException { return false; }
	public boolean offerLast(E var0) { return false; }
	public boolean offerLast(E var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException { return false; }
	public E peek() { return null; }
	public E peekFirst() { return null; }
	public E peekLast() { return null; }
	public E poll() { return null; }
	public E poll(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return null; }
	public E pollFirst() { return null; }
	public E pollFirst(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return null; }
	public E pollLast() { return null; }
	public E pollLast(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return null; }
	public E pop() { return null; }
	public void push(E var0) { }
	public void put(E var0) throws java.lang.InterruptedException { }
	public void putFirst(E var0) throws java.lang.InterruptedException { }
	public void putLast(E var0) throws java.lang.InterruptedException { }
	public int remainingCapacity() { return 0; }
	public E removeFirst() { return null; }
	public boolean removeFirstOccurrence(java.lang.Object var0) { return false; }
	public E removeLast() { return null; }
	public boolean removeLastOccurrence(java.lang.Object var0) { return false; }
	public int size() { return 0; }
	public E take() throws java.lang.InterruptedException { return null; }
	public E takeFirst() throws java.lang.InterruptedException { return null; }
	public E takeLast() throws java.lang.InterruptedException { return null; }
}


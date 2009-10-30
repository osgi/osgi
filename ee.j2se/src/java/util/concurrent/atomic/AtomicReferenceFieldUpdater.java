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

package java.util.concurrent.atomic;
public abstract class AtomicReferenceFieldUpdater<T,V> {
	protected AtomicReferenceFieldUpdater() { } 
	public abstract boolean compareAndSet(T var0, V var1, V var2);
	public abstract V get(T var0);
	public V getAndSet(T var0, V var1) { return null; }
	public static <U,W> java.util.concurrent.atomic.AtomicReferenceFieldUpdater<U,W> newUpdater(java.lang.Class<U> var0, java.lang.Class<W> var1, java.lang.String var2) { return null; }
	public abstract void set(T var0, V var1);
	public abstract boolean weakCompareAndSet(T var0, V var1, V var2);
}


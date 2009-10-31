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
public abstract class AtomicIntegerFieldUpdater<T> {
	protected AtomicIntegerFieldUpdater() { } 
	public int addAndGet(T var0, int var1) { return 0; }
	public abstract boolean compareAndSet(T var0, int var1, int var2);
	public int decrementAndGet(T var0) { return 0; }
	public abstract int get(T var0);
	public int getAndAdd(T var0, int var1) { return 0; }
	public int getAndDecrement(T var0) { return 0; }
	public int getAndIncrement(T var0) { return 0; }
	public int getAndSet(T var0, int var1) { return 0; }
	public int incrementAndGet(T var0) { return 0; }
	public abstract void lazySet(T var0, int var1);
	public static <U> java.util.concurrent.atomic.AtomicIntegerFieldUpdater<U> newUpdater(java.lang.Class<U> var0, java.lang.String var1) { return null; }
	public abstract void set(T var0, int var1);
	public abstract boolean weakCompareAndSet(T var0, int var1, int var2);
}


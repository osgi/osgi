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
public abstract class AtomicLongFieldUpdater<T> {
	protected AtomicLongFieldUpdater() { } 
	public long addAndGet(T var0, long var1) { return 0l; }
	public abstract boolean compareAndSet(T var0, long var1, long var2);
	public long decrementAndGet(T var0) { return 0l; }
	public abstract long get(T var0);
	public long getAndAdd(T var0, long var1) { return 0l; }
	public long getAndDecrement(T var0) { return 0l; }
	public long getAndIncrement(T var0) { return 0l; }
	public long getAndSet(T var0, long var1) { return 0l; }
	public long incrementAndGet(T var0) { return 0l; }
	public static <U> java.util.concurrent.atomic.AtomicLongFieldUpdater<U> newUpdater(java.lang.Class<U> var0, java.lang.String var1) { return null; }
	public abstract void set(T var0, long var1);
	public abstract boolean weakCompareAndSet(T var0, long var1, long var2);
}


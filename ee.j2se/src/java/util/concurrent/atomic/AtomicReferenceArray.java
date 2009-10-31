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
public class AtomicReferenceArray<E> implements java.io.Serializable {
	public AtomicReferenceArray(int var0) { } 
	public AtomicReferenceArray(E[] var0) { } 
	public final boolean compareAndSet(int var0, E var1, E var2) { return false; }
	public final E get(int var0) { return null; }
	public final E getAndSet(int var0, E var1) { return null; }
	public final void lazySet(int var0, E var1) { }
	public final int length() { return 0; }
	public final void set(int var0, E var1) { }
	public final boolean weakCompareAndSet(int var0, E var1, E var2) { return false; }
}


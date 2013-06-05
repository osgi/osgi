/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.test.support.concurrent;

public class AtomicInteger extends Number {
	private static final long	serialVersionUID	= 1L;
	private int					value;

	public AtomicInteger(int value) {
		set(value);
	}

	public AtomicInteger() {
		// use default value;
	}

	public final synchronized int get() {
		return value;
	}

	public final synchronized void set(int newValue) {
		this.value = newValue;
	}

	public final synchronized int getAndSet(int newValue) {
		int old = this.value;
		this.value = newValue;
		return old;
	}

	public final synchronized boolean compareAndSet(int old, int newValue) {
		if (this.value != old) {
			return false;
		}
		this.value = newValue;
		return true;
	}

	public final synchronized int addAndGet(int delta) {
		this.value += delta;
		return this.value;
	}

	public final synchronized int getAndAdd(int delta) {
		int old = this.value;
		this.value += delta;
		return old;
	}

	public final int getAndDecrement() {
		return getAndAdd(-1);
	}

	public final int getAndIncrement() {
		return getAndAdd(+1);
	}

	public final int incrementAndGet() {
		return addAndGet(+1);
	}

	public final int decrementAndGet() {
		return addAndGet(-1);
	}

	@Override
	public int intValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public long longValue() {
		return get();
	}

	@Override
	public String toString() {
		return Integer.toString(get());
	}
}

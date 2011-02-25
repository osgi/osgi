/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

import java.io.Serializable;

public class AtomicReference<T> implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private T					reference;

	public AtomicReference(T reference) {
		set(reference);
	}

	public AtomicReference() {
		// use default value
	}

	public final synchronized T get() {
		return reference;
	}

	public final synchronized void set(T newValue) {
		this.reference = newValue;
	}

	public final synchronized boolean compareAndSet(T old, T newValue) {
		if (this.reference != old) {
			return false;
		}
		this.reference = newValue;
		return true;
	}

	public final synchronized T getAndSet(T newValue) {
		T old = this.reference;
		this.reference = newValue;
		return old;
	}

	public String toString() {
		return String.valueOf(get());
	}
}

/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.util.promise;

import java.lang.reflect.InvocationTargetException;

/**
 * Resolved Promise implementation.
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link PromiseFactory#resolved(Object)} or
 * {@link PromiseFactory#failed(Throwable)} to create a resolved
 * {@link Promise}.
 * 
 * @param <T> The result type associated with the Promise.
 * @since 1.1
 * @ThreadSafe
 * @author $Id$
 */
final class ResolvedPromiseImpl<T> extends PromiseImpl<T> {
	/**
	 * The value of this Promise if successfully resolved.
	 */
	private final T			value;
	/**
	 * The failure of this Promise if resolved with a failure or {@code null} if
	 * successfully resolved.
	 */
	private final Throwable	fail;

	/**
	 * Initialize this resolved Promise.
	 * 
	 * @param v The value of this resolved Promise.
	 * @param f The failure of this resolved Promise.
	 * @param factory The factory to use for callbacks and scheduled operations.
	 */
	ResolvedPromiseImpl(T v, Throwable f, PromiseFactory factory) {
		super(factory);
		if (f == null) {
			value = v;
			fail = null;
		} else {
			value = null;
			fail = f;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDone() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getValue() throws InvocationTargetException {
		if (fail == null) {
			return value;
		}
		throw new InvocationTargetException(fail);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Throwable getFailure() {
		return fail;
	}

	/**
	 * Return a holder of the result of this PromiseImpl.
	 */
	@Override
	Result<T> collect() {
		if (fail == null) {
			return new Result<T>(value);
		}
		return new Result<T>(fail);
	}

	@Override
	public String toString() {
		if (fail == null) {
			return super.toString() + "[resolved: " + value + "]";
		}
		return super.toString() + "[failed: " + fail + "]";
	}
}

/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Static helper methods for {@link Promise}s.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public class Promises {
	private Promises() {
		// disallow object creation
	}
	
	/**
	 * Create a new Promise that has been resolved with the specified value.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param <S> A subtype of the value type associated with the returned
	 *        Promise.
	 * @param value The value of the resolved Promise.
	 * @return A new Promise that has been resolved with the specified value.
	 */
	public static <T, S extends T> Promise<T> newResolvedPromise(S value) {
		return new PromiseImpl<T>(value, null);
	}

	/**
	 * Create a new Promise that has been resolved with the specified failure.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param failure The failure of the resolved Promise. Must not be
	 *        {@code null}.
	 * @return A new Promise that has been resolved with the specified failure.
	 */
	public static <T> Promise<T> newFailedPromise(Throwable failure) {
		return new PromiseImpl<T>(null, PromiseImpl.requireNonNull(failure));
	}

	/**
	 * Create a new Promise that is a latch on the resolution of the specified
	 * Promises.
	 * 
	 * <p>
	 * The new Promise acts as a gate and must be resolved after all of the
	 * specified Promises are resolved.
	 * 
	 * @param <T> The value type associated with the specified Promises.
	 * @param promises The Promises which must be resolved before the returned
	 *        Promise must be resolved. Must not be {@code null}.
	 * @return A Promise that is resolved only when all the specified Promises
	 *         are resolved. The returned Promise will be successfully resolved,
	 *         with the value {@code null}, if all the specified Promises are
	 *         successfully resolved. The returned Promise will be resolved with
	 *         a failure of {@link FailedPromisesException} if any of the
	 *         specified Promises are resolved with a failure. The failure
	 *         {@link FailedPromisesException} must contain all of the specified
	 *         Promises which resolved with a failure.
	 */
	public static <T> Promise<Void> newLatchPromise(Collection<Promise<T>> promises) {
		if (promises.isEmpty()) {
			return newResolvedPromise((Void) null);
		}
		Deferred<Void> chained = new Deferred<Void>();
		LatchPromise<T> latchPromise = new LatchPromise<T>(chained, promises.size());
		for (Promise<T> promise : promises) {
			promise.then(latchPromise, latchPromise);
		}
		return chained.getPromise();
	}

	/**
	 * A callback used to resolve a Deferred when the specified count of
	 * Promises are resolved for the
	 * {@link Promises#newLatchPromise(Collection)} method.
	 * 
	 * @ThreadSafe
	 */
	private static final class LatchPromise<T> implements Success<T, Void>, Failure {
		private final Deferred<Void>	chained;
		private final List<Promise<?>>	failed;		// @GuardedBy("this")
		private int						count;		// @GuardedBy("this")

		LatchPromise(Deferred<Void> chained, int count) {
			this.chained = chained;
			this.count = count;
			this.failed = new ArrayList<Promise<?>>(count);
		}

		public Promise<Void> call(Promise<T> resolved) throws Exception {
			resolveChained(null);
			return null;
		}

		public void fail(Promise<?> resolved) throws Exception {
			resolveChained(resolved);
		}

		private void resolveChained(Promise<?> failedPromise) {
			Throwable failure = null;
			synchronized (this) {
				if (failedPromise != null) {
					failed.add(failedPromise);
				}
				if (--count > 0) {
					return;
				}
				if (!failed.isEmpty()) {
					failure = new FailedPromisesException(failed);
				}
			}
			if (failure == null) {
				chained.resolve(null);
			} else {
				chained.fail(failure);
			}
		}
	}

	/**
	 * Create a new Promise that is a latch on the resolution of the specified
	 * Promises.
	 * 
	 * <p>
	 * The new Promise acts as a gate and must be resolved after all of the
	 * specified Promises are resolved.
	 * 
	 * @param promises The Promises which must be resolved before the returned
	 *        Promise must be resolved. Must not be {@code null}.
	 * @return A Promise that is resolved only when all the specified Promises
	 *         are resolved. The returned Promise will be successfully resolved,
	 *         with the value {@code null}, if all the specified Promises are
	 *         successfully resolved. The returned Promise will be resolved with
	 *         a failure of {@link FailedPromisesException} if any of the
	 *         specified Promises are resolved with a failure. The failure
	 *         {@link FailedPromisesException} must contain all of the specified
	 *         Promises which resolved with a failure.
	 */
	public static Promise<Void> newLatchPromise(Promise<?>... promises) {
		@SuppressWarnings("unchecked")
		List<Promise<Object>> list = Arrays.asList((Promise<Object>[]) promises);
		return newLatchPromise(list);
	}
}

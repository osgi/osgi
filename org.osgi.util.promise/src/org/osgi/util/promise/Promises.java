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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	 * @param value The value of the resolved Promise.
	 * @return A new Promise that has been resolved with the specified value.
	 */
	public static <T> Promise<T> newResolvedPromise(T value) {
		return new PromiseImpl<T>(value, null);
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
	public static <T> Promise<Void> newLatchPromise(Collection<Promise<T>> promises) {
		if (promises.isEmpty()) {
			return newResolvedPromise(null);
		}
		final Deferred<Void> deferred = new Deferred<Void>();
		final int size = promises.size();
		final List<Promise<?>> failed = Collections.synchronizedList(new ArrayList<Promise<?>>(size));
		final AtomicInteger count = new AtomicInteger(size);
		for (final Promise<?> promise : promises) {
			promise.onResolve(new Runnable() {
				public void run() {
					final boolean interrupted = Thread.interrupted();
					try {
						Throwable f = null;
						try {
							f = promise.getFailure();
						} catch (InterruptedException e) {
							/*
							 * This should not happen since (1) we are a
							 * callback on a resolved Promise and (2) we cleared
							 * the interrupt status above.
							 */
							throw new Error(e);
						}
						if (f != null) {
							failed.add(promise);
						}
						// If last specified promise to resolve
						if (count.decrementAndGet() == 0) {
							if (failed.isEmpty()) {
								deferred.resolve(null);
							} else {
								deferred.fail(new FailedPromisesException(failed));
							}
						}
					} finally {
						if (interrupted) { // restore interrupt status
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		}
		return deferred.getPromise();
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

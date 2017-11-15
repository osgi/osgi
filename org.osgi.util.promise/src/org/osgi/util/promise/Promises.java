/*
 * Copyright (c) OSGi Alliance (2014, 2017). All Rights Reserved.
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

import static org.osgi.util.promise.PromiseExecutors.defaultExecutors;

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
	 * Returns a new Promise that has been resolved with the specified value.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param value The value of the resolved Promise.
	 * @return A new Promise which uses the default callback executor and
	 *         default scheduled executor that has been resolved with the
	 *         specified value.
	 * @see PromiseExecutors#resolved(Object)
	 */
	public static <T> Promise<T> resolved(T value) {
		return defaultExecutors.resolved(value);
	}

	/**
	 * Returns a new Promise that has been resolved with the specified failure.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param failure The failure of the resolved Promise. Must not be
	 *            {@code null}.
	 * @return A new Promise which uses the default callback executor and
	 *         default scheduled executor that has been resolved with the
	 *         specified failure.
	 * @see PromiseExecutors#failed(Throwable)
	 */
	public static <T> Promise<T> failed(Throwable failure) {
		return defaultExecutors.failed(failure);
	}

	/**
	 * Returns a new Promise that is a latch on the resolution of the specified
	 * Promises.
	 * <p>
	 * The returned Promise acts as a gate and must be resolved after all of the
	 * specified Promises are resolved.
	 * 
	 * @param <T> The value type of the List value associated with the returned
	 *            Promise.
	 * @param <S> A subtype of the value type of the List value associated with
	 *            the returned Promise.
	 * @param promises The Promises which must be resolved before the returned
	 *            Promise must be resolved. Must not be {@code null} and all of
	 *            the elements in the collection must not be {@code null}.
	 * @return A Promise which uses the default callback executor and default
	 *         scheduled executor that is resolved only when all the specified
	 *         Promises are resolved. The returned Promise must be successfully
	 *         resolved with a List of the values in the order of the specified
	 *         Promises if all the specified Promises are successfully resolved.
	 *         The List in the returned Promise is the property of the caller
	 *         and is modifiable. The returned Promise must be resolved with a
	 *         failure of {@link FailedPromisesException} if any of the
	 *         specified Promises are resolved with a failure. The failure
	 *         {@link FailedPromisesException} must contain all of the specified
	 *         Promises which resolved with a failure.
	 * @see PromiseExecutors#all(Collection)
	 */
	public static <T, S extends T> Promise<List<T>> all(
			Collection<Promise<S>> promises) {
		return defaultExecutors.all(promises);
	}

	/**
	 * Returns a new Promise that is a latch on the resolution of the specified
	 * Promises.
	 * <p>
	 * The new Promise acts as a gate and must be resolved after all of the
	 * specified Promises are resolved.
	 * 
	 * @param <T> The value type associated with the specified Promises.
	 * @param promises The Promises which must be resolved before the returned
	 *            Promise must be resolved. Must not be {@code null} and all of
	 *            the arguments must not be {@code null}.
	 * @return A Promise which uses the default callback executor and scheduled
	 *         executor that is resolved only when all the specified Promises
	 *         are resolved. The returned Promise must be successfully resolved
	 *         with a List of the values in the order of the specified Promises
	 *         if all the specified Promises are successfully resolved. The List
	 *         in the returned Promise is the property of the caller and is
	 *         modifiable. The returned Promise must be resolved with a failure
	 *         of {@link FailedPromisesException} if any of the specified
	 *         Promises are resolved with a failure. The failure
	 *         {@link FailedPromisesException} must contain all of the specified
	 *         Promises which resolved with a failure.
	 */
	@SafeVarargs
	public static <T> Promise<List<T>> all(Promise<? extends T>... promises) {
		@SuppressWarnings("unchecked")
		List<Promise<T>> list = Arrays.asList((Promise<T>[]) promises);
		return defaultExecutors.all(list);
	}
}

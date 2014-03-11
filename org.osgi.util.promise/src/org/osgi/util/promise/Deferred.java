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

/**
 * A Deferred Promise implementation.
 * 
 * <p>
 * Instances of this class can be used to create a {@link Promise} that can be
 * resolved in the future. The {@link #getPromise() associated} Promise can be
 * successfully resolved with {@link #resolve(Object)} or resolved with a
 * failure with {@link #fail(Throwable)}.
 * 
 * <p>
 * The associated Promise can be provided to any one, but the Deferred instance
 * should be made available only to the party that will responsible for
 * resolving the Promise.
 * 
 * @param <T> The result type associated with the created Promise.
 * 
 * @Immutable
 * @author $Id$
 */
public class Deferred<T> {
	private final PromiseImpl<T>	promise;

	/**
	 * Create a new Deferred with an associated Promise.
	 */
	public Deferred() {
		promise = new PromiseImpl<T>();
	}

	/**
	 * Returns the Promise associated with this Deferred.
	 * 
	 * @return The Promise associated with this Deferred.
	 */
	public Promise<T> getPromise() {
		return promise;
	}

	/**
	 * Successfully resolve the Promise associated with this Deferred.
	 * 
	 * <p>
	 * After the associated Promise is resolved with the specified value, all
	 * registered {@link Promise#onResolve(Runnable) callbacks} are called and
	 * any {@link Promise#then(Success, Failure) chained} Promises are resolved.
	 * 
	 * <p>
	 * Resolving the associated Promise <i>happens-before</i> any registered
	 * callback is called. That is, in a registered callback,
	 * {@link Promise#isDone()} must return {@code true} and
	 * {@link Promise#getValue()} and {@link Promise#getFailure()} must not block.
	 * 
	 * @param value The value of the resolved Promise.
	 * @throws IllegalStateException If the associated Promise was already
	 *         resolved.
	 */
	public void resolve(T value) {
		promise.resolve(value, null);
	}

	/**
	 * Fail the Promise associated with this Deferred.
	 * 
	 * <p>
	 * After the associated Promise is resolved with the specified failure, all
	 * registered {@link Promise#onResolve(Runnable) callbacks} are called and
	 * any {@link Promise#then(Success, Failure) chained} Promises are resolved.
	 * 
	 * <p>
	 * Resolving the associated Promise <i>happens-before</i> any registered
	 * callback is called. That is, in a registered callback,
	 * {@link Promise#isDone()} must return {@code true} and
	 * {@link Promise#getValue()} and {@link Promise#getFailure()} must not block.
	 * 
	 * @param failure The failure of the resolved Promise.
	 * @throws IllegalStateException If the associated Promise was already
	 *         resolved.
	 */
	public void fail(Throwable failure) {
		promise.resolve(null, failure);
	}
}

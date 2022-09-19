/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.util.promise;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.osgi.util.function.Consumer;
import org.osgi.util.function.Function;

/**
 * Resolved Promise implementation.
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link PromiseFactory#resolved(Object)} to create a resolved {@link Promise}.
 * 
 * @param <T> The result type associated with the Promise.
 * @since 1.1
 * @ThreadSafe
 * @author $Id$
 */
final class ResolvedPromiseImpl<T> extends PromiseImpl<T> {
	/**
	 * The value of this resolved Promise.
	 */
	private final T			value;

	/**
	 * Initialize this resolved Promise.
	 * 
	 * @param value The value of this resolved Promise.
	 * @param factory The factory to use for callbacks and scheduled operations.
	 */
	ResolvedPromiseImpl(T value, PromiseFactory factory) {
		super(factory);
		this.value = value;
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
	public T getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Throwable getFailure() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void result(Result< ? super T> consumer) {
		consumer.accept(value, null);
	}

	@Override
	public String toString() {
		return super.toString() + "[resolved: " + value + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> onFailure(Consumer< ? super Throwable> failure) {
		requireNonNull(failure);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <F> Promise<T> onFailure(Consumer< ? super F> failure,
			Class< ? extends F> failureType) {
		requireNonNull(failure);
		requireNonNull(failureType);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> then(Success< ? super T, ? extends R> success,
			Failure failure) {
		if (success == null) {
			return resolved(null);
		}
		return super.then(success, failure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recover(Function<Promise< ? >, ? extends T> recovery) {
		requireNonNull(recovery);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recover(Function<Promise< ? >, ? extends T> recovery,
			Class< ? > failureType) {
		requireNonNull(recovery);
		requireNonNull(failureType);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recoverWith(
			Function<Promise< ? >,Promise< ? extends T>> recovery) {
		requireNonNull(recovery);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recoverWith(
			Function<Promise< ? >,Promise< ? extends T>> recovery,
			Class< ? > failureType) {
		requireNonNull(recovery);
		requireNonNull(failureType);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> fallbackTo(Promise< ? extends T> fallback) {
		requireNonNull(fallback);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> fallbackTo(Promise< ? extends T> fallback,
			Class< ? > failureType) {
		requireNonNull(fallback);
		requireNonNull(failureType);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> timeout(long millis) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompletionStage<T> toCompletionStage() {
		return CompletableFuture.completedFuture(value);
	}
}

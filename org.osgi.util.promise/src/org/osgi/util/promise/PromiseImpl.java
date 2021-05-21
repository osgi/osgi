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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.util.function.Consumer;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;

/**
 * Abstract Promise implementation.
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link PromiseFactory} to create a {@link Promise}.
 * 
 * @param <T> The result type associated with the Promise.
 * @ThreadSafe
 * @author $Id$
 */
abstract class PromiseImpl<T> implements Promise<T> {
	/**
	 * The factory to use for callbacks and scheduled operations.
	 */
	private final PromiseFactory					factory;
	/**
	 * A ConcurrentLinkedQueue to hold the callbacks for this Promise, so no
	 * additional synchronization is required to write to or read from the
	 * queue.
	 */
	private final ConcurrentLinkedQueue<Runnable>	callbacks;

	/**
	 * Initialize this Promise.
	 * 
	 * @param factory The factory to use for callbacks and scheduled
	 *            operations.
	 */
	PromiseImpl(PromiseFactory factory) {
		this.factory = requireNonNull(factory);
		callbacks = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Return a new {@link DeferredPromiseImpl} using the {@link PromiseFactory}
	 * of this PromiseImpl.
	 * 
	 * @return A new DeferredPromiseImpl.
	 * @since 1.1
	 */
	<V> DeferredPromiseImpl<V> deferred() {
		return new DeferredPromiseImpl<>(factory);
	}

	/**
	 * Return a new {@link ResolvedPromiseImpl} using the {@link PromiseFactory}
	 * of this PromiseImpl.
	 * 
	 * @param v Value for the ResolvedPromiseImpl.
	 * @return A new ResolvedPromiseImpl.
	 * @since 1.1
	 */
	<V> ResolvedPromiseImpl<V> resolved(V v) {
		return new ResolvedPromiseImpl<>(v, factory);
	}

	/**
	 * Return a new {@link FailedPromiseImpl} using the {@link PromiseFactory}
	 * of this PromiseImpl.
	 * 
	 * @param f Failure for the FailedPromiseImpl.
	 * @return A new FailedPromiseImpl.
	 * @since 1.1
	 */
	<V> FailedPromiseImpl<V> failed(Throwable f) {
		return new FailedPromiseImpl<>(f, factory);
	}

	/**
	 * Marker interface for internal callbacks which do not call user code and
	 * should be run on the current thread for an already resolved promise. Such
	 * internal callbacks should be directly called to resolve a
	 * DeferredPromiseImpl so that its {@link DeferredPromiseImpl#orDone()}
	 * method can convert it to a resolved {@link PromiseImpl} type for
	 * efficiency.
	 * 
	 * @since 1.2
	 */
	interface InlineCallback {
		// Internal callback that should be called inline for a resolved
		// promise.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> onResolve(Runnable callback) {
		requireNonNull(callback);
		if (isDone() && ((callback instanceof InlineCallback)
				|| factory.allowCurrentThread())) {
			try {
				callback.run();
			} catch (Throwable t) {
				uncaughtException(t);
			}
		} else {
			callbacks.offer(callback);
			notifyCallbacks(); // call any registered callbacks
		}
		return this;
	}

	/**
	 * Call any registered callbacks if this Promise is resolved.
	 */
	void notifyCallbacks() {
		if (!isDone()) {
			return; // return if not resolved
		}
		/*
		 * Note: multiple threads can be in this method removing callbacks from
		 * the queue and executing them, so the order in which callbacks are
		 * executed cannot be specified.
		 */
		for (Runnable callback; (callback = callbacks.poll()) != null;) {
			execute(callback);
		}
	}

	/**
	 * Execute a operation on the executor.
	 * 
	 * @since 1.2
	 */
	void execute(Runnable operation) {
		try {
			try {
				factory.executor().execute(operation);
			} catch (RejectedExecutionException e) {
				operation.run();
			}
		} catch (Throwable t) {
			uncaughtException(t);
		}
	}

	/**
	 * Schedule a operation on the scheduled executor.
	 * 
	 * @since 1.1
	 */
	ScheduledFuture< ? > schedule(Runnable operation, long delay,
			TimeUnit unit) {
		try {
			try {
				return factory.scheduledExecutor().schedule(operation, delay,
						unit);
			} catch (RejectedExecutionException e) {
				execute(operation);
			}
		} catch (Throwable t) {
			uncaughtException(t);
		}
		return null;
	}

	/**
	 * Handle an uncaught exception from a Runnable.
	 * 
	 * @param t The uncaught exception.
	 * @since 1.1
	 */
	static void uncaughtException(Throwable t) {
		try {
			Thread thread = Thread.currentThread();
			thread.getUncaughtExceptionHandler().uncaughtException(thread, t);
		} catch (Throwable ignored) {
			// we ignore this
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> onSuccess(Consumer< ? super T> success) {
		return onResolve(new OnSuccess(success));
	}

	/**
	 * A callback used for the {@link #onSuccess(Consumer)} method.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class OnSuccess implements Runnable, Result<T> {
		private final Consumer< ? super T> success;

		OnSuccess(Consumer< ? super T> success) {
			this.success = requireNonNull(success);
		}

		@Override
		public void run() {
			result(this);
		}

		@Override
		public void accept(T v, Throwable f) {
			if (f == null) {
				try {
					success.accept(v);
				} catch (Throwable e) {
					uncaughtException(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> onFailure(Consumer< ? super Throwable> failure) {
		return onResolve(new OnFailure(failure));
	}

	/**
	 * A callback used for the {@link #onFailure(Consumer)} method.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class OnFailure implements Runnable, Result<T> {
		private final Consumer< ? super Throwable> failure;

		OnFailure(Consumer< ? super Throwable> failure) {
			this.failure = requireNonNull(failure);
		}

		@Override
		public void run() {
			result(this);
		}

		@Override
		public void accept(T v, Throwable f) {
			if (f != null) {
				try {
					failure.accept(f);
				} catch (Throwable e) {
					uncaughtException(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> then(Success<? super T, ? extends R> success, Failure failure) {
		DeferredPromiseImpl<R> chained = deferred();
		onResolve(chained.new Then<>(this, success, failure));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> then(Success<? super T, ? extends R> success) {
		return then(success, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> thenAccept(Consumer< ? super T> consumer) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new ThenAccept(this, consumer));
		return chained.orDone();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> filter(Predicate<? super T> predicate) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Filter(this, predicate));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> map(Function<? super T, ? extends R> mapper) {
		DeferredPromiseImpl<R> chained = deferred();
		onResolve(chained.new Map<>(this, mapper));
		return chained.orDone();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> mapper) {
		DeferredPromiseImpl<R> chained = deferred();
		onResolve(chained.new FlatMap<>(this, mapper));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recover(Function<Promise<?>, ? extends T> recovery) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Recover(this, recovery));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recoverWith(Function<Promise<?>, Promise<? extends T>> recovery) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new RecoverWith(this, recovery));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> fallbackTo(Promise<? extends T> fallback) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new FallbackTo(this, fallback));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> timeout(long millis) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Timeout(this, millis));
		return chained.orDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> delay(long millis) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Delay(this, millis));
		return chained.orDone();
	}

	/**
	 * A consumer of the result of a Promise.
	 * 
	 * @since 1.2
	 */
	interface Result<R> {
		void accept(R value, Throwable fail);
	}

	/**
	 * Accept the result of this PromiseImpl.
	 * 
	 * @since 1.2
	 */
	abstract void result(Result< ? super T> consumer);

	/**
	 * Accept the result of the specified Promise.
	 * 
	 * @since 1.2
	 */
	static <R> void result(Promise< ? extends R> promise,
			Result< ? super R> consumer) {
		if (promise instanceof PromiseImpl) {
			PromiseImpl< ? extends R> impl = (PromiseImpl< ? extends R>) promise;
			impl.result(consumer);
			return;
		}
		if (!promise.isDone()) {
			consumer.accept(null, new AssertionError("promise not resolved"));
			return;
		}
		R value = null;
		Throwable fail = null;
		final boolean interrupted = Thread.interrupted();
		try {
			fail = promise.getFailure();
			if (fail == null) {
				value = promise.getValue();
			}
		} catch (Throwable e) {
			fail = e; // propagate new exception
		} finally {
			if (interrupted) { // restore interrupt status
				Thread.currentThread().interrupt();
			}
		}
		consumer.accept(value, fail);
	}
}

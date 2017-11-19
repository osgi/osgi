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
		this.factory = factory;
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
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> onResolve(Runnable callback) {
		callbacks.offer(callback);
		notifyCallbacks(); // call any registered callbacks
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
		for (Runnable callback = callbacks.poll(); callback != null; callback = callbacks.poll()) {
			try {
				try {
					factory.executor().execute(callback);
				} catch (RejectedExecutionException e) {
					callback.run();
				}
			} catch (Throwable t) {
				uncaughtException(t);
			}
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
				operation.run();
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
	 * 
	 * @since 1.1
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
	private final class OnSuccess implements Runnable {
		private final Consumer< ? super T> success;

		OnSuccess(Consumer< ? super T> success) {
			this.success = requireNonNull(success);
		}

		@Override
		public void run() {
			Result<T> result = collect();
			if (result.fail == null) {
				try {
					success.accept(result.value);
				} catch (Throwable e) {
					uncaughtException(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
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
	private final class OnFailure implements Runnable {
		private final Consumer< ? super Throwable> failure;

		OnFailure(Consumer< ? super Throwable> failure) {
			this.failure = requireNonNull(failure);
		}

		@Override
		public void run() {
			Result<T> result = collect();
			if (result.fail != null) {
				try {
					failure.accept(result.fail);
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
		return chained;
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
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> thenAccept(Consumer< ? super T> consumer) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new ThenAccept(this, consumer));
		return chained;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> filter(Predicate<? super T> predicate) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Filter(this, predicate));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> map(Function<? super T, ? extends R> mapper) {
		DeferredPromiseImpl<R> chained = deferred();
		onResolve(chained.new Map<>(this, mapper));
		return chained;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> mapper) {
		DeferredPromiseImpl<R> chained = deferred();
		onResolve(chained.new FlatMap<>(this, mapper));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recover(Function<Promise<?>, ? extends T> recovery) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new Recover(this, recovery));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recoverWith(Function<Promise<?>, Promise<? extends T>> recovery) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new RecoverWith(this, recovery));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> fallbackTo(Promise<? extends T> fallback) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new FallbackTo(this, fallback));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> timeout(long millis) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(chained.new ChainImpl(this));
		if (!isDone()) {
			PromiseImpl<T> timedout = new FailedPromiseImpl<>(
					new TimeoutException(), factory);
			onResolve(new Timeout(chained.new ChainImpl(timedout), millis,
					TimeUnit.MILLISECONDS));
		}
		return chained;
	}

	/**
	 * Timeout class used by the {@link PromiseImpl#timeout(long)} method to
	 * cancel timeout when the Promise is resolved.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class Timeout implements Runnable {
		private final ScheduledFuture< ? > future;

		Timeout(Runnable operation, long delay, TimeUnit unit) {
			future = schedule(operation, delay, unit);
		}

		@Override
		public void run() {
			if (future != null) {
				future.cancel(false);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> delay(long millis) {
		DeferredPromiseImpl<T> chained = deferred();
		onResolve(new Delay(chained.new ChainImpl(this), millis,
				TimeUnit.MILLISECONDS));
		return chained;
	}

	/**
	 * Delay class used by the {@link PromiseImpl#delay(long)} method to delay
	 * chaining a promise.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class Delay implements Runnable {
		private final Runnable	operation;
		private final long		delay;
		private final TimeUnit	unit;

		Delay(Runnable operation, long delay, TimeUnit unit) {
			this.operation = operation;
			this.delay = delay;
			this.unit = unit;
		}

		@Override
		public void run() {
			schedule(operation, delay, unit);
		}
	}

	/**
	 * A holder of the result of a Promise.
	 * 
	 * @NotThreadSafe
	 * @since 1.1
	 */
	static final class Result<P> {
		P			value;
		Throwable	fail;
	
		Result(P value) {
			this.value = value;
			this.fail = null;
		}
	
		Result(Throwable fail) {
			this.value = null;
			this.fail = fail;
		}
	}

	/**
	 * Return a holder of the result of this PromiseImpl.
	 * 
	 * @since 1.1
	 */
	abstract Result<T> collect();

	/**
	 * Return a holder of the result of the specified Promise.
	 * 
	 * @since 1.1
	 */
	static <R> Result<R> collect(Promise< ? extends R> promise) {
		if (promise instanceof PromiseImpl) {
			@SuppressWarnings("unchecked")
			PromiseImpl<R> impl = (PromiseImpl<R>) promise;
			return impl.collect();
		}
		if (!promise.isDone()) {
			return new Result<R>(new AssertionError("promise not resolved"));
		}
		final boolean interrupted = Thread.interrupted();
		try {
			Throwable fail = promise.getFailure();
			if (fail == null) {
				return new Result<R>(promise.getValue());
			}
			return new Result<R>(fail);
		} catch (Throwable e) {
			return new Result<R>(e); // propagate new exception
		} finally {
			if (interrupted) { // restore interrupt status
				Thread.currentThread().interrupt();
			}
		}
	}
}

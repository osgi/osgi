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

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.util.function.Consumer;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;

/**
 * Promise implementation.
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link Deferred} to create a resolvable {@link Promise}.
 * 
 * @param <T> The result type associated with the Promise.
 * @ThreadSafe
 * @author $Id$
 */
final class PromiseImpl<T> implements Promise<T> {
	/**
	 * The executors to use for callbacks and scheduled operations.
	 */
	private final PromiseExecutors					executors;
	/**
	 * A ConcurrentLinkedQueue to hold the callbacks for this Promise, so no
	 * additional synchronization is required to write to or read from the
	 * queue.
	 */
	private final ConcurrentLinkedQueue<Runnable>	callbacks;
	/**
	 * A CountDownLatch to manage the resolved state of this Promise.
	 * <p>
	 * This object is used as the synchronizing object to provide a critical
	 * section in {@link #tryResolve(Object, Throwable)} so that only a single
	 * thread can write the resolved state variables and open the latch.
	 * <p>
	 * The resolved state variables, {@link #value} and {@link #fail}, must only
	 * be written when the latch is closed (getCount() != 0) and must only be
	 * read when the latch is open (getCount() == 0). The latch state must
	 * always be checked before writing or reading since the resolved state
	 * variables' memory consistency is guarded by the latch.
	 */
	private final CountDownLatch					resolved;
	/**
	 * The value of this Promise if successfully resolved.
	 * 
	 * @GuardedBy("resolved")
	 * @see #resolved
	 */
	private T										value;
	/**
	 * The failure of this Promise if resolved with a failure or {@code null} if
	 * successfully resolved.
	 * 
	 * @GuardedBy("resolved")
	 * @see #resolved
	 */
	private Throwable								fail;

	/**
	 * Initialize this Promise.
	 * 
	 * @param executors The executors to use for callbacks and scheduled
	 *            operations.
	 */
	PromiseImpl(PromiseExecutors executors) {
		this.executors = executors;
		callbacks = new ConcurrentLinkedQueue<>();
		resolved = new CountDownLatch(1);
	}

	/**
	 * Initialize and resolve this Promise.
	 * 
	 * @param v The value of this resolved Promise.
	 * @param f The failure of this resolved Promise.
	 * @param executors The executors to use for callbacks and scheduled
	 *            operations.
	 */
	PromiseImpl(T v, Throwable f, PromiseExecutors executors) {
		if (f == null) {
			value = v;
		} else {
			fail = f;
		}
		this.executors = executors;
		callbacks = new ConcurrentLinkedQueue<>();
		resolved = new CountDownLatch(0);
	}

	/**
	 * Try to resolve this Promise.
	 * <p>
	 * If this Promise was already resolved, return false. Otherwise, resolve
	 * this Promise and return true.
	 * 
	 * @param v The value of this Promise.
	 * @param f The failure of this Promise.
	 * @return false if this Promise was already resolved; true if this method
	 *         resolved this Promise.
	 * @since 1.1
	 */
	boolean tryResolve(T v, Throwable f) {
		// critical section: only one resolver at a time
		synchronized (resolved) {
			if (resolved.getCount() == 0) {
				return false;
			}
			/*
			 * The resolved state variables must be set before opening the
			 * latch. This safely publishes them to be read by other threads
			 * that must verify the latch is open before reading.
			 */
			if (f == null) {
				value = v;
			} else {
				fail = f;
			}
			resolved.countDown();
		}
		notifyCallbacks(); // call any registered callbacks
		return true;
	}

	/**
	 * Resolve this Promise.
	 * <p>
	 * If this Promise was already resolved, throw IllegalStateException.
	 * Otherwise, resolve this Promise.
	 * 
	 * @param v The value of this Promise.
	 * @param f The failure of this Promise.
	 * @throws IllegalStateException If this Promise was already resolved.
	 */
	void resolve(T v, Throwable f) {
		if (!tryResolve(v, f)) {
			throw new IllegalStateException("Already resolved");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDone() {
		return resolved.getCount() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getValue() throws InvocationTargetException, InterruptedException {
		resolved.await();
		if (fail == null) {
			return value;
		}
		throw new InvocationTargetException(fail);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Throwable getFailure() throws InterruptedException {
		resolved.await();
		return fail;
	}

	/**
	 * @since 1.1
	 */
	@Override
	public String toString() {
		if (!isDone()) { // ensure latch open before reading state
			return super.toString() + "[unresolved]";
		}
		if (fail == null) {
			return super.toString() + "[resolved: " + value + "]";
		}
		return super.toString() + "[failed: " + fail + "]";
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
	private void notifyCallbacks() {
		if (resolved.getCount() != 0) {
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
					executors.executor().execute(callback);
				} catch (RejectedExecutionException e) {
					callback.run();
				}
			} catch (Throwable t) {
				uncaughtException(t);
			}
		}
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
		PromiseImpl<R> chained = new PromiseImpl<>(executors);
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
	 * A callback used to chain promises for the {@link #then(Success, Failure)}
	 * method.
	 * 
	 * @Immutable
	 */
	private final class Then<P> implements Runnable {
		private final PromiseImpl<P>			promise;
		private final Success<P, ? extends T>	success;
		private final Failure					failure;

		@SuppressWarnings("unchecked")
		Then(PromiseImpl<P> promise, Success< ? super P, ? extends T> success,
				Failure failure) {
			this.promise = promise;
			this.success = (Success<P, ? extends T>) success;
			this.failure = failure;
		}

		@Override
		public void run() {
			Result<P> result = promise.collect();
			if (result.fail != null) {
				if (failure != null) {
					try {
						failure.fail(promise);
					} catch (Throwable e) {
						result.fail = e; // propagate new exception
					}
				}
			} else if (success != null) {
				Promise< ? extends T> returned = null;
				try {
					returned = success.call(promise);
				} catch (Throwable e) {
					result.fail = e; // propagate new exception
				}
				if (returned != null) {
					// resolve chained when returned promise is resolved
					returned.onResolve(new Chain(returned));
					return;
				}
			}
			tryResolve(null, result.fail);
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the Promise is
	 * resolved.
	 * 
	 * @Immutable
	 */
	private final class Chain implements Runnable {
		private final Promise< ? extends T> promise;
	
		Chain(Promise< ? extends T> promise) {
			this.promise = promise;
		}
	
		@Override
		public void run() {
			Result<T> result = collect(promise);
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the PromiseImpl is
	 * resolved.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class ChainImpl implements Runnable {
		private final PromiseImpl<T> promise;
	
		ChainImpl(PromiseImpl<T> promise) {
			this.promise = promise;
		}
	
		@Override
		public void run() {
			Result<T> result = promise.collect();
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> thenAccept(Consumer< ? super T> consumer) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		onResolve(chained.new ThenAccept(this, consumer));
		return chained;
	}

	/**
	 * A callback used to resolve the chained Promise and call a Consumer when
	 * the PromiseImpl is resolved.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class ThenAccept implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Consumer< ? super T> consumer;
	
		ThenAccept(PromiseImpl<T> promise, Consumer< ? super T> consumer) {
			this.promise = promise;
			this.consumer = requireNonNull(consumer);
		}
	
		@Override
		public void run() {
			Result<T> result = promise.collect();
			if (result.fail == null) {
				try {
					consumer.accept(result.value);
				} catch (Throwable e) {
					result.fail = e;
				}
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * Resolve this Promise with the specified Promise.
	 * <p>
	 * If the specified Promise is successfully resolved, this Promise is
	 * resolved with the value of the specified Promise. If the specified
	 * Promise is resolved with a failure, this Promise is resolved with the
	 * failure of the specified Promise.
	 * 
	 * @param with A Promise whose value or failure must be used to resolve this
	 *            Promise. Must not be {@code null}.
	 * @return A Promise that is resolved only when this Promise is resolved by
	 *         the specified Promise. The returned Promise must be successfully
	 *         resolved with the value {@code null}, if this Promise was
	 *         resolved by the specified Promise. The returned Promise must be
	 *         resolved with a failure of {@link IllegalStateException}, if this
	 *         Promise was already resolved when the specified Promise was
	 *         resolved.
	 */
	Promise<Void> resolveWith(Promise<? extends T> with) {
		PromiseImpl<Void> chained = new PromiseImpl<>(executors);
		with.onResolve(chained.new ResolveWith<>(this, with));
		return chained;
	}

	/**
	 * A callback used to resolve a Promise with another Promise for the
	 * {@link PromiseImpl#resolveWith(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class ResolveWith<P> implements Runnable {
		private final PromiseImpl<P>		promise;
		private final Promise< ? extends P>	with;

		ResolveWith(PromiseImpl<P> promise, Promise< ? extends P> with) {
			this.promise = promise;
			this.with = requireNonNull(with);
		}

		@Override
		public void run() {
			Throwable f = null;
			Result<P> result = collect(with);
			try {
				promise.resolve(result.value, result.fail);
			} catch (Throwable e) {
				f = e; // propagate new exception
			}
			tryResolve(null, f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> filter(Predicate<? super T> predicate) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		onResolve(chained.new Filter(this, predicate));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#filter(Predicate)} method.
	 * 
	 * @Immutable
	 */
	private final class Filter implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Predicate<? super T>	predicate;

		Filter(PromiseImpl<T> promise, Predicate< ? super T> predicate) {
			this.promise = promise;
			this.predicate = requireNonNull(predicate);
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			if (result.fail == null) {
				try {
					if (!predicate.test(result.value)) {
						result.fail = new NoSuchElementException();
					}
				} catch (Throwable e) { // propagate new exception
					result.fail = e;
				}
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> map(Function<? super T, ? extends R> mapper) {
		PromiseImpl<R> chained = new PromiseImpl<>(executors);
		onResolve(chained.new Map<>(this, mapper));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#map(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class Map<P> implements Runnable {
		private final PromiseImpl<P>					promise;
		private final Function<? super P, ? extends T>	mapper;

		Map(PromiseImpl<P> promise,
				Function< ? super P, ? extends T> mapper) {
			this.promise = promise;
			this.mapper = requireNonNull(mapper);
		}

		@Override
		public void run() {
			Result<P> result = promise.collect();
			T v = null;
			if (result.fail == null) {
				try {
					v = mapper.apply(result.value);
				} catch (Throwable e) { // propagate new exception
					result.fail = e;
				}
			}
			tryResolve(v, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> mapper) {
		PromiseImpl<R> chained = new PromiseImpl<>(executors);
		onResolve(chained.new FlatMap<>(this, mapper));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#flatMap(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class FlatMap<P> implements Runnable {
		private final PromiseImpl<P>								promise;
		private final Function< ? super P,Promise< ? extends T>>	mapper;

		FlatMap(PromiseImpl<P> promise,
				Function< ? super P,Promise< ? extends T>> mapper) {
			this.promise = promise;
			this.mapper = requireNonNull(mapper);
		}

		@Override
		public void run() {
			Result<P> result = promise.collect();
			if (result.fail == null) {
				Promise< ? extends T> flatmap = null;
				try {
					flatmap = mapper.apply(result.value);
				} catch (Throwable e) { // propagate new exception
					result.fail = e;
				}
				if (flatmap != null) {
					flatmap.onResolve(new Chain(flatmap));
					return;
				}
			}
			tryResolve(null, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recover(Function<Promise<?>, ? extends T> recovery) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		onResolve(chained.new Recover(this, recovery));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recover(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class Recover implements Runnable {
		private final PromiseImpl<T>						promise;
		private final Function<Promise< ? >, ? extends T>	recovery;

		Recover(PromiseImpl<T> promise,
				Function<Promise< ? >, ? extends T> recovery) {
			this.promise = promise;
			this.recovery = requireNonNull(recovery);
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			if (result.fail != null) {
				try {
					T v = recovery.apply(promise);
					if (v != null) {
						result.value = v;
						result.fail = null;
					}
				} catch (Throwable e) { // propagate new exception
					result.fail = e;
				}
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> recoverWith(Function<Promise<?>, Promise<? extends T>> recovery) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		onResolve(chained.new RecoverWith(this, recovery));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recoverWith(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class RecoverWith implements Runnable {
		private final PromiseImpl<T>								promise;
		private final Function<Promise<?>, Promise<? extends T>>	recovery;

		RecoverWith(PromiseImpl<T> promise,
				Function<Promise< ? >,Promise< ? extends T>> recovery) {
			this.promise = promise;
			this.recovery = requireNonNull(recovery);
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			if (result.fail != null) {
				Promise< ? extends T> recovered = null;
				try {
					recovered = recovery.apply(promise);
				} catch (Throwable e) { // propagate new exception
					result.fail = e;
				}
				if (recovered != null) {
					recovered.onResolve(new Chain(recovered));
					return;
				}
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> fallbackTo(Promise<? extends T> fallback) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		onResolve(chained.new FallbackTo(this, fallback));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#fallbackTo(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class FallbackTo implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Promise<? extends T>	fallback;

		FallbackTo(PromiseImpl<T> promise, Promise< ? extends T> fallback) {
			this.promise = promise;
			this.fallback = requireNonNull(fallback);
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			if (result.fail != null) {
				fallback.onResolve(new FallbackChain(fallback, result.fail));
				return;
			}
			tryResolve(result.value, null);
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the fallback Promise
	 * is resolved.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private final class FallbackChain implements Runnable {
		private final Promise< ? extends T>	fallback;
		private final Throwable failure;
	
		FallbackChain(Promise< ? extends T> fallback, Throwable failure) {
			this.fallback = fallback;
			this.failure = failure;
		}

		@Override
		public void run() {
			Result<T> result = collect(fallback);
			if (result.fail != null) {
				result.fail = failure;
			}
			tryResolve(result.value, result.fail);
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
				return executors.scheduledExecutor().schedule(operation, delay,
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
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> timeout(long millis) {
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
		if (!isDone()) {
			PromiseImpl<T> timedout = new PromiseImpl<>(null,
					new TimeoutException(), executors);
			onResolve(new Timeout(chained.new ChainImpl(timedout), millis,
					TimeUnit.MILLISECONDS));
		}
		onResolve(chained.new ChainImpl(this));
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
		PromiseImpl<T> chained = new PromiseImpl<>(executors);
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
		Throwable	fail;
		P			value;
	
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
	Result<T> collect() {
		if (!isDone()) { // ensure latch open before reading state
			return new Result<T>(new AssertionError("promise not resolved"));
		}
		if (fail == null) {
			return new Result<T>(value);
		}
		return new Result<T>(fail);
	}

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

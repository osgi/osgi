/*
 * Copyright (c) OSGi Alliance (2014, 2018). All Rights Reserved.
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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.util.function.Consumer;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;

/**
 * Deferred Promise implementation.
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link PromiseFactory#deferred()} to create a {@link Deferred} which can be
 * used to obtain a Promise whose resolution can be deferred.
 * 
 * @param <T> The result type associated with the Promise.
 * @since 1.1
 * @ThreadSafe
 * @author $Id$
 */
final class DeferredPromiseImpl<T> extends PromiseImpl<T> {
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
	private final CountDownLatch	resolved;

	/**
	 * The value of this Promise if successfully resolved.
	 * 
	 * @see resolved
	 */
	// @GuardedBy("resolved")
	private T						value;

	/**
	 * The failure of this Promise if resolved with a failure or {@code null} if
	 * successfully resolved.
	 * 
	 * @see resolved
	 */
	// @GuardedBy("resolved")
	private Throwable				fail;

	/**
	 * Initialize this Promise.
	 * 
	 * @param factory The factory to use for callbacks and scheduled operations.
	 */
	DeferredPromiseImpl(PromiseFactory factory) {
		super(factory);
		resolved = new CountDownLatch(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDone() {
		return resolved.getCount() == 0;
	}

	/**
	 * Return a resolved PromiseImpl if this DeferredPromiseImpl is resolved.
	 * 
	 * @return A ResolvedPromiseImpl holding the value of this
	 *         DeferredPromiseImpl or a FailedPromiseImpl holding the failure of
	 *         this DeferredPromiseImpl or this DeferredPromiseImpl if this
	 *         DeferredPromiseImpl is not resolved.
	 */
	PromiseImpl<T> orDone() {
		// ensure latch open before reading state
		if (!isDone()) {
			return this;
		}
		if (fail == null) {
			return resolved(value);
		}
		return failed(fail);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getValue() throws InvocationTargetException, InterruptedException {
		// ensure latch open before reading state
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
		// ensure latch open before reading state
		resolved.await();
		return fail;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Result<T> collect() {
		// ensure latch open before reading state
		if (!isDone()) {
			return new Result<T>(new AssertionError("promise not resolved"));
		}
		if (fail == null) {
			return new Result<T>(value);
		}
		return new Result<T>(fail);
	}

	@Override
	public String toString() {
		// ensure latch open before reading state
		if (!isDone()) {
			return super.toString() + "[unresolved]";
		}
		if (fail == null) {
			return super.toString() + "[resolved: " + value + "]";
		}
		return super.toString() + "[failed: " + fail + "]";
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
	 */
	boolean tryResolve(T v, Throwable f) {
		// critical section: only one resolver at a time
		synchronized (resolved) {
			if (isDone()) {
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
	Promise<Void> resolveWith(Promise< ? extends T> with) {
		DeferredPromiseImpl<Void> chained = deferred();
		with.onResolve(chained.new ResolveWith<>(with, this));
		return chained.orDone();
	}

	/**
	 * A callback used to resolve a Promise with another Promise for the
	 * {@link #resolveWith(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class ResolveWith<P> implements Runnable {
		private final Promise< ? extends P>		promise;
		private final DeferredPromiseImpl<P>	target;

		ResolveWith(Promise< ? extends P> promise,
				DeferredPromiseImpl<P> target) {
			this.promise = requireNonNull(promise);
			this.target = requireNonNull(target);
		}

		@Override
		public void run() {
			Throwable f = null;
			Result<P> result = collect(promise);
			try {
				target.resolve(result.value, result.fail);
			} catch (Throwable e) {
				f = e; // propagate new exception
			}
			tryResolve(null, f);
		}
	}

	/**
	 * A callback used to chain promises for the
	 * {@link PromiseImpl#then(Success, Failure)} method.
	 * 
	 * @Immutable
	 */
	final class Then<P> implements Runnable {
		private final PromiseImpl<P>			promise;
		private final Success<P, ? extends T>	success;
		private final Failure					failure;

		@SuppressWarnings("unchecked")
		Then(PromiseImpl<P> promise, Success< ? super P, ? extends T> success,
				Failure failure) {
			this.promise = requireNonNull(promise);
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
			this.promise = requireNonNull(promise);
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
	 */
	private final class ChainImpl implements Runnable {
		private final PromiseImpl<T> promise;

		ChainImpl(PromiseImpl<T> promise) {
			this.promise = requireNonNull(promise);
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * A callback used by the {@link PromiseImpl#thenAccept(Consumer)} method.
	 * 
	 * @Immutable
	 */
	final class ThenAccept implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Consumer< ? super T>	consumer;

		ThenAccept(PromiseImpl<T> promise, Consumer< ? super T> consumer) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#filter(Predicate)} method.
	 * 
	 * @Immutable
	 */
	final class Filter implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Predicate< ? super T>	predicate;

		Filter(PromiseImpl<T> promise, Predicate< ? super T> predicate) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#map(Function)} method.
	 * 
	 * @Immutable
	 */
	final class Map<P> implements Runnable {
		private final PromiseImpl<P>					promise;
		private final Function< ? super P, ? extends T>	mapper;

		Map(PromiseImpl<P> promise, Function< ? super P, ? extends T> mapper) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#flatMap(Function)} method.
	 * 
	 * @Immutable
	 */
	final class FlatMap<P> implements Runnable {
		private final PromiseImpl<P>								promise;
		private final Function< ? super P,Promise< ? extends T>>	mapper;

		FlatMap(PromiseImpl<P> promise,
				Function< ? super P,Promise< ? extends T>> mapper) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#recover(Function)} method.
	 * 
	 * @Immutable
	 */
	final class Recover implements Runnable {
		private final PromiseImpl<T>						promise;
		private final Function<Promise< ? >, ? extends T>	recovery;

		Recover(PromiseImpl<T> promise,
				Function<Promise< ? >, ? extends T> recovery) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#recoverWith(Function)} method.
	 * 
	 * @Immutable
	 */
	final class RecoverWith implements Runnable {
		private final PromiseImpl<T>								promise;
		private final Function<Promise< ? >,Promise< ? extends T>>	recovery;

		RecoverWith(PromiseImpl<T> promise,
				Function<Promise< ? >,Promise< ? extends T>> recovery) {
			this.promise = requireNonNull(promise);
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
	 * A callback used by the {@link PromiseImpl#fallbackTo(Promise)} method.
	 * 
	 * @Immutable
	 */
	final class FallbackTo implements Runnable {
		private final PromiseImpl<T>		promise;
		private final Promise< ? extends T>	fallback;

		FallbackTo(PromiseImpl<T> promise, Promise< ? extends T> fallback) {
			this.promise = requireNonNull(promise);
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
	 */
	private final class FallbackChain implements Runnable {
		private final Promise< ? extends T>	fallback;
		private final Throwable				failure;

		FallbackChain(Promise< ? extends T> fallback, Throwable failure) {
			this.fallback = requireNonNull(fallback);
			this.failure = requireNonNull(failure);
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
	 * A callback used by the {@link PromiseImpl#timeout(long)} method to
	 * schedule the timeout and to resolve the chained Promise and cancel the
	 * timeout.
	 * 
	 * @Immutable
	 */
	final class Timeout implements Runnable {
		private final PromiseImpl<T>		promise;
		private final ScheduledFuture< ? > future;

		Timeout(PromiseImpl<T> promise, long millis) {
			this.promise = requireNonNull(promise);
			if (promise.isDone()) {
				this.future = null;
			} else {
				FailedPromiseImpl<T> timedout = failed(new TimeoutException());
				Runnable operation = new ChainImpl(timedout);
				this.future = schedule(operation, millis, TimeUnit.MILLISECONDS);
			}
		}

		@Override
		public void run() {
			Result<T> result = promise.collect();
			tryResolve(result.value, result.fail);
			if (future != null) {
				future.cancel(false);
			}
		}
	}

	/**
	 * A callback used by the {@link PromiseImpl#delay(long)} method to delay
	 * chaining a promise.
	 * 
	 * @Immutable
	 */
	final class Delay implements Runnable {
		private final Runnable	operation;
		private final long		millis;

		Delay(PromiseImpl<T> promise, long millis) {
			this.operation = new ChainImpl(promise);
			this.millis = millis;
		}

		@Override
		public void run() {
			schedule(operation, millis, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * A callback used by the {@link PromiseFactory#submit(Callable)} method.
	 * 
	 * @Immutable
	 */
	final class Submit implements Runnable {
		private final Callable< ? extends T> task;

		Submit(Callable< ? extends T> task) {
			this.task = requireNonNull(task);
		}

		@Override
		public void run() {
			try {
				tryResolve(task.call(), null);
			} catch (Throwable t) {
				tryResolve(null, t);
			}
		}
	}
}

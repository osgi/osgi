/*
 * Copyright (c) OSGi Alliance (2014, 2016). All Rights Reserved.
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
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.util.function.Callback;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;

/**
 * Promise implementation.
 * 
 * <p>
 * This class is not used directly by clients. Clients should use
 * {@link Deferred} to create a resolvable {@link Promise}.
 * 
 * @param <T> The result type associated with the Promise.
 * 
 * @ThreadSafe
 * @author $Id$
 */
final class PromiseImpl<T> implements Promise<T> {
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
	 */
	PromiseImpl() {
		callbacks = new ConcurrentLinkedQueue<>();
		resolved = new CountDownLatch(1);
	}

	/**
	 * Initialize and resolve this Promise.
	 * 
	 * @param v The value of this resolved Promise.
	 * @param f The failure of this resolved Promise.
	 */
	PromiseImpl(T v, Throwable f) {
		if (f == null) {
			value = v;
		} else {
			fail = f;
		}
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
			Callbacks.execute(callback);
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
		if (!isDone()) {
			return super.toString() + "[unresolved]";
		}
		final boolean interrupted = Thread.interrupted();
		try {
			Throwable t = getFailure();
			if (t != null) {
				return super.toString() + "[failed: " + t + "]";
			}
			return super.toString() + "[resolved: " + getValue() + "]";
		} catch (InterruptedException | InvocationTargetException e) {
			return super.toString() + "[" + e + "]";
		} finally {
			if (interrupted) { // restore interrupt status
				Thread.currentThread().interrupt();
			}
		}
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
	 * {@inheritDoc}
	 */
	@Override
	public <R> Promise<R> then(Success<? super T, ? extends R> success, Failure failure) {
		PromiseImpl<R> chained = new PromiseImpl<>();
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
		private final Promise<P>				promise;
		private final Success<P, ? extends T>	success;
		private final Failure					failure;

		@SuppressWarnings("unchecked")
		Then(Promise<P> promise, Success< ? super P, ? extends T> success,
				Failure failure) {
			this.promise = promise;
			this.success = (Success<P, ? extends T>) success;
			this.failure = failure;
		}

		@Override
		public void run() {
			Throwable f = Result.collect(promise).fail;
			if (f != null) {
				if (failure != null) {
					try {
						failure.fail(promise);
					} catch (Throwable e) {
						f = e; // propagate new exception
					}
				}
			} else if (success != null) {
				Promise< ? extends T> returned = null;
				try {
					returned = success.call(promise);
				} catch (Throwable e) {
					f = e; // propagate new exception
				}
				if (returned != null) {
					// resolve chained when returned promise is resolved
					returned.onResolve(new Chain(returned));
					return;
				}
			}
			tryResolve(null, f);
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the Promise promise
	 * is resolved.
	 * 
	 * @Immutable
	 */
	private final class Chain implements Runnable {
		private final Promise< ? extends T>	promise;
		private final Throwable				failure;
		private final Callback				callback;

		Chain(Promise< ? extends T> promise) {
			this.promise = promise;
			this.failure = null;
			this.callback = null;
		}

		Chain(Promise< ? extends T> promise, Throwable failure) {
			this.promise = promise;
			this.failure = requireNonNull(failure);
			this.callback = null;
		}

		Chain(Promise< ? extends T> promise, Callback callback) {
			this.promise = promise;
			this.failure = null;
			this.callback = requireNonNull(callback);
		}

		@Override
		public void run() {
			if (callback != null) {
				try {
					callback.run();
				} catch (Throwable e) {
					tryResolve(null, e);
					return;
				}
			}
			Result<T> result = Result.collect(promise);
			if ((result.fail != null) && (failure != null)) {
				result.fail = failure;
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> then(Callback callback) {
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(chained.new Chain(this, callback));
		return chained;
	}

	/**
	 * Resolve this Promise with the specified Promise.
	 * 
	 * <p>
	 * If the specified Promise is successfully resolved, this Promise is
	 * resolved with the value of the specified Promise. If the specified
	 * Promise is resolved with a failure, this Promise is resolved with the
	 * failure of the specified Promise.
	 * 
	 * @param with A Promise whose value or failure must be used to resolve this
	 *        Promise. Must not be {@code null}.
	 * @return A Promise that is resolved only when this Promise is resolved by
	 *         the specified Promise. The returned Promise must be successfully
	 *         resolved with the value {@code null}, if this Promise was
	 *         resolved by the specified Promise. The returned Promise must be
	 *         resolved with a failure of {@link IllegalStateException}, if this
	 *         Promise was already resolved when the specified Promise was
	 *         resolved.
	 */
	Promise<Void> resolveWith(Promise<? extends T> with) {
		PromiseImpl<Void> chained = new PromiseImpl<>();
		with.onResolve(new ResolveWith(with, chained));
		return chained;
	}

	/**
	 * A callback used to resolve this Promise with another Promise for the
	 * {@link PromiseImpl#resolveWith(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class ResolveWith implements Runnable {
		private final Promise< ? extends T>	promise;
		private final PromiseImpl<Void>	chained;

		ResolveWith(Promise< ? extends T> promise, PromiseImpl<Void> chained) {
			this.promise = promise;
			this.chained = chained;
		}

		@Override
		public void run() {
			Throwable f = null;
			Result<T> result = Result.collect(promise);
			try {
				resolve(result.value, result.fail);
			} catch (Throwable e) {
				f = e; // propagate new exception
			}
			chained.tryResolve(null, f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Promise<T> filter(Predicate<? super T> predicate) {
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(chained.new Filter(this, predicate));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#filter(Predicate)} method.
	 * 
	 * @Immutable
	 */
	private final class Filter implements Runnable {
		private final Promise< ? extends T>	promise;
		private final Predicate<? super T>	predicate;

		Filter(Promise< ? extends T> promise, Predicate< ? super T> predicate) {
			this.promise = promise;
			this.predicate = requireNonNull(predicate);
		}

		@Override
		public void run() {
			Result<T> result = Result.collect(promise);
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
		PromiseImpl<R> chained = new PromiseImpl<>();
		onResolve(chained.new Map<>(this, mapper));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#map(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class Map<P> implements Runnable {
		private final Promise< ? extends P>				promise;
		private final Function<? super P, ? extends T>	mapper;

		Map(Promise< ? extends P> promise,
				Function< ? super P, ? extends T> mapper) {
			this.promise = promise;
			this.mapper = requireNonNull(mapper);
		}

		@Override
		public void run() {
			Result<P> result = Result.collect(promise);
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
		PromiseImpl<R> chained = new PromiseImpl<>();
		onResolve(chained.new FlatMap<>(this, mapper));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#flatMap(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class FlatMap<P> implements Runnable {
		private final Promise< ? extends P>							promise;
		private final Function< ? super P,Promise< ? extends T>>	mapper;

		FlatMap(Promise< ? extends P> promise,
				Function< ? super P,Promise< ? extends T>> mapper) {
			this.promise = promise;
			this.mapper = requireNonNull(mapper);
		}

		@Override
		public void run() {
			Result<P> result = Result.collect(promise);
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
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(chained.new Recover(this, recovery));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recover(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class Recover implements Runnable {
		private final Promise<T>							promise;
		private final Function<Promise< ? >, ? extends T>	recovery;

		Recover(Promise<T> promise,
				Function<Promise< ? >, ? extends T> recovery) {
			this.promise = promise;
			this.recovery = requireNonNull(recovery);
		}

		@Override
		public void run() {
			Result<T> result = Result.collect(promise);
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
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(chained.new RecoverWith(this, recovery));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recoverWith(Function)} method.
	 * 
	 * @Immutable
	 */
	private final class RecoverWith implements Runnable {
		private final Promise<T>									promise;
		private final Function<Promise<?>, Promise<? extends T>>	recovery;

		RecoverWith(Promise<T> promise,
				Function<Promise< ? >,Promise< ? extends T>> recovery) {
			this.promise = promise;
			this.recovery = requireNonNull(recovery);
		}

		@Override
		public void run() {
			Result<T> result = Result.collect(promise);
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
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(chained.new FallbackTo(this, fallback));
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#fallbackTo(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class FallbackTo implements Runnable {
		private final Promise<T>			promise;
		private final Promise<? extends T>	fallback;

		FallbackTo(Promise<T> promise, Promise< ? extends T> fallback) {
			this.promise = promise;
			this.fallback = requireNonNull(fallback);
		}

		@Override
		public void run() {
			Result<T> result = Result.collect(promise);
			if (result.fail != null) {
				fallback.onResolve(new Chain(fallback, result.fail));
				return;
			}
			tryResolve(result.value, result.fail);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> timeout(long millis) {
		PromiseImpl<T> chained = new PromiseImpl<>();
		if (!isDone()) {
			onResolve(chained.new Timeout(millis, TimeUnit.MILLISECONDS));
		}
		onResolve(chained.new Chain(this));
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

		Timeout(long timeout, TimeUnit unit) {
			future = Callbacks.schedule(new TimeoutAction(), timeout, unit);
		}

		@Override
		public void run() {
			if (future != null) {
				future.cancel(false);
			}
		}
	}
	
	/**
	 * Callback used to fail the Promise if the timeout expires.
	 * 
	 * @Immutable
	 */
	private final class TimeoutAction implements Runnable {
		TimeoutAction() {}
		
		@Override
		public void run() {
			tryResolve(null, new TimeoutException());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public Promise<T> delay(long millis) {
		PromiseImpl<T> chained = new PromiseImpl<>();
		onResolve(new Delay(chained.new Chain(this), millis,
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
	private static final class Delay implements Runnable {
		private final Runnable	callback;
		private final long		delay;
		private final TimeUnit	unit;

		Delay(Runnable callback, long delay, TimeUnit unit) {
			this.callback = callback;
			this.delay = delay;
			this.unit = unit;
		}

		@Override
		public void run() {
			Callbacks.schedule(callback, delay, unit);
		}
	}

	/**
	 * Callback handler used to asynchronously execute callbacks.
	 * 
	 * @Immutable
	 * @since 1.1
	 */
	private static final class Callbacks
			implements ThreadFactory, RejectedExecutionHandler, Runnable {
		private static final Callbacks			callbacks;
		private static final ScheduledExecutor	scheduledExecutor;
		private static final ThreadPoolExecutor	callbackExecutor;
		static {
			callbacks = new Callbacks();
			scheduledExecutor = new ScheduledExecutor(2, callbacks);
			callbackExecutor = new ThreadPoolExecutor(0, 64, 60L,
					TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
					callbacks, callbacks);
		}

		/**
		 * Schedule a callback on the scheduled executor
		 */
		static ScheduledFuture< ? > schedule(Runnable callback, long delay,
				TimeUnit unit) {
			try {
				return scheduledExecutor.schedule(callback, delay, unit);
			} catch (RejectedExecutionException e) {
				callbacks.rejectedExecution(callback, scheduledExecutor);
				return null;
			}
		}

		/**
		 * Execute a callback on the callback executor
		 */
		static void execute(Runnable callback) {
			callbackExecutor.execute(callback);
		}

		static void uncaughtException(Throwable t) {
			try {
				Thread thread = Thread.currentThread();
				thread.getUncaughtExceptionHandler().uncaughtException(thread,
						t);
			} catch (Throwable ignored) {
				// we ignore this
			}
		}

		private final AtomicBoolean	shutdownHookInstalled;
		private final ThreadFactory	delegateThreadFactory;

		private Callbacks() {
			shutdownHookInstalled = new AtomicBoolean();
			delegateThreadFactory = Executors.defaultThreadFactory();
		}

		/**
		 * Executor threads should not prevent VM from exiting
		 */
		@Override
		public Thread newThread(Runnable r) {
			if (shutdownHookInstalled.compareAndSet(false, true)) {
				Thread shutdownThread = delegateThreadFactory.newThread(this);
				shutdownThread.setName("ExecutorShutdownHook,"
						+ shutdownThread.getName());
				try {
					Runtime.getRuntime().addShutdownHook(shutdownThread);
				} catch (IllegalStateException e) {
					// VM is already shutting down...
					callbackExecutor.shutdown();
					scheduledExecutor.shutdown();
				}
			}
			Thread t = delegateThreadFactory.newThread(r);
			t.setName("PromiseImpl," + t.getName());
			t.setDaemon(true);
			return t;
		}

		/**
		 * Call the callback using the caller's thread because the thread pool
		 * rejected the execution.
		 */
		@Override
		public void rejectedExecution(Runnable callback,
				ThreadPoolExecutor executor) {
			try {
				callback.run();
			} catch (Throwable t) {
				uncaughtException(t);
			}
		}

		/**
		 * Shutdown hook
		 */
		@Override
		public void run() {
			// limit new thread creation
			callbackExecutor.setMaximumPoolSize(
					Math.max(1, callbackExecutor.getPoolSize()));
			// Run all delayed callbacks now
			scheduledExecutor.shutdown();
			BlockingQueue<Runnable> queue = scheduledExecutor.getQueue();
			if (!queue.isEmpty()) {
				for (Object r : queue.toArray()) {
					if (r instanceof RunnableScheduledFuture< ? >) {
						RunnableScheduledFuture< ? > future = (RunnableScheduledFuture< ? >) r;
						if ((future.getDelay(TimeUnit.NANOSECONDS) > 0L)
								&& queue.remove(future)) {
							future.run();
							scheduledExecutor.afterExecute(future, null);
						}
					}
				}
				scheduledExecutor.shutdown();
			}
			try {
				scheduledExecutor.awaitTermination(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			// Shutdown callback executor
			callbackExecutor.shutdown();
			try {
				callbackExecutor.awaitTermination(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		/**
		 * ScheduledThreadPoolExecutor for scheduled execution.
		 * 
		 * @ThreadSafe
		 */
		private static final class ScheduledExecutor
				extends ScheduledThreadPoolExecutor {
			ScheduledExecutor(int corePoolSize, ThreadFactory threadFactory) {
				super(corePoolSize, threadFactory);
			}

			/**
			 * Handle uncaught exceptions
			 */
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				if ((t == null) && (r instanceof Future< ? >)) {
					boolean interrupted = Thread.interrupted();
					try {
						((Future< ? >) r).get();
					} catch (CancellationException e) {
						// ignore
					} catch (InterruptedException e) {
						interrupted = true;
					} catch (ExecutionException e) {
						t = e.getCause();
					} finally {
						if (interrupted) { // restore interrupt status
							Thread.currentThread().interrupt();
						}
					}
				}
				if (t != null) {
					uncaughtException(t);
				}
			}
		}
	}

	/**
	 * A holder of the result of a promise.
	 * 
	 * @NotThreadSafe
	 */
	static final class Result<P> {
		Throwable	fail;
		P			value;
	
		Result() {}
	
		static <R> Result<R> collect(Promise< ? extends R> promise) {
			Result<R> result = new Result<>();
			final boolean interrupted = Thread.interrupted();
			try {
				result.fail = promise.getFailure();
				if (result.fail == null) {
					result.value = promise.getValue();
				}
			} catch (Throwable e) {
				result.fail = e; // propagate new exception
			} finally {
				if (interrupted) { // restore interrupt status
					Thread.currentThread().interrupt();
				}
			}
			return result;
		}
	}

	static <V> V requireNonNull(V value) {
		if (value != null) {
			return value;
		}
		throw new NullPointerException();
	}
}

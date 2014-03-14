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

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
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
	 * 
	 * <p>
	 * This object is used as the synchronizing object to provide a critical
	 * section in {@link #resolve(Object, Throwable)} so that only a single
	 * thread can write the resolved state variables and open the latch.
	 * 
	 * <p>
	 * The resolved state variables, {@link #value} and {@link #fail}, must
	 * only be written when the latch is closed (getCount() != 0) and must only
	 * be read when the latch is open (getCount() == 0). The latch state must
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
		callbacks = new ConcurrentLinkedQueue<Runnable>();
		resolved = new CountDownLatch(1);
	}

	/**
	 * Initialize and resolve this Promise.
	 * 
	 * @param v The value of this resolved Promise.
	 * @param f The failure of this resolved Promise.
	 */
	PromiseImpl(T v, Throwable f) {
		value = v;
		fail = f;
		callbacks = new ConcurrentLinkedQueue<Runnable>();
		resolved = new CountDownLatch(0);
	}

	/**
	 * Resolve this Promise.
	 * 
	 * @param v The value of this Promise.
	 * @param f The failure of this Promise.
	 */
	void resolve(T v, Throwable f) {
		// critical section: only one resolver at a time
		synchronized (resolved) {
			if (resolved.getCount() == 0) {
				throw new IllegalStateException("Already resolved");
			}
			/*
			 * The resolved state variables must be set before opening the
			 * latch. This safely publishes them to be read by other threads
			 * that must verify the latch is open before reading.
			 */
			value = v;
			fail = f;
			resolved.countDown();
		}
		notifyCallbacks(); // call any registered callbacks
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
		 * the queue and calling them, so the order in which callbacks are
		 * called cannot be specified.
		 */
		for (Runnable callback = callbacks.poll(); callback != null; callback = callbacks.poll()) {
			try {
				callback.run();
			} catch (Throwable t) {
				Logger.logCallbackException(t);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDone() {
		return resolved.getCount() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getValue() throws InvocationTargetException, InterruptedException {
		resolved.await();
		if (fail != null) {
			throw new InvocationTargetException(fail);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Throwable getFailure() throws InterruptedException {
		resolved.await();
		return fail;
	}

	/**
	 * {@inheritDoc}
	 */
	public void onResolve(Runnable callback) {
		callbacks.offer(callback);
		notifyCallbacks(); // call any registered callbacks
	}

	/**
	 * {@inheritDoc}
	 */
	public <R, S extends R> Promise<R> then(Success<? super T, S> success, Failure failure) {
		PromiseImpl<R> chained = new PromiseImpl<R>();
		onResolve(new Then<R, S>(chained, success, failure));
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	public <R, S extends R> Promise<R> then(Success<? super T, S> success) {
		return then(success, null);
	}

	/**
	 * A callback used to chain promises for the {@link #then(Success, Failure)}
	 * method.
	 * 
	 * @Immutable
	 */
	private final class Then<R, S extends R> implements Runnable {
		private final PromiseImpl<R>	chained;
		private final Success<T, S>		success;
		private final Failure			failure;

		@SuppressWarnings("unchecked")
		Then(PromiseImpl<R> chained, Success<? super T, S> success, Failure failure) {
			this.chained = chained;
			this.success = (Success<T, S>) success;
			this.failure = failure;
		}

		public void run() {
			final boolean interrupted = Thread.interrupted();
			try {
				Throwable f = null;
				try {
					f = getFailure();
				} catch (InterruptedException e) {
					/*
					 * This should not happen since (1) we are a callback on a
					 * resolved Promise and (2) we cleared the interrupt status
					 * above.
					 */
					throw new Error(e);
				}
				if (f != null) {
					if (failure != null) {
						try {
							failure.fail(PromiseImpl.this);
						} catch (Throwable e) {
							// propagate new exception
							f = e;
						}
					}
					// fail chained
					chained.resolve(null, f);
					return;
				}
				Promise<S> returned = null;
				if (success != null) {
					try {
						returned = success.call(PromiseImpl.this);
					} catch (Throwable e) {
						chained.resolve(null, e);
						return;
					}
				}
				if (returned == null) {
					// resolve chained with null value
					chained.resolve(null, null);
				} else {
					// resolve chained after promise promise is resolved
					returned.onResolve(new Chain<R, S>(chained, returned));
				}
			} finally {
				if (interrupted) { // restore interrupt status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the Promise promise
	 * is resolved.
	 * 
	 * @Immutable
	 */
	private final static class Chain<R, S extends R> implements Runnable {
		private final PromiseImpl<R>	chained;
		private final Promise<S>		promise;

		Chain(PromiseImpl<R> chained, Promise<S> promise) {
			this.chained = chained;
			this.promise = promise;
		}

		public void run() {
			final boolean interrupted = Thread.interrupted();
			try {
				Throwable f = null;
				try {
					f = promise.getFailure();
				} catch (InterruptedException e) {
					/*
					 * This should not happen since (1) we are a callback on a
					 * resolved Promise and (2) we cleared the interrupt status
					 * above.
					 */
					throw new Error(e);
				}
				if (f != null) {
					chained.resolve(null, f);
					return;
				}
				S value;
				try {
					value = promise.getValue();
				} catch (InvocationTargetException e) {
					// This should not happen since we checked fail above
					throw new Error(e);
				} catch (InterruptedException e) {
					/*
					 * This should not happen since (1) we are a callback on a
					 * resolved Promise and (2) we cleared the interrupt status
					 * above.
					 */
					throw new Error(e);
				}
				chained.resolve(value, null);
			} finally {
				if (interrupted) { // restore interrupt status
					Thread.currentThread().interrupt();
				}
			}
		}
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
	 * @param with A Promise whose value or failure will be used to resolve this
	 *        Promise.
	 * @return A Promise that is resolved only when this Promise is resolved by
	 *         the specified Promise. The returned Promise will be successfully
	 *         resolved, with the value {@code null}, if this Promise was
	 *         resolved by the specified Promise. The returned Promise will be
	 *         resolved with a failure of {@link IllegalStateException} if this
	 *         Promise was already resolved when the specified Promise was
	 *         resolved.
	 */
	Promise<Void> resolveWith(Promise<? extends T> with) {
		PromiseImpl<Void> chained = new PromiseImpl<Void>();
		ResolveWith resolveWith = new ResolveWith(chained);
		with.then(resolveWith, resolveWith);
		return chained;
	}

	/**
	 * A callback used to resolve this Promise with another Promise for the
	 * {@link PromiseImpl#resolveWith(Promise)} method.
	 * 
	 * @Immutable
	 */
	private final class ResolveWith implements Success<T, Void>, Failure {
		private final PromiseImpl<Void>	chained;

		ResolveWith(PromiseImpl<Void> chained) {
			this.chained = chained;
		}

		public Promise<Void> call(Promise<T> with) throws Exception {
			try {
				resolve(with.getValue(), null);
			} catch (Throwable e) {
				chained.resolve(null, e);
				return null;
			}
			chained.resolve(null, null);
			return null;
		}

		public void fail(Promise<?> with) throws Exception {
			try {
				resolve(null, with.getFailure());
			} catch (Throwable e) {
				chained.resolve(null, e);
				return;
			}
			chained.resolve(null, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Promise<T> filter(Predicate<? super T> predicate) {
		return then(new Filter<T>(predicate));
	}

	/**
	 * A callback used by the {@link PromiseImpl#filter(Predicate)} method.
	 * 
	 * @Immutable
	 */
	private static final class Filter<T> implements Success<T, T> {
		private final Predicate<? super T>	predicate;

		Filter(Predicate<? super T> predicate) {
			this.predicate = predicate;
		}

		public Promise<T> call(Promise<T> resolved) throws Exception {
			if (predicate.test(resolved.getValue())) {
				return resolved;
			}
			throw new NoSuchElementException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <R, S extends R> Promise<R> map(Function<? super T, S> mapper) {
		return then(new Map<T, S>(mapper));
	}

	/**
	 * A callback used by the {@link PromiseImpl#map(Function)} method.
	 * 
	 * @Immutable
	 */
	private static final class Map<T, S> implements Success<T, S> {
		private final Function<? super T, S>	mapper;

		Map(Function<? super T, S> mapper) {
			this.mapper = mapper;
		}

		public Promise<S> call(Promise<T> resolved) throws Exception {
			return new PromiseImpl<S>(mapper.apply(resolved.getValue()), null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <R, S extends R> Promise<R> flatMap(Function<? super T, Promise<S>> mapper) {
		return then(new FlatMap<T, S>(mapper));
	}

	/**
	 * A callback used by the {@link PromiseImpl#flatMap(Function)} method.
	 * 
	 * @Immutable
	 */
	private static final class FlatMap<T, S> implements Success<T, S> {
		private final Function<? super T, Promise<S>>	mapper;

		FlatMap(Function<? super T, Promise<S>> mapper) {
			this.mapper = mapper;
		}

		public Promise<S> call(Promise<T> resolved) throws Exception {
			return mapper.apply(resolved.getValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <S extends T> Promise<T> recover(Function<Promise<?>, S> recovery) {
		PromiseImpl<T> chained = new PromiseImpl<T>();
		Recover<T, S> recover = new Recover<T, S>(chained, recovery);
		then(recover, recover);
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recover(Function)} method.
	 * 
	 * @Immutable
	 */
	private static final class Recover<T, S extends T> implements Success<T, Void>, Failure {
		private final PromiseImpl<T>					chained;
		private final Function<Promise<?>, S>	recovery;

		Recover(PromiseImpl<T> chained, Function<Promise<?>, S> recovery) {
			this.chained = chained;
			this.recovery = recovery;
		}

		public Promise<Void> call(Promise<T> resolved) throws Exception {
			chained.resolve(resolved.getValue(), null);
			return null;
		}

		public void fail(Promise<?> resolved) throws Exception {
			S recovered;
			try {
				recovered = recovery.apply(resolved);
			} catch (Throwable e) {
				chained.resolve(null, e);
				return;
			}
			if (recovered == null) {
				chained.resolve(null, resolved.getFailure());
			} else {
				chained.resolve(recovered, null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <S extends T> Promise<T> recoverWith(Function<Promise<?>, Promise<S>> recovery) {
		PromiseImpl<T> chained = new PromiseImpl<T>();
		RecoverWith<T, S> recoverWith = new RecoverWith<T, S>(chained, recovery);
		then(recoverWith, recoverWith);
		return chained;
	}

	/**
	 * A callback used by the {@link PromiseImpl#recoverWith(Function)} method.
	 * 
	 * @Immutable
	 */
	private static final class RecoverWith<T, S extends T> implements Success<T, Void>, Failure {
		private final PromiseImpl<T>								chained;
		private final Function<Promise<?>, Promise<S>>	recovery;

		RecoverWith(PromiseImpl<T> chained, Function<Promise<?>, Promise<S>> recovery) {
			this.chained = chained;
			this.recovery = recovery;
		}

		public Promise<Void> call(Promise<T> resolved) throws Exception {
			chained.resolve(resolved.getValue(), null);
			return null;
		}

		public void fail(Promise<?> resolved) throws Exception {
			Promise<S> recovered;
			try {
				recovered = recovery.apply(resolved);
			} catch (Throwable e) {
				chained.resolve(null, e);
				return;
			}
			if (recovered == null) {
				chained.resolve(null, resolved.getFailure());
			} else {
				recovered.onResolve(new Chain<T, S>(chained, recovered));
			}
		}
	}

	/**
	 * Use the lazy initialization holder class idiom to delay creating a Logger
	 * until we actually need it.
	 */
	private static final class Logger {
		private final static java.util.logging.Logger	LOGGER;
		static {
			LOGGER = java.util.logging.Logger.getLogger(PromiseImpl.class.getName());
		}
		static void logCallbackException(Throwable t) {
			LOGGER.log(java.util.logging.Level.WARNING, "Exception from Promise callback", t);
		}
	}
}

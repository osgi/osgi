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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

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
	 * The resolved state variables, {@link #value} and {@link #error}, must
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
	private Throwable								error;

	/**
	 * Initialize this Promise.
	 */
	PromiseImpl() {
		callbacks = new ConcurrentLinkedQueue<Runnable>();
		resolved = new CountDownLatch(1);
	}

	/**
	 * Initialize and resolve this Promise with the specified value.
	 * 
	 * @param v The value of this resolved Promise.
	 */
	PromiseImpl(T v) {
		value = v;
		error = null;
		callbacks = new ConcurrentLinkedQueue<Runnable>();
		resolved = new CountDownLatch(0);
	}

	/**
	 * Resolve this Promise.
	 * 
	 * @param v The value of this Promise.
	 * @param t The failure of this Promise.
	 */
	void resolve(T v, Throwable t) {
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
			error = t;
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
		if (error != null) {
			throw new InvocationTargetException(error);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Throwable getError() throws InterruptedException {
		resolved.await();
		return error;
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
	public <R> Promise<R> then(Success<? super T, R> success, Failure failure) {
		PromiseImpl<R> chained = new PromiseImpl<R>();
		@SuppressWarnings("unchecked")
		Runnable then = new Then<R>(chained, (Success<T, R>) success, failure);
		onResolve(then);
		return chained;
	}

	/**
	 * {@inheritDoc}
	 */
	public <R> Promise<R> then(Success<? super T, R> success) {
		return then(success, null);
	}

	/**
	 * A callback used to chain promises for the {@link #then(Success, Failure)}
	 * method.
	 * 
	 * @Immutable
	 */
	private final class Then<R> implements Runnable {
		private final PromiseImpl<R>	chained;
		private final Success<T, R>		success;
		private final Failure			failure;

		Then(PromiseImpl<R> chained, Success<T, R> success, Failure failure) {
			this.chained = chained;
			this.success = success;
			this.failure = failure;
		}

		public void run() {
			final boolean interrupted = Thread.interrupted();
			try {
				Throwable t = null;
				try {
					t = getError();
				} catch (InterruptedException e) {
					/*
					 * This can't happen since (1) we are a callback on a
					 * resolved Promise and (2) we cleared the interrupt status
					 * above.
					 */
					throw new Error(e);
				}
				if (t != null) {
					if (failure != null) {
						try {
							failure.fail(PromiseImpl.this);
						} catch (Throwable e) {
							// propagate new exception
							t = e;
						}
					}
					// fail chained
					chained.resolve(null, t);
					return;
				}
				Promise<R> returned = null;
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
					// resolve chained after returned promise is resolved
					returned.onResolve(new Chain<R>(chained, returned));
				}
			} finally {
				if (interrupted) { // restore interrupt status
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * A callback used to resolve the chained Promise when the Promise returned
	 * by the Success callback is resolved.
	 * 
	 * @Immutable
	 */
	private final static class Chain<R> implements Runnable {
		private final PromiseImpl<R>	chained;
		private final Promise<R>		returned;

		Chain(PromiseImpl<R> chained, Promise<R> returned) {
			this.chained = chained;
			this.returned = returned;
		}

		public void run() {
			final boolean interrupted = Thread.interrupted();
			try {
				Throwable t = null;
				try {
					t = returned.getError();
				} catch (InterruptedException e) {
					/*
					 * This can't happen since (1) we are a callback on a
					 * resolved Promise and (2) we cleared the interrupt status
					 * above.
					 */
					throw new Error(e);
				}
				if (t != null) {
					chained.resolve(null, t);
					return;
				}
				R value;
				try {
					value = returned.getValue();
				} catch (InvocationTargetException e) {
					// This can't happen since we checked error above
					throw new Error(e);
				} catch (InterruptedException e) {
					/*
					 * This can't happen since (1) we are a callback on a
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
	 * Use the lazy initialization holder class idiom to delay creating a Logger
	 * until we actually need it.
	 */
	private static class Logger {
		private final static java.util.logging.Logger	LOGGER;
		static {
			LOGGER = java.util.logging.Logger.getLogger(PromiseImpl.class.getName());
		}
		static void logCallbackException(Throwable t) {
			LOGGER.log(java.util.logging.Level.WARNING, "Exception from Promise callback", t);
		}
	}
}

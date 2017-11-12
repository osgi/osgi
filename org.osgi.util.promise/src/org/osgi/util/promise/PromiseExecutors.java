/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
import static org.osgi.util.promise.PromiseImpl.uncaughtException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * The executors for Promise callbacks and scheduled operations.
 * <p>
 * Instances of this class can be used to create a Deferred that can be resolved
 * in the future as well as resolved Promises. The returned Deferred and Promise
 * objects all use the executors used to construct this object for any callback
 * or scheduled operation execution.
 * 
 * @Immutable
 * @author $Id$
 * @since 1.1
 */
@ConsumerType
public class PromiseExecutors {
	/**
	 * The default executors.
	 */
	final static PromiseExecutors			defaultExecutors	= new PromiseExecutors(
			null, null);

	/**
	 * The executor to use for callbacks. If {@code null}, the default
	 * callback executor is used.
	 */
	private final Executor					callbackExecutor;
	/**
	 * The executor to use for scheduled operations. If {@code null}, the
	 * default scheduled executor is used.
	 */
	private final ScheduledExecutorService	scheduledExecutor;


	/**
	 * Create a new PromiseExecutors with the specified callback executor.
	 * <p>
	 * The default scheduled executor will be used.
	 * 
	 * @param callbackExecutor The executor to use for callbacks. {@code null}
	 *            can be specified for the default callback executor.
	 */
	public PromiseExecutors(Executor callbackExecutor) {
		this(callbackExecutor, null);
	}

	/**
	 * Create a new PromiseExecutors with the specified callback executor and
	 * specified scheduled executor.
	 * 
	 * @param callbackExecutor The executor to use for callbacks. {@code null}
	 *            can be specified for the default callback executor.
	 * @param scheduledExecutor The scheduled executor for use for scheduled
	 *            operations. {@code null} can be specified for the default
	 *            scheduled executor.
	 */
	public PromiseExecutors(Executor callbackExecutor,
			ScheduledExecutorService scheduledExecutor) {
		this.callbackExecutor = callbackExecutor;
		this.scheduledExecutor = scheduledExecutor;
	}

	/**
	 * Returns the executor to use for callbacks.
	 * 
	 * @return The executor to use for callbacks. This will be the default
	 *         callback executor if {@code null} was specified for the callback
	 *         executor when this PromiseExecutors was created.
	 */
	protected Executor executor() {
		if (callbackExecutor == null) {
			return DefaultExecutors.callbackExecutor();
		}
		return callbackExecutor;
	}

	/**
	 * Returns the executor to use for scheduled operations.
	 * 
	 * @return The executor to use for scheduled operations. This will be the
	 *         default scheduled executor if {@code null} was specified for the
	 *         scheduled executor when this PromiseExecutors was created.
	 */
	protected ScheduledExecutorService scheduledExecutor() {
		if (scheduledExecutor == null) {
			return DefaultExecutors.scheduledExecutor();
		}
		return scheduledExecutor;
	}

	/**
	 * Create a new Deferred with the callback executor and scheduled executor
	 * of this PromiseExecutors object.
	 * <p>
	 * Use this method instead of {@link Deferred#Deferred()} to create a new
	 * {@link Deferred} whose associated Promise uses executors other than the
	 * default executors.
	 * 
	 * @return A new {@link Deferred} with the callback and scheduled executors
	 *         of this PromiseExecutors object
	 */
	public <T> Deferred<T> deferred() {
		return new Deferred<>(this);
	}

	/**
	 * Returns a new Promise that has been resolved with the specified value.
	 * <p>
	 * The returned Promise uses the callback executor and scheduled executor of
	 * this PromiseExecutors object
	 * <p>
	 * Use this method instead of {@link Promises#resolved(Object)} to create a
	 * Promise which uses executors other than the default executors.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param value The value of the resolved Promise.
	 * @return A new Promise that has been resolved with the specified value.
	 */
	public <T> Promise<T> resolved(T value) {
		return new PromiseImpl<>(value, null, this);
	}

	/**
	 * Returns a new Promise that has been resolved with the specified failure.
	 * <p>
	 * The returned Promise uses the callback executor and scheduled executor of
	 * this PromiseExecutors object
	 * <p>
	 * Use this method instead of {@link Promises#failed(Throwable)} to create a
	 * Promise which uses executors other than the default executors.
	 * 
	 * @param <T> The value type associated with the returned Promise.
	 * @param failure The failure of the resolved Promise. Must not be
	 *            {@code null}.
	 * @return A new Promise that has been resolved with the specified failure.
	 */
	public <T> Promise<T> failed(Throwable failure) {
		return new PromiseImpl<>(null, requireNonNull(failure), this);
	}

	/**
	 * Returns a new Promise that will hold the result of the specified task.
	 * <p>
	 * The specified task will be executed on the {@link #executor() callback
	 * executor}.
	 * 
	 * @param task The task whose result will be available from the returned
	 *            Promise.
	 * @return A new Promise that will hold the result of the specified task.
	 */
	public <V> Promise<V> submit(final Callable< ? extends V> task) {
		requireNonNull(task);
		final Deferred<V> deferred = deferred();
		try {
			executor().execute(new Runnable() {
				@Override
				public void run() {
					try {
						deferred.resolve(task.call());
					} catch (Throwable t) {
						deferred.fail(t);
					}
				}
			});
		} catch (Exception t) {
			deferred.fail(t);
		}
		return deferred.getPromise();
	}

	/**
	 * Returns an Executor implementation that executes tasks immediately on the
	 * thread calling the {@code Executor.execute} method.
	 * 
	 * @return An Executor implementation that executes tasks immediately on the
	 *         thread calling the {@code Executor.execute} method.
	 */
	public static Executor inlineExecutor() {
		return new InlineExecutor();
	}

	/**
	 * An Executor implementation which executes the task immediately on the
	 * thread calling the {@code Executor.execute} method.
	 * 
	 * @Immutable
	 */
	private static final class InlineExecutor implements Executor {
		InlineExecutor() {}

		@Override
		public void execute(Runnable callback) {
			callback.run();
		}
	}

	/**
	 * Default executors for Promises.
	 * 
	 * @Immutable
	 */
	private static final class DefaultExecutors
			implements ThreadFactory, RejectedExecutionHandler, Runnable {
		private static final DefaultExecutors	callbacks;
		private static final ScheduledExecutor	scheduledExecutor;
		private static final ThreadPoolExecutor	callbackExecutor;
		static {
			callbacks = new DefaultExecutors();
			scheduledExecutor = new ScheduledExecutor(2, callbacks);
			callbackExecutor = new ThreadPoolExecutor(0, 64, 60L,
					TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
					callbacks, callbacks);
		}

		static Executor callbackExecutor() {
			return callbackExecutor;
		}

		static ScheduledExecutorService scheduledExecutor() {
			return scheduledExecutor;
		}

		private final AtomicBoolean	shutdownHookInstalled;
		private final ThreadFactory	delegateThreadFactory;

		private DefaultExecutors() {
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
				shutdownThread.setName(
						"ExecutorShutdownHook," + shutdownThread.getName());
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
}

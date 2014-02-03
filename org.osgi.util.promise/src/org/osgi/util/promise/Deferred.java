package org.osgi.util.promise;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A {@link Deferred} is the manager of a {@link Promise}. The Promise is used
 * by the clients but the code that actually does the async work must use the
 * {@link Deferred} to resolve the {@link Promise} with a value or an error.
 * 
 * @param <T>
 *            The associated value type
 */
public class Deferred<T> {
	Object lock = new Object();
	boolean resolved;
	boolean cancelled;
	T value;
	Throwable error;
	ConcurrentLinkedQueue<Runnable> onresolve = new ConcurrentLinkedQueue<Runnable>();

	/**
	 * Create the associated Promise with this resolver. Each resolver has only
	 * one promise.
	 */
	final Promise<T> promise = new Promise<T>() {

		/**
		 * Wait for the result and then return it.
		 */
		public T getValue() throws InvocationTargetException, InterruptedException {
			synchronized (lock) {
				while (!resolved) {
					System.out.println("wait for get");
					lock.wait();
				}

				System.out.println("get is ready = " + value);
				if (error != null)
					throw new InvocationTargetException(error);
				else
					return value;
			}
		}

		/**
		 * Answer if this is promise done.
		 */
		public boolean isDone() {
			synchronized (lock) {
				return resolved;
			}
		}

		/**
		 * The chain call. Success and fail will be called depending how this
		 * promise is resolved.
		 */
		public <R> Promise<R> then(final Success<R, ? super T> ok, final Failure<? super T> fail) {

			final Deferred<R> nextStage = new Deferred<R>();

			onResolve(new Runnable() {
				public void run() {
					stage(nextStage, ok, fail);
				}
			});
			dequeue();
			return nextStage.getPromise();
		}

		public <R> Promise<R> then(Success<R, ? super T> success) {
			return then(success, null);
		}

		/**
		 * Callback when this promise is resolved.
		 */
		public void onResolve(Runnable done) throws NullPointerException {
			if(done == null) throw new NullPointerException("No callback supplied");
			synchronized (lock) {
				onresolve.add(done);
			}
			dequeue();
		}

		/**
		 * Wait and return the error.
		 */
		public Throwable getError() throws InterruptedException {
			synchronized (lock) {
				while (!resolved)
					lock.wait();
			}
			return error;
		}

		public boolean isCancelled() {
			synchronized (lock) {
				return cancelled;
			}
		}

		public boolean cancel() {
			if(isDone()) {
				return isCancelled();
			}
			try {
				done(null, new CancelledPromiseException("Cancelled by the client"), true);
				return true;
			} catch (IllegalStateException ise) {
				//
				// This might indicate a race between cancellations, so check isCancelled
				// rather than just returning false
				//
				return isCancelled();
			}
		}

		/**
		 * Called when this promise is resolved either with an error or a value.
		 * We need to callback our associated success and failure callbacks and
		 * chain the results.
		 * 
		 * @param nextStage
		 *            The next stage to resolve
		 * @param ok
		 *            the success callback
		 * @param fail
		 *            the fail callback
		 */

		private <R> void stage(final Deferred<R> nextStage,
				final Success<R, ? super T> ok, final Failure<? super T> fail) {
			try {
				if (error == null)
					success(nextStage, ok);
				else
					fail(nextStage, fail);

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		/**
		 * If we fail then we fail our promise and the next stage.
		 * 
		 * @param nextStage
		 *            next stage to fail
		 * @param fail
		 *            the callback for this promise
		 * @throws Exception
		 */
		private <R> void fail(final Deferred<R> nextStage, final Failure<? super T> fail)
				throws Exception {
			if (fail != null)
				fail.fail(this);
			nextStage.fail(error);
		}

		/**
		 * If we succeed then we call the ok callback and use the returned
		 * promise to add a callback that when called will resolve the next
		 * stage.
		 * 
		 * @param nextStage
		 * @param ok
		 * @throws Exception
		 */
		private <R> void success(final Deferred<R> nextStage,
				final Success<R, ? super T> ok) throws Exception {
			try {
				final Promise<R> nextResult = ok != null ? ok.call(this) : null;
				if (nextResult == null) {

					//
					// We directly resolve it if we have no
					// promise
					//

					nextStage.resolve(null);

				} else {

					//
					// We have to wait for the next result
					// to become available so we register
					// a callback
					//

					nextResult.onResolve(new Runnable() {
						public void run() {
							try {
								R value = nextResult.getValue();
								nextStage.resolve(value);
							} catch (Exception e) {
								nextStage.fail(e);
							}
						}
					});
				}
			} catch (Exception e) {
				nextStage.fail(e);
			}
		}
	};

	/**
	 * Protected constructor for creating a directly resolved promise. This is used by
	 * {@link Promises#newResolvedPromise(Object)}
	 * 
	 * @param directValue
	 *            the direct value
	 */
	Deferred(T directValue) {
		this.value = directValue;
		this.resolved = true;
	}

	/**
	 * Create a new Deferred result that can be resolved using {@link #resolve(Object)}
	 */
	public Deferred() {
	}

	/**
	 * Return the promise associated with this resolver.
	 * 
	 * @return
	 */
	public Promise<T> getPromise() {
		return promise;
	}

	/**
	 * Resolve this promise with a value.
	 * 
	 * @param value
	 *            the value to resolve with.
	 */
	public void resolve(T value) {
		done(value, null, false);
	}

	/**
	 * Resolve the promise with a failure.
	 * 
	 * @param t
	 *            the failure
	 */
	public void fail(Throwable t) {
		done(null, t, false);
	}

	/**
	 * The private method that does the heavy lifting. It atomically sets the
	 * value/error and resolved state. It will also dequeue any callbacks.
	 * 
	 * @param value
	 * @param error
	 */
	private void done(T value, Throwable error, boolean cancelled) {
		System.out.println("resolved " + value + " " + error);
		synchronized (lock) {
			if (resolved)
				throw new IllegalStateException("Already resolved " + this);
			this.value = value;
			this.error = error;
			this.cancelled = cancelled;
			resolved = true;
			lock.notifyAll();
		}
		dequeue();
	}

	/**
	 * Dequeue any callbacks if resolved. The access to the resolved variable is
	 * not synchronized because it does not matter. This method is called after
	 * all the methods that change the list of callback and the resolve state.
	 * It is therefore impossible (in theory) to miss dequeueing
	 */
	private void dequeue() {
		if (!resolved)
			return;

		Runnable r;
		while ((r = onresolve.poll()) != null) {
			try {
				r.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

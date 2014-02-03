package org.osgi.util.promise;

import java.lang.reflect.InvocationTargetException;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * A Promise represents a future value, it handles the interaction to do
 * asynchronous processing. Promises can be created with a {@link Deferred}. 
 * The Promise is used by the caller of an asynchronous function to get the 
 * result or handle the errors. It can either get a callback when the Promise 
 * is resolved with a value or an error, or it can be used in chaining. 
 * In chaining callbacks are provided that receive the resolved promise, and
 * a new promise is generated that resolves when the callback resolves
 * <p>
 * Both onresolve and chaining (then) can be repeated any number of times, even
 * long after the value has been resolved.
 * <p>
 * Example onresolve usage:
 * 
 * <pre>
 * final Promise&lt;String&gt; foo = foo();
 * foo.onresolve(new Runnable() {
 * 	public void run() {
 * 		System.out.println(foo.get());
 * 	}
 * });
 * </pre>
 * 
 * Example chaining usage;
 * 
 * <pre>
 *      Success<String,String> doubler = new Success<>() {
 *      	public Promise{@code<String>} call(Promise{@code<String>} p) {
 *              return Deferred.getDirectPromise(p.get()+p.get());
 *          }
 *      };
 *  	final Promise<String> foo = foo().then(doubler).then(doubler);
 *      foo.( new Runnable() { public void run() {
 *      	 System.out.println( foo.get() );
 *      } });
 * </pre>
 * 
 * @param <T>
 *            The result type associated with this promise
 */
@ConsumerType
public interface Promise<T> {

	/**
	 * Chain promise calls so they are executed in sequence. This promise will
	 * call one of the given callbacks when it gets resolved with either a value
	 * or an error. 
	 * 
	 * <p>
	 * The return value from this method is a new promise that will get resolved 
	 * with an error if this promise is resolved with an error, or when the success
	 * callback throws an error. Otherwise the returned promise will get resolved 
	 * with the same object as the promise returned by the success callback.
	 * 
	 * <p>
	 * Note that callbacks may be called immediately on the thread that calls 
	 * {@link #then(Success)}, immediately on a different thread, or at some
	 * point in the future after this promise resolves.
	 * 
	 * <p>
	 * There is a defined "happens before" relationship in promise resolution
	 * which means that the promise is always resolved before the callback occurs,
	 * i.e. {@link #isDone()} will return <code>true</code> and {@link #getValue()}
	 * will not block.
	 * 
	 * @param success
	 *            Callback when this promise is successfully resolved. May be <code>null</code> if no
	 *            success behaviour is required.
	 * @param failure
	 *            Callback when this promise is resolved with an error. May be <code>null</code> if no
	 *            failure behaviour is required.
     *
	 * @return A new promise that will get resolved when the success method
	 *         successfully returns or when there is a failure
	 */
	<R> Promise<R> then(Success<R, ? super T> success, Failure<? super T> failure);

	/**
	 * Chain promise calls so they are executed in sequence. This promise will
	 * call the supplied callback when it gets resolved with a value.
	 * 
	 * <p>
	 * The return value from this method is a new promise that will get resolved 
	 * with an error if this promise is resolved with an error, or when the success
	 * callback throws an error. Otherwise the returned promise will get resolved 
	 * with the same object as the promise returned by the success callback.
	 * 
	 * <p>
	 * Note that callbacks may be called immediately on the thread that calls 
	 * {@link #then(Success)}, immediately on a different thread, or at some
	 * point in the future after this promise resolves.
	 * 
	 * <p>
	 * There is a defined "happens before" relationship in promise resolution
	 * which means that the promise is always resolved before the callback occurs,
	 * i.e. {@link #isDone()} will return <code>true</code> and {@link #getValue()}
	 * will not block.
	 * 
	 * @param success
	 *            Callback when this promise is successfully resolved. May be <code>null</code> if no
	 *            success behaviour is required.
	 * @return A new promise that will get resolved when the success method
	 *         successfully returns or when there is an error
	 */
	<R> Promise<R> then(Success<R, ? super T> success);

	/**
	 * Register a {@link Runnable} that will be called when this promise has
	 * been resolved with either an error or a value. This method can be used 
	 * at any time.
	 * 
	 * @param done
	 *            the Runnable called when this promise is resolved.
	 * @throws NullPointerException if the supplied Runnable is <code>null</code>
	 */
	void onResolve(Runnable done) throws NullPointerException;

	/**
	 * Returns <tt>true</tt> if this task has completed.
	 * 
	 * Completion may be due to normal termination, an exception, or
	 * cancellation -- in all of these cases, this method will return
	 * <tt>true</tt>.
	 * 
	 * @return <tt>true</tt> if this task completed
	 */
	boolean isDone();

	/**
	 * Waits if necessary for the computation to complete, and then retrieves
	 * its result. If the promise resolves with an error then calls to this
	 * method will throw an {@link InvocationTargetException} wrapping the
	 * Exception that resolved the promise
	 * 
	 * @return the computed result
	 * @throws InvocationTargetException
	 *             if the promise was resolved with an error
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 */
	T getValue() throws InvocationTargetException, InterruptedException;

	/**
	 * Waits if necessary for the asynchronous computation to complete, and then retrieves
	 * the error that occurred. If no error happens then this method will return <code>null</code>.
	 * 
	 * @return the error that occurred when calculating the return value for this promise, 
	 * 	or <code>null</code> if no error occurred
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 */
	Throwable getError() throws InterruptedException;
	
	/**
	 * Returns <tt>true</tt> if this task was cancelled before it resolved, or
	 * <tt>false</tt> if the task has not been cancelled.
	 * 
	 * @return <tt>true</tt> if this task completed by being cancelled
	 */
	boolean isCancelled();
	
	/**
	 * Cancel this promise, and resolve it immediately with a {@link CancelledPromiseException}.
	 * This will also resolve any chained promises, and is useful for indicating that the
	 * background work is no longer useful or meaningful.
	 * 
	 * <p>
	 * When cancelling a promise there is an inherent race condition with the thread that would
	 * normally resolve the promise. The return value from this method is identical to the value 
	 * that would be returned by calling {@link #isCancelled()} immediately after calling
	 * {@link #cancel()} on the promise. If this method returns false then it means that the caller
	 * lost the race and the promise resolved with a real value or error.
	 * 
	 * <p>
	 * Note that this method gives few guarantees about the state of any background work after 
	 * cancellation. If this promise is a chained promise then the background work must continue
	 * normally so that the original promise, and any other chained promises, can complete normally.
	 * If this promise is the "root" promise then the exact behaviour of {@link #cancel()} is
	 * implementation dependent. The background work may be halted, interrupted, or left to continue
	 * normally.
	 * 
	 * @return <tt>true</tt> if this task completed by being cancelled
	 */
	boolean cancel();
	
}

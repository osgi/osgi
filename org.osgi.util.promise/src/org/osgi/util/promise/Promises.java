package org.osgi.util.promise;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilities for promises.
 */
public class Promises {
	/**
	 * Run all the given promises in parallel. This will return a new promise that will
	 * be resolved when all the given promises have been resolved.
	 * 
	 * @param promises the array of promises
	 * @return a new promise
	 * @throws Exception
	 */
	static public Promise<Object[]> parallel(Promise<?>... promises) throws Exception {
		return parallel(new Object[promises.length], promises);
	}
	
	/**
	 * Run all the given promises in parallel. This will return a new promise that will
	 * be resolved when all the given promises have been resolved.
	 * 
	 * @param array the array that the return values from the promises are to
     *   be stored in. If the array is not big enough then a new array of the
     *   same element type will be used.
	 * @param promises the array of promises
	 * @return a new promise that will resolve when all of the supplied promises resolve.
	 * @throws Exception
	 */
	static public <T> Promise<T[]> parallel(T[] array, Promise<? extends T>... promises) throws Exception {
		final Deferred<T[]> resolver = new Deferred<T[]>();
		final AtomicInteger count = new AtomicInteger(promises.length);
		final Throwable[] exceptions = new Throwable[promises.length];
		
		final T[] values = array.length >= promises.length ? array :
				(T[]) Array.newInstance(array.getClass().getComponentType(), promises.length);
		final AtomicBoolean errors = new AtomicBoolean(false);
		
		int i = 0;
		for (final Promise<? extends T> p : promises) {
			final int index = i;
			
			p.onResolve(new Runnable() {
				
				public void run() {
					try {
						if ((exceptions[index] = p.getError()) == null)
							values[index] = p.getValue();
						else
							errors.set(true);
						
					} catch (Exception e) {
						exceptions[index] = e;
						errors.set(true);
					}
					
					if (count.decrementAndGet() == 0) {
						if (errors.get())
							resolver.fail(new ParallelException(values,
									exceptions));
						else
							resolver.resolve(values);
					}
				}
				
			});
			i++;
		}
		return resolver.getPromise();
	}

	/**
	 * Create a promise that is already resolved with the supplied value
	 * 
	 * @param value
	 * @return
	 */
	public static <T> Promise<T> newResolvedPromise(T value) {
		return new Deferred<T>(value).getPromise();
	}
	
	/**
	 * Create a copy of a promise that can safely be shared with other clients.
	 * 
	 * <p>
	 * This method is useful for when a promise needs to be shared with a client
	 * that may call {@link Promise#cancel()} on promises that it does not own.
	 * 
	 * @param promise A new promise that will resolve when the supplied promise
	 * resolves.
	 * @return
	 */
	public static <T> Promise<T> shareableCopyOf(Promise<T> promise) {
		return promise.then(null);
	}
}

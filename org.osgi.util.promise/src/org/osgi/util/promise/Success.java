package org.osgi.util.promise;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A Callback that is made when the promise resolves successfully.
 *
 * @param <Return> The type of the chained promise created by this callback 
 * @param <Value> The type of the promise expected by this callback
 */
@ConsumerType
public interface Success<Return,Value> {
	/**
	 * Called when the promise that this callback was registered with completed successfully. 
	 * 
	 * To simplify the remainder of this description we will refer to the {@link Promise}
	 * returned by {@link #call(Promise)} as 'p' and the {@link Promise} returned by
	 * {@link Promise#then(Success)}  as 'q'.
	 * 
	 * If the return value 'p' is <code>null</code> then the chained promise 'q' will resolve
	 * immediately with a successful value of <code>null</code>.
	 * 
	 * If 'p' is not null then the chained promise 'q' will resolve when the 
	 * promise 'p' resolves.
	 * 
	 * @param promise the {@link Promise} that resolved successfully
	 * @return A new promise in the chain, or null if the chain should 
	 *   resolve immediately.
	 */
	Promise<Return> call(Promise<? extends Value> promise) throws Exception;
}

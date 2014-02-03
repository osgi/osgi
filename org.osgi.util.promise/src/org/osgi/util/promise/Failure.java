package org.osgi.util.promise;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A Callback that is made when the promise resolves with a failure.
 *
 * @param <Value> The type of the promise expected by this callback
 */
@ConsumerType
public interface Failure<Value> {
	/**
	 * Called when the promise was resolved with an error.
	 * @param promise the promise this callback was registered on
	 */
	void fail(Promise<? extends Value> promise) throws Exception;
}

package org.osgi.util.promise;

/**
 * This exception is not normally thrown. Typically it is used to resolve
 * a {@link Promise} in the event that it is cancelled by the client 
 * before it is resolved normally.
 */
public class CancelledPromiseException extends RuntimeException {

	private static final long serialVersionUID = 1680637365138356009L;

	public CancelledPromiseException(String message) {
		super(message);
	}
}
package org.osgi.service.serial;

/**
 * Thrown when a driver doesn't allow the specified operation.
 */
public class UnsupportedCommOperationException extends Exception{

	/**
	 * Constructs an UnsupportedCommOperationException with no detail message.<br>
	 */
	public UnsupportedCommOperationException() {
		super();
	}

	/**
	 * Constructs an UnsupportedCommOperationException with the specified detail message.<br>
	 *
	 * @param message the detail message
	 */
	public UnsupportedCommOperationException(String message) {
		super(message);
	}
}

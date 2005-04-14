package org.osgi.service.application;

/**
 * An exception that is raised when a singleton application (i.e. it can have
 * zero or one instance at a time) have one instance and launching a new
 * instance is attempted.
 */
public class SingletonException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Parameterless constructor.
	 */
	public SingletonException() {
		super();
	}

	/**
	 * Constructor to create the exception with the specified message.
	 * 
	 * @param message
	 *            the message for the exception
	 */
	public SingletonException(String message) {
		super(message);
	}
}
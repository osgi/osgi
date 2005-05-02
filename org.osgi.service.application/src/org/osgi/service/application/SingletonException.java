package org.osgi.service.application;

/**
 * An exception that is raised when an application that can only be 
 * launched once is attempted to be launched twice.
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
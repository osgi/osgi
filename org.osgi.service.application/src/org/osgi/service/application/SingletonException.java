package org.osgi.service.application;

/**
 * An exception that is raised when a singleton application (i.e. it can have
 * zero or one instance at a time) have one instance and launching a new
 * instance is attempted.
 * 
 * @modelguid {8D8736B8-2E13-4B38-8774-C52257123A03}
 */
public class SingletonException extends Exception {

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
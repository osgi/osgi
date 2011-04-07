package org.osgi.service.subsystem;

/**
 * Exception thrown by SubsystemAdmin or Subsystem when a problem occurs.
 */
public class SubsystemException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Construct a subsystem exception with no message.
	 */
	public SubsystemException() {
	}

	/**
	 * Construct a subsystem exception specifying a message. 
	 * @param message The message to include in the exception.
	 */
	public SubsystemException(String message) {
		super(message);
	}

	/**
	 * Construct a subsystem exception wrapping an existing exception.
	 * @param cause The cause of the exception.
	 */
	public SubsystemException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a subsystem exception specifying a message and wrapping an 
	 * existing exception.
	 * @param message The message to include in the exception.
	 * @param cause The cause of the exception.
	 */
	public SubsystemException(String message, Throwable cause) {
		super(message, cause);
	}
}

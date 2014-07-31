
package org.osgi.impl.service.resourcemanagement.util;

/**
 * Reports an exception when trying to persist or restore ResourceMonitors.
 * 
 * @author mpcy8647
 * 
 */
public class PersistenceException extends Exception {

	/** generated */
	private static final long	serialVersionUID	= 7079982829353135283L;

	/**
	 * @param message
	 */
	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param t
	 */
	public PersistenceException(String message, Throwable t) {
		super(message + " : " + t.getMessage());
	}
}

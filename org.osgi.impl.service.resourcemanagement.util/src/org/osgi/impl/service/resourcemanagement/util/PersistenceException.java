package org.osgi.impl.service.resourcemanagement.util;

/**
 * Reports an exception when trying to persist or restore ResourceMonitors.
 * 
 * @author mpcy8647
 * 
 */
public class PersistenceException extends Exception {

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Throwable t) {
		super(message + " : " + t.getMessage());
	}
}

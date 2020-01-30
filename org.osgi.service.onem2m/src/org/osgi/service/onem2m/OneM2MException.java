package org.osgi.service.onem2m;

import java.io.IOException;

/**
 * 
 * General Exception for oneM2M. 
 */
public class OneM2MException extends IOException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 7025371906099079000L;
	/**
	 * Error Code
	 */
	public int errorCode;
	/**
	 * Cause of Exception
	 */
	public String cause;
}

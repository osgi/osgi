package org.osgi.service.onem2m.dto;

import java.io.IOException;

/**
 * 
 * General Exception for oneM2M. 
 */
public class OneM2MException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7025371906099079000L;
	public int errorCode;
	public String cause;
}

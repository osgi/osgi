/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin.api;

/**
 * Exception thrown on the {@link DownloadAgent}and the {@link ProtocolPlugin}
 * interfaces if any error occures during the download operations.
 */
public class DownloadException extends Exception {
	/**
	 * Constructor
	 */
	public DownloadException() {
		super();
	}

	/**
	 * Constructor
	 */
	public DownloadException(String text) {
		super(text);
	}

	/**
	 * Constructor
	 */
	public DownloadException(Throwable cause) {
		//super(cause);
		super();
	}

	/**
	 * Constructor
	 */
	public DownloadException(String text, Throwable cause) {
		//super(text, cause);
		super(text);
	}
}

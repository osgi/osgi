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

import java.io.*;
import java.util.Map;

/**
 * Extends the functionality of the {@link java.io.InputStream}class to provide
 * additional information to the input stream of the resource being downloaded.
 */
public class DownloadInputStream extends InputStream {
	/**
	 * The map returned by the {@link #getDescriptor()}method has to contain
	 * the MIME type of the rsource the InputStream relates to under this key.
	 */
	public static final String	MIMETYPE	= "mimeType";
	private InputStream			stream;
	private Map					descr;

	/**
	 * The construcor usually used by the Deployment Admin module only.
	 */
	public DownloadInputStream(InputStream stream, Map descr) {
		this.stream = stream;
		this.descr = descr;
	}

	/**
	 * The same as in the achestor class.
	 */
	public int read() throws IOException {
		return stream.read();
	}

	/**
	 * Returns the descriptor of the stream that contains additional information
	 * for the input stream.
	 * <p>
	 * 
	 * @return the descriptor
	 */
	public Map getDescriptor() {
		return descr;
	}
}

/*
 * $Header$
 *
 * Copyright (c) 2002 - IBM Corporation
 * All Rights Reserved.
 *
 * These materials have been contributed to the OSGi Alliance
 * as "MEMBER LICENSED MATERIALS" as defined in, and
 * subject to the terms of, the OSGi Member Agreement by and between the OSGi Alliance and
 * IBM, specifically including but not limited to, the license
 * rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof.
 *
 * All company, brand and product names contained within this document may be
 * trademarks that are the sole property of the respective owners.
 *
 * The above notice must be included on all copies of this document that are
 * made.
 */
package org.osgi.service.url;

/**
 * Defines standard names for property keys associated with
 * {@link URLStreamHandlerService}and <tt>java.net.ContentHandler</tt>
 * services.
 * 
 * <p>
 * The values associated with these keys are of type <tt>java.lang.String[]</tt>,
 * unless otherwise indicated.
 * 
 * @version $Revision$
 */
public interface URLConstants {
	/**
	 * Service property naming the protocols serviced by a
	 * URLStreamHandlerService. The property's value is an array of protocol
	 * names.
	 */
	public static final String	URL_HANDLER_PROTOCOL	= "url.handler.protocol";
	/**
	 * Service property naming the MIME types serviced by a
	 * java.net.ContentHandler. The property's value is an array of MIME types.
	 */
	public static final String	URL_CONTENT_MIMETYPE	= "url.content.mimetype";
}

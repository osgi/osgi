/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2002, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.url;

/**
 * Defines standard names for property keys associated with
 * {@link URLStreamHandlerService}and <code>java.net.ContentHandler</code>
 * services.
 * 
 * <p>
 * The values associated with these keys are of type <code>java.lang.String[]</code>,
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
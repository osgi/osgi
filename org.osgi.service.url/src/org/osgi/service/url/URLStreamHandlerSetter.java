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

import java.net.URL;

/**
 * Interface used by <tt>URLStreamHandlerService</tt> objects to call the <tt>setURL</tt> method
 * on the proxy <tt>URLStreamHandler</tt> object.
 *
 * <p>Objects of this type are passed to the {@link URLStreamHandlerService#parseURL}
 * method.
 * Invoking the <tt>setURL</tt> method on the <tt>URLStreamHandlerSetter</tt> object will invoke the
 * <tt>setURL</tt> method on the proxy <tt>URLStreamHandler</tt> object that is actually
 * registered with <tt>java.net.URL</tt> for the protocol.
 *
 * @version $Revision$
 */
public interface URLStreamHandlerSetter
{
    /**
     * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
     *
     * @deprecated This method is only for compatibility with handlers written
     *             for JDK 1.1.
     */
    public void setURL(URL u,
		       String protocol,
		       String host,
		       int port,
		       String file,
		       String ref);

    /**
     * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
     */
    public void setURL(URL u,
		       String protocol,
		       String host,
		       int port,
		       String authority,
		       String userInfo,
		       String path,
		       String query,
		       String ref);
}

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
import java.net.URLConnection;
import java.net.InetAddress;

/**
 * Service interface with public versions of the protected
 * <tt>java.net.URLStreamHandler</tt> methods.
 * <p>
 * The important differences between this interface and the
 * <tt>URLStreamHandler</tt> class are that the <tt>setURL</tt> method is
 * absent and the <tt>parseURL</tt> method takes a
 * {@link URLStreamHandlerSetter}object as the first argument. Classes
 * implementing this interface must call the <tt>setURL</tt> method on the
 * <tt>URLStreamHandlerSetter</tt> object received in the <tt>parseURL</tt>
 * method instead of <tt>URLStreamHandler.setURL</tt> to avoid a
 * <tt>SecurityException</tt>.
 * 
 * @see AbstractURLStreamHandlerService
 * 
 * @version $Revision$
 */
public interface URLStreamHandlerService {
	/**
	 * @see "java.net.URLStreamHandler.openConnection"
	 */
	public URLConnection openConnection(URL u) throws java.io.IOException;

	/**
	 * Parse a URL. This method is called by the <tt>URLStreamHandler</tt>
	 * proxy, instead of <tt>java.net.URLStreamHandler.parseURL</tt>, passing
	 * a <tt>URLStreamHandlerSetter</tt> object.
	 * 
	 * @param realHandler The object on which <tt>setURL</tt> must be invoked
	 *        for this URL.
	 * @see "java.net.URLStreamHandler.parseURL"
	 */
	public void parseURL(URLStreamHandlerSetter realHandler, URL u,
			String spec, int start, int limit);

	/**
	 * @see "java.net.URLStreamHandler.toExternalForm"
	 */
	public String toExternalForm(URL u);

	/**
	 * @see "java.net.URLStreamHandler.equals(URL, URL)"
	 */
	public boolean equals(URL u1, URL u2);

	/**
	 * @see "java.net.URLStreamHandler.getDefaultPort"
	 */
	public int getDefaultPort();

	/**
	 * @see "java.net.URLStreamHandler.getHostAddress"
	 */
	public InetAddress getHostAddress(URL u);

	/**
	 * @see "java.net.URLStreamHandler.hashCode(URL)"
	 */
	public int hashCode(URL u);

	/**
	 * @see "java.net.URLStreamHandler.hostsEqual"
	 */
	public boolean hostsEqual(URL u1, URL u2);

	/**
	 * @see "java.net.URLStreamHandler.sameFile"
	 */
	public boolean sameFile(URL u1, URL u2);
}

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

import java.net.*;

/**
 * Abstract implementation of the <code>URLStreamHandlerService</code> interface.
 * All the methods simply invoke the corresponding methods on
 * <code>java.net.URLStreamHandler</code> except for <code>parseURL</code> and
 * <code>setURL</code>, which use the <code>URLStreamHandlerSetter</code>
 * parameter. Subclasses of this abstract class should not need to override the
 * <code>setURL</code> and <code>parseURL(URLStreamHandlerSetter,...)</code>
 * methods.
 * 
 * @version $Revision$
 */
public abstract class AbstractURLStreamHandlerService extends URLStreamHandler
		implements URLStreamHandlerService {
	/**
	 * @see "java.net.URLStreamHandler.openConnection"
	 */
	public abstract URLConnection openConnection(URL u)
			throws java.io.IOException;

	/**
	 * The <code>URLStreamHandlerSetter</code> object passed to the parseURL
	 * method.
	 */
	protected URLStreamHandlerSetter	realHandler;

	/**
	 * Parse a URL using the <code>URLStreamHandlerSetter</code> object. This
	 * method sets the <code>realHandler</code> field with the specified
	 * <code>URLStreamHandlerSetter</code> object and then calls
	 * <code>parseURL(URL,String,int,int)</code>.
	 * 
	 * @param realHandler The object on which the <code>setURL</code> method must
	 *        be invoked for the specified URL.
	 * @see "java.net.URLStreamHandler.parseURL"
	 */
	public void parseURL(URLStreamHandlerSetter realHandler, URL u,
			String spec, int start, int limit) {
		this.realHandler = realHandler;
		parseURL(u, spec, start, limit);
	}

	/**
	 * This method calls <code>super.toExternalForm</code>.
	 * 
	 * @see "java.net.URLStreamHandler.toExternalForm"
	 */
	public String toExternalForm(URL u) {
		return super.toExternalForm(u);
	}

	/**
	 * This method calls <code>super.equals(URL,URL)</code>.
	 * 
	 * @see "java.net.URLStreamHandler.equals(URL,URL)"
	 */
	public boolean equals(URL u1, URL u2) {
		return super.equals(u1, u2);
	}

	/**
	 * This method calls <code>super.getDefaultPort</code>.
	 * 
	 * @see "java.net.URLStreamHandler.getDefaultPort"
	 */
	public int getDefaultPort() {
		return super.getDefaultPort();
	}

	/**
	 * This method calls <code>super.getHostAddress</code>.
	 * 
	 * @see "java.net.URLStreamHandler.getHostAddress"
	 */
	public InetAddress getHostAddress(URL u) {
		return super.getHostAddress(u);
	}

	/**
	 * This method calls <code>super.hashCode(URL)</code>.
	 * 
	 * @see "java.net.URLStreamHandler.hashCode(URL)"
	 */
	public int hashCode(URL u) {
		return super.hashCode(u);
	}

	/**
	 * This method calls <code>super.hostsEqual</code>.
	 * 
	 * @see "java.net.URLStreamHandler.hostsEqual"
	 */
	public boolean hostsEqual(URL u1, URL u2) {
		return super.hostsEqual(u1, u2);
	}

	/**
	 * This method calls <code>super.sameFile</code>.
	 * 
	 * @see "java.net.URLStreamHandler.sameFile"
	 */
	public boolean sameFile(URL u1, URL u2) {
		return super.sameFile(u1, u2);
	}

	/**
	 * This method calls
	 * <code>realHandler.setURL(URL,String,String,int,String,String)</code>.
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
	 * @deprecated This method is only for compatibility with handlers written
	 *             for JDK 1.1.
	 */
	protected void setURL(URL u, String proto, String host, int port,
			String file, String ref) {
		realHandler.setURL(u, proto, host, port, file, ref);
	}

	/**
	 * This method calls
	 * <code>realHandler.setURL(URL,String,String,int,String,String,String,String)</code>.
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
	 */
	protected void setURL(URL u, String proto, String host, int port,
			String auth, String user, String path, String query, String ref) {
		realHandler.setURL(u, proto, host, port, auth, user, path, query, ref);
	}
}
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

import java.net.URL;

/**
 * Interface used by <code>URLStreamHandlerService</code> objects to call the
 * <code>setURL</code> method on the proxy <code>URLStreamHandler</code> object.
 * 
 * <p>
 * Objects of this type are passed to the
 * {@link URLStreamHandlerService#parseURL}method. Invoking the <code>setURL</code>
 * method on the <code>URLStreamHandlerSetter</code> object will invoke the
 * <code>setURL</code> method on the proxy <code>URLStreamHandler</code> object that
 * is actually registered with <code>java.net.URL</code> for the protocol.
 * 
 * @version $Revision$
 */
public interface URLStreamHandlerSetter {
	/**
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
	 * 
	 * @deprecated This method is only for compatibility with handlers written
	 *             for JDK 1.1.
	 */
	public void setURL(URL u, String protocol, String host, int port,
			String file, String ref);

	/**
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
	 */
	public void setURL(URL u, String protocol, String host, int port,
			String authority, String userInfo, String path, String query,
			String ref);
}
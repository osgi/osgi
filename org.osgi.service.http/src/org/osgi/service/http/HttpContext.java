/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.http;

import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines methods that the Http Service may call to get
 * information about a registration.
 * 
 * <p>
 * Servlets and resources may be registered with an <code>HttpContext</code>
 * object; if no <code>HttpContext</code> object is specified, a default
 * <code>HttpContext</code> object is used. Servlets that are registered using the
 * same <code>HttpContext</code> object will share the same
 * <code>ServletContext</code> object.
 * 
 * <p>
 * This interface is implemented by users of the <code>HttpService</code>.
 * 
 * @version $Revision$
 */
public abstract interface HttpContext {
	/**
	 * <code>HttpServletRequest</code> attribute specifying the name of the
	 * authenticated user. The value of the attribute can be retrieved by
	 * <code>HttpServletRequest.getRemoteUser</code>. This attribute name is
	 * <code>org.osgi.service.http.authentication.remote.user</code>.
	 * 
	 * @since 1.1
	 */
	public static final String	REMOTE_USER			= "org.osgi.service.http.authentication.remote.user";
	/**
	 * <code>HttpServletRequest</code> attribute specifying the scheme used in
	 * authentication. The value of the attribute can be retrieved by
	 * <code>HttpServletRequest.getAuthType</code>. This attribute name is
	 * <code>org.osgi.service.http.authentication.type</code>.
	 * 
	 * @since 1.1
	 */
	public static final String	AUTHENTICATION_TYPE	= "org.osgi.service.http.authentication.type";
	/**
	 * <code>HttpServletRequest</code> attribute specifying the
	 * <code>Authorization</code> object obtained from the
	 * <code>org.osgi.service.useradmin.UserAdmin</code> service. The value of the
	 * attribute can be retrieved by
	 * <code>HttpServletRequest.getAttribute(HttpContext.AUTHORIZATION)</code>.
	 * This attribute name is <code>org.osgi.service.useradmin.authorization</code>.
	 * 
	 * @since 1.1
	 */
	public static final String	AUTHORIZATION		= "org.osgi.service.useradmin.authorization";

	/**
	 * Handles security for the specified request.
	 * 
	 * <p>
	 * The Http Service calls this method prior to servicing the specified
	 * request. This method controls whether the request is processed in the
	 * normal manner or an error is returned.
	 * 
	 * <p>
	 * If the request requires authentication and the Authorization header in
	 * the request is missing or not acceptable, then this method should set the
	 * WWW-Authenticate header in the response object, set the status in the
	 * response object to Unauthorized(401) and return <code>false</code>. See
	 * also RFC 2617: <i>HTTP Authentication: Basic and Digest Access
	 * Authentication </i> (available at http://www.ietf.org/rfc/rfc2617.txt).
	 * 
	 * <p>
	 * If the request requires a secure connection and the <code>getScheme</code>
	 * method in the request does not return 'https' or some other acceptable
	 * secure protocol, then this method should set the status in the response
	 * object to Forbidden(403) and return <code>false</code>.
	 * 
	 * <p>
	 * When this method returns <code>false</code>, the Http Service will send
	 * the response back to the client, thereby completing the request. When
	 * this method returns <code>true</code>, the Http Service will proceed with
	 * servicing the request.
	 * 
	 * <p>
	 * If the specified request has been authenticated, this method must set the
	 * {@link #AUTHENTICATION_TYPE}request attribute to the type of
	 * authentication used, and the {@link #REMOTE_USER}request attribute to
	 * the remote user (request attributes are set using the
	 * <code>setAttribute</code> method on the request). If this method does not
	 * perform any authentication, it must not set these attributes.
	 * 
	 * <p>
	 * If the authenticated user is also authorized to access certain resources,
	 * this method must set the {@link #AUTHORIZATION}request attribute to the
	 * <code>Authorization</code> object obtained from the
	 * <code>org.osgi.service.useradmin.UserAdmin</code> service.
	 * 
	 * <p>
	 * The servlet responsible for servicing the specified request determines
	 * the authentication type and remote user by calling the
	 * <code>getAuthType</code> and <code>getRemoteUser</code> methods,
	 * respectively, on the request.
	 * 
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @return <code>true</code> if the request should be serviced, <code>false</code>
	 *         if the request should not be serviced and Http Service will send
	 *         the response back to the client.
	 * @exception java.io.IOException may be thrown by this method. If this
	 *            occurs, the Http Service will terminate the request and close
	 *            the socket.
	 */
	public abstract boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	/**
	 * Maps a resource name to a URL.
	 * 
	 * <p>
	 * Called by the Http Service to map a resource name to a URL. For servlet
	 * registrations, Http Service will call this method to support the
	 * <code>ServletContext</code> methods <code>getResource</code> and
	 * <code>getResourceAsStream</code>. For resource registrations, Http Service
	 * will call this method to locate the named resource. The context can
	 * control from where resources come. For example, the resource can be
	 * mapped to a file in the bundle's persistent storage area via
	 * <code>bundleContext.getDataFile(name).toURL()</code> or to a resource in
	 * the context's bundle via <code>getClass().getResource(name)</code>
	 * 
	 * @param name the name of the requested resource
	 * @return URL that Http Service can use to read the resource or
	 *         <code>null</code> if the resource does not exist.
	 */
	public abstract URL getResource(String name);

	/**
	 * Maps a name to a MIME type.
	 * 
	 * Called by the Http Service to determine the MIME type for the name. For
	 * servlet registrations, the Http Service will call this method to support
	 * the <code>ServletContext</code> method <code>getMimeType</code>. For
	 * resource registrations, the Http Service will call this method to
	 * determine the MIME type for the Content-Type header in the response.
	 * 
	 * @param name determine the MIME type for this name.
	 * @return MIME type (e.g. text/html) of the name or <code>null</code> to
	 *         indicate that the Http Service should determine the MIME type
	 *         itself.
	 */
	public abstract String getMimeType(String name);
}

/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * This interface defines methods
 * that the Http Service may call to get information about a registration.
 *
 * <p>Servlets and resources may be registered with an <tt>HttpContext</tt>
 * object; if no <tt>HttpContext</tt> object is specified, a default
 * <tt>HttpContext</tt> object is used.
 * Servlets that are registered using the same <tt>HttpContext</tt> object will
 * share the same <tt>ServletContext</tt> object.
 *
 * <p>This interface is implemented by users of the <tt>HttpService</tt>.
 *
 * @version $Revision$
 */

public abstract interface HttpContext {

    /**
     * <tt>HttpServletRequest</tt> attribute specifying the name of the
     * authenticated user. The value of the attribute can be retrieved
     * by <tt>HttpServletRequest.getRemoteUser</tt>. This attribute name is
     * <tt>org.osgi.service.http.authentication.remote.user</tt>.
     * @since 1.1
     */
    public static final String REMOTE_USER =
    "org.osgi.service.http.authentication.remote.user";

    /**
     * <tt>HttpServletRequest</tt> attribute specifying the scheme used in
     * authentication. The value of the attribute can be retrieved by
     * <tt>HttpServletRequest.getAuthType</tt>. This attribute name is
     * <tt>org.osgi.service.http.authentication.type</tt>.
     * @since 1.1
     */
    public static final String AUTHENTICATION_TYPE =
    "org.osgi.service.http.authentication.type";

    /**
     * <tt>HttpServletRequest</tt> attribute specifying the
     * <tt>Authorization</tt> object obtained from the
     * <tt>org.osgi.service.useradmin.UserAdmin</tt> service. The
     * value of the attribute can be retrieved by
     * <tt>HttpServletRequest.getAttribute(HttpContext.AUTHORIZATION)</tt>.
     * This attribute name is
     * <tt>org.osgi.service.useradmin.authorization</tt>.
     * @since 1.1
     */
    public static final String AUTHORIZATION =
    "org.osgi.service.useradmin.authorization";

    /**
     * Handles security for the specified request.
     *
     * <p>The Http Service calls this method prior to servicing the specified
     * request.
     * This method controls whether the request is processed in the normal
     * manner or an error is returned.
     *
     * <p> If the request requires authentication and the
     * Authorization header in the request
     * is missing or not acceptable, then this method should
     * set the WWW-Authenticate header in the response object,
     * set the status in the response object to Unauthorized(401)
     * and return <tt>false</tt>.
     * See also RFC 2617:
     * <i>HTTP Authentication: Basic and Digest Access Authentication</i>
     * (available at http://www.ietf.org/rfc/rfc2617.txt).
     *
     * <p>If the request requires a secure connection and the
     * <tt>getScheme</tt> method in the request
     * does not return 'https' or some other acceptable secure
     * protocol, then this method should
     * set the status in the response object to Forbidden(403)
     * and return <tt>false</tt>.
     *
     * <p>When this method returns <tt>false</tt>,
     * the Http Service will send the response back to the client, thereby
     * completing the request.
     * When this method returns <tt>true</tt>,
     * the Http Service will proceed with servicing the request.
     *
     * <p>If the specified request has been authenticated, this method must
     * set the {@link #AUTHENTICATION_TYPE} request attribute
     * to the type of authentication used, and the
     * {@link #REMOTE_USER} request attribute to the
     * remote user (request attributes are set using the
     * <tt>setAttribute</tt> method on the request). If this method does not
     * perform any authentication, it must not set these attributes.
     *
     * <p>If the authenticated user is also authorized to access
     * certain resources, this method must set the
     * {@link #AUTHORIZATION} request attribute to the
     * <tt>Authorization</tt> object obtained from the
     * <tt>org.osgi.service.useradmin.UserAdmin</tt> service.
     *
     * <p> The servlet responsible for servicing the specified
     * request determines the authentication type and remote user by
     * calling the <tt>getAuthType</tt> and <tt>getRemoteUser</tt> methods,
     * respectively, on the request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @return <tt>true</tt> if the request should be serviced,
     *         <tt>false</tt> if the request should not be serviced
     *         and Http Service will send the response back to the client.
     * @exception java.io.IOException may be thrown by this method.
     * If this occurs, the Http Service will terminate the request and close
     * the socket.
     */
    public abstract boolean handleSecurity(HttpServletRequest request,
                                           HttpServletResponse response)
    throws IOException;

    /**
     * Maps a resource name to a URL.
     *
     * <p>Called by the Http Service to map a resource name to a URL.
     * For servlet registrations, Http Service will call this method
     * to support the <tt>ServletContext</tt> methods
     * <tt>getResource</tt> and <tt>getResourceAsStream</tt>.
     * For resource registrations, Http Service will call this method
     * to locate the named resource.
     * The context can control from where resources come.
     * For example, the resource can be mapped to a file
     * in the bundle's persistent storage area via
     * <tt>bundleContext.getDataFile(name).toURL()</tt>
     * or to a resource in the context's bundle via
     * <tt>getClass().getResource(name)</tt>
     *
     * @param name the name of the requested resource
     * @return URL that Http Service can use to read the resource
     *         or <tt>null</tt> if the resource does not
     *         exist.
     */
    public abstract URL getResource(String name);

    /**
     * Maps a name to a MIME type.
     *
     * Called by the Http Service to determine the MIME type for the
     * name. For servlet registrations, the Http Service will call this method
     * to support the <tt>ServletContext</tt> method <tt>getMimeType</tt>.
     * For resource registrations, the Http Service will call this method
     * to determine the MIME type for the Content-Type
     * header in the response.
     *
     * @param name determine the MIME type for this name.
     * @return MIME type (e.g. text/html) of the name or
     *         <tt>null</tt> to indicate that the Http Service should determine
     *         the MIME type itself.
     */
    public abstract String getMimeType(String name);
}

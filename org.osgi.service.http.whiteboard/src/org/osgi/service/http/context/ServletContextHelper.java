/*
 * Copyright (c) OSGi Alliance (2000, 2014). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.http.context;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;

/**
 * Helper service for the servlet context used by whiteboard services for HTTP
 * requests.
 * 
 * <p>
 * This service defines methods that the Http Whiteboard Service implementation
 * may call to get information for a request when dealing with whiteboard
 * services.
 * 
 * <p>
 * Servlets, servlet filters, resources, and listeners services may be
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_NAME
 * associated} with an {@code ServletContextHelper} service. Those whiteboard
 * services that are associated using the same {@code ServletContextHelper}
 * object will share the same {@code ServletContext} object.
 * 
 * <p>
 * If no {@code ServletContextHelper} service is associated, a default
 * {@code ServletContextHelper} is used. The behavior of the methods on the
 * default {@code ServletContextHelper} is defined as follows:
 * <ul>
 * <li>{@code getMimeType} - Does not define any customized MIME types for the
 * {@code Content-Type} header in the response, and calls the parent context if
 * it exists. Otherwise it always returns {@code null}.</li>
 * <li>{@code handleSecurity} - Performs implementation-defined authentication
 * on the request.</li>
 * <li>{@code getResource} - Assumes the named resource is in the bundle of the
 * whiteboard service. This method calls the whiteboard service bundle's
 * {@code Bundle.getEntry} method, and returns the appropriate URL to access the
 * resource. On a Java runtime environment that supports permissions, the Http
 * Whiteboard Service needs to be granted
 * {@code org.osgi.framework.AdminPermission[*,RESOURCE]}.</li>
 * <li>{@code getResourcePaths} - Assumes that the resources are in the bundle
 * of the whiteboard service. This method calls {@code Bundle.findEntries}
 * method, and returns the found entries. On a Java runtime environment that
 * supports permissions, the Http Whiteboard Service needs to be granted
 * {@code org.osgi.framework.AdminPermission[*,RESOURCE]}.</li>
 * <li>{@code getRealPath} - This method returns {@code null}.
 * </ul>
 * 
 * It is possible to register own {@code ServletContextHelper} services with a
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_NAME
 * service property}.
 * 
 * <p>
 * A context can be registered with the
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_PATH
 * service property} to define a path under which all services registered with
 * this context are reachable.
 * 
 * @ThreadSafe
 * @author $Id$
 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_NAME
 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_PATH
 */
@ConsumerType
public abstract class ServletContextHelper {
	/**
	 * {@code HttpServletRequest} attribute specifying the name of the
	 * authenticated user. The value of the attribute can be retrieved by
	 * {@code HttpServletRequest.getRemoteUser}. This attribute name is
	 * {@code org.osgi.service.http.authentication.remote.user}.
	 */
	public static final String	REMOTE_USER			= "org.osgi.service.http.authentication.remote.user";
	/**
	 * {@code HttpServletRequest} attribute specifying the scheme used in
	 * authentication. The value of the attribute can be retrieved by
	 * {@code HttpServletRequest.getAuthType}. This attribute name is
	 * {@code org.osgi.service.http.authentication.type}.
	 */
	public static final String	AUTHENTICATION_TYPE	= "org.osgi.service.http.authentication.type";
	/**
	 * {@code HttpServletRequest} attribute specifying the {@code Authorization}
	 * object obtained from the {@code org.osgi.service.useradmin.UserAdmin}
	 * service. The value of the attribute can be retrieved by
	 * {@code HttpServletRequest.getAttribute(HttpContext.AUTHORIZATION)}. This
	 * attribute name is {@code org.osgi.service.useradmin.authorization}.
	 */
	public static final String	AUTHORIZATION		= "org.osgi.service.useradmin.authorization";

	/** Bundle associated with this context. */
	private final Bundle		bundle;

	/**
	 * Default constructor
	 */
	public ServletContextHelper() {
		// default constructor
		this(null);
	}

	/**
	 * Construct a new context helper and set the bundle associated with this
	 * context.
	 * 
	 * @param b The bundle
	 */
	public ServletContextHelper(final Bundle b) {
		this.bundle = b;
	}

	/**
	 * Handles security for the specified request.
	 * 
	 * <p>
	 * The Http Whiteboard Service calls this method prior to servicing the
	 * specified request. This method controls whether the request is processed
	 * in the normal manner or an error is returned.
	 * 
	 * <p>
	 * If the request requires authentication and the Authorization header in
	 * the request is missing or not acceptable, then this method should set the
	 * WWW-Authenticate header in the response object, set the status in the
	 * response object to Unauthorized(401) and return {@code false}. See also
	 * RFC 2617: <i>HTTP Authentication: Basic and Digest Access Authentication
	 * </i> (available at http://www.ietf.org/rfc/rfc2617.txt).
	 * 
	 * <p>
	 * If the request requires a secure connection and the {@code getScheme}
	 * method in the request does not return 'https' or some other acceptable
	 * secure protocol, then this method should set the status in the response
	 * object to Forbidden(403) and return {@code false}.
	 * 
	 * <p>
	 * When this method returns {@code false}, the Http Whiteboard Service will
	 * send the response back to the client, thereby completing the request.
	 * When this method returns {@code true}, the Http Whitboard Service will
	 * proceed with servicing the request.
	 * 
	 * <p>
	 * If the specified request has been authenticated, this method must set the
	 * {@link #AUTHENTICATION_TYPE} request attribute to the type of
	 * authentication used, and the {@link #REMOTE_USER} request attribute to
	 * the remote user (request attributes are set using the
	 * {@code setAttribute} method on the request). If this method does not
	 * perform any authentication, it must not set these attributes.
	 * 
	 * <p>
	 * If the authenticated user is also authorized to access certain resources,
	 * this method must set the {@link #AUTHORIZATION} request attribute to the
	 * {@code Authorization} object obtained from the
	 * {@code org.osgi.service.useradmin.UserAdmin} service.
	 * 
	 * <p>
	 * The servlet responsible for servicing the specified request determines
	 * the authentication type and remote user by calling the
	 * {@code getAuthType} and {@code getRemoteUser} methods, respectively, on
	 * the request.
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @return {@code true} if the request should be serviced, {@code false} if
	 *         the request should not be serviced and Http Whiteboard Service
	 *         will send the response back to the client.
	 * @throws java.io.IOException may be thrown by this method. If this occurs,
	 *         the Http Whiteboard Service will terminate the request and close
	 *         the socket.
	 */
	public boolean handleSecurity(final HttpServletRequest request,
			                      final HttpServletResponse response)
			throws IOException {
		return true;
	}

	/**
	 * Maps a resource name to a URL.
	 * 
	 * <p>
	 * Called by the Http Whiteboard Service to map the specified resource name
	 * to a URL. For servlets, Http Whiteboard Service will call this method to
	 * support the {@code ServletContext} methods {@code getResource} and
	 * {@code getResourceAsStream}. For resource servlets, Http Whiteboard
	 * Service will call this method to locate the named resource.
	 * 
	 * <p>
	 * The context can control from where resources come. For example, the
	 * resource can be mapped to a file in the bundle's persistent storage area
	 * via {@code bundleContext.getDataFile(name).toURL()} or to a resource in
	 * the context's bundle via {@code getClass().getResource(name)}
	 * 
	 * @param name The name of the requested resource.
	 * @return A URL that Http Whiteboard Service can use to read the resource
	 *         or {@code null} if the resource does not exist.
	 */
	public URL getResource(String name) {
		final Bundle localBundle = this.bundle;
		if (name != null && localBundle != null) {
			if (name.startsWith("/")) {
				name = name.substring(1);
			}

			return this.bundle.getEntry(name);
		}
		return null;
	}

	/**
	 * Maps a name to a MIME type.
	 * 
	 * <p>
	 * Called by the Http Whiteboard Service to determine the MIME type for the
	 * specified name. For whiteboard services, the Http Whiteboard Service will
	 * call this method to support the {@code ServletContext} method
	 * {@code getMimeType}. For resource servlets, the Http Whiteboard Service
	 * will call this method to determine the MIME type for the
	 * {@code Content-Type} header in the response.
	 *
	 * @param name The name for which to determine the MIME type.
	 * @return The MIME type (e.g. text/html) of the specified name or
	 *         {@code null} to indicate that the Http Service should determine
	 *         the MIME type itself.
	 */
	public String getMimeType(final String name) {
		return null;
	}

	/**
	 * Returns a directory-like listing of all the paths to resources within the
	 * web application whose longest sub-path matches the supplied path
	 * argument.
	 * 
	 * <p>
	 * Called by the Http Whiteboard Service to support the
	 * {@code ServletContext} method {@code getResourcePaths} for whiteboard
	 * services.
	 * 
	 * @param path the partial path used to match the resources, which must
	 *        start with a /
	 * @return a Set containing the directory listing, or null if there are no
	 *         resources in the web application whose path begins with the
	 *         supplied path.
	 */
	public Set<String> getResourcePaths(final String path) {
		final Bundle localBundle = this.bundle;
		if (path != null && localBundle != null) {
			final Enumeration<URL> e = localBundle.findEntries(path, null, false);
			if (e != null) {
				final Set<String> result = new HashSet<String>();
				while (e.hasMoreElements()) {
					result.add(e.nextElement().getPath());
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * Gets the real path corresponding to the given virtual path.
	 * 
	 * <p>
	 * Called by the Http Whiteboard Service to support the
	 * {@code ServletContext} method {@code getRealPath} for whiteboard
	 * services.
	 * 
	 * @param path the virtual path to be translated to a real path
	 * @return the real path, or null if the translation cannot be performed
	 */
	public String getRealPath(final String path) {
		return null;
	}
}

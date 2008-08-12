/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import org.osgi.service.http.HttpContext;

//  ******************** ServletContextImpl ********************
/**
 * * This class implements the ServletContext interface defined by the servlet
 * API. * There is a 1:1 mapping between HttpContext and this class, i.e.
 * servlets registered * with the same HttpContext will also share
 * ServletContext * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$ *
 * @see HttpContext
 */
public final class ServletContextImpl implements ServletContext {
	private HttpServer				httpServer;
	private HttpContext				httpContext;
	private Log						log;
	private AccessControlContext	acc;
	int								refCnt			= 0;
	private final static Vector		whenDeprecated	= new Vector(0);	// empty
																			 // vector
	private final Hashtable			attributes		= new Hashtable();

	public ServletContextImpl(HttpServer httpServer, HttpContext httpContext,
			Log log, AccessControlContext acc) {
		this.httpServer = httpServer;
		this.httpContext = httpContext;
		this.log = log;
		this.acc = acc;
	}

	//----------------------------------------------------------------------------
	//	IMPLEMENTS - ServletContext
	//----------------------------------------------------------------------------
	public void log(String msg) {
		log.info(msg);
	}

	/**
	 * *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public void log(Exception e, String msg) {
		log.error(msg, e);
	}

	public void log(String msg, Throwable t) {
		log.error(msg, t);
	}

	/**
	 * * Virtual paths are not supported, always return null *
	 * 
	 * @param path the virtual path to be translated into a real path
	 */
	public String getRealPath(String path) {
		return null;
	}

	/**
	 * * Return the mime type for a file. First query the servlet's HttpContext
	 * for a mime type. * If the HttpContext return null, query HttpServer for a
	 * mime-type (which guarantees a * default mime type of no mime type is set
	 * for this file type. *
	 * 
	 * @param file name of the file whose mime type is required
	 */
	public String getMimeType(final String file) {
		String mimeType = null;
		try {
			mimeType = (String) AccessController.doPrivileged(
					new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							return httpContext.getMimeType(file);
						}
					}, acc);
		}
		catch (PrivilegedActionException pae) {
		}
		return (mimeType != null) ? mimeType : httpServer.getMimeType(file);
	}

	public String getServerInfo() {
		return "OSGi - HTTP Server reference implementation";
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Enumeration getAttributeNames() {
		return attributes.keys();
	}

	public void setAttribute(String key, Object o) {
		attributes.put(key, o);
	}

	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	/**
	 * * The servlet engine denies access to other servlet's ServletContext
	 * object. * This is OK according to the Servlet API spec. * *
	 * 
	 * @see ServletRequest#getContext
	 */
	public ServletContext getContext(String uripath) {
		return null;
	}

	public int getMajorVersion() {
		return 2;
	}

	public int getMinorVersion() {
		return 1;
	}

	public java.net.URL getResource(final String path)
			throws java.net.MalformedURLException {
		try {
			return (URL) AccessController.doPrivileged(
					new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							return httpContext.getResource(path);
						}
					}, acc);
		}
		catch (Exception e) {
			return null;
		}
	}

	public InputStream getResourceAsStream(String path) {
		try {
			URL url = getResource(path);
			return (url != null) ? url.openStream() : null;
		}
		catch (Exception e) {
			log.error("getResouceAsStream() failed: ", e);
			return null;
		}
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		AliasMatch aliasMatch = httpServer.findMatchingRegistration(path);
		return (aliasMatch != null) ? new RequestDispatcherImpl(aliasMatch)
				: null;
	}

	/**
	 * * Always return null since method is depracted *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public Servlet getServlet(String servletName) throws ServletException {
		return null;
	}

	/**
	 * * Always return empty enumeration since method is depracted *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public Enumeration getServlets() {
		return whenDeprecated.elements();
	}

	/**
	 * * Always return empty enumeration since method is depracted *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public Enumeration getServletNames() {
		return whenDeprecated.elements();
	}
} // ServletContextImpl

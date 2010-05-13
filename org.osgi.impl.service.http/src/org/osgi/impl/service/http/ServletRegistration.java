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

import java.io.*;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.osgi.service.http.*;

//  ******************** ServletRegistration ********************
/**
 * * A registration for a servlet. Holds the reference to the registered *
 * servlet. The class also sets up the environment for the servlet by *
 * supplying a ServletConfig and a ServletContext. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public class ServletRegistration extends AliasRegistration implements
		ServletConfig {
	private Servlet				servlet;		// The registered servlet,
												   // null -> servlet
												   // unregistered
	private Dictionary			initparams;	// Passed from the registrator
	private ServletContextImpl	servletContext;
	private HttpServer			httpServer;

	public ServletRegistration(HttpServiceImpl serviceImpl, String alias,
			final Servlet servlet, Dictionary initparams,
			HttpContext httpContext, HttpServer httpServer)
			throws NamespaceException, ServletException {
		super(serviceImpl, alias, httpContext);
		this.servlet = servlet;
		this.httpServer = httpServer;
		this.initparams = (initparams != null) ? initparams : new Hashtable(); // Create
																			   // an
																			   // empty
																			   // table,
																			   // useful
																			   // for
																			   // ServletConfig
		servletContext = serviceImpl.getHttpServer().servletEngine
				.getServletContext(httpContext, log, acc);
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					servlet.init(ServletRegistration.this);
					return null;
				}
			}, acc);
		}
		catch (PrivilegedActionException pae) {
			Exception e = pae.getException();
			if (e instanceof ServletException)
				throw (ServletException) e;
			log.error("Unhandled exception", e);
		}
		httpServer.addServlet(servlet);
	}

	// A servlet always matches, i.e. no need to peek into jar file to check
	// if a resource exist
	public AliasMatch getMatch(String uri) {
		return new AliasMatch(this, null);
	}

	public void handleRequest(final HttpServletRequest request,
			final HttpResponse response) throws IOException {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					try {
						if (servlet instanceof SingleThreadModel) {
							synchronized (ServletRegistration.this) {
								servlet.service(request, response);
							}
						}
						else {
							servlet.service(request, response);
						}
					}
					catch (ServletException e) {
						log.error("Unhandled exception in servlet "
								+ request.getRequestURI() + " ", e);
						response.sendError(e);
					}
					return null;
				}
			}, acc);
		}
		catch (PrivilegedActionException pae) {
			Exception e = pae.getException();
			if (e instanceof IOException)
				throw (IOException) e;
			log
					.error("Unhandled exception " + request.getRequestURI()
							+ " ", e);
			response.sendError(e);
		}
	}

	public InputStream getResourceAsStream(String res) {
		return null;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public void destroy() {
		if (serviceImpl.isClosed()) { // Do only call destroy when service is
									  // not closed
			log
					.info("No unregister() before ungetting HttpService. Skipping call to servlet.destroy()");
		}
		else {
			try {
				AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws Exception {
						servlet.destroy();
						return null;
					}
				}, acc);
			}
			catch (PrivilegedActionException pae) {
				log.error(
						"Exception when calling servlet's destroy() method: ",
						pae.getException());
			}
			catch (Exception e) {
				log.error(
						"Exception when calling servlet's destroy() method: ",
						e);
			}
		}
		// Remove the ServletContext
		serviceImpl.getHttpServer().servletEngine.removeServletContext(
				httpContext, servletContext);
		httpServer.removeServlet(servlet);
		servlet = null;
		super.destroy(); // Clear all references in the AliasRegistration
	}

	//----------------------------------------------------------------------------
	//	IMPLEMENTS - ServletContext
	//----------------------------------------------------------------------------
	public ServletContext getServletContext() {
		if (servlet == null)
			throw new IllegalStateException("Servlet unregistered");
		return servletContext;
	}

	public String getInitParameter(String name) {
		if (servlet == null)
			throw new IllegalStateException("Servlet unregistered");
		return (String) initparams.get(name);
	}

	public Enumeration getInitParameterNames() {
		if (servlet == null)
			throw new IllegalStateException("Servlet unregistered");
		return initparams.keys();
	}
} // ServletRegistration

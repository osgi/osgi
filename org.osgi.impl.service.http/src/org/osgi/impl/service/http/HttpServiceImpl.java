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

import java.util.Dictionary;
import javax.servlet.*;
import org.osgi.framework.*;
import org.osgi.service.http.*;

//  ******************** HttpServiceImpl ********************
/**
 * * This class implements the HttpService interface. * Instances of this class
 * is created by the service factory and * handed to the clients requesting the
 * HttpService. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class HttpServiceImpl implements HttpService {
	private HttpServer		httpServer;
	private Log				log;
	private BundleContext	bc;
	private Bundle			bundle;
	private boolean			closed	= false;

	public HttpServiceImpl(HttpServer httpServer, BundleContext bc,
			Bundle bundle) {
		this.httpServer = httpServer;
		this.bc = bc;
		this.bundle = bundle;
		this.log = new Log("HttpServiceImpl_bid#" + bundle.getBundleId());
	}

	//----------------------------------------------------------------------------
	//			IMPLEMENTS - HttpService
	//----------------------------------------------------------------------------
	//  ==================== registerResources ====================
	/**
	 * * Implements registerResources() *
	 * 
	 * @see HttpService#registerResources
	 */
	public void registerResources(String alias, String name, HttpContext context)
			throws NamespaceException, IllegalArgumentException {
		if (closed)
			throw new IllegalStateException(
					"Service has been unget and is closed");
		if (context == null)
			context = createDefaultHttpContext();
		log.info("Registering resource with alias : " + alias);
		httpServer.addRegistration(new ResourceRegistration(this, alias, name,
				context));
	}

	//  ==================== registerServlet ====================
	/**
	 * * Implements registerServlet() *
	 * 
	 * @see HttpService#registerServlet
	 */
	public void registerServlet(String alias, Servlet servlet,
			Dictionary initparams, HttpContext context)
			throws ServletException, NamespaceException,
			IllegalArgumentException {
		if (closed)
			throw new IllegalStateException(
					"Service has been unget and is closed");
		if (servlet == null)
			throw new IllegalArgumentException("Servlet parameter is null");
		if (context == null)
			context = createDefaultHttpContext();
		log.info("Registering servlet with alias : " + alias);
		AliasRegistration reg = null;
		try {
			reg = new ServletRegistration(this, alias, servlet, initparams,
					context, httpServer);
			httpServer.addRegistration(reg);
		}
		catch (ServletException se) {
			if (reg != null)
				reg.destroy();
			throw se;
		}
		catch (NamespaceException ne) {
			if (reg != null)
				reg.destroy();
			throw ne;
		}
		catch (IllegalArgumentException iae) {
			if (reg != null)
				reg.destroy();
			throw iae;
		}
	}

	//  ==================== unregister ====================
	/**
	 * * Implements unregister(). If service is closed request is * silently
	 * ignored. *
	 * 
	 * @see HttpService#registerServlet
	 */
	public void unregister(String alias) {
		if (closed) // Silently ignore
			return;
		if (alias.equals("/"))
			alias = "";
		AliasRegistration reg = httpServer.getRegistration(alias);
		if (reg == null) {
			throw new IllegalArgumentException(
					"Cannot unregister non existing alias: " + alias);
		}
		// Must be the same bundle as the bundle that made the registration
		if (this != reg.serviceImpl) {
			log.warn("Attempt to unregister someone else's alias: " + alias);
			throw new IllegalArgumentException(
					"Calling bundle did not register this alias: " + alias);
		}
		log.info("Unregister alias : " + alias);
		httpServer.removeRegistration(reg);
	}

	//  ==================== createDefaultHttpContext ====================
	/**
	 * *
	 * 
	 * @see HttpService#createDefaultHttpContext *
	 * @since 2.0
	 */
	public HttpContext createDefaultHttpContext() {
		return new DefaultHttpContext(bundle);
	}

	//----------------------------------------------------------------------------
	//	Internal methods
	//----------------------------------------------------------------------------
	HttpServer getHttpServer() {
		return httpServer;
	}

	Bundle getBundle() {
		return bundle;
	}

	void close() {
		closed = true;
		httpServer.removeBundleRegistrations(bundle);
		httpServer = null;
		bundle = null;
		log = null;
	}

	boolean isClosed() {
		return closed;
	}
}

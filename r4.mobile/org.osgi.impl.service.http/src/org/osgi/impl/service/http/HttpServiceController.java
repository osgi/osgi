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

import java.io.IOException;
import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.http.*;

//  ******************** HttpServiceController ********************
/**
 * * The HttpServiceController class is used for controlling the HttpServer. *
 * and its services. * It does so by: *
 * <ol>*
 * <li>Implementing the BundleActivator class and thereby controlling the *
 * start and stop of the HttpServer. *
 * <li>Registering the HttpService. The service is registered as a service
 * factory, * which this class implements. *
 * </ol>* *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$ *
 * @see HttpServer
 */
public class HttpServiceController implements BundleActivator, ServiceFactory {
	private BundleContext		bc;
	private HttpServer			httpServer;									// The
																				 // actual
																				 // HttpServer
	private Hashtable			services	= new Hashtable();					// Maps
																				   // <bundleId>
																				   // ->
																				   // HttpServiceImpl
	private ServiceRegistration	httpReg;										// The
																				   // registration
																				   // of
																				   // the
																				   // HttpService
	private Log					log			= new Log("HttpServiceController");
	// The simplest HttpContext
	private HttpContext			httpContext	= new HttpContext() {
												public boolean handleSecurity(
														javax.servlet.http.HttpServletRequest request,
														javax.servlet.http.HttpServletResponse response)
														throws java.io.IOException {
													return true;
												}

												public java.net.URL getResource(
														String name) {
													return getClass()
															.getResource(name);
												}

												public String getMimeType(
														String name) {
													return null;
												}
											};

	//----------------------------------------------------------------------------
	//		IMPLEMENTS - BundleActivator
	//----------------------------------------------------------------------------
	public void start(BundleContext bc) throws BundleException {
		this.bc = bc;
		try {
			Log.init(bc);
			log.info("Creating the HttpServer");
			httpServer = new HttpServer(bc.getBundle());
			log.info("Starting the HttpServer");
			httpServer.start();
			// Register HttpService
			log.info("Registering HttpService");
			Hashtable dict = new Hashtable();
			dict.put("Description", "The standard OSGi HTTP service");
			httpReg = bc.registerService("org.osgi.service.http.HttpService",
					this, dict);
			// Register a default page
			HttpService httpService = (HttpService) bc.getService(httpReg
					.getReference());
			httpService.registerResources("/osgiref/http", "www", httpContext);
		}
		catch (Exception e) {
			log.error("Failed to start HttpServer" + e);
			throw new BundleException("Failure in start method, "
					+ e.getMessage(), e);
		}
	}

	public void stop(BundleContext bc) throws BundleException {
		log.info("stop() called, starting to shutdown HTTP service");
		httpReg.unregister();
		// Check if all services were unget
		if (services.size() > 0) {
			log.warn("Strange, there seem to be services still alive");
		}
		log.info("Stopping HTTP server");
		try {
			httpServer.shutdown();
		}
		catch (IOException ioe) {
			log.error("Failed to close server socket: " + ioe);
			throw new BundleException("Failed to stop HTTP server", ioe);
		}
		log.info("stop() ready, shutdown of HTTP service completed");
		Log.close();
	}

	//----------------------------------------------------------------------------
	//		IMPLEMENTS - ServiceFactory
	//----------------------------------------------------------------------------
	public Object getService(Bundle caller, ServiceRegistration sreg) {
		log.info("Building HttpService for bundle: " + caller.getBundleId());
		HttpServiceImpl servImpl = new HttpServiceImpl(httpServer, bc, caller);
		services.put(new Long(caller.getBundleId()), servImpl);
		return servImpl;
	}

	public void ungetService(Bundle caller, ServiceRegistration sreg,
			Object service) {
		log.info("Unget on HttpService from bundle: " + caller.getBundleId());
		HttpServiceImpl servImpl = (HttpServiceImpl) service;
		servImpl.close();
		services.remove(new Long(caller.getBundleId()));
	}
}

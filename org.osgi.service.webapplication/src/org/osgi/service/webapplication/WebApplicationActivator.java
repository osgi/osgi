/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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
package org.osgi.service.webapplication;

import java.io.*;
import java.net.URL;
import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.*;

/**
 * A BundleActivator for a Web Application Bundle. The class can be used as a
 * concrete BundleActivator for a Web Application Bundle or it can be extended
 * to create a custom BundleActivator for a Web Application Bundle. This class
 * provides methods for registering a WebApplicationService.
 * 
 * @version $Revision$
 */
public class WebApplicationActivator implements BundleActivator {
	/**
	 * The ServiceRegistration object for the WebApplicationService registered
	 * with OSGi.
	 */
	protected ServiceRegistration	registration;

	/**
	 * Starts the bundle. Default implementation calls
	 * registerWebApplicationService.
	 * 
	 * @param context The execution context of the bundle being started.
	 * @exception java.lang.Exception If the WebApplicationService cannot be
	 *            registered.
	 */
	public void start(BundleContext context) throws Exception {
		registration = registerWebApplicationService(context);
	}

	/**
	 * Stops the bundle. Default implementation calls
	 * unregisterWebApplicationService
	 * 
	 * @param context The execution context of the bundle being stopped.
	 * @exception java.lang.Exception If an error occurs stopping the bundle.
	 */
	public void stop(BundleContext context) throws Exception {
		unregisterWebApplicationService();
	}

	/**
	 * Registers a WebApplicationService for this Web Application Bundle. The
	 * Default implementation of this method
	 * <ul>
	 * <li>calls getWebApplicationService to obtain the service.
	 * <li>calls getWebApplicationProperties to obtain the service's
	 * properties.
	 * <li>registers the service through the bundle context.
	 * </ul>
	 * 
	 * @param context The execution context of the Web Application Bundle.
	 * @return the service's ServiceRegistration
	 * @exception java.lang.Exception If an error occurred during the
	 *            registration.
	 */
	protected ServiceRegistration registerWebApplicationService(
			BundleContext context) throws Exception {
		return context.registerService(WebApplication.WEBAPP_SERVICE,
				getWebApplicationService(),
				getWebApplicationProperties(context));
	}

	/**
	 * Unregisters the WebApplicationService for this Web Application Bundle
	 * registered by the method registerWebApplicationService.
	 */
	protected void unregisterWebApplicationService() {
		registration.unregister();
	}

	/**
	 * Returns the WebApplicationService for this Web Application. The default
	 * implementation of this method returns a concrete implementation of
	 * WebApplicationService which
	 * <ul>
	 * <li>returns null when getServletContextAttributes is called
	 * <li>returns silently when deploymentException is called
	 * </ul>
	 * 
	 * @see org.osgi.service.webapplication.WebApplication
	 * @return the WebApplicationService for this Web Application.
	 */
	protected WebApplication getWebApplicationService() {
		return new WebApplication() {
			public Dictionary getServletContextAttributes() {
				return null;
			}

			public void deploymentException(Exception e) {
			}
		};
	}

	/**
	 * Returns the service properties for this web application. Default
	 * implementation obtains the properties from resource
	 * /WEB-INF/wab.properties.
	 * 
	 * @param context The execution context of the Web Application Bundle.
	 * @return the Dictionary of service properties used to register the
	 *         WebApplicationService.
	 */
	protected Dictionary getWebApplicationProperties(BundleContext context)
			throws Exception {
		InputStream propsIn = null;
		Properties props = new Properties();
		try {
			URL propsUrl = context.getBundle().getEntry(
					"/WEB-INF/wab.properties"); //$NON-NLS-1$
			if (propsUrl != null) {
				propsIn = new BufferedInputStream(propsUrl.openStream());
				props.load(propsIn);
			}
		}
		finally {
			try {
				if (propsIn != null) {
					propsIn.close();
				}
			}
			catch (IOException e) {
			}
		}
		return props;
	}
}
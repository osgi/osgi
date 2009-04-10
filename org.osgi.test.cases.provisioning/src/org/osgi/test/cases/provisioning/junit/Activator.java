/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
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

package org.osgi.test.cases.provisioning.junit;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @testcase org.osgi.service.provisioning
 * @version $Revision$
 */
public class Activator implements BundleActivator, HttpContext {
	ServiceTracker	tracker;

	public static long	TIMEOUT1	= 60000;
	public static long	TIMEOUT2	= 10000;
	public static long	TIMEOUT4	= 100;
	public static long	TIMEOUT5	= 50;

	static {
		String scalingStr = System.getProperty("org.osgi.test.testcase.scaling");	  
		if (scalingStr != null) {
			try {
				long scale = Long.parseLong(scalingStr);
				if (scale > 0) {
					TIMEOUT1 *= scale;
					TIMEOUT2 *= scale;
					TIMEOUT4 *= scale;
					TIMEOUT5 *= scale;
				}
			}
			catch (Exception e) {
				// empty
			}
		}
	}

	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return true;
	}
	
	public String getMimeType(String name) {
		if (name.endsWith(".ipa") || name.endsWith(".jar")
				|| name.endsWith(".zip")) {
			return "application/zip";
		}
		return null;
	}

	public URL getResource(String name) {
		if (name.endsWith("delay.jar")) {
			try {		
				Thread.sleep(TIMEOUT2);
			}
			catch (InterruptedException e) {
				// empty
			}
		}
		URL url = null;
		try {
			url = getClass().getResource(name);
			if (url == null)
				url = getClass().getResource("/" + name);

			if (url == null && name.startsWith("/www")) {
				name = name.substring(4);
				url = getClass().getResource(name);
			}
			return url;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String	hostName;
	public static String	hostPort;

	public void start(BundleContext c) throws Exception {
		tracker = new ServiceTracker(c, HttpService.class
				.getName(), null) {
			public Object addingService(ServiceReference ref) {
				hostName = context
						.getProperty("org.osgi.service.http.hostname");
				if (hostName == null) {
					try {
						hostName = InetAddress.getLocalHost().getHostName();
					}
					catch (UnknownHostException e) {
						hostName = "localhost";
					}
				}

				hostPort = (String) ref
						.getProperty("http.port");
				if (hostPort == null) {
					hostPort = context
							.getProperty("org.osgi.service.http.port");
					if (hostPort == null) {
						throw new AssertionError(
								"unknown port for http service");
					}
				}
				final HttpService http = (HttpService) super.addingService(ref);
				try {
					context.getService(ref);
					http.registerServlet("/test/rsh", new RshServlet(), null,
							Activator.this);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return http;
			}
		};
		tracker.open();
	}

	public void stop(BundleContext c) {
		tracker.close();
	}
}

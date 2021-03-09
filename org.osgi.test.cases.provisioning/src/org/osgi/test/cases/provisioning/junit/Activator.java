/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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
import org.osgi.test.support.sleep.Sleep;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 *
 * TODO Add Javadoc comment for this type.
 *
 * @testcase org.osgi.service.provisioning
 * @author $Id$
 */
public class Activator implements BundleActivator, HttpContext {
	ServiceTracker<HttpService,HttpService>	tracker;

	public static long	TIMEOUT1	= 60000;
	public static long	TIMEOUT2	= 10000;
	public static long	TIMEOUT4	= 100;
	public static long	TIMEOUT5	= 50;

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
		if (name.endsWith("delay-prov.jar")) {
			try {
				Sleep.sleep(TIMEOUT2);
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
		String scalingStr = c.getProperty("org.osgi.test.testcase.scaling");
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
		tracker = new ServiceTracker<HttpService,HttpService>(c,
				HttpService.class, null) {
			public HttpService addingService(
					ServiceReference<HttpService> ref) {
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
				final HttpService http = super.addingService(ref);
				try {
					http.registerServlet("/test/rsh", new RshServlet(), null,
							Activator.this);
					http.registerResources("/test/ipa", "/ipa", Activator.this);
					http.registerResources("/test/www", "/www", Activator.this);
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

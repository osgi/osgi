/*
 * $Header$
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

package org.osgi.test.cases.provisioning;

import java.net.URL;

import org.osgi.framework.*;
import org.osgi.service.http.HttpService;
import org.osgi.test.cases.util.director.DefaultTestCase;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @testcase org.osgi.service.provisioning
 * @version $Revision$
 */
public class ProvisioningTestCase extends DefaultTestCase {
	HttpService		http;
	ServiceTracker	tracker;

	static long TIMEOUT2 = 10000;

	static {
		String scalingStr = System.getProperty("org.osgi.test.testcase.scaling");	  
		if (scalingStr != null) {
			try {
				long scale = Long.parseLong(scalingStr);
				if (scale > 0) {
					TIMEOUT2 *= scale;
				}
			} catch (Exception e) {}
		}
	}

	public String getMimeType(String name) {
		if (name.endsWith(".ipa") || name.endsWith(".jar")
				|| name.endsWith(".zip"))
			return "application/zip";
		return null;
	}

	public URL getResource(String name) {
		if (name.endsWith("delay.jar"))
			try {		
				Thread.sleep(TIMEOUT2);
			}
			catch (InterruptedException e) {}
		return super.getResource(name);
	}

	public void xinit() throws Exception {
		final ProvisioningTestCase p = this;

		tracker = new ServiceTracker(getBundleContext(), HttpService.class
				.getName(), null) {
			public Object addingService(ServiceReference ref) {
				HttpService http = (HttpService) super.addingService(ref);
				try {
					getBundleContext().getService(ref);
					http
							.registerServlet("/test/rsh", new RshServlet(),
									null, p);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return http;
			}
		};
		tracker.open();
	}

	public void xdeinit() {
		tracker.close();
	}

	public void start(BundleContext c) {
		super.start(c);
		try {
			xinit();
		}
		catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e + "");
		}
	}

	public void stop(BundleContext c) {
		xdeinit();
		super.stop(c);
	}

}

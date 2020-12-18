/*
 * Copyright (c) OSGi Alliance (2005, 2020). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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

package org.osgi.test.cases.framework.launch.secure.fragments.tb16a;

import java.io.InputStream;
import java.util.Collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * Bundle for Extension Bundles tests. Invoker without
 * AdminPermission[<bundle>, EXTENSIONLIFECYCLE] should not be able to
 * install extension bundles.
 * 
 * @author jorge.mascena@cesar.org.br
 * 
 * @author $Id$
 */
public class Activator implements BundleActivator{

	/**
	 * Starts Bundle. Confirms an extension bundle cannot be installed.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */

	public void start(BundleContext context) throws Exception {
		Collection<ServiceReference<InputStream>> bundleRefs;
		bundleRefs = context.getServiceReferences(InputStream.class,
				"(bundle=fragments.tb16b.jar)");
		if (bundleRefs.isEmpty())
			throw new BundleException(
					"fragments.tb16b.jar bundle inputstream unavailable");
		InputStream in = context.getService(bundleRefs.iterator().next());
		try {
			// Install extension bundle
			context.installBundle("fragments.tb16b.jar", in);
			throw new BundleException(
					"expected extension bundle to fail install due to lack of permission");
		}
		catch (SecurityException e) {
			// this is the expected exception, since this bundle doesn't
			// have AdminPermission[<bundle>, EXTENSIONLIFECYCLE] to
			// install framework extension bundles.
		}
	}

	/**
	 * Stops Bundle. Nothing to be done here.
	 * 
	 * @param context
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) {
		// empty
	}

}

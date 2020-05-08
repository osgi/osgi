/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
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

package org.osgi.test.cases.framework.classloading.tb4e;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.classloading.exports.service.SomeService;

/**
 * A bundle activator which execute some tests
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {

	}

	/**
	 * Start the bundle and execute the tests
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ServiceReference< ? >[] refs = context
				.getAllServiceReferences(SomeService.class.getName(), null);
		if (refs == null || refs.length == 0) {
			throw new BundleException("Found no service.");
		}
		for (ServiceReference< ? > ref : refs) {
			if (ref.isAssignableTo(context.getBundle(),
					SomeService.class.getName())) {
				throw new BundleException(
						"The method isAssignableTo must return false when the package source of the getter bundle is not the same of the registrant bundle");
			}
		}
	}

	/**
	 * Stop the bundle
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}

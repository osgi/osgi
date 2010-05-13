/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.metatype.tb2;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * This bundle activator will register a managed service that implements the
 * interface MetaTypeProvider
 * 
 * @author left
 * @version $Id$
 */
public class Activator implements BundleActivator {

	private ServiceRegistration	sr;

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {
		// empty
	}

	/**
	 * Start the bundle registering a service that implements the interface
	 * MetaTypeProvider
	 * 
	 * @param context The execution context of the bundle being started.
	 * @see Bundle#start
	 */
	public void start(BundleContext context) {
		Hashtable properties;

		properties = new Hashtable();
		properties.put(Constants.SERVICE_PID,
				"org.osgi.test.cases.metatype.ocd1");

		sr = context.registerService(new String[] {
				ManagedService.class.getName(),
				MetaTypeProvider.class.getName()}, new MetaTypeProviderImpl(),
				properties);
	}

	/**
	 * Unregister the service
	 * 
	 * @param context The execution context of the bundle being stopped.
	 * @see Bundle#stop
	 */
	public void stop(BundleContext context) {
		sr.unregister();

		sr = null;
	}

}
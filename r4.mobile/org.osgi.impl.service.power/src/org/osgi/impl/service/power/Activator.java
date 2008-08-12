/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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
package org.osgi.impl.service.power;

import org.osgi.framework.*;
import org.osgi.service.power.*;

/**
 * 
 * @version $Revision$
 */
public class Activator implements BundleActivator, ServiceFactory {

	private BundleContext		context;
	private PowerManagerImpl	powerManager;
	private ServiceRegistration	powerManagerReg;

	/**
	 * @param context
	 * @throws java.lang.Exception
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;

		// Create and register System Power
		powerManagerReg = context.registerService(ServiceFactory.class
				.getName(), this, null);
	}

	/**
	 * @param context
	 * @throws java.lang.Exception
	 */
	public void stop(BundleContext context) throws Exception {
		this.context = null;
		if (powerManagerReg != null) {
			powerManagerReg.unregister();
			powerManagerReg = null;
		}
	}

	/**
	 * @param bundle
	 * @param registration
	 * @return
	 * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle,
	 *      org.osgi.framework.ServiceRegistration)
	 */
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		if (powerManager == null)
			powerManager = new PowerManagerImpl(context, registration);
		return powerManager;
	}

	/**
	 * @param bundle
	 * @param registration
	 * @param service
	 * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle,
	 *      org.osgi.framework.ServiceRegistration, java.lang.Object)
	 */
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
	}
}
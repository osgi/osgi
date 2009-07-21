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

package org.osgi.test.cases.cm.tb2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.junit.CMControl;

/**
 * <p>
 * This bundle will generate <code>ConfigurationEvent</code> instances by
 * updating and deleting <code>Configuration</code> instances. These events
 * must be caught by the test bundle that registered a
 * <code>CondfigurationListener</code> and installed this bundle.
 * </p>
 * <p>
 * This test checks if an event broadcast includes other bundles that not the
 * event's originating bundle.
 * </p>
 * 
 * @author Jorge Mascena
 */
public class Activator implements BundleActivator {

	/**
	 * Generates <code>ConfigurationEvent</code> instances to be caught by
	 * other bundle's <code>ConfigurationListener</code> instance.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ServiceReference serviceReference = context
				.getServiceReference(ConfigurationAdmin.class.getName());
		try {
			ConfigurationAdmin cm = (ConfigurationAdmin) context
					.getService(serviceReference);
			Configuration config = cm
					.getConfiguration(CMControl.PACKAGE
					+ ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
			Hashtable props = new Hashtable();
			props.put("key", "value1");
			config.update(props);
			config.delete();
			config = cm
					.createFactoryConfiguration(CMControl.PACKAGE
					+ ".tb2factorypid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
			config.update(props);
			config.delete();
		}
		finally {
			context.ungetService(serviceReference);
		}
	}

	/**
	 * Nothing special to be done when finished with this bundle.
	 * 
	 * @param context the execution environment where the bundle is executed
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}

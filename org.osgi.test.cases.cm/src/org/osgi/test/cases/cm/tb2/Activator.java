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

package org.osgi.test.cases.cm.tb2;

import java.util.Dictionary;
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
		ServiceReference<ConfigurationAdmin> serviceReference = context
				.getServiceReference(ConfigurationAdmin.class);
		try {
			ConfigurationAdmin cm = context
					.getService(serviceReference);
			Configuration config = cm
					.getConfiguration(CMControl.PACKAGE
					+ ".tb2pid."
					+ ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
			Dictionary<String,Object> props = new Hashtable<>();
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

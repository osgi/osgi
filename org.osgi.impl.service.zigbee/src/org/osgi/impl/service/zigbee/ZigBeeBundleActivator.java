/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeBaseDriver;

/**
 * Activator for mocked ZigBee basedriver.
 * 
 * @author $Id$
 */
public class ZigBeeBundleActivator implements BundleActivator {

	private ZigBeeBaseDriver basedriver;

	/**
	 * This method is called when ZigBee Bundle starts, so that the Bundle can
	 * perform Bundle specific operations.
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bc) {
		try {
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - Instantiate the ZigBeeBaseDriver.");
			basedriver = new ZigBeeBaseDriver(bc);
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - Start the ZigBeeBaseDriver instance.");
			basedriver.start();
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - The ZigBeeBaseDriver instance is started.");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is called when ZigBee Bundle stops.
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bc) {
		try {
			System.out.println(this.getClass().getName() + " - Stop the ZigBeeBaseDriver instance.");
			basedriver.stop();
			System.out.println(this.getClass().getName() + " - The ZigBeeBaseDriver instance is stopped.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

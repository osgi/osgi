/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.test.cases.enoceansimulation;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.enoceansimulation.teststep.TestStep;
import org.osgi.test.cases.enoceansimulation.teststep.impl.TestStepForEnOceanImpl;

/**
 * The bundle activator.
 */
public class Activator implements BundleActivator {

	private ServiceRegistration	userInteractionSReg;

	// private ServiceTracker deviceSimulatorTracker;

	public void start(BundleContext bc) throws Exception {
		log(this.getClass(), "org.osgi.test.cases.enoceansimulation.Activator.start(...): Create, and register EnOcean's Test Step OSGi service.");
		// this.deviceSimulatorTracker = new ServiceTracker(bc,
		// DeviceSimulator.class.getName(), null);
		// this.deviceSimulatorTracker.open();
		// this.userInteractionSReg = bc.registerService(
		// TestStep.class.getName(), new
		// TestStepForEnOceanImpl(deviceSimulatorTracker), null);
		this.userInteractionSReg = bc.registerService(
				TestStep.class.getName(), new TestStepForEnOceanImpl(), null);
		log(this.getClass(), "org.osgi.test.cases.enoceansimulation.Activator.start(...): EnOcean's Test Step OSGi service has been created, and registered.");
	}

	public void stop(BundleContext bc) throws Exception {
		log(this.getClass(), "org.osgi.test.cases.enoceansimulation.Activator.stop(...): Unregister EnOcean's Test Step OSGi service.");
		this.userInteractionSReg.unregister();
		// this.deviceSimulatorTracker.close();
		log(this.getClass(), "org.osgi.test.cases.enoceansimulation.Activator.stop(...): EnOcean's Test Step OSGi service has been unregistered.");
	}

	public static void log(Class javaclass, String message) {
		System.out.println(javaclass.getName() + " - " + message);
	}

}

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

package org.osgi.impl.service.dal.step;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dal.simulator.DeviceSimulator;
import org.osgi.test.support.step.TestStep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The bundle activator.
 */
public class Activator implements BundleActivator { // NO_UCD

	private ServiceRegistration<TestStep>					testStepSReg;
	private ServiceTracker<DeviceSimulator,DeviceSimulator>	deviceSimulatorTracker;

	@Override
	public void start(BundleContext bc) {
		this.deviceSimulatorTracker = new ServiceTracker<>(bc,
				DeviceSimulator.class, null);
		this.deviceSimulatorTracker.open();
		this.testStepSReg = bc.registerService(
				TestStep.class, new TestStepImpl(deviceSimulatorTracker),
				null);
	}

	@Override
	public void stop(BundleContext bc) {
		this.testStepSReg.unregister();
		this.deviceSimulatorTracker.close();
	}
}

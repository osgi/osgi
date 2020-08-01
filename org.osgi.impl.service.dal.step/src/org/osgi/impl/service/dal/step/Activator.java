/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

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

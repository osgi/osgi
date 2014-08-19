/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.step.dal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.simulator.DeviceSimulator;
import org.osgi.test.cases.step.TestStep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The bundle activator.
 */
public class Activator implements BundleActivator {

	private ServiceRegistration	userInteractionSReg;
	private ServiceTracker		deviceSimulatorTracker;

	public void start(BundleContext bc) throws Exception {
		this.deviceSimulatorTracker = new ServiceTracker(bc, DeviceSimulator.class.getName(), null);
		this.deviceSimulatorTracker.open();
		this.userInteractionSReg = bc.registerService(
				TestStep.class.getName(), new TestStepImpl(deviceSimulatorTracker), null);
	}

	public void stop(BundleContext bc) throws Exception {
		this.userInteractionSReg.unregister();
		this.deviceSimulatorTracker.close();
	}

}

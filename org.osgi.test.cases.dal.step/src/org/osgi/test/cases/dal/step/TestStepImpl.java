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


package org.osgi.test.cases.dal.step;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Constants;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.simulator.DeviceSimulator;
import org.osgi.test.cases.step.TestStep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * An implementation of the test stepper based on RI simulator.
 */
public class TestStepImpl implements TestStep {

	private static final String		DEVICE_UID_PREFIX	= "test-step-simulator:";

	private final ServiceTracker	deviceSimulatorTracker;

	private int						deviceCounter;

	/**
	 * Constructs a new test stepper.
	 * 
	 * @param deviceSimulatorTracker Simulator service tracker.
	 */
	public TestStepImpl(ServiceTracker deviceSimulatorTracker) {
		this.deviceSimulatorTracker = deviceSimulatorTracker;
	}

	/**
	 * @see org.osgi.test.cases.step.TestStep#execute(java.lang.String,
	 *      java.lang.String[])
	 */
	public String[] execute(String command, String[] parameters) {
		if (Commands.REGISTER_DEVICE.equals(command)) {
			return new String[] {(String) registerNewDevice(parameters).getServiceProperty(Device.SERVICE_UID)};
		} else {
			throw new IllegalArgumentException("The user message is not supported.");
		}
	}

	private Device registerNewDevice(String[] parameters) {
		DeviceSimulator deviceSimulator = getDeviceSimulator();
		Dictionary deviceProps = new Hashtable(3, 1F);
		final String deviceUID = DEVICE_UID_PREFIX + deviceCounter++;
		deviceProps.put(Device.SERVICE_UID, deviceUID);
		deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
		deviceProps.put(Device.SERVICE_DRIVER, deviceUID + "-driver");
		Dictionary[] functionProps = null;
		if (null != parameters) {
			functionProps = new Dictionary[parameters.length];
			for (int i = 0; i < functionProps.length; i++) {
				final String functionUID = deviceUID + ':' + i;
				functionProps[i] = new Hashtable(6, 1F);
				functionProps[i].put(Constants.OBJECTCLASS, parameters[i]);
				functionProps[i].put(Function.SERVICE_UID, functionUID);
				functionProps[i].put(Function.SERVICE_VERSION, functionUID + "-version");
				functionProps[i].put(Function.SERVICE_DESCRIPTION, functionUID + "-description");
				functionProps[i].put(Function.SERVICE_DEVICE_UID, deviceUID);
				functionProps[i].put(Function.SERVICE_TYPE, functionUID + "-type");
			}
		}
		return deviceSimulator.registerDevice(deviceProps, functionProps);
	}

	private DeviceSimulator getDeviceSimulator() {
		DeviceSimulator deviceSimulator = (DeviceSimulator) this.deviceSimulatorTracker.getService();
		if (null == deviceSimulator) {
			throw new IllegalStateException("The device simulator service is missing.");
		}
		return deviceSimulator;
	}

}

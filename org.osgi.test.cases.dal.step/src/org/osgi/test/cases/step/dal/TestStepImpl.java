/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.step.dal;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Constants;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.BooleanControl;
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
		} else
			if (Commands.REGISTER_DEVICE_SINGLE_FUNCTION.equals(command)) {
				String[] params = new String[] {BooleanControl.class.getName()};
				return new String[] {(String) registerNewDevice(params).getServiceProperty(Device.SERVICE_UID)};
			} else
				if (Commands.PUBLISH_PROPERTY_EVENT.equals(command)) {
					getDeviceSimulator().publishEvent(parameters[0], parameters[1]);
				} else {
					throw new IllegalArgumentException("The user message is not supported.");
				}
		return null;
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

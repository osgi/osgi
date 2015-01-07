/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal.step;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Constants;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.Alarm;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.Keypad;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.simulator.DeviceSimulator;
import org.osgi.test.cases.dal.DeviceTestSteps;
import org.osgi.test.cases.dal.functions.FunctionsTestSteps;
import org.osgi.test.cases.dal.secure.SecureDeviceTestSteps;
import org.osgi.test.support.step.TestStep;
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
	 * @see org.osgi.test.support.step.TestStep#execute(java.lang.String,
	 *      java.lang.String)
	 */
	public String execute(String stepId, String userPrompt) {
		if (DeviceTestSteps.STEP_ID_REGISTER_DEVICE.equals(stepId) ||
				DeviceTestSteps.STEP_ID_AVAILABLE_DEVICE.equals(stepId) ||
				DeviceTestSteps.STEP_ID_DEVICES_ALL_PROPS.equals(stepId) ||
				SecureDeviceTestSteps.STEP_ID_AVAILABLE_DEVICE.equals(stepId)) {
			registerNewDevice(null);
		} else
			if (DeviceTestSteps.STEP_ID_REGISTER_DEVICE_FUNCTION.equals(stepId) ||
					DeviceTestSteps.STEP_ID_AVAILABLE_FUNCTION.equals(stepId) ||
					DeviceTestSteps.STEP_ID_AVAILABLE_OPERATION.equals(stepId) ||
					DeviceTestSteps.STEP_ID_PROPERTY_READABLE.equals(stepId) ||
					DeviceTestSteps.STEP_ID_PROPERTY_WRITABLE.equals(stepId) ||
					DeviceTestSteps.STEP_ID_FUNCTIONS_ALL_PROPS.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_BC.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_BS.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_METER.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_MLC.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_MLS.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_WAKE_UP.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_ALARM.equals(stepId) ||
					FunctionsTestSteps.STEP_ID_AVAILABLE_KEYPAD.equals(stepId)) {
				registerNewDevice(new String[] {
						BooleanControl.class.getName(),
						BooleanSensor.class.getName(),
						MultiLevelControl.class.getName(),
						MultiLevelSensor.class.getName(),
						Meter.class.getName(),
						WakeUp.class.getName(),
						Alarm.class.getName(),
						Keypad.class.getName()});
			} else
				if (FunctionsTestSteps.STEP_ID_EVENT_METER_CURRENT.equals(stepId)) {
					getDeviceSimulator().publishEvent(Meter.class.getName(), Meter.PROPERTY_CURRENT);
				} else
					if (FunctionsTestSteps.STEP_ID_EVENT_METER_TOTAL.equals(stepId)) {
						getDeviceSimulator().publishEvent(Meter.class.getName(), Meter.PROPERTY_TOTAL);
					} else
						if (FunctionsTestSteps.STEP_ID_EVENT_ALARM.equals(stepId)) {
							getDeviceSimulator().publishEvent(Alarm.class.getName(), Alarm.PROPERTY_ALARM);
						} else
							if (FunctionsTestSteps.STEP_ID_EVENT_KEYPAD.equals(stepId)) {
								getDeviceSimulator().publishEvent(Keypad.class.getName(), Keypad.PROPERTY_KEY);
							} else
								if (FunctionsTestSteps.STEP_ID_EVENT_BS.equals(stepId)) {
									getDeviceSimulator().publishEvent(BooleanSensor.class.getName(), BooleanSensor.PROPERTY_DATA);
								} else
									if (FunctionsTestSteps.STEP_ID_EVENT_MLS.equals(stepId)) {
										getDeviceSimulator().publishEvent(MultiLevelSensor.class.getName(), MultiLevelSensor.PROPERTY_DATA);
									} else
										if (FunctionsTestSteps.STEP_ID_EVENT_MLC.equals(stepId)) {
											getDeviceSimulator().publishEvent(MultiLevelControl.class.getName(), MultiLevelControl.PROPERTY_DATA);
										}
		return null;
	}

	private Device registerNewDevice(String[] functionClassNames) {
		DeviceSimulator deviceSimulator = getDeviceSimulator();
		Dictionary deviceProps = new Hashtable();
		String deviceUID = DEVICE_UID_PREFIX + deviceCounter++;
		deviceProps.put(Device.SERVICE_UID, deviceUID);
		deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_PROCESSING);
		deviceProps.put(Device.SERVICE_STATUS_DETAIL, Device.STATUS_DETAIL_CONNECTING);
		deviceProps.put(Device.SERVICE_DRIVER, deviceUID + "-driver");
		deviceProps.put(Device.SERVICE_NAME, deviceUID + "-name");
		deviceProps.put(Device.SERVICE_DESCRIPTION, deviceUID + "-description");
		deviceProps.put(Device.SERVICE_DRIVER, deviceUID + "-driver");
		deviceProps.put(Device.SERVICE_FIRMWARE_VENDOR, deviceUID + "-firmware_vendor");
		deviceProps.put(Device.SERVICE_FIRMWARE_VERSION, deviceUID + "-firmware_version");
		deviceProps.put(Device.SERVICE_HARDWARE_VENDOR, deviceUID + "-hardware_vendor");
		deviceProps.put(Device.SERVICE_HARDWARE_VERSION, deviceUID + "-hardware_version");
		deviceProps.put(Device.SERVICE_MODEL, deviceUID + "-model");
		deviceProps.put(Device.SERVICE_SERIAL_NUMBER, deviceUID + "-serial_number");
		deviceProps.put(Device.SERVICE_TYPES, new String[] {deviceUID + "-type"});
		deviceProps.put(Device.SERVICE_REFERENCE_UIDS, new String[] {"fake-ref-uid"});
		Dictionary[] functionProps = null;
		if (null != functionClassNames) {
			functionProps = new Dictionary[functionClassNames.length];
			for (int i = 0; i < functionProps.length; i++) {
				String functionUID = deviceUID + ':' + i;
				functionProps[i] = new Hashtable();
				functionProps[i].put(Constants.OBJECTCLASS, functionClassNames[i]);
				functionProps[i].put(Function.SERVICE_UID, functionUID);
				functionProps[i].put(Function.SERVICE_VERSION, functionUID + "-version");
				functionProps[i].put(Function.SERVICE_DESCRIPTION, functionUID + "-description");
				functionProps[i].put(Function.SERVICE_DEVICE_UID, deviceUID);
				functionProps[i].put(Function.SERVICE_TYPE, functionUID + "-type");
				if (i != 0) {
					String[] referenceFunctions = new String[i];
					for (int ii = 0; ii < i; ii++) {
						referenceFunctions[ii] = (String) functionProps[ii].get(Function.SERVICE_UID);
					}
					functionProps[i].put(Function.SERVICE_REFERENCE_UIDS, referenceFunctions);
				}
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

/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
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
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The bundle activator.
 */
public final class Activator implements BundleActivator { // NO_UCD

	private static final String	DEVICE_UID_PREFIX		= "dal-simulator:";
	private static final String	FUNCTION_TYPE_SIMULATOR	= "Simulator";
	private static final int	DEVICE_COUNT			= 10;

	private DeviceSimulatorImpl	deviceSimulator;
	private ServiceTracker<EventAdmin,EventAdmin>	eventAdminTracker;
	private Timer				timer;

	@Override
	public void start(BundleContext bc) {
		this.timer = new Timer();
		this.eventAdminTracker = new ServiceTracker<>(bc, EventAdmin.class,
				null);
		this.eventAdminTracker.open();
		this.deviceSimulator = new DeviceSimulatorImpl(bc, this.eventAdminTracker, this.timer);
		this.deviceSimulator.start();
		registerInitialDevices();
	}

	@Override
	public void stop(BundleContext bc) {
		this.deviceSimulator.stop();
		this.eventAdminTracker.close();
		this.timer.cancel();
	}

	private void registerInitialDevices() {
		String[] referenceDeviceUIDs = new String[DEVICE_COUNT];
		for (int i = 0; i < DEVICE_COUNT; i++) {
			String deviceUID = getDeviceUID(i);
			referenceDeviceUIDs[i] = deviceUID;
			Dictionary<String,Object> deviceProps = new Hashtable<>(13, 1F);
			if (i > 0) {
				String[] referenceUIDs = new String[i];
				System.arraycopy(referenceDeviceUIDs, 0, referenceUIDs, 0, i);
				deviceProps.put(Device.SERVICE_REFERENCE_UIDS, referenceUIDs);
			}
			if (0 == (i % 2)) {
				deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
			} else {
				deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_OFFLINE);
				deviceProps.put(Device.SERVICE_STATUS_DETAIL, Device.STATUS_DETAIL_BROKEN);
			}
			deviceProps.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, Device.DEVICE_CATEGORY);
			deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
			deviceProps.put(Device.SERVICE_UID, deviceUID);
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
			this.deviceSimulator.registerDevice(deviceProps, getFunctionProperties(deviceUID));
		}
	}

	private Dictionary<String,Object>[] getFunctionProperties(
			String deviceUID) {
		@SuppressWarnings("unchecked")
		Dictionary<String,Object>[] functionProps = new Dictionary[8];
		String[] referenceFunctionUIDs = new String[functionProps.length - 1];

		// setup the boolean control
		functionProps[0] = getFunctionProps(
				deviceUID, BooleanControl.class.getName(), 0, referenceFunctionUIDs);
		referenceFunctionUIDs[0] = (String) functionProps[0].get(Function.SERVICE_UID);

		// setup the boolean sensor
		functionProps[1] = getFunctionProps(
				deviceUID, BooleanSensor.class.getName(), 1, referenceFunctionUIDs);
		referenceFunctionUIDs[1] = (String) functionProps[1].get(Function.SERVICE_UID);

		// setup the multi-level sensor
		functionProps[2] = getFunctionProps(
				deviceUID, MultiLevelSensor.class.getName(), 2, referenceFunctionUIDs);
		referenceFunctionUIDs[2] = (String) functionProps[2].get(Function.SERVICE_UID);

		// setup the multi-level control
		functionProps[3] = getFunctionProps(
				deviceUID, MultiLevelControl.class.getName(), 3, referenceFunctionUIDs);
		referenceFunctionUIDs[3] = (String) functionProps[3].get(Function.SERVICE_UID);

		// setup meter control
		functionProps[4] = getFunctionProps(
				deviceUID, Meter.class.getName(), 4, referenceFunctionUIDs);
		functionProps[4].put(Meter.SERVICE_FLOW, Meter.FLOW_OUT);
		referenceFunctionUIDs[4] = (String) functionProps[4].get(Function.SERVICE_UID);

		// setup wake up
		functionProps[5] = getFunctionProps(
				deviceUID, WakeUp.class.getName(), 5, referenceFunctionUIDs);
		referenceFunctionUIDs[5] = (String) functionProps[5].get(Function.SERVICE_UID);

		// setup alarm
		functionProps[6] = getFunctionProps(
				deviceUID, Alarm.class.getName(), 6, referenceFunctionUIDs);
		referenceFunctionUIDs[6] = (String) functionProps[6].get(Function.SERVICE_UID);

		// setup keypad
		functionProps[7] = getFunctionProps(
				deviceUID, Keypad.class.getName(), 7, referenceFunctionUIDs);

		return functionProps;
	}

	private static Dictionary<String,Object> getFunctionProps(
			String deviceUID, String functionClass, int index, String[] referenceFunctionUIDs) {
		Dictionary<String,Object> functionProps = new Hashtable<>();
		if (index > 0) {
			String[] referenceFunctionUIDsLocal = new String[index];
			System.arraycopy(referenceFunctionUIDs, 0, referenceFunctionUIDsLocal, 0, index);
			functionProps.put(Function.SERVICE_REFERENCE_UIDS, referenceFunctionUIDsLocal);
		}
		String functionUID = deviceUID + ":function:" + index;
		functionProps.put(Function.SERVICE_UID, functionUID);
		functionProps.put(Constants.OBJECTCLASS, functionClass);
		functionProps.put(Function.SERVICE_DESCRIPTION, functionUID + "-description");
		functionProps.put(Function.SERVICE_DEVICE_UID, deviceUID);
		functionProps.put(Function.SERVICE_TYPE, FUNCTION_TYPE_SIMULATOR);
		functionProps.put(Function.SERVICE_VERSION, functionUID + "-version");
		return functionProps;
	}

	private static String getDeviceUID(int index) {
		return DEVICE_UID_PREFIX + index;
	}
}

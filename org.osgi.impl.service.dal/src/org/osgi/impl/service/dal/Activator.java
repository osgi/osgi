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


package org.osgi.impl.service.dal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.functions.SimulatedBooleanControl;
import org.osgi.impl.service.dal.functions.SimulatedBooleanSensor;
import org.osgi.impl.service.dal.functions.SimulatedMeter;
import org.osgi.impl.service.dal.functions.SimulatedMultiLevelControl;
import org.osgi.impl.service.dal.functions.SimulatedMultiLevelSensor;
import org.osgi.impl.service.dal.functions.SimulatedWakeUp;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The bundle activator.
 */
public final class Activator implements BundleActivator {

	private static final String	DEVICE_UID_PREFIX	= "dal-simulator:";
	private static final String	FUNCTION_TYPE_SIMULATOR	= "Simulator";
	private static final int	DEVICE_COUNT	= 10;

	private SimulatedDevice[]	simulatedDevices;
	private DeviceSimulatorImpl	deviceSimulator;
	private ServiceTracker		eventAdminTracker;
	private Timer				timer;

	public void start(BundleContext bc) throws Exception {
		this.timer = new Timer();
		this.eventAdminTracker = new ServiceTracker(bc, EventAdmin.class.getName(), null);
		this.eventAdminTracker.open();
		registerDevices(bc);
		this.deviceSimulator = new DeviceSimulatorImpl(bc, this.eventAdminTracker, this.timer);
		this.deviceSimulator.start();
	}

	public void stop(BundleContext bc) throws Exception {
		for (int i = 0; i < this.simulatedDevices.length; i++) {
			DeviceUtil.silentDeviceRemove(this.simulatedDevices[i]);
		}
		this.deviceSimulator.stop();
		this.eventAdminTracker.close();
		this.timer.cancel();
	}

	private void registerDevices(BundleContext bc) {
		this.simulatedDevices = new SimulatedDevice[DEVICE_COUNT];
		final String[] referenceDeviceUIDs = new String[this.simulatedDevices.length];
		for (int i = 0; i < this.simulatedDevices.length; i++) {
			final String deviceUID = getDeviceUID(i);
			referenceDeviceUIDs[i] = deviceUID;
			final Dictionary deviceProps = new Hashtable(13, 1F);
			if (i > 0) {
				final String[] referenceUIDs = new String[i];
				System.arraycopy(referenceDeviceUIDs, 0, referenceUIDs, 0, i);
				deviceProps.put(Device.SERVICE_REFERENCE_UIDS, referenceUIDs);
			}
			if (0 == (i % 2)) {
				deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
			} else {
				deviceProps.put(Device.SERVICE_STATUS, Device.STATUS_OFFLINE);
				deviceProps.put(Device.SERVICE_STATUS_DETAIL, Device.STATUS_DETAIL_DEVICE_BROKEN);
			}
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
			this.simulatedDevices[i] = new SimulatedDevice(
					deviceProps,
					bc,
					registerFunctions(deviceUID, bc));
		}
	}

	private SimulatedFunction[] registerFunctions(String deviceUID, BundleContext bc) {
		SimulatedFunction[] functions = new SimulatedFunction[6];
		String[] referenceFunctionUIDs = new String[functions.length - 1];

		// setup the boolean control
		functions[0] = new SimulatedBooleanControl(
				getFunctionProps(deviceUID, 0, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker);
		referenceFunctionUIDs[0] = (String) functions[0].getServiceProperty(Function.SERVICE_UID);

		// setup the booelean sensor
		functions[1] = new SimulatedBooleanSensor(
				getFunctionProps(deviceUID, 1, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker);
		referenceFunctionUIDs[1] = (String) functions[1].getServiceProperty(Function.SERVICE_UID);

		// setup the multi-level sensor
		functions[2] = new SimulatedMultiLevelSensor(
				getFunctionProps(deviceUID, 2, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker);
		referenceFunctionUIDs[2] = (String) functions[2].getServiceProperty(Function.SERVICE_UID);

		// setup the multi-level control
		functions[3] = new SimulatedMultiLevelControl(
				getFunctionProps(deviceUID, 3, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker);
		referenceFunctionUIDs[3] = (String) functions[3].getServiceProperty(Function.SERVICE_UID);

		// setup meter control
		functions[4] = new SimulatedMeter(
				getFunctionProps(deviceUID, 4, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker);
		referenceFunctionUIDs[4] = (String) functions[4].getServiceProperty(Function.SERVICE_UID);

		// setup wake up
		functions[5] = new SimulatedWakeUp(
				getFunctionProps(deviceUID, 5, referenceFunctionUIDs),
				bc,
				this.eventAdminTracker,
				this.timer);

		return functions;
	}

	private static Dictionary getFunctionProps(
			String deviceUID, int index, String[] referenceFunctionUIDs) {
		final Dictionary functionProps = new Hashtable();
		if (index > 0) {
			String[] referenceFunctionUIDsLocal = new String[index];
			System.arraycopy(referenceFunctionUIDs, 0, referenceFunctionUIDsLocal, 0, index);
			functionProps.put(Function.SERVICE_REFERENCE_UIDS, referenceFunctionUIDsLocal);
		}
		final String functionUID = deviceUID + ":function:" + index;
		functionProps.put(Function.SERVICE_UID, functionUID);
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

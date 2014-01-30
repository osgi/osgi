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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.simulator.DeviceSimulator;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Device simulator implementation.
 */
public class DeviceSimulatorImpl implements DeviceSimulator {

	private static final Class[]	FUNCTION_CONSTRUCTOR_ARGS	= new Class[] {
																Dictionary.class,
																BundleContext.class,
																ServiceTracker.class
																};

	private final BundleContext	bc;
	private final ServiceTracker	eventAdminTracker;
	private final Object		lock	= new Object();

	private List				registeredDevices;
	private ServiceRegistration	deviceSimulatorSReg;

	/**
	 * Constructs the device simulator implementation with the given arguments.
	 * 
	 * @param bc The bundle context.
	 * @param eventAdminTracker The event admin tracker.
	 */
	public DeviceSimulatorImpl(final BundleContext bc, final ServiceTracker eventAdminTracker) {
		this.registeredDevices = new ArrayList();
		this.bc = bc;
		this.eventAdminTracker = eventAdminTracker;
	}

	/*
	 * All device function implementations must provide a constructor with three
	 * parameters: properties, bundle context and event admin tracker.
	 */
	public Device registerDevice(Dictionary deviceProps, Dictionary[] functionProps) {
		if (null == deviceProps) {
			throw new NullPointerException("The device properties are null.");
		}
		SimulatedDeviceFunction[] deviceFunctions = null;
		if (null != functionProps) {
			deviceFunctions = new SimulatedDeviceFunction[functionProps.length];
			for (int i = 0; i < functionProps.length; i++) {
				Class functionClass;
				try {
					functionClass = Class.forName(
							getSimulatedFunctionClass((String) functionProps[i].get(Constants.OBJECTCLASS)));
					Constructor functionConstructor = functionClass.getConstructor(
							FUNCTION_CONSTRUCTOR_ARGS);
					deviceFunctions[i] = (SimulatedDeviceFunction) functionConstructor.newInstance(
							new Object[] {functionProps[i], this.bc, this.eventAdminTracker});
				} catch (Exception e) {
					for (int ii = 0; ii < i; ii++) {
						deviceFunctions[ii].remove();
					}
					throw new IllegalArgumentException("The device function type is not supported: " +
							(String) functionProps[i].get(Constants.OBJECTCLASS));
				}
			}
		}
		Device device = new SimulatedDevice(deviceProps, this.bc, deviceFunctions);
		synchronized (this.lock) {
			if (null != this.registeredDevices) {
				this.registeredDevices.add(device);
				return device;
			}
		}
		DeviceUtil.silentDeviceRemove(device);
		throw new IllegalStateException("The device simulator service is unregistred.");
	}

	/**
	 * Starts the simulator.
	 */
	public void start() {
		this.deviceSimulatorSReg = this.bc.registerService(DeviceSimulator.class.getName(), this, null);
	}

	/**
	 * Stops the simulator.
	 */
	public void stop() {
		synchronized (this.lock) {
			for (int i = 0, size = this.registeredDevices.size(); i < size; i++) {
				DeviceUtil.silentDeviceRemove((Device) this.registeredDevices.get(i));
			}
			this.registeredDevices = null;
		}
		this.deviceSimulatorSReg.unregister();
	}

	private static String getSimulatedFunctionClass(String functionClass) {
		return "org.osgi.impl.service.dal.functions.Simulated" +
				functionClass.substring(functionClass.lastIndexOf('.') + 1);
	}

}

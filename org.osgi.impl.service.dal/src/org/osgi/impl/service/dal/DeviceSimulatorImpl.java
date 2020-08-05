/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Timer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dal.simulator.DeviceSimulator;
import org.osgi.service.dal.Device;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Device simulator implementation.
 */
class DeviceSimulatorImpl implements DeviceSimulator {

	private static final Class< ? >[]					FUNCTION_CONSTRUCTOR_ARGS		= new Class[] {
																	Dictionary.class,
																	BundleContext.class,
																	ServiceTracker.class
																	};

																	private static final Class< ? >[] FUNCTION_CONSTRUCTOR_ARGS_TIMER = new Class[] {
																	Dictionary.class,
																	BundleContext.class,
																	ServiceTracker.class,
																	Timer.class
																	};

	private final BundleContext		bc;
	private final ServiceTracker<EventAdmin,EventAdmin>	eventAdminTracker;
	private final Timer				timer;
	private final Object			lock							= new Object();

	private List<Device>								registeredDevices;
	private ServiceRegistration<DeviceSimulator>		deviceSimulatorSReg;

	/**
	 * Constructs the device simulator implementation with the given arguments.
	 * 
	 * @param bc The bundle context.
	 * @param eventAdminTracker The event admin tracker.
	 * @param timer The timer used by some functions.
	 */
	DeviceSimulatorImpl(BundleContext bc,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker,
			Timer timer) {
		this.registeredDevices = new ArrayList<>();
		this.bc = bc;
		this.eventAdminTracker = eventAdminTracker;
		this.timer = timer;
	}

	/*
	 * All function implementations must provide a constructor with three
	 * parameters: properties, bundle context and event admin tracker.
	 */
	@Override
	public Device registerDevice(Dictionary<String,Object> deviceProps,
			Dictionary<String,Object>[] functionProps) {
		if (null == deviceProps) {
			throw new NullPointerException("The device properties are null."); // NOPMD
		}
		SimulatedFunction[] functions = null;
		if (null != functionProps) {
			functions = new SimulatedFunction[functionProps.length];
			for (int i = 0; i < functionProps.length; i++) {
				Class< ? > functionClass;
				try {
					functionClass = Class.forName(
							getSimulatedFunctionClass((String) functionProps[i].get(Constants.OBJECTCLASS)));
					functions[i] = newFunctionInstance(functionClass, functionProps[i]);
				} catch (Exception e) {
					for (int ii = 0; ii < i; ii++) {
						functions[ii].remove();
					}
					throw new IllegalArgumentException("The function type is not supported: " + // NOPMD
							(String) functionProps[i].get(Constants.OBJECTCLASS));
				}
			}
		}
		Device device = new SimulatedDevice(deviceProps, this.bc, functions);
		synchronized (this.lock) {
			if (null != this.registeredDevices) {
				this.registeredDevices.add(device);
				return device;
			}
		}
		DeviceUtil.silentDeviceRemove(device);
		throw new IllegalStateException("The device simulator service is unregistred.");
	}

	@Override
	public void publishEvent(String functionClassName, String propertyName) {
		synchronized (this.lock) {
			if (null == this.registeredDevices) {
				throw new IllegalStateException("The device simulator service is unregistred.");
			}
			for (int i = 0, size = this.registeredDevices.size(); i < size; i++) {
				SimulatedFunction[] functions = ((SimulatedDevice) this.registeredDevices.get(i)).getFunctions(functionClassName);
				if (null != functions) {
					functions[0].publishEvent(propertyName);
					return;
				}
			}
			throw new IllegalArgumentException("There is no function with the given class name: " + functionClassName);
		}
	}

	/**
	 * Starts the simulator.
	 */
	void start() {
		this.deviceSimulatorSReg = this.bc
				.registerService(DeviceSimulator.class, this, null);
	}

	/**
	 * Stops the simulator.
	 */
	void stop() {
		synchronized (this.lock) {
			for (int i = 0, size = this.registeredDevices.size(); i < size; i++) {
				DeviceUtil.silentDeviceRemove(this.registeredDevices.get(i));
			}
			this.registeredDevices = null;
		}
		this.deviceSimulatorSReg.unregister();
	}

	private SimulatedFunction newFunctionInstance(Class< ? > functionClass,
			Dictionary<String,Object> functionProps)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException,
			NoSuchMethodException {
		try {
			Constructor< ? > functionConstructor = functionClass
					.getConstructor(FUNCTION_CONSTRUCTOR_ARGS);
			return (SimulatedFunction) functionConstructor.newInstance(
					new Object[] {functionProps, this.bc, this.eventAdminTracker});
		} catch (NoSuchMethodException nsme) {
			Constructor< ? > functionConstructor = functionClass
					.getConstructor(FUNCTION_CONSTRUCTOR_ARGS_TIMER);
			return (SimulatedFunction) functionConstructor.newInstance(
					new Object[] {functionProps, this.bc, this.eventAdminTracker, this.timer});
		}
	}

	private static String getSimulatedFunctionClass(String functionClass) {
		return "org.osgi.impl.service.dal.functions.Simulated" +
				functionClass.substring(functionClass.lastIndexOf('.') + 1);
	}
}

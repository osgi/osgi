/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.step;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.support.step.TestStepProxy;

/**
 * The class is wrapper over {@code TestStepProxy}. It simplifies the usage of
 * {@code TestStep} service in Device Abstraction Layer test cases.
 */
public final class TestStepDeviceProxy implements ServiceListener, EventHandler {

	private static final String			FILTER	= "(|(" + Constants.OBJECTCLASS + '=' +
														Device.class.getName() + ")(" + Constants.OBJECTCLASS + '=' +
														Function.class.getName() + "))";

	private final BundleContext			bc;
	private final TestStepProxy			testStepProxy;
	private final ServiceRegistration<EventHandler>	handlerSReg;

	private volatile boolean			printEvent;

	/**
	 * Constructs a new proxy instance.
	 * 
	 * @param bc The bundle context is used for an initialization.
	 */
	public TestStepDeviceProxy(BundleContext bc) {
		this.bc = bc;
		this.testStepProxy = new TestStepProxy(bc);
		try {
			this.bc.addServiceListener(this, FILTER);
		} catch (InvalidSyntaxException e) {
			// correct filter is used
		}
		Dictionary<String,Object> regProps = new Hashtable<>(1, 1F);
		regProps.put(EventConstants.EVENT_TOPIC, FunctionEvent.TOPIC_PROPERTY_CHANGED);
		this.handlerSReg = bc.registerService(EventHandler.class, this,
				regProps);
	}

	/**
	 * Executes the step with {@code TestStep} service. Helpful events are
	 * printed during the step execution.
	 * 
	 * @param stepId The step identifier.
	 * @param userPrompt The step human readable message.
	 */
	public void execute(String stepId, String userPrompt) {
		this.printEvent = true;
		try {
			this.testStepProxy.execute(stepId, userPrompt);
		} finally {
			this.printEvent = false;
		}
	}

	/**
	 * Releases the proxy allocated resources.
	 */
	public void close() {
		this.bc.removeServiceListener(this);
		this.handlerSReg.unregister();
		this.testStepProxy.close();
	}

	/**
	 * Prints the received registered event for a device or function.
	 * 
	 * @param event The service event to be printed.
	 * 
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		if (printEvent && (ServiceEvent.REGISTERED == event.getType())) {
			ServiceReference< ? > sRef = event.getServiceReference();
			String message = toFunctionString(sRef);
			if (null != message) {
				System.out.println(message);
				return;
			}
			message = toDeviceString(sRef);
			if (null != message) {
				System.out.println(message);
				return;
			}
		}
	}

	/**
	 * Prints the received event for a function property change.
	 * 
	 * @param event The event to be printed.
	 * 
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	public void handleEvent(Event event) {
		if (event instanceof FunctionEvent) {
			System.out.println(toPropertyChangeString((FunctionEvent) event));
		}
	}

	private static String toPropertyChangeString(FunctionEvent functionEvent) {
		return '[' + functionEvent.getFunctionUID() + "][" + functionEvent.getFunctionPropertyName() +
				"] Property has been changed to: " + functionEvent.getFunctionPropertyValue();
	}

	private static String toFunctionString(ServiceReference< ? > sRef) {
		String functionUID = (String) sRef.getProperty(Function.SERVICE_UID);
		if (null == functionUID) {
			return null;
		}
		String deviceUID = (String) sRef.getProperty(Function.SERVICE_DEVICE_UID);
		return '[' + functionUID + "][" + ((String[]) sRef.getProperty(Constants.OBJECTCLASS))[0] +
				"] Function has been registered" +
				((null == deviceUID) ? "" : " for [" + deviceUID + "] device") + '.';
	}

	private static String toDeviceString(ServiceReference< ? > sRef) {
		String deviceUID = (String) sRef.getProperty(Device.SERVICE_UID);
		if (null == deviceUID) {
			return null;
		}
		return '[' + deviceUID + "] Device has been registered.";
	}
}

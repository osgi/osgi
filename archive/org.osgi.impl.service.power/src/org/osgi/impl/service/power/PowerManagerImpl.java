/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.power;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;
import org.osgi.service.power.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * 
 * @author Olivier Pav�é, Siemens AG
 * @version $Revision$
 */
public class PowerManagerImpl implements PowerManager {

	private int					currentPowerState;

	private BundleContext		context;

	private ServiceTracker		eventTracker;

	private ServiceRegistration	reg;

	private static final String	EVENT_TOPIC_S0		= "org/osgi/service/power/SystemEvent/S0";

	private static final String	EVENT_TOPIC_TO_S1	= "org/osgi/service/power/SystemEvent/TO_S1";

	private static final String	EVENT_TOPIC_TO_S2	= "org/osgi/service/power/SystemEvent/TO_S2";

	private static final String	EVENT_TOPIC_TO_S3	= "org/osgi/service/power/SystemEvent/TO_S3";

	private static final String	EVENT_TOPIC_TO_S4	= "org/osgi/service/power/SystemEvent/TO_S4";

	private static final String	EVENT_TOPIC_TO_S5	= "org/osgi/service/power/SystemEvent/TO_S5";

	private static final String	EVENT_TOPIC_UNKNOWN	= "unknown System Power State";

	/**
	 * Create a new instance of PowerManager implementation
	 * 
	 * @param context The bundle context
	 */
	public PowerManagerImpl(BundleContext context, ServiceRegistration reg) {
		this.context = context;
		this.reg = reg;
		eventTracker = new ServiceTracker(context, EventAdmin.class.getName(),
				null);
		eventTracker.open();
		currentPowerState = PowerManager.S0;
	}

	/**
	 * Returns the current system power state.
	 * 
	 * @return the current system power state.
	 */
	public int getPowerState() {
		return currentPowerState;
	}

	/**
	 * <p>
	 * Changes the system power state with the given value.
	 * 
	 * <p>
	 * The state must be one of the system power state values.
	 * 
	 * <p>
	 * if <code>urgent</code> is set to <code>true</code>, all interested
	 * {@link PowerHandler}services are asked for request permission by calling
	 * {@link PowerHandler#handleQuery(int)}method. In case of veto from a
	 * {@link PowerHandler}, all previouly notified {@link PowerHandler}s must
	 * be called on {@link PowerHandler#handleQueryFailed(int)}to inform that
	 * the transition has been rejected. Then the method ends and
	 * <code>false</code> is returned. If not, the process continues.
	 * 
	 * Then, Power Manager sents the appropriate event to
	 * {@link EventAdmin#sendEvent(Event)}for publishing the corresponding
	 * power state change.
	 * 
	 * @param state the state into which the system is going to transit.
	 * @param urgent <code>true</code> to force the system to change state
	 *        without requesting any permission from {@link PowerHandler}s.
	 *        <code>true</code> to query all {@link PowerHandler}s for
	 *        permission. In case of at least one of the {@link PowerHandler}s
	 *        denies the query, the Power Manager informs in reverse order all
	 *        {@link PowerHandler}s that the query failed and an array of
	 *        {@link ServiceReference}s which rejected the request is returned.
	 *        In circumstances like a low power or emergency shutdown,
	 *        <code>urgent</code> should be set to <code>true</code>.
	 * @return array of {@link PowerHandler}s {@link ServiceReference}s which
	 *         have denied the request; <code>null</code> otherwize.
	 * @throws IllegalArgumentException if the given state value is not one of
	 *         the system power states or the transition is not allowed.
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         appropriate <code>PowerPermission[system,setSystemPower]</code>,
	 *         and the Java Runtime Environment supports permissions.
	 */
	public synchronized ServiceReference[] setPowerState(int state,
			boolean urgent) {
		ServiceReference refs[] = null;
		PowerHandler handler;
		boolean queryFailed = false;
		Vector failedRefs = null;

		// Check if the caller has the permission to do it
		// First check "*" then check "system" if the first test fails
		AccessController.checkPermission(new PowerPermission(
				PowerPermission.SET_SYSTEM_POWER, "system"));

		// Check if the power state is a valid one and different from the
		// previous one
		if ((state < PowerManager.S0) || (state > PowerManager.S5)
				|| (currentPowerState == state))
			throw (new IllegalArgumentException());

		// Check if the transition request is a valid one
		// The following transitions are not allowed:
		// S5 (SOFT_OFF) -> Sx (1-4 SLEEPING)
		// Sx (1-4 SLEEPING) -> S5 (SOFT_OFF)
		if ((currentPowerState == PowerManager.S5)
				&& ((state >= PowerManager.S1) && (state <= PowerManager.S4)))
			throw (new IllegalArgumentException());

		if (((currentPowerState >= PowerManager.S1) && (currentPowerState <= PowerManager.S4))
				&& (state == PowerManager.S5))
			throw (new IllegalArgumentException());

		// The transition is to S1-S5 and it is not urgent
		// All power hanlders must be queried
		try {
			if ((state != PowerManager.S0) && !urgent) {
				refs = context.getServiceReferences(PowerHandler.class
						.getName(), null);

				if (refs != null) {
					boolean accepted = true;
					for (int i = 0; i < refs.length; i++) {
						handler = (PowerHandler) context.getService(refs[i]);

						// Retrieve and check if a value is provided for this
						// transition
						// Check if the power state is supported by the device
						if (handler != null) {
							try {
								accepted = handler.handleQuery(state);
							}
							catch (Throwable t) {
								log(
										LogService.LOG_ERROR,
										"Error while sending request to Power Handlers",
										t);
							}
							if (!accepted) {
								queryFailed = true;
								if (failedRefs == null)
									failedRefs = new Vector();
								failedRefs.add(refs[i]);
							}
						}
					}
				}
			}
		}
		catch (InvalidSyntaxException ise) {
		}

		// Informs in reverse order all handlers that the transition is denied
		if (queryFailed) {
			if (refs.length > 0) {
				for (int i = refs.length - 1; i == 0; i--) {
					handler = (PowerHandler) context.getService(refs[i]);
					if (handler != null) {
						try {
							handler.handleQueryFailed(state);
						}
						catch (Throwable t) {
							log(
									LogService.LOG_ERROR,
									"Error while sending cancelation request to Power Handlers",
									t);
						}
					}
				}
			}

			return (ServiceReference[]) failedRefs.toArray();
		}

		// Start the transition
		// Get the service tracker

		switch (state) {
			// The system is starting up or resuming
			// Devices must be set to DO and event handlers must be informed
			case PowerManager.S0 :
				// Sets the current state
				currentPowerState = state;

				// Set power state to D0 by default
				handleDevices(state);

				// Send event to handlers
				sendEvent(state);
				break;

			case PowerManager.S1 :
			case PowerManager.S2 :
			case PowerManager.S3 :
			case PowerManager.S4 :
			case PowerManager.S5 :
				// Send event to handlers
				sendEvent(state);

				// Set power state to D3 by default
				handleDevices(state);

				// Sets the current state
				currentPowerState = state;
				break;

			default :
				break;
		}

		return null;
	}

	/**
	 * Sets the device power state to the registered device power
	 * 
	 * @param systemPowerState The new system power state.
	 */
	private void handleDevices(int systemPowerState) {
		ServiceReference refs[];
		int devicePowerState;
		DevicePower device;
		
		try {
			refs = context.getServiceReferences(DevicePower.class.getName(),
					null);

			for (int i = 0; i < refs.length; i++) {
				if (refs[i] == null)
					continue;

				device = (DevicePower) context.getService(refs[i]);
				try {
					// Determine the right device power state and set it to the
					// device
					devicePowerState = determineDevicePowerState(refs[i],
							systemPowerState);
					if ((devicePowerState != DevicePower.UNSPECIFIED_STATE)
							&& (device != null))
						setDevicePowerState(device, devicePowerState);
				}
				catch (IllegalArgumentException iae) {
					log(
							LogService.LOG_ERROR,
							"Error while setting a new power state to the device",
							iae);
				}
				context.ungetService(refs[i]);
			}
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	/**
	 * Determines the device power state to set according to the new system
	 * power state, the device power mapping and device power capabilities.
	 * 
	 * @param ref The ServiceReference of the DevicePower service.
	 * @param systemPowerState The new system power state
	 * @return the new device power state to set.
	 */
	private int determineDevicePowerState(ServiceReference ref,
			int systemPowerState) {
		int devicePowerState = DevicePower.UNSPECIFIED_STATE;
		int[] mappings;
		Integer capa;

		mappings = (int[]) (ref.getProperty(DevicePower.DEVICE_POWER_MAPPING));
		capa = (Integer) (ref
				.getProperty(DevicePower.DEVICE_POWER_CAPABILITIES));

		// Does the device define a device power state for this transition
		if ((mappings != null)
				&& (mappings.length == 6)
				&& (mappings[systemPowerState] != DevicePower.UNSPECIFIED_STATE)) {
			devicePowerState = mappings[systemPowerState];
		}

		// If a device power state is not defined then we take the default value
		if (devicePowerState == DevicePower.UNSPECIFIED_STATE) {
			switch (systemPowerState) {
				// In case of transition to S0, devices must be in D0 by default
				case PowerManager.S0 :
					devicePowerState = DevicePower.D0;
					break;
				// In case of other transitions the device must in D3 by default
				case PowerManager.S1 :
				case PowerManager.S2 :
				case PowerManager.S3 :
				case PowerManager.S4 :
				case PowerManager.S5 :
					devicePowerState = DevicePower.D3;
					break;
			}
		}

		// Check if this state is supported by the device
		if ((devicePowerState & capa.intValue()) != devicePowerState)
			devicePowerState = DevicePower.UNSPECIFIED_STATE;

		return devicePowerState;
	}

	/**
	 * Create the event topic according to the transition that has occured or
	 * will occur.
	 * 
	 * @param systemPowerState The new system power state.
	 * @return the event topic.
	 */
	private String eventTopic(int systemPowerState) {
		String eventTopic;

		switch (systemPowerState) {
			case PowerManager.S0 :
				eventTopic = EVENT_TOPIC_S0;
				break;
			case PowerManager.S1 :
				eventTopic = EVENT_TOPIC_TO_S1;
				break;
			case PowerManager.S2 :
				eventTopic = EVENT_TOPIC_TO_S2;
				break;
			case PowerManager.S3 :
				eventTopic = EVENT_TOPIC_TO_S3;
				break;
			case PowerManager.S4 :
				eventTopic = EVENT_TOPIC_TO_S4;
				break;
			case PowerManager.S5 :
				eventTopic = EVENT_TOPIC_TO_S5;
				break;
			default :
				eventTopic = EVENT_TOPIC_UNKNOWN;
				break;
		}

		return eventTopic;
	}

	private void setDevicePowerState(final DevicePower device, final int devicePowerState) {

		// Send event to handlers
		try {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					device.setPowerState(devicePowerState);
					return null;
				}
			});
		}
		catch (Throwable t) {
			log(
					LogService.LOG_ERROR,
					"Error while setting a new power state to the device",
					t);
		}
	}
	
	
	private void sendEvent(final int systemPowerState) {

		// Send event to handlers
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				EventAdmin eventAdmin = (EventAdmin) eventTracker.getService();

				if (eventAdmin == null)
					return null;

				Hashtable props = new Hashtable();
				props.put("bundle", context.getBundle());
				props.put(EventConstants.SERVICE, reg.getReference());
				props.put(EventConstants.TIMESTAMP, new Long(System
						.currentTimeMillis()));
				eventAdmin.sendEvent(new Event(eventTopic(systemPowerState),
						props));
				return null;
			}
		});
	}

	/**
	 * Log a message with the Log service
	 */
	protected void log(int severity, String message, Throwable throwable) {
		ServiceReference serviceRef = context
				.getServiceReference("org.osgi.service.log.LogService");
		LogService logService = (LogService) context.getService(serviceRef);
		if (logService != null) {
			try {
				logService.log(severity, message, throwable);
			}
			finally {
				context.ungetService(serviceRef);
			}
		}
	}
}
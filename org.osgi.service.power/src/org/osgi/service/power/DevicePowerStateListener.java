/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.power;

/**
 * The listener interface for receiving device power events. This interface must be implemented
 * and registered to the framework in order to recieve events.
 * 
 * If a listener wants to filter these events he has to register the property POWER_EVENT_FILTER
 * during listener registration to indicate devices that he wants to listen to. 
 * If the filter syntax is incorrect the filter is ignored.
 * 
 * Note that a <code>PowerStateEvent</code> is sent only if a transition occured. A listener will not have
 * any event while registering if a transition does not occur. 
 */
public interface DevicePowerStateListener 
{
	/**
	 * Property name used to indicate a filter. The value is a String indicating the
	 * filter.
	 * 
	 * The valid constants used for the filter definition are:
	 * Device.ID - The PID of a specific device to listen for events
	 */
	public static final String POWER_EVENT_FILTER = "power.event.filter";
   
   
   /**
    * Invoked when the power state is going to change or changed within the device.
    * 
    * A <code>PowerStateEvent</code> should be delivered before a transition to less power.
    * 
    * A <code>PowerStateEvent</code> should be delivered after a transition to more power.    
    * 
    * @param event the power state event broadcasted to listeners
    * @throws PowerException A <code>PowerException</code> with error code
    * KEEP_CURRENT_STATE may be thrown by the system power listener if the listener 
    * wants to oppose the change and keep the current state. If the urgent flag is set to
    * true then the opposition is not considered by the application, otherwise (false) the 
    * behaviour is application dependent. 
    */
	void devicePowerStateChange(PowerStateEvent event) throws PowerException;
}

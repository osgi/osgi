/*
 * $Header$
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
    * KEEP_CURRENT_STATE may be thrown by the listener if the listener wants to 
    * oppose the current state change event. The listener should use the isUrgent 
    * method in the <code>PowerStateEvent</code> object to examine if the state 
    * change event can be opposed. If the return value from the isUrgent method 
    * call is true, the <code>PowerException</code> thrown by the listener will be ignored 
    * by the event source; otherwise, the exception will be serviced by the
    * event source. The response to the exception is implementation dependent.
    */
	void devicePowerStateChange(PowerStateEvent event) throws PowerException;
}

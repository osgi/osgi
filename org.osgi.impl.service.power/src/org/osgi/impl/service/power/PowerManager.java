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

package org.osgi.impl.service.power;

import java.util.LinkedList;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.power.*;

/**
 * 
 * 
 * @author Olivier Pavé, Siemens AG
 * @version $Revision$
 */
public class PowerManager implements SystemPowerStateListener {
	
	private BundleContext context;
	
	public PowerManager(BundleContext context) {
		this.context = context;
	}

	/**
	 * @param event
	 * @throws PowerException
	 */
	public void systemPowerStateChange(PowerStateEvent event) throws PowerException {
		// TODO Auto-generated method stub
		if (!broadcastPowerStateToDevice(event))
			throw (new PowerException(PowerException.KEEP_CURRENT_STATE));
	}

	private boolean broadcastPowerStateToDevice(PowerStateEvent event) {
    	ServiceReference refs[];
    	DevicePower device;
    	int sysPowerState, devicePowerState;
    	LinkedList devices;
    	
    	sysPowerState = event.getNewState();
    	devices = new LinkedList();
    	
    	// In this implementation SLEEP and PM_ACTIVE states do not change
    	// device power states
    	if ((sysPowerState == SystemPowerState.SLEEP) ||
    		(sysPowerState == SystemPowerState.PM_ACTIVE))
    		return true;
    		
    	try {
    		devicePowerState = determineDevicePowerState(sysPowerState);
    		if (devicePowerState == -1)
    			return true;
    		
    		refs = context.getServiceReferences(DevicePower.class.getName(), null);
        	
    		for (int i=0; i<refs.length; i++) {
    			if (refs[i] == null)
    				continue;
    				
   				device = (DevicePower)context.getService(refs[i]);
   				try {
					device.setPowerState(devicePowerState, event.isUrgent());
					devices.addLast(device);
    			}
    			catch (PowerException pe) {
    				log(LogService.LOG_ERROR, "Error while setting a new power state to the device", pe);
    				
    				switch (pe.getErrorCode()) {
						case PowerException.KEEP_CURRENT_STATE:
							break;
						
    					case PowerException.ILLEGAL_STATE_TRANSITION_REQUEST:
    					case PowerException.STATE_TRANSITION_FAILURE:
    						if (!event.isUrgent()) {
								devicePowerState = determineDevicePowerState(event.getPreviousState());
    							while (!devices.isEmpty()) {
    								device = (DevicePower)devices.removeLast();
    								try {
    									device.setPowerState(devicePowerState, true);
    								}
    								catch (PowerException e) {
    				    				log(LogService.LOG_ERROR, "Error while setting a new power state to the device", e);    									
    								}
    							}
    							return false;
    						}
    						break;
    				}
    			}
    			context.ungetService(refs[i]);
    		}
    	}
    	catch (InvalidSyntaxException ise) {	
			log(LogService.LOG_ERROR, "Error in the syntax to get DevicePower services", ise);
    	}
    	return true;
	}
	
	private int determineDevicePowerState(int sysPowerState) {
		int devicePowerState = -1;
		
		// According to the system power state transition the request to the
		// device is different
		switch (sysPowerState) {
			// In case of transition to FULL_POWER, devices must transit to D0
			case SystemPowerState.FULL_POWER: 
				devicePowerState = DevicePowerState.D0; 
				break; 								
			// In case of transition to SUSPEND, devices must transit to D2
			case SystemPowerState.SUSPEND:
				devicePowerState = DevicePowerState.D2; 
				break;
			// In case of transition to OFF, devices must transit to D3
			case SystemPowerState.OFF:
				devicePowerState = DevicePowerState.D3; 
				break;
		}
		
		return devicePowerState;
	}
	
	protected void log(int severity, String message, Throwable throwable) {
		ServiceReference serviceRef = context.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
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
}

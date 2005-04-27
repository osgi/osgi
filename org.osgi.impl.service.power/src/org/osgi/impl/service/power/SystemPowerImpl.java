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

//import java.security.*;
import java.security.AccessController;
import java.util.LinkedList;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.power.*;

/**
 * Service that takes care of the system power state. 
 * The native implementation of the system power management is usually implemented
 * inside this service or connected to it.
 * 
 * It informs all listeners when the system power changes.
 * 
 * @author Olivier Pav�, Siemens AG
 */
public class SystemPowerImpl implements SystemPower {
	
	private int currentPowerState;
	private BundleContext context;
	
	public SystemPowerImpl(BundleContext context) {
		this.context = context;
		currentPowerState = SystemPowerState.OFF;
	}
	
    /**
     * Returns the current system power state of the system.
     * 
     * @return the current system power state.
     */
    public int getPowerState() {
    	return currentPowerState;
    }
    
    /**
     * Changes the current system power state to the value given as parameter.
     * The implementation of this method must perform the system dependent 
     * security check to guarantee that only privileged programs are allowed 
     * to access or initiate this method call. This is because this method 
     * will initiate system power state transitions. 
     * 
     * @param powerState the power state that the system must transit to
     * @param urgency If urgency is set to true, force the system to change 
     * state regardless of any objections which may be opposed by applications.
     * If urgency is set to false, applications may be able to oppose the state change event. 
     * @throws PowerException
     * @throws IllegalArgumentException
     * 
     * @throws PowerException A <code>PowerException</code> must be thrown with the 
     * error code STATE_TRANSITION_FAILURE if an error is encountered during the state 
     * change process. A <code>PowerException</code> must be thrown with the error code 
     * ILLEGAL_STATE_TRANSITION_REQUEST if un unrecognized "state" value is passed to 
     * the method or when there is an invalid state request transition.
     * @throws java.lang.SecurityException A <code>java.lang.SecurityException</code> 
     * must be thrown if the caller does not have the valid permission to initiate the call. 
     */
    public void setPowerState(int powerState, boolean urgency) throws PowerException {
    	
    	int previousPowerState;
    	PowerStateEvent event;
   	
    	// Check if the caller has the permission to do it
    	// First check "*" then check "system" if the first test fails
    	try {
    		AccessController.checkPermission(new PowerPermission("*"));
    	}
    	catch (SecurityException se) {
    		AccessController.checkPermission(new PowerPermission("system"));
    	}
    	
    	// Check if the power state is a valid one and different from the previous one
    	if ((powerState < SystemPowerState.OFF) ||
			(powerState > SystemPowerState.FULL_POWER) ||
			(currentPowerState == powerState))
    		throw(new PowerException(PowerException.ILLEGAL_STATE_TRANSITION_REQUEST));
    	
    	// Check if the transition request is a valid one
    	// The following transitions are not allowed:
    	// Off -> Suspend, Sleep
    	// Suspend -> Sleep, Off
    	if ((currentPowerState == SystemPowerState.OFF) && 
    		((powerState == SystemPowerState.SLEEP) || (powerState == SystemPowerState.SUSPEND)))
    		throw(new PowerException(PowerException.ILLEGAL_STATE_TRANSITION_REQUEST));
    	    
    	if ((currentPowerState == SystemPowerState.SUSPEND) && 
        		((powerState == SystemPowerState.SLEEP) || (powerState == SystemPowerState.OFF)))
        		throw(new PowerException(PowerException.ILLEGAL_STATE_TRANSITION_REQUEST));
    	
       	// The previousPowerState is the current one
       	previousPowerState = currentPowerState;
       	event = new PowerStateEvent(this, powerState, previousPowerState, urgency);
        	
       	// The power is decreasing then the event is broadcasted before
       	// effective power state change
       	Object monitor = new Object();
       	if (powerState < currentPowerState) {
       		synchronized(monitor) {
       			if (!broadcastPowerStateToListeners(event))
       				throw(new PowerException(PowerException.STATE_TRANSITION_FAILURE));
       			currentPowerState = powerState;
       		}
       	}
       	// The power is increasing then the event is broadcasted after
       	// effective power state change
       	else {
       		synchronized(monitor) {
       			currentPowerState = powerState;
       			broadcastPowerStateToListeners(event);
       		}
       	}   		
   }
    
    /**
     * Broadcast the power state change to all listeners 
     */
    private boolean broadcastPowerStateToListeners(PowerStateEvent event) {
    	ServiceReference refs[];
    	SystemPowerStateListener listener;
    	PowerStateEvent reverseEvent;
    	LinkedList listeners;
    	Bundle bundle;
    	
    	listeners = new LinkedList();
    	
    	try {
    		// Retrieve all system power state listeners registered within the framework
        	refs = context.getServiceReferences(SystemPowerStateListener.class.getName(), null);
        	
        	// If references are null, there is nothing to do
        	if (refs == null)
        		return true;
        	
       		// For all listeners send the event
           	for (int i=0; i<refs.length; i++) {
           		if (refs[i] == null)
           			continue;
           		
           		bundle = refs[i].getBundle();
           		
            	// The bundle must be not null and active
            	if ((bundle != null) && (bundle.getState() == Bundle.ACTIVE)) {
            		listener = (SystemPowerStateListener)context.getService(refs[i]);
            		if (listener == null)
            			continue;
            		try {
            			listener.systemPowerStateChange(event);
            			listeners.addLast(listener);
             		}
            		catch (PowerException pe) {
        				// Log 
        				log(LogService.LOG_ERROR, "Error while sending system power state change to listeners", pe);
        						
        				switch (pe.getErrorCode()) {
            				// These execeptions should not be thrown because
            				// they are meaningless in this context
            				case PowerException.ILLEGAL_STATE_TRANSITION_REQUEST:
            				case PowerException.STATE_TRANSITION_FAILURE:
            					break;
            				// The power state transition is opposed
            				case PowerException.KEEP_CURRENT_STATE:
            					// If the transition is not urgent then the power state transition
            					// must be stopped and reversed only if the power is decreasing
            					if (!event.isUrgent() && (event.getNewState() < event.getPreviousState())) {
            			       		reverseEvent = new PowerStateEvent(	this, 
            			       											event.getPreviousState(), 
																		event.getNewState(), true);
            			       		while (!listeners.isEmpty()) {
            			       			listener = (SystemPowerStateListener)listeners.removeLast();
            			       			try {
            			       				listener.systemPowerStateChange(reverseEvent);
            			       			}
            			       			catch (PowerException e) {
            		        				// Log 
            		        				log(LogService.LOG_ERROR, "Error while reversing system power state transition", e);           			       				
            			       			}
            			       		}
            			       		return false;
             					}
            					break;
            			}
            		}
            		// Unget the listener
           			context.ungetService(refs[i]);
            	}
    		}
    	}
    	catch (InvalidSyntaxException ise) {	
    	}
    	
    	return true;
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

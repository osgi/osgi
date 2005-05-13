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
 * This service manages the current system power state. It is singleton because
 * there is only one current system power state at a time. This service
 * usually encapsulates a native implementation of system power management. 
 */
public interface SystemPower
{
    /**
     * Returns the current system power state of the system.
     * 
     * @return the current system power state.
     */
    public int getPowerState();
    
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
     * 
     * @throws PowerException A <code>PowerException</code> must be thrown with the 
     * error code STATE_TRANSITION_FAILURE if the system power state transition fails. 
     * A <code>PowerException</code> must be thrown with the error code 
     * ILLEGAL_STATE_TRANSITION_REQUEST if the requested state is invalid or if the transition 
     * request is invalid.
     * @throws java.lang.SecurityException A <code>java.lang.SecurityException</code> 
     * must be thrown if the caller does not have the valid <code>PowerPermission</code> 
     * to initiate the call. 
     * @see PowerPermission
     */
    public void setPowerState(int powerState, boolean urgency) throws PowerException, SecurityException;
}

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
 * This interface has to be implemented by services that play the role of a 
 * device with power management capabilities. 
 */
public interface DevicePower 
{
   
  /**
   * Returns the current power state of the device.
   * 
   * @return the current device power value  
   */
   public int getPowerState();

   
  /**
   * Set the device power state.
   * This method is generally called by Power Manager for a transition
   * to another power state. The implementation of this method must perform
   * a security check to guarantee that only privileged application can perform
   * this call. The caller must have a <code>PowerPermission</code> with the device
   * id, '*' or <<ALL DEVICES>> as name of the permission. 
   *
   * @param powerState the power state that the device must transit to
   * @param urgency If urgency is set to true, force the device to change 
   * state regardless of any objections which may be opposed by applications.
   * If urgency is set to false, applications may be able to oppose the state change event. 
   * 
   * @throws PowerException A <code>PowerException</code> must be thrown with the 
   * error code STATE_TRANSITION_FAILURE if the device power state transition fails. 
   * A <code>PowerException</code> must be thrown with the error code 
   * ILLEGAL_STATE_TRANSITION_REQUEST if the requested state is invalid or if the 
   * transition request is invalid.
   * @throws java.lang.SecurityException A <code>java.lang.SecurityException</code> 
   * must be thrown if the caller does not have the valid <code>PowerPermission</code> 
   * to initiate the call. 
   * @see PowerPermission
   */
   void setPowerState(int powerState, boolean urgency) throws PowerException, java.lang.SecurityException;  
}

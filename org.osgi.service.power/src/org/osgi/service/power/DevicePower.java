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
   * error code STATE_TRANSITION_FAILURE if an error is encountered during the state 
   * change process. A <code>PowerException</code> must be thrown with the error code 
   * ILLEGAL_STATE_TRANSITION_REQUEST if an unrecognized "state" value is passed to 
   * the method or when there is an invalid state request transition.
   * @throws java.lang.SecurityException A <code>java.lang.SecurityException</code> 
   * must be thrown if the caller does not have the valid <code>PowerPermission</code> 
   * to initiate the call. 
   * @see PowerPermission
   */
   void setPowerState(int powerState, boolean urgency) throws PowerException, java.lang.SecurityException;  
}

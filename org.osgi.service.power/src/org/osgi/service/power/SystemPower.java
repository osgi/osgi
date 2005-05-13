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

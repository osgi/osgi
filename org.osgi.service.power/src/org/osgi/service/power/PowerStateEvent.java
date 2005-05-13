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

import java.util.EventObject;

/**
 * Event object that is used for system power state or device power 
 * state change notifications. Listeners should use the getNewState and 
 * getPreviousState methods to make queries for system / device power state 
 * information, and should use the isUrgent method to determine if the state 
 * change event can be opposed.
 */
public class PowerStateEvent extends EventObject
{
    private int newState;
    private int previousState;
    private boolean urgency;

    /**
     * Creates a PowerStateEvent object that will be sent to System Power listeners.
     * 
     * @param source the system power from where the event is generated
     * @param previousState the state at the time when the event object is
     * created.
     * @param newState the state that the system is going to transit to.
     * @param urgency If urgent is true, it indicates that objections to the
     * state change event from the event listeners will be ignored.
     * Note: The value of urgent should reflect the value of the "urgency" parameter 
     * used in the setPowerState() method.
     */
    public PowerStateEvent(SystemPower source, int newState, int previousState, boolean urgency)
    {
        super(source);
        this.newState = newState;
        this.previousState = previousState;
        this.urgency = urgency;
    }

    /**
     * Creates a PowerStateEvent object that will be sent to DevicePower listeners
     * 
     * @param source the device from where the event is generated
     * @param previousState the state at the time when the event object is
     * created.
     * @param newState the state that the system is going to transit to.
     * @param urgency If urgent is true, it indicates that objections to the
     * state change event from the event listeners will be ignored.
     * Note: The value of urgent should reflect the value of the "urgency" parameter 
     * used in the setPowerState() method.
     */
    public PowerStateEvent(DevicePower source, int newState, int previousState, boolean urgency)
    {
        super(source);
        this.newState = newState;
        this.previousState = previousState;
        this.urgency = urgency;
    }


    /**
     * Returns the new power state that the system is going to transit to.
     * 
     * @return The new power state
     */
    public int getNewState()
    {
        return newState;
    }

    /**
     * Returns the previous power state, which is the state at the time when
     * the event object is created.
     * 
     * @return The previous power state
     */
    public int getPreviousState()
    {
        return previousState;
    }

    /**
     * Event listeners will use this method to determine if the power state change can
     * be opposed or not. If the power state change is urgent then execptions that listeners
     * may thrown will not be considered by the application. If the power state change is not
     * urgent then the behaviour is application dependent.
     * @return true if urgent, otherwise false. 
     */
    public boolean isUrgent()
    {
        return urgency;
    }
}
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
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
 * Interface of listener to power state changes. This interface must be
 * implemented and registered to the framework in order to receive events. 
 */
public interface SystemPowerStateListener
{
   /**
    * Invoked when the system is going to change state, or has changed state.
    * The following paragraphs describe when a PowerStateEvent is valid and the valid state transitions.
    * <p>A <code>PowerStateEvent</code> should be delivered before a transition to less power</p>
    * <p>A <code>PowerStateEvent</code> should be delivered after a transition to more power</p>
    * 
    * @param event the power state event broadcasted to listeners
    * @throws PowerException A <code>PowerException</code> with error code
    * KEEP_CURRENT_STATE may be thrown by the device power listener if the listener 
    * wants to oppose the change and keep the current device power state. If the urgent flag is set to
    * true then the opposition is not considered by the application, otherwise (false) the 
    * behaviour is application dependent. 
    */
    public void systemPowerStateChange(PowerStateEvent event) throws PowerException;
}
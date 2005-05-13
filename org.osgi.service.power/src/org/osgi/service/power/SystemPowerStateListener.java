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
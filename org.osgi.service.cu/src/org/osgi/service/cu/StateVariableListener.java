/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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
package org.osgi.service.cu;

/**
 * StateVariableListeners are registered as OSGi Services. For 
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit}
 * and 
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory ControlUnitFactory}
 * instances whiteboard pattern is used to notify the listeners, 
 * i.e. {@link org.osgi.service.cu.admin.ControlUnitAdmin} is
 * responsible for tracking these services and to deliver them the appropriate
 * events. <code>ManagedControlUnit</code> and <code>ControlUnitFactory</code>
 * instances notify the <code>ControlUnitAdmin</code> about state variable changes 
 * through the {@link  org.osgi.service.cu.admin.spi.CUAdminCallback} 
 * provided to them upon registration in the framework by the <code>ControlUnitAdmin</code>. 
 * <BR>
 * 
 * Simple control units (those implementing only the 
 * {@link org.osgi.service.cu.ControlUnit} interface) on the other hand have
 * the responsibility to track and notify the listeners on their own.
 * 
 * <p>
 * 
 * A service registration property may be used as a filter to limit the number
 * of received events and to specify certain control unit and/or state variables
 * to listen for. The key of this property is
 * {@link ControlUnitConstants#EVENT_FILTER} whose value is a
 * <code>String</code> representing an LDAP filtering expression. The properties,
 * which may be used in the LDAP filter are {@link ControlUnitConstants#TYPE},
 * {@link ControlUnitConstants#ID} and
 * {@link ControlUnitConstants#STATE_VARIABLE_ID}. The listener will be notified
 * only for changes in the values of state variables which satisfy this filter.
 * <p>
 * 
 * If property {@link ControlUnitConstants#EVENT_FILTER} is not present, the
 * listener will receive events for all units state variables changes.
 * <p>
 * 
 * Here are some examples:
 * <ul>
 * <li>To listen for all state variable changes on a certain control unit, construct
 * a filter containing the ID and the type of the selected unit.
 * <li>To listen for all state variable changes on a group of similar control
 * units, give only the type of the units.
 * <li>To listen for a change on specific variable, put the state variable ID in
 * the filter.
 * </ul>
 * <p>
 * 
 * Listeners may use the {@link ControlUnitConstants#EVENT_SYNC} property to
 * specify that events should be delivered synchronously.
 * <p>
 * 
 * A listener that has the property
 * {@link ControlUnitConstants#EVENT_AUTO_RECEIVE} automatically receives the
 * current values of all registered control units and state variables that match
 * the current filter.
 * 
 * @version $Revision$
 */
public interface StateVariableListener {

  /**
   * This is the listener's callback method. It is called when a state variable
   * of a certain control unit is changed.
   * 
   * @param controlUnitType The control unit type.
   * @param controlUnitID The ID of the control unit whose variable has changed.
   * @param stateVariableID The ID of the changed state variable.
   * @param value The new value of the state variable.
   */
  public void stateVariableChanged(String controlUnitType, String controlUnitID,
                                   String stateVariableID, Object value);

}

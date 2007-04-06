/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

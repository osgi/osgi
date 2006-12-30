/*
 * $Header$
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
 * Control unit is an object, which provides formal representation of a certain
 * resource (device, software or hardware components, etc.) so it can be managed
 * in a uniform way by different applications. <br>
 * The public interface of the control unit is represented by a set of valued
 * attributes called <code>state variables</code> and set of operations called
 * <code>actions</code>.
 * <p>
 * 
 * A control unit instance is characterized by its <code>type</code> and
 * <code>ID</code>. The type of a control unit defines its allowed set of
 * state variables and actions. The ID of the control unit instance identifies
 * it uniquely in the scope of its type.
 * <p>
 * 
 * A control unit instance can be exported (made available) to the management
 * applications either by registering
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit}, which represents
 * single control unit instance or by registering a
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory} service, which
 * maintains a set of control unit instances of the same type.
 * <p>
 * 
 * Control units may be arranged hierarchically - every control unit instance
 * may have one or more sub control units and one or more parent control units.
 * The implementers of the control units must avoid cycles in the control unit
 * hierarchy. Organizing control units may be convenient for logical grouping of
 * control units, but is especially useful for representing more complex
 * resources - devices, hardware and software systems, which may be decomposed
 * to a hierarchy of sub-components, achieving arbitrary level of granularity.
 * 
 * @version $Revision$
 */
public interface ControlUnit {

  /**
   * Returns the ID of the control unit, which uniquely identifies it in the scope of
   * its parent.
   * 
   * @return The ID of the control unit
   */
  public String getId();

  /**
   * Returns the type of the control unit. This type is used to retrieve 
   * metatype information from the metatype service. 
   * 
   * @return The type of the control unit
   */
  public String getType();

  /**
   * Returns the value of a specified state variable. State variables supported
   * by a control unit and their types are defined by the metadata of the
   * control unit.
   * 
   * @param stateVariableID The ID of the variable
   * @return The value of the variable
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the stateVariableID is <code>null</code>. 
   */
  public Object queryStateVariable(String stateVariableID) throws ControlUnitException;

  /**
   * Executes the specified action over this control unit. Actions supported by
   * a control unit and the number and types of the input and output arguments
   * of each action are defined by the metadata of the control unit.
   * 
   * @param actionID the ID of the action
   * @param arguments the input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more then one, the value
   *          must be an <code>Object</code> array and arguments are retrieved 
   *          from that array.
   * 
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value.
   * @throws ControlUnitException if error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the actionID is <code>null</code>.
   */
  public Object invokeAction(String actionID, Object arguments) throws ControlUnitException;
}

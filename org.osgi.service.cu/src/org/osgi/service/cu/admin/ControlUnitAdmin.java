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
package org.osgi.service.cu.admin;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;

/**
 * Represents the facade of the Control Unit Admin layer. This interface is
 * available as a service in the OSGi registry and may be used by the
 * applications to manage all Control Units exported in the OSGi framework.
 * There must be exactly one such service registered in the OSGi framework.
 * 
 * @see org.osgi.service.cu.ControlUnit
 * @version $Revision$
 */
public interface ControlUnitAdmin {

  /**
   * Actions defined in the metadata of the control unit 
   * whose action ID starts with this prefix may be used for explicit creation 
   * of new control units. <BR>
   * 
   * These actions are called constructors and their action ID must be 
   * supplied as argument to the  
   * {@link #createControlUnit(String, String, Object) ControlUnitAdmin.createControlUnit} or 
   * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory#createControlUnit(String, Object) ControlUnitFactory.createControlUnit} methods.
   * A control unit type may have arbitrary number of constructors.
   * <p>
   * 
   * The value of this constant is "$create."
   */
  public static final String CONSTRUCTOR_PREFIX = "$create.";

  /**
   * An action with ID equal to the value of this constant and taking a single 
   * argument of type <code>String</code> should be
   * defined in the metadata of a control unit to specify that the 
   * corresponding type of control units can be explicitly removed. <BR>
   *  
   * This action is called destructor. It takes as single argument the ID 
   * of the control unit to be destroyed and its responsibility is to remove 
   * the control unit from the framework and to free the resources used by it. 
   * A control unit type may have one or zero destructor.<BR>
   * 
   * The destructor may be invoked through the 
   * {@link #destroyControlUnit(String, String) ControlUnitAdmin.destroyControlUnit} or 
   * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory#destroyControlUnit(String) ControlUnitFactory.destroyControlUnit}
   * methods. <BR>
   * 
   * <p>
   * 
   * The value of this constant is "$destroy"
   */
  public static final String DESTRUCTOR = "$destroy";

  /**
   * Actions defined in the metadata of the control unit that starts with this
   * prefix, may be used for searching the control units.
   * <p>
   * 
   * The value of this constant is "$find."
   */
  public static final String FINDER_PREFIX = "$find.";

  /**
   * This service registration property may be used by
   * {@link ControlUnitAdminListener}s and {@link HierarchyListener}s as attribute
   * in their filter definition to narrow the type of events they wish to
   * receive. Its value must be a String representation of one of the possible
   * values for the <code>eventType</code> parameter of
   * {@link ControlUnitAdminListener#controlUnitEvent} for
   * <code>ControlUnitAdminListener</code> and
   * {@link HierarchyListener#hierarchyChanged} for
   * <code>HierarchyListener</code>. If the filter doesn't restrict the
   * event types to be received the listener will receive all events matching
   * the control unit-filtering criterion.
   * <p>
   * 
   * The value of this constant is "org.osgi.control.event.type"
   */
  public static final String EVENT_TYPE = "org.osgi.control.event.type";

  /**
   * Returns all distinct types of control units currently exported in the
   * framework.
   * 
   * @return An array of Control Unit types or null, if there are no Control Unit
   *         types exported in the framework.
   */
  public String[] getControlUnitTypes();

  /**
   * Returns the current version for the given type of control units currently
   * exported in the framework.
   * 
   * @param controlUnitType The control unit type
   * @return The version, or <code>null</code>, if the given type has no version
   * @throws ControlUnitAdminException if there is no such type of control units exported in the framework
   * @throws NullPointerException if the control unit type is <code>null</code>.
   */
  public String getControlUnitTypeVersion(String controlUnitType) throws ControlUnitAdminException;

  /**
   * Returns the IDs of the control units with the specified type, 
   * located by the finder method with the given ID and the given finder 
   * parameters. Supported finder methods are
   * specific to the type of the control unit and are specified in the control
   * unit metadata as a special class of actions whose identifier starts with
   * <code>"$find."</code>.
   * <p>
   * 
   * Every finder method may have different number and/or type of arguments,
   * which are also specified in the metadata.
   * <p>
   * 
   * Supplying <code>null</code> as finder method ID and
   * <code>arguments</code> to this method returns all ids of all control units
   * of the specified control unit type. Otherwise the exact
   * searching of the Control Units is delegated to the
   * <code>ControlUnitFactory</code> for this control unit type. Therefore,
   * invoking this method with non-<code>null</code> finder method ID
   * and <code>arguments</code> is not supported for finding control units
   * exported by <code>ManagedControlUnit<code> services.
   * <p>
   * 
   * If there are no control units that satisfy the finder condition the method returns
   * <code>null</code>.
   * 
   * @param  controlUnitType  the type restriction of the control units list that is searched. 
   * @param  finderID  the ID of the finder method. Must start with <code>"$find."<code>.
   * @param  arguments  the finder argument(s). If the argument is only one 
   *                    this is the argument itself. If the arguments are more 
   *                    than one, the value must be an <code>Object</code> array 
   *                    and arguments are retrieved from it. 
   *
   * @return An array of <code>ControlUnit<code> identifiers
   * @throws ControlUnitException if the search operation cannot be performed due to an error.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if there is no such a type of control units 
   * available in the framework, searching is not supported by this type or it  
   * does not have a finder with the given finder method ID.
   * @throws NullPointerException if the control unit type or finder method ID is <code>null</code>.
   */
  public String[] findControlUnits(String controlUnitType, String finderID, Object arguments)
      throws ControlUnitException;

  /**
   * Returns the control unit with the given type and  ID.
   * 
   * @param controlUnitType the type of the Control Unit
   * @param controlUnitID the ID of the Control Unit
   * 
   * @return The <code>ControlUnit</code> identified by the specified type 
   *         and ID pair or <code>null</code> if there is no such control unit 
   *         exported in the framework.
   * @throws ControlUnitAdminException if there is no such type of units exported in the framework. 
   * @throws NullPointerException if the control unit type or control unit ID is <code>null</code>.
   */
  public ControlUnit getControlUnit(String controlUnitType, String controlUnitID) throws ControlUnitAdminException;

  /**
   * Returns the IDs of the children of the control unit, specified by 
   * its type and ID, that have the given type.
   * If both the parent control unit type and ID are null, this method returns 
   * the IDs of the control units with the specified type that have no parent.
   * 
   * @param parentControlUnitType the type of the parent control unit.
   * @param parentControlUnitID the ID of the parent control unit.
   * @param childControlUnitType the type of the child control units.
   * 
   * @return An array of child control units or null if there are 
   * no child control units found for this parent type and ID.
   * 
   * @throws NullPointerException if the type of the children control units is <code>null</code>.
   */
  public String[] getSubControlUnits(String parentControlUnitType, String parentControlUnitID,
                                     String childControlUnitType);

  /**
   * Returns the types of the exported control units that may be children 
   * of the control units with the given type.
   * 
   * @param childControlUnitType The type of the children control units
   * @return The types of the parent control units or <code>null</code> if the given children control units have no parents
   * @throws NullPointerException if the type of the children control units is <code>null</code>.
   */
  public String[] getParentControlUnitTypes(String childControlUnitType);

  /**
   * Returns the IDs of the exported control units with the given type that 
   * are parents of the control units with the specified type and ID.
   * 
   * @param childControlUnitType The child control unit type
   * @param childControlUnitID The child control unit ID
   * @param parentControlUnitType The parent control units type
   * @return The IDs of the parent control units or <code>null</code> if 
   *         the given child control unit has no parents of the given type
   * @throws ControlUnitAdminException if there is no such child control unit 
   *         provided in the framework. 
   * @throws NullPointerException if any parameter is <code>null</code>.
   */
  public String[] getParentControlUnits(String childControlUnitType, String childControlUnitID,
                                        String parentControlUnitType) throws ControlUnitAdminException;

  /**
   * Explicitly creates a control unit instance with the specified type 
   * using the supplied constructor and returns the ID of the newly 
   * created control unit.
   * Supported ways for creating a control unit are specific to the type of the
   * control unit and are specified in the corresponding metadata definition.
   * For every control unit type there may be defined zero, one or more
   * constructors. A constructor is defined as a special class of action whose
   * identifier starts with <code>"$create."</code>.
   * <p>
   * 
   * Every constructor may have different number and/or types of arguments, which
   * are also defined in the metadata.
   * <p>
   * 
   * The exact creation of the control unit is delegated to the
   * <code>ControlUnitFactory</code> of this control unit type. Therefore this
   * method is not supported for control units exported by
   * <code>ManagedControlUnit<code> services.
   * 
   * @param  controlUnitType - the type of the control unit.
   * @param  constructorID  the ID of the constructors. Must start with <code>"$create."<code>.
   * @param  arguments - the 'constructor' argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more than one, the 
   *         value must be an <code>Object</code> array and arguments are retrieved from that array.
   *
   * @return The ID of the newly created control unit
   * @throws ControlUnitException if the control unit cannot be created for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if there is no such a type of control units 
   * available in the framework, creation is not supported for this type or it  
   * does not have a constructor with the given constructor ID.
   * @throws NullPointerException if the control unit type or the constructor ID is <code>null</code>.
   */
  public String createControlUnit(String controlUnitType, String constructorID,
                                  Object arguments) throws ControlUnitException;

  /**
   * Explicitly removes the control unit instance with the given type and ID. Some
   * type of control units may not support explicit removing. In that case this
   * method throws <code>ControlUnitAdminException</code>. Support for
   * explicit removing of control units is specified in the control unit
   * metadata by the presence of an action with ID <code>"$destroy"</code>.
   * 
   * @param controlUnitType - The type of the control unit.
   * @param controlUnitID - The control unit ID.
   * @throws ControlUnitException if the control unit cannot be destroyed for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if there is no such a type of control units 
   * available in the framework or destruction of control units is not 
   * supported for this type.
   * @throws NullPointerException if the control unit type or the control unit ID is <code>null</code>.
   */
  public void destroyControlUnit(String controlUnitType, String controlUnitID)
      throws ControlUnitException;

  /**
   * Returns types of the exported control units that may be children of
   * control units with a given type.
   * 
   * @param parentControlUnitType The parent control unit type
   * @return An array of the children control unit types
   * @throws NullPointerException if the parent control unit type is <code>null</code>.
   */
  public String[] getSubControlUnitTypes(String parentControlUnitType);

  /**
   * Queries the control unit with the given type and ID for the value of 
   * the given state variable.
   * 
   * @param controlUnitType the type of the control unit
   * @param controlUnitID the ID of the control unit
   * @param stateVariableID the ID of the variable
   * @return The value of the variable
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if there is no such a type of control units 
   * available in the framework or a control unit with the given control unit type
   * and control unit ID does not exist.
   * @throws NullPointerException if any parameter is <code>null</code>.
   */
  public Object queryStateVariable(String controlUnitType, String controlUnitID, String stateVariableID)
      throws ControlUnitException;

  /**
   * Executes the given action over the control unit with the given type and ID.
   * 
   * @param controlUnitType the type of the control unit
   * @param controlUnitID the ID of the control unit
   * @param actionID the ID of the action
   * @param arguments the input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more than one, the value
   *          must be an <code>Object</code> array and arguments are retrieved from it.
   * 
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value.
   * @throws ControlUnitException if an error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if there is no such a type of control units 
   * available in the framework or a control unit with the given control unit type
   * and control unit ID does not exist.
   * @throws NullPointerException if either the control unit type, the control unit ID or the action ID is <code>null</code>.
   */
  public Object invokeAction(String controlUnitType, String controlUnitID, String actionID, Object arguments)
      throws ControlUnitException;
}

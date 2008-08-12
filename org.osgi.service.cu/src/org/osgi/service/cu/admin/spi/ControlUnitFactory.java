/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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
package org.osgi.service.cu.admin.spi;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.ControlUnitAdminException;

/**
 * This interface must be registered as an OSGi service in order to make more
 * than one resource of the same type manageable through the control unit
 * abstraction. The ControlUnitFactory services should not be used directly by
 * the applications. Instead applications access
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service, which delegates
 * the requests to the appropriate <code>ControlUnitFactory</code> or
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit ManagedControlUnit} 
 * service. <code>ControlUnitFactory</code> objects are suitable for representing variable number
 * of resources with similar characteristics. An advantage is that it is not
 * necessary to permanently hold control unit instance for every resource,
 * because the corresponding wrapper may be created on demand.
 * <p>
 * 
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service is responsible for
 * tracking all <code>ControlUnitFactory</code> services registered in the
 * service registry of the framework. <br>
 * One <code>ControlUnitFactory</code> may be responsible for providing
 * ControlUnit instances of exactly one type. It is not allowed to have more than
 * one factory for the same type and it is not allowed to have both
 * <code>ControlUnitFactory</code> and
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit ManagedControlUnit}
 * services for the same control unit type.
 * <p>
 * 
 * To be properly handled by the <code>ControlUnitAdmin</code> the
 * <code>ControlUnitFactory</code> service must be registered with property
 * {@link org.osgi.service.cu.ControlUnitConstants#TYPE} with value of type
 * <code>String</code> specifying the type of the control unit instances
 * provided by this factory. Optionally the registration properties may contain
 * property {@link org.osgi.service.cu.ControlUnitConstants#PARENT_TYPE} with
 * value of type <code>String</code> or <code>String[]</code> specifying the
 * type(s) of parent control units in the control unit hierarchy. Factories which
 * support versioning of their control units' type should additionally register
 * with the property {@link org.osgi.service.cu.ControlUnitConstants#VERSION}
 * with value of type <code>String</code>.
 * 
 * @version $Revision$
 */
public interface ControlUnitFactory {

  /**
   * Supplies the <code>CUAdminCallback</code> interface to the implementation of
   * the <code>ControlUnitFactory</code> service.
   * <p>
   * 
   * This method is invoked by the <code>ControlUnitAdmin</code> service with a 
   * non-<code>null</code> argument after registration of the
   * <code>ControlUnitFactory</code> service or after start-up of the 
   * <code>ControlUnitAdmin</code> for already registered factories.
   * <p>
   * 
   * It is supposed that the <code>ControlUnitFactory</code> will assign this reference to
   * an instance variable and use it later to notify the 
   * <code>ControlUnitAdmin</code> for
   * creation or removal of control unit instances, attaching/detaching of
   * control units to/from given parent and for changes in state variables.
   * <p>
   * 
   * The method is invoked with a <code>null</code> argument during
   * unregistration of the <code>ControlUnitFactory</code> service or when the
   * <code>ControlUnitAdmin</code> is stopped.
   * 
   * @param adminCallback reference to the control unit callback interface or
   *          <code>null</code> if previously set reference is not longer
   *          valid.
   */
  public void setControlUnitCallback(CUAdminCallback adminCallback);

  /**
   * Returns the <code>ControlUnit</code> object identified by the given ID.
   * If there is no such control unit maintained by this factory,
   * <code>null</code> is returned.
   * 
   * @param controlUnitID The ID of the requested control unit
   * @return The <code>ControlUnit</code> object with the given ID
   */
  public ControlUnit getControlUnit(String controlUnitID);

  /**
   * Returns the IDs of the control unit instances, which are children of 
   * the control unit with the given type and ID. <BR>
   * 
   * Supplying <code>null</code> as arguments
   * to this method results in returning only those control units provided by this
   * factory, which have no parent, omitting all other provided control units. <BR>
   * 
   * @param parentControlUnitType The type of the parent control unit
   * @param parentControlUnitID The ID of the parent control unit
   * 
   * @return The sub-control units of the specified control unit
   */
  public String[] getControlUnits(String parentControlUnitType, String parentControlUnitID);

  /**
   * Returns the IDs of all control units currently provided by this factory.
   * 
   * @return The control unit IDs provided by this factory or <code>null</code> it doesn't provide any control unit.
   */
  public String[] listControlUnits();

  /**
   * Returns the IDs of the control units satisfying the finder method 
   * and the supplied argument(s). The <code>ControlUnitFactory</code> may optionally
   * define in its metadata one or more methods for filtering control units.
   * Those methods are called finders and are specified in the metadata definition
   * for the particular type of control units. A finder is defined as a special
   * class of action whose identifier starts with <code>"$find."</code>.
   * Every finder method may have different number and/or type of arguments,
   * specified in the metadata and implemented by the corresponding
   * <code>ControlUnitFactory</code>.
   * <p>
   * 
   * If there is no control unit that satisfies the finder condition the
   * method returns <code>null</code>.
   * 
   * @param finderID The ID of the finder method. Must start with
   *          <code>"$find."<code>.
   * @param  arguments  The <code>finder</code> argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more than one, the 
   *         value must be an <code>Object</code> array and arguments are retrieved from it. 
   * 
   * @return the sub-control units of the specified control unit.
   *
   * @throws ControlUnitException if the search operation cannot be performed due to an error.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if searching is not supported by the 
   * factory or there is no finder with the given finderID.
   */
  public String[] findControlUnits(String finderID, Object arguments) throws ControlUnitException;

  /**
   * Returns the IDs of the parents of the given control unit specified by its ID.
   * This method returns only IDs of the control units for the specified parent 
   * type.
   * 
   * @param childControlUnitID The ID of the child control unit
   * @param parentControlUnitType The type of the returned parent control units
   * @return The IDs of parent control units or <code>null</code>, if the given control
   *         unit has no parents of the specified type.
   * @throws ControlUnitAdminException if there is no such child control unit 
   *         provided in the framework. 
   */
  public String[] getParents(String childControlUnitID, String parentControlUnitType) throws ControlUnitAdminException;

  /**
   * Queries the control unit with the specified ID for the value of the 
   * specified state variable.
   * 
   * @param controlUnitID The ID of the control unit provided by this factory
   * @param stateVariableID The ID of the variable
   * 
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if a control unit with the given 
   * control unit ID does not exist.
   * @return The value of the variable.
   */
  public Object queryStateVariable(String controlUnitID, String stateVariableID) throws ControlUnitException;

  /**
   * Executes the specified action over the control unit with specified ID.
   * 
   * @param controlUnitID The ID of the control unit
   * @param actionID The ID of the action
   * @param arguments The input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more than one, the value
   *          must be an <code>Object[]</code> and arguments are retrieved from
   *          that array.
   * 
   * @throws ControlUnitException if an error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if a control unit with the given 
   * control unit ID does not exist.
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value.
   */
  public Object invokeAction(String controlUnitID, String actionID, Object arguments)
      throws ControlUnitException;

  /**
   * Explicitly creates a control unit and returns the ID of the newly 
   * created control unit. The <code>ControlUnitFactory</code> may optionally define
   * in its metadata one or more methods for creating new control units.
   * These methods are called constructors and are specified in the metadata
   * definition for the particular type of control units. A constructor is
   * defined as a special class of action whose identifier starts with
   * <code>"$create."</code>. Every constructor method may have different
   * number and/or types of arguments, specified in the metadata and implemented
   * by the corresponding <code>ControlUnitFactory</code>.
   * 
   * @param constructorID The ID of the constructor. Must start with
   *          <code>"$create."<code>.
   * @param  arguments - The 'constructor' argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more then one, the 
   *         value must be a <code>Object</code> array and arguments are retrieved from it.
   *
   * @throws ControlUnitException if the control unit cannot be created for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if creation is not supported 
   * by the factory or there is no constructor with the given constructor ID.
   * @return The ID of the newly created control unit.
   */
  public String createControlUnit(String constructorID, Object arguments)
      throws ControlUnitException;

  /**
   * Explicitly removes the control unit instance with the given ID. Some type of
   * control units may not support explicit removing of the resources
   * represented by the corresponding control units. In that case this method
   * throws <code>ControlUnitAdminException</code>. Support for explicit
   * destroying of control units is specified in the control unit metadata by
   * the presence of an action with ID <code>"$destroy"</code>.
   * 
   * @param controlUnitID The control unit ID.
   * @throws ControlUnitException if the control unit cannot be destroyed for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws ControlUnitAdminException if destruction of control units is 
   * not supported by the factory.
   */
  public void destroyControlUnit(String controlUnitID) throws ControlUnitException;
}

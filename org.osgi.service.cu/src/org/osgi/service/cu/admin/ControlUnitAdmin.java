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
   * Actions defined in the metadata of the control unit, 
   * which action ID starts with this prefix may be used for explicit creation 
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
   * This action is called destructor. It takes as single argument the 
   * ID of control unit to be destroyed and its responsibility is to remove 
   * the control unit from the framework and free the resources used by it. A 
   * control unit type may have one or zero destructors.<BR>
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
   * Actions defined in the metadata of the control unit, which starts with this
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
   * receive. It's value must be a string representation of one of the possible
   * values for the <code>eventType</code> parameter of
   * {@link ControlUnitAdminListener#controlUnitEvent} for
   * <code>ControlUnitAdminListener</code>
   * {@link HierarchyListener#hierarchyChanged} for
   * <code>HierarchyListeners</code>. If the filter doesn't restrict the
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
   * Returns the current version for given type of control units currently
   * exported in the framework.
   * 
   * @param controlUnitType control unit type
   * @return The type, or <code>null</code>, if the give type has no version
   * @throws ControlUnitAdminException if there is no such type of units exported in the framework
   * @throws NullPointerException if the controlUnitType is <code>null</code>.
   */
  public String getControlUnitTypeVersion(String controlUnitType) throws ControlUnitAdminException;

  /**
   * Returns the IDs of control units of the specified type, located by the 
   * finder method specified by its ID an according the given finder 
   * parameters. Supported finder methods are
   * specific to the type of the control unit and are specified in the control
   * unit metadata as a special class of actions with identifier starting with
   * <code>"$find."</code>.
   * <p>
   * 
   * Every finder method may have different number and/or type of arguments,
   * which are also specified in the metadata.
   * <p>
   * 
   * Supplying <code>null</code> as <code>finderId</code> and
   * <code>arguments</code> to method returns all ids of all control units of
   * the specified by the <code>cuType</code> type. Otherwise the exact
   * searching of the Control Units is delegated to the
   * <code>ControlUnitFactory</code> for this control unit type. Therefore
   * invoking this method with non- <code>null</code> as <code>finderId</code>
   * and <code>arguments</code> is not supported for finding control units
   * exported by <code>ManagedControlUnit<code> services.
   * <p>
   * 
   * If there are no control units that satisfy the finder condition the method returns
   * <code>null</code>.
   * 
   * @param  controlUnitType  the type restriction of the control units list that is searched. 
   * @param  finderID  the id of the finder method. Must start with <code>"$find."<code>.
   * @param  arguments  the <code>finder</code> argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more then one, the 
   *         value must be a <code>Object</code> array and arguments are retrieved from it. 
   *
   * @return An array of <code>ControlUnit<code> identifiers
   * @throws ControlUnitException if the search operation cannot be performed due to an error.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the controlUnitType or finderID is <code>null</code>.
   */
  public String[] findControlUnits(String controlUnitType, String finderID, Object arguments)
      throws ControlUnitException;

  /**
   * Returns the Control Unit of a specified type and with a specified id.
   * 
   * @param controlUnitType the type of the Control Unit
   * @param controlUnitID the id of the Control Unit
   * 
   * @return The <code>ControlUnit</code> identified by the specified
   *         <code>(controlUnitType, controlUnitId)</code> pair or
   *         <code>null</code> if there is not such control unit exported in
   *         the framework.
   * @throws ControlUnitAdminException if there is no such type of units exported in the framework 
   * @throws NullPointerException if the controlUnitType or controlUnitID is <code>null</code>.
   */
  public ControlUnit getControlUnit(String controlUnitType, String controlUnitID) throws ControlUnitAdminException;

  /**
   * Returns ids of children of a control unit specified by a given type and id.
   * Method returns only control units of a specified sub-type. If parentCUType ==
   * parentCUId == null this method returns unit of the given subCUType which
   * have no parent.
   * 
   * @param parentControlUnitType the type of the parent Control Unit.
   * @param parentControlUnitID the id of the parent Control Unit.
   * @param childControlUnitType the type of the child Control Units.
   * 
   * @return An array of child control units or null if there are 
   * no child control units found for this parent type and id.
   * 
   * @throws NullPointerException if the childControlUnitType is <code>null</code>.
   */
  public String[] getSubControlUnits(String parentControlUnitType, String parentControlUnitID,
                                     String childControlUnitType);

  /**
   * Returns types of the exported control units that may be parents of control
   * units with a given type.
   * 
   * @param childControlUnitType child unit type
   * @return The parent types or null if the given control unit may have no parents
   * @throws NullPointerException if the childControlUnitType is <code>null</code>.
   */
  public String[] getParentControlUnitTypes(String childControlUnitType);

  /**
   * Returns ids of the exported control units of given type that are parents of
   * control units with a given type and id.
   * 
   * @param childControlUnitType child unit type
   * @param childControlUnitID child unit id
   * @param parentControlUnitType parent units type
   * @return The parent types or null if the given control unit has no parents of
   *         the given type
   * @throws ControlUnitAdminException if there is no such child control unit 
   *         provided in the framework 
   * @throws NullPointerException if the childControlUnitType,
   *         childControlUnitID or parentControlUnitType is <code>null</code>.
   */
  public String[] getParentControlUnits(String childControlUnitType, String childControlUnitID,
                                        String parentControlUnitType) throws ControlUnitAdminException;

  /**
   * Explicitly creates control unit instance of a specified type using a
   * supplied constructor and returns the id of the newly created control unit.
   * Supported ways for creating a control unit are specific to the type of the
   * control unit and are specified in the corresponding metadata definition.
   * For every control unit type there may be defined zero, one or more
   * constructors. A constructor is defined as a special class of action with
   * identifier starting with <code>"$create."</code>.
   * <p>
   * 
   * Every constructor may have different number and/or types of arguments, which
   * are also defined in the metadata.
   * <p>
   * 
   * The exact creation of the Control Unit is delegated to the
   * <code>ControlUnitFactory</code> of this control unit type. Therefore this
   * method is not supported for control units exported by
   * <code>ManagedControlUnit<code> services.
   * 
   * @param  controlUnitType - the type of the Control Unit.
   * @param  constructorID  the id of the constructors. Must start with <code>"$create."<code>.
   * @param  arguments - the 'constructors' argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more then one, the 
   *         value must be a <code>Object</code> array and arguments are retrieved from that array.
   *
   * @return The ID of the newly created control unit
   * @throws ControlUnitException if the control unit cannot be created for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the controlUnitType or constructorID is <code>null</code>.
   */
  public String createControlUnit(String controlUnitType, String constructorID,
                                  Object arguments) throws ControlUnitException;

  /**
   * Explicitly removes control unit instance with a given type and id. Some
   * type of control units may not support explicit removing. In that case this
   * method throws <code>ControlUnitAdminException</code>. Support for
   * explicit removing of Control Units is specified in the control unit
   * metadata by presence of an action with id <code>"$destroy"</code>.
   * 
   * @param controlUnitType - the type of the Control Unit.
   * @param controlUnitID - controlUnitId control unit id.
   * @throws ControlUnitException if the control unit cannot be destroyed for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the controlUnitType or controlUnitID is <code>null</code>.
   */
  public void destroyControlUnit(String controlUnitType, String controlUnitID)
      throws ControlUnitException;

  /**
   * Returns types of the exported control units that may be children of
   * control units with a given type.
   * 
   * @param parentControlUnitType parent control unit type
   * @return An array of the child control unit types
   * @throws NullPointerException if the parentControlUnitType is <code>null</code>.
   */
  public String[] getSubControlUnitTypes(String parentControlUnitType);

  /**
   * Queries a control unit with a given type and id for the value of a given
   * state variable.
   * 
   * @param controlUnitType the type of the control unit
   * @param controlUnitID the id of the control unit
   * @param stateVariableID the id of the variable
   * @return The value of the variable
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the controlUnitType, controlUnitID or stateVariableID is <code>null</code>.
   */
  public Object queryStateVariable(String controlUnitType, String controlUnitID, String stateVariableID)
      throws ControlUnitException;

  /**
   * Executes a given action over a control unit with a given id.
   * 
   * @param controlUnitType the type of the control unit
   * @param controlUnitID the id of the control unit
   * @param actionID the id of the action
   * @param arguments the input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more then one, the value
   *          must be a <code>Object</code> array and arguments are retrieved from it.
   * 
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value.
   * @throws ControlUnitException if an error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @throws NullPointerException if the controlUnitType, controlUnitID or actionID is <code>null</code>.
   */
  public Object invokeAction(String controlUnitType, String controlUnitID, String actionID, Object arguments)
      throws ControlUnitException;
}

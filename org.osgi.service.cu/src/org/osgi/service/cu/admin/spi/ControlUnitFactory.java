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
package org.osgi.service.cu.admin.spi;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;

/**
 * This interface must be registered as an OSGi service in order to make more
 * then one resources of the same type manageable through the Control Unit
 * abstraction. The ControlUnitFactory services should not be used directly by
 * the applications. Instead application access
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service, which delegates
 * the requests to the appropriate <code>ControlUnitFactory</code> or
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit ManagedControlUnit} 
 * service. ControlUnitFactories are suitable for representing variable number
 * of resources with similar characteristics. An advantage is that it is not
 * necessary to permanently hold Control Unit instance for every resource,
 * because the corresponding wrapper may be created on demand.
 * <p>
 * 
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} service is responsible for
 * tracking all <code>ControlUnitFactory</code> services registered in the
 * service registry of the framework. <br>
 * One <code>ControlUnitFactory</code> may be responsible for providing
 * ControlUnit instances of exactly one type. It is not allowed to have more then
 * one factory for the same type and it is not allowed to have both
 * <code>ControlUnitFactory</code> and
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit ManagedControlUnit}
 * services for the same ControlUnit type.
 * <p>
 * 
 * To be properly handled by the Control Unit Admin the
 * <code>ControlUnitFactory</code> service must be registered with property
 * {@link org.osgi.service.cu.ControlUnitConstants#TYPE}with value of type
 * <code>String</code> specifying the type of the Control Unit instances
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
   * Supplies the Control Unit admin callback interface to the implementation of
   * the <code>ControlUnitFactory</code> service.
   * <p>
   * 
   * This method is invoked by the Control Unit Admin bundle with a non-
   * <code>null</code> argument after registration of the
   * <code>ControlUnitFactory</code> service or after start-up of the Control
   * Unit Admin for already registered factories.
   * <p>
   * 
   * It is supposed that the Control Unit Factory will assign this reference to
   * a instance variable and use it later to notify the Control Unit Admin for
   * creation or removal or Control Unit instances, attaching/detaching of
   * Control Units to/from given parent and for changes in state variables.
   * <p>
   * 
   * The method is invoked with a <code>null</code> argument during
   * unregistration of the <code>ControlUnitFactory</code> service or when the
   * Control Unit Admin is stopped.
   * 
   * @param adminCallback reference to the control unit callback interface or
   *          <code>null</code> if previously set reference is not longer
   *          valid.
   */
  public void setControlUnitCallback(CUAdminCallback adminCallback);

  /**
   * Returns the <code>ControlUnit</code> object, identified by the given id.
   * If there is no such control unit maintained by this factory,
   * <code>null</code> is returned.
   * 
   * @param controlUnitID the id of the requested control unit
   * @return The <code>ControlUnit</code> object with the given ID
   */
  public ControlUnit getControlUnit(String controlUnitID);

  /**
   * Returns ids of the control unit instances, which are children of a control 
   * unit with a given type, and id. <BR>
   * 
   * Supplying <code>null</code> as arguments
   * to this method results in returning only those control units provided by this
   * factory, which have no parent, omitting all other provided units. <BR>
   * 
   * @param parentControlUnitType type of the parent control unit
   * @param parentControlUnitID id of the parent control unit
   * 
   * @return The sub-control units of the specified control unit
   */
  public String[] getControlUnits(String parentControlUnitType, String parentControlUnitID);

  /**
   * Returns the ids of all control units currently provided by this factory.
   * 
   * @return The control unit IDs or <code>null</code>, if the factory does not provide any units.
   */
  public String[] listControlUnits();

  /**
   * Returns ids of the control units satisfying the finder method and the
   * supplied argument(s). The <code>ControlUnitFactory</code> may optionally
   * define in its metadata one or more methods for filtering of control units.
   * This methods are called finders are specified in the metadata definition
   * for particular type of Control Units. A finder is defined as a special
   * class of action with identifier starting with <code>"$find."</code>.
   * Every finder method may have different number and/or type of arguments,
   * specified in the metadata and implemented by the corresponding
   * <code>ControlUnitFactory</code>.
   * <p>
   * 
   * If there are no control units that satisfy the finder condition the
   * method returns <code>null</code>.
   * 
   * @param finderID the id of the finder method. Must start with
   *          <code>"$find."<code>.
   * @param  arguments  the <code>finder</code> argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more then one, the 
   *         value must be a <code>Object</code> array and arguments are retrieved from it. 
   * 
   * @return the sub-control units of the specified control unit.
   *
   * @throws ControlUnitException if the search operation cannot be performed due to an error.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   */
  public String[] findControlUnits(String finderID, Object arguments) throws ControlUnitException;

  /**
   * Returns the parent unit IDs for the specified child control unit.
   * This method returns only ids of the control units for a specified parent
   * type.
   * 
   * @param childControlUnitID id of the child unit
   * @param parentControlUnitType type of the returned parent units
   * @return The IDs of parent units or <code>null</code>, if the given control
   *         unit has no parents of the specified type
   */
  public String[] getParents(String childControlUnitID, String parentControlUnitType);

  /**
   * Queries a control unit with a specified id for the value of the specified
   * state variable.
   * 
   * @param controlUnitID the id of the control unit provided by this factory
   * @param stateVariableID the id of the variable
   * 
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * @return The value of the variable
   */
  public Object queryStateVariable(String controlUnitID, String stateVariableID) throws ControlUnitException;

  /**
   * Executes the specified action over a control unit with specified id.
   * 
   * @param controlUnitID the id of the control unit
   * @param actionID the id of the action
   * @param arguments the input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more then one, the value
   *          must be a <code>Object[]</code> and arguments are retrieved from
   *          that array.
   * 
   * @throws ControlUnitException if an error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * 
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value
   */
  public Object invokeAction(String controlUnitID, String actionID, Object arguments)
      throws ControlUnitException;

  /**
   * Explicitly creates control unit and returns the id of the newly created
   * control unit. The <code>ControlUnitFactory</code> may optionally define
   * in its metadata one or more methods for creating of new control units.
   * These methods are called constructors and are specified in the metadata
   * definition for the particular type of Control Units. A constructor is
   * defined as a special class of action with identifier starting with
   * <code>"$create."</code>. Every constructor method may have different
   * number and/or types of arguments, specified in the metadata and implemented
   * by the corresponding <code>ControlUnitFactory</code>.
   * 
   * @param constructorID the id of the constructors. Must start with
   *          <code>"$create."<code>.
   * @param  arguments - the 'constructors' argument(s). If the argument is only 
   *         one this is the argument itself. If the arguments are more then one, the 
   *         value must be a <code>Object</code> array and arguments are retrieved from it.
   *
   * @throws ControlUnitException if the control unit cannot be created for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   * 
   * @return the id of the newly created control unit.
   */
  public String createControlUnit(String constructorID, Object arguments)
      throws ControlUnitException;

  /**
   * Explicitly removes control unit instance with a given id. Some type of
   * control units may not support explicit removing of the resources
   * represented by the corresponding control units. In that case this method
   * throws <code>IllegalArgumentException</code>. Support for explicit
   * destroying of Control Units is specified in the control unit metadata by
   * presence of an action with id <code>"$destroy"</code>.
   * 
   * @param controlUnitID control unit id.
   * @throws ControlUnitException if the control unit cannot be destroyed for some
   * reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getNestedException()} methods can be used 
   * to determine the actual cause.
   */
  public void destroyControlUnit(String controlUnitID) throws ControlUnitException;
}

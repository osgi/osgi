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
 * Control Unit is an object, which provides formal representation of a certain
 * resource (device, software or hardware components, etc.) so it can be managed
 * in a uniform way by different applications. <br>
 * The public interface of the Control Unit is represented by a set of valued
 * attributes called <code>state variables</code> and set of operations called
 * <code>actions</code>.
 * <p>
 * 
 * A Control Unit instance is characterized by its <code>type</code> and
 * <code>id</code>. The type of a Control Unit defines its allowed set of
 * state variables and actions. The id of the Control Unit instance identifies
 * it uniquely in the scope of its type.
 * <p>
 * 
 * A Control Unit instance can be exported (made available) to the management
 * applications either by registering
 * {@link org.osgi.service.cu.admin.spi.ManagedControlUnit}, which represents
 * single control unit instance or by registering a
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory} service, which
 * maintains a set of Control Unit instances of the same type.
 * <p>
 * 
 * Control Units may be arranged hierarchically - every control unit instance
 * may have one or more sub control units and one or more parent control units.
 * The implementers of the Control Units must avoid cycles in the Control Unit
 * hierarchy. Organizing Control Units may be convenient for logical grouping of
 * Control Units, but is especially useful for representing more complex
 * resources - devices, hardware and software systems, which may be decomposed
 * to a hierarchy of sub-components, achieving arbitrary level of granularity.
 * 
 * @version $Revision$
 */
public interface ControlUnit {

  /**
   * Returns id of the control unit, which uniquely identifies it in the scope of
   * its parent.
   * 
   * @return The id of the control unit
   */
  public String getId();

  /**
   * Returns type of the control unit.
   * 
   * @return The type of the control unit
   */
  public String getType();

  /**
   * Returns the value of a specified state variable. State variables supported
   * by a control unit and their types are defined by the metadata of the
   * control unit.
   * 
   * @param stateVariableID The id of the variable
   * @return The value of the variable
   * @throws ControlUnitException if the state variable's value cannot be
   * retrieved for some reason. {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getApplicationException()} methods can be used 
   * to determine the actual cause.
   */
  public Object queryStateVariable(String stateVariableID) throws ControlUnitException;

  /**
   * Executes the specified action over this control unit. Actions supported by
   * a control unit and the number and types of the input and output arguments
   * of each action are defined by the metadata of the control unit.
   * 
   * @param actionID the id of the action
   * @param arguments the input argument(s). If the argument is only one this is
   *          the argument itself. If the arguments are more then one, the value
   *          must be a <code>Object</code> array and arguments are retrieved 
   *          from that array.
   * 
   * @return The output argument(s) or <code>null</code> if the action does
   *         not return value.
   * @throws ControlUnitException if error prevents the execution of the action.
   * {@link ControlUnitException#getErrorCode()}
   * and {@link ControlUnitException#getApplicationException()} methods can be used 
   * to determine the actual cause.
   */
  public Object invokeAction(String actionID, Object arguments) throws ControlUnitException;
}

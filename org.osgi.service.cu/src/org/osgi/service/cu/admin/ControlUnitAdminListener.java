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

/**
 * Applications interested in receiving events for creation and/or deletion of 
 * a specified set of control unit instances may implement this interface and 
 * register it as a service in the OSGi registry. {@link ControlUnitAdmin} 
 * service is responsible for tracking these services and to deliver the 
 * appropriate events to them.
 * <p>
 * 
 * A service registration property may be used as a filter to limit the number
 * of received events and to specify certain control unit to listen for. The key
 * of the property is
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_FILTER} whos value is
 * a <code>String</code> representing an LDAP filtering expression. The
 * properties that may be used in the LDAP filer are
 * {@link ControlUnitAdmin#EVENT_TYPE},
 * {@link org.osgi.service.cu.ControlUnitConstants#TYPE} and
 * {@link org.osgi.service.cu.ControlUnitConstants#ID}.
 * <p>
 * 
 * The filter states what types of events, and events for which control units to be
 * received. The listener will only be notified for events regarding control
 * units whose ID and type satisfy this filter, if the events match the event-
 * filtering criterion. If property
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_FILTER} is not
 * present, the listener will receive events for all control units.
 * <p>
 * 
 * Listeners may use the
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_SYNC} property to
 * specify that events must be delivered to them synchronously.
 * 
 * @version $Revision$
 */
public interface ControlUnitAdminListener {

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent} method to indicate that a new
   * control unit is available.
   * <p>
   * 
   * The value of this constant is 1
   */
  public static final int CONTROL_UNIT_ADDED = 1;

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent} method to indicate that a
   * control unit is not available anymore.
   * <p>
   * 
   * The value of this constant is 2
   */
  public static final int CONTROL_UNIT_REMOVED = 2;

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent} method to indicate that a new
   * control unit type is available, i.e. a control unit of a type 
   * (or a control unit factory providing control units of a type) not 
   * available before in the framework was registered.<BR>
   * 
   * When the event is of this type the
   * parameter of {@link #controlUnitEvent controlUnitEvent} method specifying a
   * control unit ID is <code>null</code>.
   * <p>
   * 
   * The value of this constant is 3
   */
  public static final int CONTROL_UNIT_TYPE_ADDED = 3;

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent} method to indicate that control
   * units of the given type are no more available, i.e. the last control unit
   * of this type (or the factory providing control units of this type) was 
   * unregistered from the framework. When the event is of this
   * type the parameter of {@link #controlUnitEvent controlUnitEvent} method
   * specifying a control unit ID is <code>null</code>.
   * <p>
   * 
   * The value of this constant is 4
   */
  public static final int CONTROL_UNIT_TYPE_REMOVED = 4;

  /**
   * Invoked by the <code>ControlUnitAdmin</code> service when a control unit instance has
   * been created or removed.
   * 
   * @param eventType one of {@link #CONTROL_UNIT_ADDED},
   *          {@link #CONTROL_UNIT_REMOVED},
   *          {@link #CONTROL_UNIT_TYPE_ADDED} or
   *          {@link #CONTROL_UNIT_TYPE_REMOVED}
   * @param controlUnitType the type of the control unit instance
   * @param controlUnitID the ID of the control unit instance
   */
  public void controlUnitEvent(int eventType, String controlUnitType, String controlUnitID);

}

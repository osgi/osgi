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
 * Applications interested in receiving events for creating and/or deleting of a
 * specified set of Control Unit instances may implement this interface and
 * register as service in the OSGi registry. Control Unit Admin service is
 * responsible for tracking these services and to deliver them the appropriate
 * events to the registered listeners.
 * <p>
 * 
 * A service registration property may be used as a filter to limit the number
 * of received events and to specify certain control unit to listen for. The key
 * of the property is
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_FILTER} with value of
 * type <code>String</code> representing LDAP filtering expression. The
 * properties that may be used in the LDAP filer are
 * {@link ControlUnitAdmin#EVENT_TYPE},
 * {@link org.osgi.service.cu.ControlUnitConstants#TYPE} and
 * {@link org.osgi.service.cu.ControlUnitConstants#ID}.
 * <p>
 * 
 * The filter states what types of events, and events for which units to be
 * received. The listener will be notified only for events regarding Control
 * Units whose id and type satisfies this filter, if the events match the event-
 * filtering criterion. If property
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_FILTER} does not
 * present, the listener will receive events for all control units.
 * <p>
 * 
 * Listeners may use the
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_SYNC} property to
 * specify that events are delivered synchronously.
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
   * {@link #controlUnitEvent controlUnitEvent} method when to indicate that a
   * control unit is not available anymore.
   * <p>
   * 
   * The value of this constant is 2
   */
  public static final int CONTROL_UNIT_REMOVED = 2;

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent} method to indicate that a new
   * control unit type is available. When the event is of this type the
   * parameter of {@link #controlUnitEvent controlUnitEvent} method specifying a
   * control unit id is <code>null</code>.
   * <p>
   * 
   * The value of this constant is 3
   */
  public static final int CONTROL_UNIT_TYPE_APPEARED = 3;

  /**
   * This constant is used as first parameter in
   * {@link #controlUnitEvent controlUnitEvent}method to indicate that control
   * units of the given type are no more available. When the event is of this
   * type the parameter of {@link #controlUnitEvent controlUnitEvent} method
   * specifying a control unit id is <code>null</code>.
   * <p>
   * 
   * The value of this constant is 4
   */
  public static final int CONTROL_UNIT_TYPE_DISAPPEARED = 4;

  /**
   * Invoked by the Control Unit admin service when a Control Unit instance has
   * been created or removed.
   * 
   * @param eventType one of {@link #CONTROL_UNIT_ADDED},
   *          {@link #CONTROL_UNIT_REMOVED},
   *          {@link #CONTROL_UNIT_TYPE_APPEARED} or
   *          {@link #CONTROL_UNIT_TYPE_DISAPPEARED}
   * @param controlUnitType the type of the Control Unit instance
   * @param controUnitID the id of the Control Unit instance
   */
  public void controlUnitEvent(int eventType, String controlUnitType, String controUnitID);

}

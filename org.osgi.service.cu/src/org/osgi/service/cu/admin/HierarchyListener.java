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
 * Applications interested in receiving events for changes in the hierarchy of
 * control units (attaching of a control unit to new parent or detaching it from
 * old one) may implement this interface and register as service in the OSGi
 * registry. Control Unit Admin service is responsible for tracking these
 * services and for delivering the appropriate events to the registered
 * listeners.
 * <p>
 * 
 * A service registration property may be used as a filter to limit the number
 * of received events and to specify certain control unit and/or event types to
 * listen for. The key of this property is
 * {@link org.osgi.service.cu.ControlUnitConstants#EVENT_FILTER} with value of
 * type <code>String</code> representing LDAP filtering expression.
 * <p>
 * 
 * The properties, which may be used in the LDAP filer are:
 * <ul>
 * <li>{@link ControlUnitAdmin#EVENT_TYPE} - to limit the types of events
 * received by the listener. Valid values are {@link #DETACHED} and
 * {@link #ATTACHED}.
 * <li>{@link org.osgi.service.cu.ControlUnitConstants#TYPE} and
 * {@link org.osgi.service.cu.ControlUnitConstants#ID} are used to limit events
 * based on the child control units to which the events are relevant.
 * <li>{@link org.osgi.service.cu.ControlUnitConstants#PARENT_TYPE} and
 * {@link org.osgi.service.cu.ControlUnitConstants#PARENT_ID} can be used to
 * specify that the listener is only interested in receiving events about
 * hierarchy changes regarding given parent control units.
 * </ul>
 * 
 * @version $Revision$
 */
public interface HierarchyListener {

  /**
   * This constant is used as first parameter in {@link #hierarchyChanged} 
   * method to indicate that the control control has detached from the given
   * parent.
   * <p>
   * 
   * The value of this constant is 1
   */
  public static final int DETACHED = 1;

  /**
   * This constant is used as first parameter in {@link #hierarchyChanged} 
   * method to indicate that the control control has attached to the given
   * parent.
   * <p>
   * 
   * The value of this constant is 2
   */
  public static final int ATTACHED = 2;

  public void hierarchyChanged(int eventType, String cuType, String cuID,
                               String parentType, String parentID);

}
/*
 * $Id$
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
package org.osgi.impl.service.cu.admin;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.cu.admin.ControlUnitAdmin;

/**
 * This class is used to represend in a common way all three types of events 
 * related to control units - control units events, state variables events and
 * hierarchy changed events.
 * 
 * It is used by {@link org.osgi.impl.service.cu.admin.ListenersTracker ListenersTracker}s
 * and {@link org.osgi.impl.service.cu.admin.NotifyListenersThread NotifyListenersThread}.
 * 
 * @version $Revision$
 */
class EventData {
  
  static final int SV_EVENT = 0;
  static final int CU_EVENT = 1;
  static final int HIERARCHY_EVENT = 2;
  
  protected EventData next;
  
  private int type;
  
  private Object stateVariableValue;
  private Hashtable data = new Hashtable();
  
  /**
   * Create a control unit event.
   * 
   * @param eventType one of the event types from 
   * {@link org.osgi.service.cu.ControlUnitAdminListener ControlUnitListener}.
   * 
   * @param cuType type of the control unit for which the event is
   * @param cuID id of the control unit for which the event is
   */
  EventData(int eventType, String cuType, String cuID) {
    this.type = CU_EVENT;
    
    data.put(ControlUnitAdmin.EVENT_TYPE, Integer.toString(eventType));
    data.put(ControlUnitConstants.TYPE, cuType);
    
    if (cuID != null) {
      data.put(ControlUnitConstants.ID, cuID);
    }
  }
  
  /**
   * Create a hierarchy changed event.
   * 
   * @param eventType one of the event types from 
   * {@link org.osgi.service.cu.HierarchyListener HierarchyListener}.
   * 
   * @param cuType type of the control unit for which the event is
   * @param cuID id of the control unit for which the event is
   * @param parentType type of the parent control unit for which the event is
   * @param parentID id of the parent control unit for which the event is
   */
  EventData(int eventType, String cuType, String cuID, String parentType, String parentID) {
    this.type = HIERARCHY_EVENT;
    
    data.put(ControlUnitAdmin.EVENT_TYPE, Integer.toString(eventType));
    data.put(ControlUnitConstants.TYPE, cuType);
    data.put(ControlUnitConstants.ID, cuID);
    data.put(ControlUnitConstants.PARENT_TYPE, parentType);
    data.put(ControlUnitConstants.PARENT_ID, parentID);
  }
  
  /**
   * Create a state variable changed event.
   * 
   * @param cuType type of the control unit for which the event is
   * @param cuID id of the control unit for which the event is
   * @param varID id of the changed state variable
   * @param value the new state variable value
   */
  EventData(String cuType, String cuID, String varID, Object value) {
    this.type = SV_EVENT;
    
    data.put(ControlUnitConstants.TYPE, cuType);
    data.put(ControlUnitConstants.ID, cuID);
    data.put(ControlUnitConstants.STATE_VARIABLE_ID, varID);
    
    this.stateVariableValue = value;
  }
  
  /**
   * Get the dictionary which would be used for matching with the listeners filter.
   * 
   * @return event data dictionary
   */
  final Dictionary getEventData() {
    return (Dictionary)data.clone();
  }
  
  /**
   * Get the new state variable value.
   * @return the new state variable value, if this event is a state variable 
   * changed event, otherwise - <code>null</code>
   */
  final Object getVarValue() {
    return stateVariableValue;
  }
  
  /**
   * Get the type of the event.
   * @return {@link #SV_EVENT}, {@link #CU_EVENT} or {@link #HIERARCHY_EVENT}
   */
  final int getType() {
    return type;
  }
  
  /**
   * Get the event's type (for control unit and hierarchy events only).
   * 
   * @return event's type
   */
  final int getEventType() {
    return Integer.parseInt( (String)data.get(ControlUnitAdmin.EVENT_TYPE) );
  }
  
  /**
   * @return type of the control unit for which this event is
   */
  final String getCUType() {
    return (String)data.get(ControlUnitConstants.TYPE);
  }
  
  /**
   * @return id of the control unit for which this event is
   */
  final String getCUID() {
    return (String)data.get(ControlUnitConstants.ID);
  }
  
  /**
   * @return type of the parent control unit for which this event is 
   * (if the event is not a hierarchy changed event <code>null</code> is returned)
   */
  final String getParentType() {
    return (String)data.get(ControlUnitConstants.PARENT_TYPE);
  }
  
  /**
   * @return ID of the parent control unit for which this event is 
   * (if the event is not a hierarchy changed event <code>null</code> is returned)
   */
  final String getParentID() {
    return (String)data.get(ControlUnitConstants.PARENT_ID);
  }
  
  
  /**
   * @return if this event is state variable changed - ID of the changed variable,
   * otherwise <code>null</code> is returned 
   */
  final String getVarID() {
    return (String)data.get(ControlUnitConstants.STATE_VARIABLE_ID);
  }
  
}
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
package org.osgi.impl.service.cu.admin;

import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.service.cu.admin.HierarchyListener;

/**
 * A {@link org.osgi.impl.service.cu.admin.ListenersTracker} for 
 * hieararchy changed listeners.
 *  
 * @version $Revision$
 */
class HierarchyListenersTracker extends ListenersTracker {
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext} of the ControlUnitAdmin bundle
   */
  HierarchyListenersTracker(BundleContext bc) {
    super(bc, HierarchyListener.class);
  }
  
  /**
   * Notify listeners which registered for synchronous delivery of events.
   * 
   * @param eventType one of the event types from {@link HierarchyListener}
   * @param cuType type of the control unit for which the event is
   * @param cuID ID of the control unit for which the event is
   * @param parentType type of the parent control unit for which the event is
   * @param parentID ID of the parent control unit for which the event is
   */
  public void notifySyncListeners(int eventType, String cuType, 
    String cuID, String parentType, String parentID) {
      
    synchronized (syncListenersData) {
      if (syncListenersData.size() > 0) {
        EventData eventData = new EventData(eventType, cuType, cuID, parentType, parentID);
        notifyListeners(syncListenersData, eventData);
      }
    }
  }
  
  /**
   * Notify listeners which registered for asynchronous delivery of events.
   * 
   * @param eventData event data
   */
  public void notifyAsyncListeners(EventData eventData) {
    notifyListeners(asyncListenersData, eventData);
  }
  
  private void notifyListeners(Vector listenersData, EventData eventData) {
    synchronized (listenersData) {
      
      for (int i = 0; i < listenersData.size(); i++) {
        ListenerData curData = (ListenerData)listenersData.elementAt(i);
        if ( curData.isMatching(eventData.getEventData()) ) {
          HierarchyListener listener = (HierarchyListener)curData.listener;

          try {
            listener.hierarchyChanged( eventData.getEventType(), eventData.getCUType(), 
              eventData.getCUID(), eventData.getParentType(), eventData.getParentID() );
          } catch (Throwable ex) {
            ex.printStackTrace(System.err);
          }
        } // if
      } // for
      
    }
  }

}
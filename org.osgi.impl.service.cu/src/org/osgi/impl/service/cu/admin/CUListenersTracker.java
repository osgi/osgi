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
import org.osgi.service.cu.admin.ControlUnitAdminListener;


/**
 * This class is used for tracking and notifying about control unit events 
 * the registered in the framework 
 * {@link org.osgi.service.cu.ControlUnitAdminListener ControlUnitListener}s.
 * 
 * @version $Revision$
 */
class CUListenersTracker extends ListenersTracker {
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext BundleContext} of the ControlUnitAdmin bundle
   */
  CUListenersTracker(BundleContext bc) {
    super(bc, ControlUnitAdminListener.class);
  }
  
  /**
   * Notify listeners which registered for synchronous delivery of events.
   * 
   * @param eventType one of the events from {@link ControlUnitAdminListener}
   * @param cuType type of the control unit for which this event is
   * @param cuID ID of the control unit for which this event is
   */
  public void notifySyncListeners(int eventType, String cuType, String cuID) {
    synchronized (syncListenersData) {
      if (syncListenersData.size() > 0) {
        EventData eventData = new EventData(eventType, cuType, cuID);
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
          ControlUnitAdminListener cuListener = (ControlUnitAdminListener)curData.listener;
        
          try {
            cuListener.controlUnitEvent( eventData.getEventType(), eventData.getCUType(), 
              eventData.getCUID() );
          } catch (Throwable ex) {
            ex.printStackTrace(System.err);
          }
        }
      } // for
      
    }
  }

}
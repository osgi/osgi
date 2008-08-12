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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.cu.StateVariableListener;

/**
 * This class is used for tracking and notifying about state variable unit events 
 * the registered in the framework 
 * {@link org.osgi.service.cu.StateVariableListener}s.
 * 
 * @version $Revision$
 */
class SVListenersTracker extends ListenersTracker {
  
  private Vector initialValuesListeners;
  private Hashtable cuProviders;
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext} of the ControlUnitAdmin bundle
   * @param cuProviders control unit providers table
   */
  SVListenersTracker(BundleContext bc, Hashtable cuProviders) {
    super(bc, StateVariableListener.class);
    
    this.cuProviders = cuProviders;
    
    initialValuesListeners = new Vector(5, 5);
  }
  
  /**
   * Should be called when new control unit is added in the framework.
   * 
   * @param cuType type of the newly added control unit
   * @param cuID ID of the newly added control unit
   */
  void controlUnitAdded(String cuType, String cuID) {
    synchronized (cuProviders) {
      Provider provider = (Provider)cuProviders.get(cuType);
      
      for (int i = 0; i < initialValuesListeners.size(); i++) {
        ListenerData data = (ListenerData)initialValuesListeners.elementAt(i);
      
        sendInitialValues((StateVariableListener)data.listener, data.filter, cuType, 
          cuID, provider);
      }
    } // synchronized
  }
  
  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ListenersTracker#listenerAdded(java.lang.Object, org.osgi.framework.ServiceReference, org.osgi.framework.Filter)
   */
  protected ListenerData listenerAdded(Object listener, ServiceReference reference, Filter filter) {
    ListenerData result = new ListenerData(listener, reference, filter);
    
    if (reference.getProperty(ControlUnitConstants.EVENT_AUTO_RECEIVE) != null) {
      initialValuesListeners.addElement(result);
      
      sendInitialValues((StateVariableListener)listener, filter);
    }
    
    return result;
  }

  /* (non-Javadoc)
   * @see org.osgi.impl.service.cu.ListenersTracker#listenerRemoved(org.osgi.impl.service.cu.ListenerData)
   */
  protected void listenerRemoved(ListenerData listenerData) {
    initialValuesListeners.removeElement(listenerData);
  }
  
  /**
   * Notify listeners which registered for synchronous delivery of events.
   * 
   * @param cuType type of the control unit for which this event is
   * @param cuID ID of the control unit for which this event is
   * @param varID ID of the changed state variable
   * @param value new state variable's value
   */
  public void notifySyncListeners(String cuType, String cuID, String varID, Object value) {
    synchronized (syncListenersData) {
      if (syncListenersData.size() > 0) {
        EventData eventData = new EventData(cuType, cuID, varID, value);
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
          StateVariableListener svListener = (StateVariableListener)curData.listener;

          try {
            svListener.stateVariableChanged( eventData.getCUType(), eventData.getCUID(), 
              eventData.getVarID(), eventData.getVarValue() );
          } catch (Throwable ex) {
            ex.printStackTrace(System.err);
          }
        } // if
      } // for
      
    }
  }
  
  private void sendInitialValues(StateVariableListener listener, Filter filter) {
    synchronized (cuProviders) {
      Enumeration cuTypes = cuProviders.keys();
      
      while ( cuTypes.hasMoreElements() ) {
        String cuType = (String)cuTypes.nextElement();
        
        sendInitialValues(listener, filter, cuType);
      } // while
      
    } // synchronized (cuProviders)
  }
  
  private void sendInitialValues(StateVariableListener listener, Filter filter, String cuType) {
    Provider provider = (Provider)cuProviders.get(cuType);
    
    String[] cuIDs = provider.listControlUnits();
    for (int i = 0; i < cuIDs.length; i++) {
      sendInitialValues(listener, filter, cuType, cuIDs [i], provider);
    }
  }
  
  private void sendInitialValues(StateVariableListener listener, Filter filter, 
    String cuType, String cuID, Provider provider) {
    
    String[] svIDs = null;
    
    try {
      svIDs = (String [])provider.queryStateVariable(cuID, ControlUnitConstants.STATE_VARIABLES_LIST);
    } catch (Throwable ex) {
      ex.printStackTrace(System.err);
    }
    
    if (svIDs == null) {
      return;
    }
    
    for (int i = 0; i < svIDs.length; i++) {
      if ( isMatching(filter, cuType, cuID, svIDs [i]) ) {
        try {
          listener.stateVariableChanged(cuType, cuID, svIDs [i], 
            provider.queryStateVariable(cuID, svIDs [i])); 
        } catch (Throwable ex) {
          ex.printStackTrace(System.err);
        }
      } 
    }
  }
  
  private boolean isMatching(Filter filter, String cuType, String cuID, String svID) {
    EventData eventData = new EventData(cuType, cuID, svID, null);
    boolean result = filter.match(eventData.getEventData());  
    
    return result;
  }

}
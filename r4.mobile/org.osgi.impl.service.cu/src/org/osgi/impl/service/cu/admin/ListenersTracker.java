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
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cu.ControlUnitConstants;

/**
 * Abstract class which holds the common functionality for tracking
 * the registered in the framework listeners interested in events related to
 * control units.
 *  
 * @version $Revision$
 */
abstract class ListenersTracker implements ServiceListener {
  
  protected static final String LOG_CATEGORY = "[CUADMIN:ListenersTracker] ";
  
  private BundleContext bc;
  
  private Class listenersClass;
  
  protected Vector syncListenersData;
  protected Vector asyncListenersData;
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext} of the ControlUnitAdmin bundle
   * @param listenersClass class of the listeners which will be tracked
   */
  ListenersTracker(BundleContext bc, Class listenersClass) {
    this.bc = bc;
    this.listenersClass = listenersClass;
  }
  
  /**
   * Start tracking. 
   */
  void initiate() {
    syncListenersData = new Vector(5, 5);
    asyncListenersData = new Vector(5, 5);
    
    collectListeners();
    
    try {
      bc.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + 
        listenersClass.getName() + ")");
    } catch (InvalidSyntaxException ex) {
      ex.printStackTrace(System.err);
    }
  }
  
  /**
   * Stop tracking.
   */
  protected void finish() {
    bc.removeServiceListener(this);
    
    releaseListeners();
    
    syncListenersData = null;
    asyncListenersData = null;
  }
  
  /**
   * Called when a new listener, in which the tracker is interesed, is found
   * in the framework.
   * 
   * Subclasses should override this method if additional work is needed when
   * a new listener is found.
   * 
   * @param listener the listener found
   * @param reference service reference of the listener
   * @param filter the filter of the listener
   * 
   * @return newly constructed {@link ListenerData}
   */
  protected ListenerData listenerAdded(Object listener, ServiceReference reference, Filter filter) {
    return new ListenerData(listener, reference, filter);
  }
  
  /**
   * Called when a listener, in which the tracker is interesed, was removed 
   * from the framework.
   * 
   * Subclasses should override this method if additional clean-up is needed
   * when a listener is removed.
   * 
   * @param listenerData {@link ListenerData} of the removed listener
   */
  protected void listenerRemoved(ListenerData listenerData) {}

  //******* ServiceListener interface implementation *******//
  
  /* (non-Javadoc)
   * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
   */
  public void serviceChanged(ServiceEvent event) {
    switch ( event.getType() ) {
      case ServiceEvent.REGISTERED : {
        getListener( event.getServiceReference() );
        break;
      }
      
      case ServiceEvent.UNREGISTERING : {
        ungetListener( event.getServiceReference() );
        break;        
      }
      
      case ServiceEvent.MODIFIED : {
        ungetListener( event.getServiceReference() );
        getListener( event.getServiceReference() );
        break;
      }
    }
  }
  
  //******* End. ServiceListener interface implementation *******//
  
  private void collectListeners() {
    ServiceReference[] references = null;
    
    try {
      references = bc.getServiceReferences(listenersClass.getName(), null);
    } catch (InvalidSyntaxException ex) {
      ex.printStackTrace(System.err);
    }
    
    if (references == null) {
      return;
    }
    
    for (int i = 0; i < references.length; i++) {
      getListener(references [i]);
    }
  }
  
  private void releaseListeners() {
    releaseListeners(syncListenersData);
    releaseListeners(asyncListenersData);
  }
  
  private void releaseListeners(Vector listenersData) {
    synchronized (listenersData) {
      for (int i = 0; i < listenersData.size(); i++) {
        ListenerData curData = (ListenerData)listenersData.elementAt(i);
        bc.ungetService(curData.reference);
      }
      
      listenersData.removeAllElements();
    }
  }
  
  private void getListener(ServiceReference reference) {
    if ( isListenerSynchronous(reference) ) {
      addListener(syncListenersData, reference);
    } else {
        addListener(asyncListenersData, reference);
    }
  }
  
  private void ungetListener(ServiceReference reference) {
    if ( isListenerSynchronous(reference) ) {
      removeListener(syncListenersData, reference);
    } else {
      removeListener(asyncListenersData, reference);
    }
  }
  
  private void addListener(Vector listenersData, ServiceReference reference) {
    synchronized (listenersData) {
      Object listener = bc.getService(reference);
      
      if (listener == null) {
        return;
      }
      
      Filter filter = getEventFilter(reference);
      if ( !containsListener(listenersData, reference) ) {        
        listenersData.addElement( listenerAdded(listener, reference, filter) );  
      }
    } 
  }
  
  private void removeListener(Vector listenersData, ServiceReference reference) {
    synchronized (listenersData) {
      bc.ungetService(reference);
      
      for (int i = 0; i < listenersData.size(); i++) {
        ListenerData curData = (ListenerData)listenersData.elementAt(i);
        
        if ( curData.reference.equals(reference) ) {
          listenersData.removeElementAt(i);
          listenerRemoved(curData);

          break;
        }
      }
    }  
  }
  
  private boolean containsListener(Vector listenersData, ServiceReference reference) {
    for (int i = 0; i < listenersData.size(); i++) {
      ListenerData curData = (ListenerData)listenersData.elementAt(i);
      
      if ( reference.equals(curData.reference) ) {
        return true;
      }
    }
    
    
    return false;
  }
  
  private Filter getEventFilter(ServiceReference reference) {
    if (reference != null) {
      try {
        String filter = (String)reference.getProperty(ControlUnitConstants.EVENT_FILTER);
        
        return filter != null ? bc.createFilter(filter) : null;
      } catch (InvalidSyntaxException ex) {
        ex.printStackTrace(System.err);
      }
    }    
    
    return null;
  } 
  
  private boolean isListenerSynchronous(ServiceReference reference) {
    return reference.getProperty(ControlUnitConstants.EVENT_SYNC) != null;
  }
}
/*
* $Header$
* 
* Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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
package org.osgi.impl.service.metatype2;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype2.MetaDataListener;
import org.osgi.service.metatype2.MetaDataService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * 
 * @version $Revision$
 */
class MetaDataListenersTracker implements ServiceTrackerCustomizer {

  private BundleContext bc;
  private ServiceTracker tracker;
  private Logger logger;
  
  MetaDataListenersTracker(BundleContext bc, Logger logger) {
    this.bc = bc;
    this.logger = logger;
    
    tracker = new ServiceTracker(bc, MetaDataListener.class.getName(), this);
  }
  
  void open() {
    tracker.open();
  }
  
  void close() {
    tracker.close();
  }
  
  void notifyListeners(String category, String id, int eventType) {
    Object[] listenerDatas = tracker.getServices(); 
    
    if (listenerDatas == null) {
      return;
    }
    
    Hashtable eventDictionary = new Hashtable(2, 1);
    eventDictionary.put(Constants.SERVICE_PID, id);
    if (category != null) {
      eventDictionary.put(MetaDataService.METATYPE_CATEGORY, category);
    }
    
    for (int i = 0; i < listenerDatas.length; i++) {
      ListenerData data = (ListenerData)listenerDatas [i];
      
      if ( data.isMatching(eventDictionary) ) {
        try {
          data.notify(category, id, eventType);
        } catch (Throwable ex) {
          logger.logError("Error while notifying MetaType Listener!", ex);
        }
      }
    }
  }
  
  //******* ServiceTrackerCustomizer interface methods *******//
  
  /* (non-Javadoc)
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
   */
  public Object addingService(ServiceReference reference) {
    Filter filter = getMetaTypeFilter(reference);
    MetaDataListener listener = (MetaDataListener)bc.getService(reference);
    
    return new ListenerData(listener, filter);
  }

  /* (non-Javadoc)
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
   */
  public void modifiedService(ServiceReference reference, Object service) {
    Filter newFilter = getMetaTypeFilter(reference);
    ListenerData data = (ListenerData)service;
    
    data.setFilter(newFilter);
  }

  /* (non-Javadoc)
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
   */
  public void removedService(ServiceReference reference, Object service) {
    bc.ungetService(reference);
  }

  //******* END ServiceTrackerCustomizer interface methods *******//
  
  private Filter getMetaTypeFilter(ServiceReference reference) {
    Object filter = reference.getProperty(MetaDataListener.METATYPE_FILTER);
    
    if (filter != null) {
      try {
        return bc.createFilter(filter.toString());
      } catch (InvalidSyntaxException ex) {
        logger.logError("Invalid filter!", reference, ex);
      }
    }
    
    return null;
  }
}

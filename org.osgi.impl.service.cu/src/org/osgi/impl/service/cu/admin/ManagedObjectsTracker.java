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

import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cu.ControlUnitConstants;
import org.osgi.service.cu.admin.spi.CUAdminCallback;

/**
 * Abstract class which holds the common functionality for tracking
 * the registered in the framework 
 * {@link org.osgi.service.cu.ControlUnit},
 * {@link org.osgi.service.cu.spi.ManagedControlUnit}s and
 * {@link org.osgi.service.cu.spi.ControlUnitFactory ControlUnitFactory}s.
 *  
 * @version $Revision$
 */
abstract class ManagedObjectsTracker implements ServiceListener {
  
  private Class[] trackedClasses;
  
  private Vector serviceReferences;
  
  protected BundleContext bc;
  
  protected CUAdminCallback cuAdminCallback;
  
  /**
   * Constructor.
   * 
   * @param bc {@link BundleContext} of the ControlUnitAdmin bundle
   * @param trackedClasses classes of the objects to be tracked
   */
  ManagedObjectsTracker(BundleContext bc, Class[] trackedClasses) {
    this.bc = bc;
    
    this.trackedClasses = trackedClasses;
    
    this.serviceReferences = new Vector(5, 5);
  }
  
  /**
   * Start tracking.
   * 
   * @param callback callback to be notified about control unit events
   */
  void initiate(CUAdminCallback callback) {
    this.cuAdminCallback = callback;
    
    try {
      bc.addServiceListener(this, getFilter());
    } catch(InvalidSyntaxException ex) {
      ex.printStackTrace(System.err);
    }
    
    collectManagedObjects();
  }
  
  private String getFilter() {
    if (trackedClasses.length == 1) {
      return "(" + Constants.OBJECTCLASS + "=" + 
        trackedClasses [0].getName() + ")";
    }
    
    StringBuffer filter = new StringBuffer("(|");
    for (int i = 0; i < trackedClasses.length; i++) {
      filter.append('(');
      filter.append(Constants.OBJECTCLASS);
      filter.append('=');
      filter.append(trackedClasses [i].getName());
      filter.append(')');
    }
    filter.append(')');
    
    return filter.toString();
  }
  
  /**
   * Stop tracking.
   */
  void finish() {
    releaseManagedObjects();
    
    bc.removeServiceListener(this);
  }
  
  //******* Abstract methods *******//
  
  /**
   * Subclasses should override this method to perform the action needed when
   * a new managed object is found.
   *  
   * @param reference service reference of the managed object
   * @param type control unit type of the managed object
   * @param isRegistering true if the managed object is just registering,
   * false - if it was already registered and found when the tracking started 
   * @return true if all went right
   */
  protected abstract boolean onManagedObjectFound(ServiceReference reference, String type, boolean isRegistering);
  
  /**
   * Subclasses should override this method to perform the action needed when
   * a managed object has modified its registration properties.
   *  
   * @param reference service reference of the managed object
   * @param type control unit type of the managed object
   */
  protected abstract void onManagedObjectModified(ServiceReference reference, String type);
  
  /**
   * Subclasses should override this method to perform the action needed when
   * a managed object has been removed.
   *  
   * @param reference service reference of the managed object
   * @param type control unit type of the managed object
   * @param isUnregistering true if the managed object has unregistered from the
   * framework, false - if it is remove because the tracking is stopped
   */
  protected abstract void onReleasingManagedObject(ServiceReference reference, String type, boolean isUnregistering);
  
  //******* End. Abstract methods *******//
  
  //******* ServiceListener interface implementation *******//
  
  /* (non-Javadoc)
   * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
   */
  public void serviceChanged(ServiceEvent event) {
    switch ( event.getType() ) {
      case ServiceEvent.REGISTERED : {
        getManagedObject(event.getServiceReference(), true);
        break;
      }
      
      case ServiceEvent.MODIFIED : {
        ServiceReference reference = event.getServiceReference();
        
        synchronized (reference) {
          onManagedObjectModified(reference, getType(reference));
        }
        
        break;
      }
      
      case ServiceEvent.UNREGISTERING : {
        ungetManagedObject(event.getServiceReference(), true);
        break;        
      }
    }
  }
  
  //******* End. ServiceListener interface implementation *******//
  
  /**
   * Log error message.
   * 
   * @param message message to be logged
   */
  protected void logError(String message) {
    System.err.println(message);
  }
  
  /**
   * Extract control unit type from given service reference.
   * 
   * @param reference service reference
   * @return control unit type
   */
  protected String getType(ServiceReference reference) {
    return getProperty(reference, ControlUnitConstants.TYPE, true);
  }
  
  /**
   * Extract control unit type version from given service reference.
   * 
   * @param reference service reference
   * @return control unit type version
   */
  protected String getVersion(ServiceReference reference) {
    return getProperty(reference, ControlUnitConstants.VERSION, false);
  }
  
  /**
   * Get a string property from a service reference. 
   * 
   * @param reference service reference
   * @param name name of the property
   * @param isRequired true if the property is required
   * @return the value of the property 
   */
  protected String getProperty(ServiceReference reference, String name, boolean isRequired) {
    Object property = reference.getProperty(name);
    
    if (property == null) {
      if (isRequired) {
        System.err.println("Required property '" + name + "' not found for reference '" + reference + "'!");
      }
        
      return null;
    }  
    
    if ( property instanceof String) {
      return (String)property;
    } 
    
    System.err.println("Property '" + name + "' is not String for reference '" + reference + "'!");
    return null;
  }
  
  private void collectManagedObjects() {
    for (int i = 0; i < trackedClasses.length; i++) {
      ServiceReference[] references = null;
      try {
        references = bc.getServiceReferences(trackedClasses [i].getName(), null);
      } catch(InvalidSyntaxException ex) {
          // exception never thrown
      }
      
      if (references == null) {
        return;
      }
      
      for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++) {
        getManagedObject(references [referenceIndex], false);
      }
    }    
  }
  
  private void releaseManagedObjects() {
    synchronized (serviceReferences) {
      for (int i = 0; i < serviceReferences.size(); i++) {
        ServiceReference reference = (ServiceReference)serviceReferences.elementAt(i);
        
        onReleasingManagedObject(reference, getType(reference), false);
      }
      
      serviceReferences.removeAllElements();
    }
  }
  
  private void getManagedObject(ServiceReference reference, boolean isRegistering) {
    synchronized (serviceReferences) {

      if ( !serviceReferences.contains(reference) ) {
        String type = getType(reference);
        
        if (type == null) {
          return;
        }
        
        if ( onManagedObjectFound(reference, type, isRegistering) ) {
          serviceReferences.addElement(reference);
        } 
      }
      
    }
  }
  
  private void ungetManagedObject(ServiceReference reference, boolean isUnregistering) {
    synchronized (serviceReferences) {
      
      if ( serviceReferences.removeElement(reference) ) {
        onReleasingManagedObject(reference, getType(reference), isUnregistering);
      }
      
    }
  }
  
}